package com.beautyspa.app.data.repository

import com.beautyspa.app.data.model.Appointment
import com.beautyspa.app.data.model.AppointmentStatus
import com.beautyspa.app.data.model.Service
import com.beautyspa.app.data.model.ServiceCategory
import com.beautyspa.app.data.model.Specialist
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FirebaseRepository(
    private val databaseUrl: String = "https://beautyspa-76678-default-rtdb.firebaseio.com/"
) {
    private val db by lazy {
        FirebaseDatabase.getInstance(databaseUrl).reference
    }

    suspend fun fetchServices(): List<Service> {
        val snapshot = db.child("services").get().await()
        val list = mutableListOf<Service>()
        snapshot.children.forEach { child ->
            val map = child.value as? Map<*, *> ?: return@forEach
            toService(map)?.let(list::add)
        }
        // When services is an array node, snapshot.children are indexed; above still works
        if (list.isEmpty() && snapshot.value is List<*>) {
            (snapshot.value as List<*>).forEach { item ->
                val map = item as? Map<*, *> ?: return@forEach
                toService(map)?.let(list::add)
            }
        }
        return list
    }

    suspend fun fetchSpecialists(): List<Specialist> {
        val snapshot = db.child("specialists").get().await()
        val list = mutableListOf<Specialist>()
        snapshot.children.forEach { child ->
            val map = child.value as? Map<*, *> ?: return@forEach
            toSpecialist(map)?.let(list::add)
        }
        if (list.isEmpty() && snapshot.value is List<*>) {
            (snapshot.value as List<*>).forEach { item ->
                val map = item as? Map<*, *> ?: return@forEach
                toSpecialist(map)?.let(list::add)
            }
        }
        return list
    }

    suspend fun fetchAppointments(): List<Appointment> {
        val snapshot = db.child("appointments").get().await()
        val services = fetchServices().associateBy { it.id }
        val specialists = fetchSpecialists().associateBy { it.id }
        val list = mutableListOf<Appointment>()
        snapshot.children.forEach { child ->
            val map = child.value as? Map<*, *> ?: return@forEach
            toAppointment(map, services, specialists)?.let(list::add)
        }
        if (list.isEmpty() && snapshot.value is List<*>) {
            (snapshot.value as List<*>).forEach { item ->
                val map = item as? Map<*, *> ?: return@forEach
                toAppointment(map, services, specialists)?.let(list::add)
            }
        }
        return list
    }

    private fun toService(map: Map<*, *>): Service? {
        val id = map["id"] as? String ?: return null
        val name = map["name"] as? String ?: return null
        val description = map["description"] as? String ?: ""
        val categoryStr = map["category"] as? String ?: "MASSAGE"
        val category = runCatching { ServiceCategory.valueOf(categoryStr) }.getOrDefault(ServiceCategory.MASSAGE)
        val duration = (map["duration"] as? Number)?.toInt() ?: 0
        val price = (map["price"] as? Number)?.toDouble() ?: 0.0
        val imageUrl = map["imageUrl"] as? String ?: ""
        val rating = (map["rating"] as? Number)?.toFloat() ?: 0f
        val reviewCount = (map["reviewCount"] as? Number)?.toInt() ?: 0
        val isFeatured = map["isFeatured"] as? Boolean ?: false
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

    private fun toSpecialist(map: Map<*, *>): Specialist? {
        val id = map["id"] as? String ?: return null
        val name = map["name"] as? String ?: return null
        val specialty = map["specialty"] as? String ?: ""
        val imageUrl = map["imageUrl"] as? String ?: ""
        val rating = (map["rating"] as? Number)?.toFloat() ?: 0f
        val experienceYears = (map["experienceYears"] as? Number)?.toInt() ?: 0
        return Specialist(
            id = id,
            name = name,
            specialty = specialty,
            imageUrl = imageUrl,
            rating = rating,
            experienceYears = experienceYears
        )
    }

    private fun toAppointment(
        map: Map<*, *>,
        servicesById: Map<String, Service>,
        specialistsById: Map<String, Specialist>
    ): Appointment? {
        val id = map["id"] as? String ?: return null
        val timeSlot = map["timeSlot"] as? String ?: ""
        val statusStr = map["status"] as? String ?: "UPCOMING"
        val status = runCatching { AppointmentStatus.valueOf(statusStr) }.getOrDefault(AppointmentStatus.UPCOMING)
        val totalPrice = (map["totalPrice"] as? Number)?.toDouble() ?: 0.0

        val serviceMap = map["service"] as? Map<*, *>
        val specialistMap = map["specialist"] as? Map<*, *>

        val service = serviceMap?.let { toService(it) } ?: (map["serviceId"] as? String)?.let { servicesById[it] }
        val specialist = specialistMap?.let { toSpecialist(it) } ?: (map["specialistId"] as? String)?.let { specialistsById[it] }

        val dateStr = map["date"] as? String ?: return null
        val date = parseIsoDate(dateStr) ?: Date()

        if (service == null || specialist == null) return null

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
            // Handles 2025-12-01T00:00:00Z
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
            sdf.parse(value)
        } catch (_: Exception) {
            null
        }
    }
}

