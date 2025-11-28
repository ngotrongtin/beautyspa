package com.beautyspa.app.data.model

data class UserPreferences(
    val favSpecialty: String = "",
    val receivePromotions: Boolean = true,
    val preferredLanguage: String = "en"
)

data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String = "",
    val membershipLevel: String = "BRONZE",
    val loyaltyPoints: Int = 0,
    val preferences: UserPreferences = UserPreferences(),
    val profileImageUrl: String = ""
) {
    val fullName: String get() = listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ")
}

