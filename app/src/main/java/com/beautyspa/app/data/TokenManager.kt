package com.beautyspa.app.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import org.json.JSONObject

object TokenManager {

    private const val PREFS_NAME = "auth_prefs"
    private const val TOKEN_KEY = "jwt_token"

    private lateinit var prefs: SharedPreferences

    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(token: String) {
        prefs.edit().putString(TOKEN_KEY, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(TOKEN_KEY, null)
    }

    /**
     * Returns a valid (non-expired) token if available. If expired, clears it and returns null.
     */
    fun getValidToken(): String? {
        val token = getToken()
        if (token.isNullOrBlank()) return null
        return if (isJwtExpired(token)) {
            clearToken()
            null
        } else token
    }

    fun clearToken() {
        prefs.edit().remove(TOKEN_KEY).apply()
    }

    /**
     * Lightweight JWT expiry check: decodes payload and checks the `exp` claim.
     * If token is not a JWT or missing exp, returns false (not treated as expired).
     */
    private fun isJwtExpired(jwt: String): Boolean {
        val parts = jwt.split('.')
        if (parts.size < 2) return false
        return try {
            val payloadB64 = parts[1]
            val decoded = Base64.decode(payloadB64.replace('-', '+').replace('_', '/'), Base64.DEFAULT)
            val json = JSONObject(String(decoded))
            val expSeconds = json.optLong("exp", 0L)
            if (expSeconds <= 0L) false else System.currentTimeMillis() >= expSeconds * 1000
        } catch (_: Exception) {
            false
        }
    }
}
