package com.beautyspa.app.data.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class ChatRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val endpoint = "https://tindev-spachatbot.hf.space/api/ask"
    private val jsonMedia = "application/json; charset=utf-8".toMediaType()

    suspend fun ask(question: String): String? = withContext(Dispatchers.IO) {
        try {
            val bodyJson = JSONObject().put("question", question).toString()
            val request = Request.Builder()
                .url(endpoint)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("User-Agent", "BeautySpaApp/1.0 (Android)")
                .post(bodyJson.toRequestBody(jsonMedia))
                .build()
            client.newCall(request).execute().use { res: Response ->
                val code = res.code
                val raw = res.body?.string()
                Log.d("ChatRepository", "HTTP $code, body=$raw")
                if (!res.isSuccessful) {
                    return@use "Server error ($code)" // surface error text instead of null
                }
                if (raw.isNullOrBlank()) return@use "Empty response" // surface as message
                // Try parse known structures
                return@use try {
                    val obj = JSONObject(raw)
                    obj.optString("answer")
                } catch (_: Exception) {
                    // Maybe an array
                    try {
                        val arr = JSONArray(raw)
                        // Pick first string element if any
                        (0 until arr.length())
                            .asSequence()
                            .map { arr.opt(it) }
                            .firstOrNull { it is String }
                            ?.toString()
                            ?: raw
                    } catch (_: Exception) {
                        raw
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ChatRepository", "ask() failed", e)
            "Network error: ${e.message}" // surface error as text
        }
    }
}