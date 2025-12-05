package com.beautyspa.app.data

import android.content.Context
import android.content.SharedPreferences

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

    fun clearToken() {
        prefs.edit().remove(TOKEN_KEY).apply()
    }
}

