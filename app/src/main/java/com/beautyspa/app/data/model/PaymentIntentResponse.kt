package com.beautyspa.app.data.model

// Represents response returned by POST /api/payments/intents
// See docs/API.md for fields

data class PaymentIntentResponse(
    val clientSecret: String,
    val paymentIntentId: String,
    val appointmentDraftId: String,
    val amount: Double,
    val currency: String,
    val expiresAt: String? = null
)
