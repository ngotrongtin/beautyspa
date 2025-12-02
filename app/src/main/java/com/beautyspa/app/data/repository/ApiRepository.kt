package com.beautyspa.app.data.repository

import com.beautyspa.app.BuildConfig
import com.beautyspa.app.data.model.Appointment
import com.beautyspa.app.data.model.AppointmentStatus
import com.beautyspa.app.data.model.Service
import com.beautyspa.app.data.model.ServiceCategory
import com.beautyspa.app.data.model.Specialist
import com.beautyspa.app.data.model.User
import com.beautyspa.app.data.model.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
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
        .callTimeout(20, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private val resolvedBase = (baseUrl ?: BuildConfig.API_BASE_URL)
    private val baseHttpUrl: HttpUrl = resolvedBase.toHttpUrl()

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
        val body = ioCall {
            client.newCall(request).execute().use { resp ->
                if (!resp.isSuccessful) throw RuntimeException("HTTP ${'$'}{resp.code} fetching services")
                resp.body?.string() ?: "[]"
            }
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
        val body = ioCall {
            client.newCall(request).execute().use { resp ->
                if (!resp.isSuccessful) throw RuntimeException("HTTP ${'$'}{resp.code} fetching specialists")
                resp.body?.string() ?: "[]"
            }
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
        status: String? = null,
        dateFromIso: String? = null,
        dateToIso: String? = null,
        page: Int = 1,
        pageSize: Int = 100
    ): List<Appointment> {
        val urlBuilder = baseHttpUrl.newBuilder()
            .addPathSegments("api/appointments")
            .addQueryParameter("page", page.toString())
            .addQueryParameter("pageSize", pageSize.toString())
        if (!status.isNullOrBlank()) urlBuilder.addQueryParameter("status", status)
        if (!dateFromIso.isNullOrBlank()) urlBuilder.addQueryParameter("dateFrom", dateFromIso)
        if (!dateToIso.isNullOrBlank()) urlBuilder.addQueryParameter("dateTo", dateToIso)
        val request = Request.Builder().url(urlBuilder.build()).get().build()
        val body = ioCall {
            client.newCall(request).execute().use { resp ->
                if (!resp.isSuccessful) throw RuntimeException("HTTP ${'$'}{resp.code} fetching appointments")
                resp.body?.string() ?: "{\"data\":[]}"
            }
        }
        val obj = JSONObject(body)
        val data = obj.optJSONArray("data") ?: JSONArray()
        val list = mutableListOf<Appointment>()
        for (i in 0 until data.length()) {
            (data.optJSONObject(i))?.let { toAppointment(it) }?.let(list::add)
        }
        return list
    }

    // User
    suspend fun fetchUser(): User? {
        val url = baseHttpUrl.newBuilder().addPathSegments("api/user").build()
        val request = Request.Builder().url(url).get().build()
        val body = ioCall {
            client.newCall(request).execute().use { resp ->
                if (!resp.isSuccessful) throw RuntimeException("HTTP ${'$'}{resp.code} fetching user")
                resp.body?.string()
            }
        } ?: return null
        val obj = JSONObject(body)
        return toUser(obj)
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
        val statusStr = obj.optString("status", "UPCOMING")
        val normalizedStatus = if (statusStr.equals("CANCELED", ignoreCase = true)) "CANCELLED" else statusStr
        val status = runCatching { AppointmentStatus.valueOf(normalizedStatus) }.getOrDefault(AppointmentStatus.UPCOMING)
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
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            sdf.parse(value)
        } catch (_: Exception) {
            null
        }
    }
}
