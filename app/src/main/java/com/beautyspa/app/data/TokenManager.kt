package com.beautyspa.app.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import org.json.JSONObject

object TokenManager {

    private const val PREFS_NAME = "auth_prefs"
    private const val TOKEN_KEY = "jwt_token"

    private lateinit var prefs: SharedPreferences
    private lateinit var appContext: Context

    fun initialize(context: Context) {
        appContext = context.applicationContext
        prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
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
     * Clear all authentication-related state on device: JWT + Google sign-in state (if present).
     * This will attempt to sign out and revoke Google access asynchronously; failures are ignored.
     */
    fun clearAllAuth() {
        // Clear stored JWT
        clearToken()

        // Try to sign out from Google if Google APIs are available
        try {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
            val client = GoogleSignIn.getClient(appContext, gso)
            // Sign out asynchronously; no need to block logout flow
            client.signOut().addOnCompleteListener { /* no-op */ }
            // Also revoke access to ensure a fresh consent on next sign-in
            client.revokeAccess().addOnCompleteListener { /* no-op */ }
        } catch (e: Throwable) {
            // Ignore any errors related to Google Play services being unavailable
        }
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
