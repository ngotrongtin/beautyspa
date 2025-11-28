package com.beautyspa.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beautyspa.app.data.model.Appointment
import com.beautyspa.app.data.model.AppointmentStatus
import com.beautyspa.app.data.model.User
import com.beautyspa.app.data.repository.FirebaseRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class ProfileViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    private var allAppointments: List<Appointment> = emptyList()

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            // Fetch in parallel
            val userDeferred = async { repository.fetchUser() }
            val apptDeferred = async { repository.fetchAppointments() }
            _user.value = userDeferred.await()
            allAppointments = apptDeferred.await()
            filterUpcoming()
        }
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
