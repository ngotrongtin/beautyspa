package com.beautyspa.app.data.model

data class Service(
    val id: String,
    val name: String,
    val description: String,
    val category: ServiceCategory,
    val duration: Int, // in minutes
    val price: Double,
    val imageUrl: String,
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val isFeatured: Boolean = false
)

enum class ServiceCategory(val displayName: String) {
    MASSAGE("Massage"),
    FACIAL("Facial"),
    NAILS("Nails"),
    HAIR("Hair"),
    BODY("Body Treatment")
}
