package com.beautyspa.app.data.repository

import android.content.Context
import android.util.Log
import com.beautyspa.app.data.TokenManager
import com.beautyspa.app.data.model.BookingState
import com.beautyspa.app.data.model.ChatResponse
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

class ChatRepository(private val context: Context) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val endpoint = "http://10.193.62.91:4000/api/assistant/step"
    private val jsonMedia = "application/json; charset=utf-8".toMediaType()

    // Store conversation history

    suspend fun ask(question: String): ChatResponse = withContext(Dispatchers.IO) {
        try {
            // Get valid JWT token
            val token = TokenManager.getValidToken()
            if (token.isNullOrBlank()) {
                return@withContext ChatResponse("Authentication required. Please log in.")
            }

            // Build request body with message and history
            val bodyJson = JSONObject().apply {
                put("message", question)
            }.toString()

            val request = Request.Builder()
                .url(endpoint)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer $token")
                .header("User-Agent", "BeautySpaApp/1.0 (Android)")
                .post(bodyJson.toRequestBody(jsonMedia))
                .build()
            client.newCall(request).execute().use { res: Response ->
                val code = res.code
                val raw = res.body?.string()
                Log.d("ChatRepository", "HTTP $code, body=$raw")
                if (!res.isSuccessful) {
                    return@use ChatResponse("Server error ($code)")
                }
                if (raw.isNullOrBlank()) return@use ChatResponse("Empty response")

                // Try parse response structure
                return@use try {
                    val obj = JSONObject(raw)

                    // Check if response has state object (payment flow)
                    val stateObj = obj.optJSONObject("state")
                    val bookingState = if (stateObj != null) {
                        BookingState(
                            status = stateObj.optString("status").takeIf { it.isNotEmpty() },
                            booking_id = stateObj.optString("booking_id").takeIf { it.isNotEmpty() },
                            payment_intent_id = stateObj.optString("payment_intent_id").takeIf { it.isNotEmpty() },
                            service_id = stateObj.optString("service_id").takeIf { it.isNotEmpty() },
                            therapist_id = stateObj.optString("therapist_id").takeIf { it.isNotEmpty() },
                            date = stateObj.optString("date").takeIf { it.isNotEmpty() },
                            time_slot = stateObj.optString("time_slot").takeIf { it.isNotEmpty() },
                            client_secret = stateObj.optString("client_secret").takeIf { it.isNotEmpty() }
                        )
                    } else null

                    val message = obj.optString("message").takeIf { it.isNotEmpty() }
                        ?: obj.optString("answer").takeIf { it.isNotEmpty() }

                    ChatResponse(message ?: "No message", bookingState)
                } catch (_: Exception) {
                    // Fallback: try to extract answer or return raw
                    try {
                        val arr = JSONArray(raw)
                        val firstString = (0 until arr.length())
                            .asSequence()
                            .map { arr.opt(it) }
                            .firstOrNull { it is String }
                            ?.toString()
                            ?: raw
                        ChatResponse(firstString)
                    } catch (_: Exception) {
                        ChatResponse(raw)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ChatRepository", "ask() failed", e)
            ChatResponse("Network error: ${e.message}")
        }
    }
}