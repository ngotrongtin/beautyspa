package com.beautyspa.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import com.beautyspa.app.data.model.Appointment
import com.beautyspa.app.data.model.AppointmentStatus
import com.beautyspa.app.data.repository.ServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

class ProfileViewModel : ViewModel() {
    
    private val repository = ServiceRepository()
    private var allAppointments: List<Appointment> = emptyList()
    
    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments.asStateFlow()
    
    fun loadAppointments() {
        allAppointments = repository.getUserAppointments()
        filterUpcoming()
    }
    
    fun filterUpcoming() {
        val now = Date()
        _appointments.value = allAppointments.filter { 
            it.status == AppointmentStatus.UPCOMING && it.date.after(now)
        }
    }
    
    fun filterPast() {
        val now = Date()
        _appointments.value = allAppointments.filter { 
            it.status == AppointmentStatus.COMPLETED || it.date.before(now)
        }
    }
}
