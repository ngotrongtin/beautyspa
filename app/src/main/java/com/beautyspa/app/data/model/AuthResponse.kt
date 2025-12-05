package com.beautyspa.app.data.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("token") val token: String,
    @SerializedName("expiresIn") val expiresIn: String,
    @SerializedName("user") val user: User
)
