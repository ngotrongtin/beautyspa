package com.beautyspa.app.data.model

data class ChatResponse(
    val message: String,
    val state: BookingState? = null
)

data class BookingState(
    val status: String? = null,
    val booking_id: String? = null,
    val payment_intent_id: String? = null,
    val service_id: String? = null,
    val therapist_id: String? = null,
    val date: String? = null,
    val time_slot: String? = null,
    val client_secret: String? = null
)

