package com.beautyspa.app.data.repository

import com.beautyspa.app.data.model.Service
import com.beautyspa.app.data.model.ServiceCategory
import com.beautyspa.app.data.model.Specialist
import com.beautyspa.app.data.model.Appointment
import com.beautyspa.app.data.model.AppointmentStatus
import java.util.Date
import java.util.UUID

class ServiceRepository {
    
    fun getFeaturedServices(): List<Service> {
        return listOf(
            Service(
                id = "1",
                name = "Swedish Massage",
                description = "Relaxing full body massage to relieve tension and stress",
                category = ServiceCategory.MASSAGE,
                duration = 60,
                price = 89.99,
                imageUrl = "https://images.unsplash.com/photo-1544161515-4ab6ce6db874",
                rating = 4.8f,
                reviewCount = 124,
                isFeatured = true
            ),
            Service(
                id = "2",
                name = "Deep Tissue Massage",
                description = "Therapeutic massage targeting deep muscle layers",
                category = ServiceCategory.MASSAGE,
                duration = 90,
                price = 119.99,
                imageUrl = "https://images.unsplash.com/photo-1519823551278-64ac92734fb1",
                rating = 4.9f,
                reviewCount = 98,
                isFeatured = true
            ),
            Service(
                id = "3",
                name = "Hydrating Facial",
                description = "Deep cleansing and moisturizing facial treatment",
                category = ServiceCategory.FACIAL,
                duration = 60,
                price = 79.99,
                imageUrl = "https://images.unsplash.com/photo-1570172619644-dfd03ed5d881",
                rating = 4.7f,
                reviewCount = 156,
                isFeatured = true
            ),
            Service(
                id = "4",
                name = "Gel Manicure",
                description = "Long-lasting gel polish application with nail care",
                category = ServiceCategory.NAILS,
                duration = 45,
                price = 49.99,
                imageUrl = "https://images.unsplash.com/photo-1604654894610-df63bc536371",
                rating = 4.6f,
                reviewCount = 203,
                isFeatured = true
            )
        )
    }
    
    fun getAllServices(): List<Service> {
        return getFeaturedServices() + listOf(
            Service(
                id = "5",
                name = "Hot Stone Massage",
                description = "Heated stones massage for deep relaxation",
                category = ServiceCategory.MASSAGE,
                duration = 75,
                price = 99.99,
                imageUrl = "https://images.unsplash.com/photo-1600334129128-685c5582fd35",
                rating = 4.8f,
                reviewCount = 87
            ),
            Service(
                id = "6",
                name = "Anti-Aging Facial",
                description = "Rejuvenating facial to reduce fine lines",
                category = ServiceCategory.FACIAL,
                duration = 75,
                price = 109.99,
                imageUrl = "https://images.unsplash.com/photo-1616394584738-fc6e612e71b9",
                rating = 4.9f,
                reviewCount = 145
            ),
            Service(
                id = "7",
                name = "Spa Pedicure",
                description = "Complete foot care with massage and polish",
                category = ServiceCategory.NAILS,
                duration = 60,
                price = 59.99,
                imageUrl = "https://images.unsplash.com/photo-1519415387722-a1c3bbef716c",
                rating = 4.7f,
                reviewCount = 178
            ),
            Service(
                id = "8",
                name = "Hair Styling",
                description = "Professional hair wash, cut and style",
                category = ServiceCategory.HAIR,
                duration = 60,
                price = 69.99,
                imageUrl = "https://images.unsplash.com/photo-1560066984-138dadb4c035",
                rating = 4.6f,
                reviewCount = 92
            ),
            Service(
                id = "9",
                name = "Body Scrub",
                description = "Exfoliating body treatment for smooth skin",
                category = ServiceCategory.BODY,
                duration = 45,
                price = 64.99,
                imageUrl = "https://images.unsplash.com/photo-1515377905703-c4788e51af15",
                rating = 4.8f,
                reviewCount = 134
            )
        )
    }
    
    fun getServicesByCategory(category: ServiceCategory): List<Service> {
        return getAllServices().filter { it.category == category }
    }
    
    fun getSpecialists(): List<Specialist> {
        return listOf(
            Specialist(
                id = "1",
                name = "Sarah Johnson",
                specialty = "Massage Therapist",
                imageUrl = "https://images.unsplash.com/photo-1438761681033-6461ffad8d80",
                rating = 4.9f,
                experienceYears = 8
            ),
            Specialist(
                id = "2",
                name = "Emily Chen",
                specialty = "Facial Specialist",
                imageUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330",
                rating = 4.8f,
                experienceYears = 6
            ),
            Specialist(
                id = "3",
                name = "Jessica Williams",
                specialty = "Nail Artist",
                imageUrl = "https://images.unsplash.com/photo-1487412720507-e7ab37603c6f",
                rating = 4.9f,
                experienceYears = 7
            ),
            Specialist(
                id = "4",
                name = "Michelle Lee",
                specialty = "Hair Stylist",
                imageUrl = "https://images.unsplash.com/photo-1489424731084-a5d8b219a5bb",
                rating = 4.7f,
                experienceYears = 5
            )
        )
    }
    
    fun getUserAppointments(): List<Appointment> {
        val services = getAllServices()
        val specialists = getSpecialists()
        
        return listOf(
            Appointment(
                id = UUID.randomUUID().toString(),
                service = services[0],
                specialist = specialists[0],
                date = Date(System.currentTimeMillis() + 86400000 * 3), // 3 days from now
                timeSlot = "10:00 AM",
                status = AppointmentStatus.UPCOMING,
                totalPrice = services[0].price
            ),
            Appointment(
                id = UUID.randomUUID().toString(),
                service = services[2],
                specialist = specialists[1],
                date = Date(System.currentTimeMillis() + 86400000 * 7), // 7 days from now
                timeSlot = "2:00 PM",
                status = AppointmentStatus.UPCOMING,
                totalPrice = services[2].price
            )
        )
    }
    
    fun getAvailableTimeSlots(): List<String> {
        return listOf(
            "9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM",
            "11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM",
            "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM",
            "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM",
            "5:00 PM", "5:30 PM", "6:00 PM"
        )
    }
}
