package com.beautyspa.app.data.model

import java.util.Date

data class Appointment(
    val id: String,
    val service: Service,
    val specialist: Specialist,
    val date: Date,
    val timeSlot: String,
    val status: AppointmentStatus,
    val totalPrice: Double
)

enum class AppointmentStatus {
    UPCOMING,
    COMPLETED,
    CANCELLED
}
