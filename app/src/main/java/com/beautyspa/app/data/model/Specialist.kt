package com.beautyspa.app.data.model

data class Specialist(
    val id: String,
    val name: String,
    val specialty: String,
    val imageUrl: String,
    val rating: Float = 0f,
    val experienceYears: Int = 0
)
