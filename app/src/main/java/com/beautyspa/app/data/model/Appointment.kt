package com.beautyspa.app.data.model

import java.util.Date

data class Appointment(
    val id: String,
    val service: Service,
    val specialist: Specialist,
    val date: Date,
    val timeSlot: String,
    val status: String,
    val totalPrice: Double
)

enum class AppointmentStatus {
    UPCOMING,
    COMPLETED,
    CANCELLED,
    // New statuses to align with backend and payments
    PENDING_PAYMENT,
    PAID,
    FAILED,
    REFUNDED
}
