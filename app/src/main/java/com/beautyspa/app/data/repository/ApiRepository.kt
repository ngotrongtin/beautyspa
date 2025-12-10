package com.beautyspa.app.data.repository

import com.beautyspa.app.BuildConfig
import com.beautyspa.app.data.model.Appointment
import com.beautyspa.app.data.model.AppointmentStatus
import com.beautyspa.app.data.model.AuthResponse
import com.beautyspa.app.data.model.PaymentIntentResponse
import com.beautyspa.app.data.model.Service
import com.beautyspa.app.data.model.ServiceCategory
import com.beautyspa.app.data.model.Specialist
import com.beautyspa.app.data.model.User
import com.beautyspa.app.data.model.UserPreferences
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class ApiRepository(
    baseUrl: String? = null
) {
    private val client: OkHttpClient = OkHttpClient.Builder()
        .callTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val resolvedBase = (baseUrl ?: BuildConfig.API_BASE_URL)
    private val baseHttpUrl: HttpUrl = resolvedBase.toHttpUrl()

    private val jsonMedia = "application/json; charset=utf-8".toMediaType()
    private val gson = Gson()

    // Helper function to add Authorization header with JWT token
    private fun Request.Builder.withAuth(): Request.Builder {
        val token = com.beautyspa.app.data.TokenManager.getValidToken()
        if (!token.isNullOrBlank()) {
            this.header("Authorization", "Bearer $token")
        }
        return this
    }

    suspend fun googleSignIn(idToken: String): AuthResponse {
        val url = baseHttpUrl.newBuilder()
            .addPathSegments("api/auth/google")
            .build()

        val requestBody = JSONObject().apply {
            put("idToken", idToken)
        }.toString().toRequestBody(jsonMedia)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val body = try {
            ioCall {
                client.newCall(request).execute().use { resp ->
                    if (!resp.isSuccessful) throw RuntimeException("HTTP ${resp.code} during Google sign-in")
                    resp.body?.string() ?: ""
                }
            }
        } catch (e: Exception) {
            // Log the exception or handle it as needed
            return AuthResponse(
                token = "",
                expiresIn = "",
                user = User(id = "", firstName = "", lastName = "", userId = "", email = "")
            ) // Return a default or error state with empty user
        }
        return gson.fromJson(body, AuthResponse::class.java)
    }

    // Small helper to guarantee blocking IO on background thread
    private suspend fun <T> ioCall(block: () -> T): T = withContext(Dispatchers.IO) { block() }

    // Services
    suspend fun fetchServices(
        category: ServiceCategory? = null,
        featured: Boolean? = null
    ): List<Service> {
        val urlBuilder = baseHttpUrl.newBuilder()
            .addPathSegments("api/services")
        if (category != null) urlBuilder.addQueryParameter("category", category.name)
        if (featured != null) urlBuilder.addQueryParameter("featured", featured.toString())
        val request = Request.Builder().url(urlBuilder.build()).get().build()
        val body = try {
            ioCall {
                client.newCall(request).execute().use { resp ->
                    if (!resp.isSuccessful) throw RuntimeException("HTTP ${resp.code} fetching services")
                    resp.body?.string() ?: "[]"
                }
            }
        } catch (e: Exception) {
            // Log the exception or handle it as needed
            return emptyList() // Return an empty list on error
        }
        val arr = JSONArray(body)
        val list = mutableListOf<Service>()
        for (i in 0 until arr.length()) {
            (arr.optJSONObject(i))?.let { toService(it) }?.let(list::add)
        }
        return list
    }

    // Specialists
    suspend fun fetchSpecialists(
        specialty: String? = null,
        minRating: Float? = null
    ): List<Specialist> {
        val urlBuilder = baseHttpUrl.newBuilder()
            .addPathSegments("api/specialists")
        if (!specialty.isNullOrBlank()) urlBuilder.addQueryParameter("specialty", specialty)
        if (minRating != null) urlBuilder.addQueryParameter("minRating", minRating.toString())
        val request = Request.Builder().url(urlBuilder.build()).get().build()
        val body = try {
            ioCall {
                client.newCall(request).execute().use { resp ->
                    if (!resp.isSuccessful) throw RuntimeException("HTTP ${resp.code} fetching specialists")
                    resp.body?.string() ?: "[]"
                }
            }
        } catch (e: Exception) {
            // Log the exception or handle it as needed
            return emptyList() // Return an empty list on error
        }
        val arr = JSONArray(body)
        val list = mutableListOf<Specialist>()
        for (i in 0 until arr.length()) {
            (arr.optJSONObject(i))?.let { toSpecialist(it) }?.let(list::add)
        }
        return list
    }

    // Appointments
    suspend fun fetchAppointments(
        userId: String? = null,
        status: String? = null,
        dateFromIso: String? = null,
        dateToIso: String? = null,
        page: Int = 1,
        pageSize: Int = 100
    ): List<Appointment> {
        val urlBuilder = baseHttpUrl.newBuilder()
        if (userId != null) {
            urlBuilder.addPathSegments("api/users")
                .addPathSegment(userId)
                .addPathSegment("appointments")
        } else {
            urlBuilder.addPathSegments("api/appointments")
        }

        urlBuilder
            .addQueryParameter("page", page.toString())
            .addQueryParameter("pageSize", pageSize.toString())
        if (!status.isNullOrBlank()) urlBuilder.addQueryParameter("status", status)
        if (!dateFromIso.isNullOrBlank()) urlBuilder.addQueryParameter("dateFrom", dateFromIso)
        if (!dateToIso.isNullOrBlank()) urlBuilder.addQueryParameter("dateTo", dateToIso)
        val request = Request.Builder().url(urlBuilder.build()).withAuth().get().build()
        val body = try {
            ioCall {
                client.newCall(request).execute().use { resp ->
                    if (!resp.isSuccessful) throw RuntimeException("HTTP ${resp.code} fetching appointments")
                    resp.body?.string() ?: "{\"items\":[]}" // default matches new shape
                }
            }
        } catch (e: Exception) {
            // Log the exception or handle it as needed
            return emptyList() // Return an empty list on error
        }
        val obj = JSONObject(body)
        val items = obj.optJSONArray("items")
        val data = if (items != null) items else obj.optJSONArray("data") ?: JSONArray()
        val list = mutableListOf<Appointment>()
        for (i in 0 until data.length()) {
            (data.optJSONObject(i))?.let { toAppointment(it) }?.let(list::add)
        }
        return list
    }

    // Payments: create payment intent
    suspend fun createPaymentIntent(
        userId: String,
        serviceId: String,
        specialistId: String,
        dateIso: String,
        timeSlot: String,
        amount: Double,
        currency: String = "usd",
        idempotencyKey: String? = null
    ): PaymentIntentResponse {
        val url = baseHttpUrl.newBuilder()
            .addPathSegments("api/payments/intents")
            .build()
        val payload = JSONObject()
            .put("userId", userId)
            .put("serviceId", serviceId)
            .put("specialistId", specialistId)
            .put("date", dateIso)
            .put("timeSlot", timeSlot)
            .put("amount", amount)
            .put("currency", currency)
        if (!idempotencyKey.isNullOrBlank()) payload.put("idempotencyKey", idempotencyKey)

        val builder = Request.Builder()
            .url(url)
            .withAuth()
            .header("Content-Type", "application/json")
            .post(payload.toString().toRequestBody(jsonMedia))
        if (!idempotencyKey.isNullOrBlank()) builder.header("Idempotency-Key", idempotencyKey)
        val request = builder.build()

        val body = try {
            ioCall {
                client.newCall(request).execute().use { resp ->
                    val raw = resp.body?.string()
                    if (!resp.isSuccessful) {
                        val msg = try {
                            val err = if (!raw.isNullOrBlank()) JSONObject(raw) else null
                            err?.optString("message")?.takeIf { it.isNotBlank() }
                        } catch (_: Exception) {
                            null
                        }
                        throw RuntimeException("HTTP ${resp.code} creating payment intent" + (msg?.let { ": $it" }
                            ?: ""))
                    }
                    raw ?: throw RuntimeException("Empty response when creating payment intent")
                }
            }
        } catch (e: Exception) {
            // Log the exception or handle it as needed
            throw RuntimeException("Failed to create payment intent: ${e.message}", e)
        }
        val obj = JSONObject(body)
        val expires = obj.optString("expiresAt").ifBlank { null }
        return PaymentIntentResponse(
            clientSecret = obj.optString("clientSecret"),
            paymentIntentId = obj.optString("paymentIntentId"),
            appointmentDraftId = obj.optString("appointmentDraftId"),
            amount = obj.optDouble("amount", amount),
            currency = obj.optString("currency", currency),
            expiresAt = expires
        )
    }

    // User
    suspend fun fetchUser(): User? {
        val url = baseHttpUrl.newBuilder().addPathSegments("api/user").build()
        val request = Request.Builder().url(url).withAuth().get().build()
        val body = try {
            ioCall {
                client.newCall(request).execute().use { resp ->
                    if (!resp.isSuccessful) throw RuntimeException("HTTP ${resp.code} fetching user")
                    resp.body?.string()
                }
            }
        } catch (e: Exception) {
            // Log the exception or handle it as needed
            return null // Return null on error
        } ?: return null
        val obj = JSONObject(body)
        return toUser(obj)
    }

    // Appointments detail
    suspend fun getAppointmentDetail(id: String): Appointment? {
        val url = baseHttpUrl.newBuilder()
            .addPathSegments("api/appointments")
            .addPathSegment(id)
            .build()
        val request = Request.Builder().url(url).withAuth().get().build()
        val body = try {
            ioCall {
                client.newCall(request).execute().use { resp ->
                    if (!resp.isSuccessful) throw RuntimeException("HTTP ${resp.code} fetching appointment detail")
                    resp.body?.string() ?: return@use null
                }
            }
        } catch (e: Exception) {
            // Log the exception or handle it as needed
            return null // Return null on error
        } ?: return null
        return toAppointment(JSONObject(body))
    }

    // Cancel appointment with optional refund
    suspend fun cancelAppointment(id: String, refund: Boolean): Appointment? {
        val url = baseHttpUrl.newBuilder()
            .addPathSegments("api/appointments")
            .addPathSegment(id)
            .addPathSegments("cancel")
            .build()
        val payload = JSONObject().put("refund", refund)
        val request = Request.Builder()
            .url(url)
            .withAuth()
            .header("Content-Type", "application/json")
            .post(payload.toString().toRequestBody(jsonMedia))
            .build()
        val body = try {
            ioCall {
                client.newCall(request).execute().use { resp ->
                    if (!resp.isSuccessful) throw RuntimeException("HTTP ${resp.code} canceling appointment")
                    resp.body?.string() ?: return@use null
                }
            }
        } catch (e: Exception) {
            // Log the exception or handle it as needed
            return null // Return null on error
        } ?: return null
        return toAppointment(JSONObject(body))
    }

    // Mapping helpers
    private fun toService(obj: JSONObject): Service? {
        val id = obj.optString("id")
        if (id.isNullOrEmpty()) return null
        val name = obj.optString("name")
        if (name.isNullOrEmpty()) return null
        val description = obj.optString("description", "")
        val categoryStr = obj.optString("category", "MASSAGE")
        val category = runCatching { ServiceCategory.valueOf(categoryStr) }.getOrDefault(ServiceCategory.MASSAGE)
        val duration = obj.optInt("duration", 0)
        val price = obj.optDouble("price", 0.0)
        val imageUrl = obj.optString("imageUrl", "")
        val rating = obj.optDouble("rating", 0.0).toFloat()
        val reviewCount = obj.optInt("reviewCount", 0)
        val isFeatured = obj.optBoolean("isFeatured", false)
        return Service(
            id = id,
            name = name,
            description = description,
            category = category,
            duration = duration,
            price = price,
            imageUrl = imageUrl,
            rating = rating,
            reviewCount = reviewCount,
            isFeatured = isFeatured
        )
    }

    private fun toSpecialist(obj: JSONObject): Specialist? {
        val id = obj.optString("id")
        if (id.isNullOrEmpty()) return null
        val name = obj.optString("name")
        if (name.isNullOrEmpty()) return null
        val specialty = obj.optString("specialty", "")
        val imageUrl = obj.optString("imageUrl", "")
        val rating = obj.optDouble("rating", 0.0).toFloat()
        val experienceYears = obj.optInt("experienceYears", 0)
        return Specialist(
            id = id,
            name = name,
            specialty = specialty,
            imageUrl = imageUrl,
            rating = rating,
            experienceYears = experienceYears
        )
    }

    private fun toUser(obj: JSONObject): User? {
        val id = obj.optString("id")
        if (id.isNullOrEmpty()) return null
        val firstName = obj.optString("firstName", "")
        val lastName = obj.optString("lastName", "")
        val userId = obj.optString("userId", "")
        val email = obj.optString("email", "")
        val phone = obj.optString("phone", "")
        val membershipLevel = obj.optString("membershipLevel", "BRONZE")
        val loyaltyPoints = obj.optInt("loyaltyPoints", 0)
        val prefsObj = obj.optJSONObject("preferences")
        val preferences = if (prefsObj != null) {
            UserPreferences(
                favSpecialty = prefsObj.optString("favSpecialty", ""),
                receivePromotions = prefsObj.optBoolean("receivePromotions", true),
                preferredLanguage = prefsObj.optString("preferredLanguage", "en")
            )
        } else UserPreferences()
        val profileImageUrl = obj.optString("profileImageUrl", "")
        return User(
            id = id,
            firstName = firstName,
            lastName = lastName,
            userId = userId,
            email = email,
            phone = phone,
            membershipLevel = membershipLevel,
            loyaltyPoints = loyaltyPoints,
            preferences = preferences,
            profileImageUrl = profileImageUrl
        )
    }

    private fun toAppointment(obj: JSONObject): Appointment? {
        val id = obj.optString("id")
        if (id.isNullOrEmpty()) return null
        val timeSlot = obj.optString("timeSlot", "")
        val status = obj.optString("status", "UPCOMING")
        val totalPrice = obj.optDouble("totalPrice", 0.0)
        val serviceObj = obj.optJSONObject("service")
        val specialistObj = obj.optJSONObject("specialist")
        val service = serviceObj?.let { toService(it) } ?: return null
        val specialist = specialistObj?.let { toSpecialist(it) } ?: return null
        val dateStr = obj.optString("date")
        if (dateStr.isNullOrEmpty()) return null
        val date = parseIsoDate(dateStr) ?: Date()
        return Appointment(
            id = id,
            service = service,
            specialist = specialist,
            date = date,
            timeSlot = timeSlot,
            status = status,
            totalPrice = totalPrice
        )
    }

    private fun parseIsoDate(value: String): Date? {
        return try {
            // Try full ISO format first
            val sdfIso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            sdfIso.timeZone = TimeZone.getTimeZone("UTC")
            sdfIso.parse(value)
        } catch (_: Exception) {
            try {
                // Fallback to simple date format (yyyy-MM-dd)
                val sdfSimple = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                sdfSimple.timeZone = TimeZone.getTimeZone("UTC")
                sdfSimple.parse(value)
            } catch (_: Exception) {
                null
            }
        }
    }
}
