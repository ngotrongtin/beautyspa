package com.beautyspa.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beautyspa.app.data.TokenManager
import com.beautyspa.app.data.model.Appointment

import com.beautyspa.app.data.model.User
import com.beautyspa.app.data.repository.ApiRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class ProfileViewModel : ViewModel() {
    private val repository = ApiRepository()
    private var allAppointments: List<Appointment> = emptyList()

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            val userResult = repository.fetchUser()
            _user.value = userResult
            if (userResult != null) {
                allAppointments = repository.fetchAppointments(userId = userResult.id, pageSize = 200)
            } else {
                allAppointments = emptyList()
            }
            filterUpcoming()
        }
    }

    fun filterUpcoming() {
        val now = Date()
        _appointments.value = allAppointments.filter {
            // Consider paid or upcoming future appointments
            (it.status == "UPCOMING" || it.status == "PAID") && it.date.after(now)
        }
    }

    fun filterPast() {
        val now = Date()
        _appointments.value = allAppointments.filter {
            // Completed, canceled, failed, refunded, or past-dated items
            it.status == "COMPLETED" ||
            it.status == "CANCELLED" ||
            it.status == "FAILED" ||
            it.status == "REFUNDED" ||
            it.date.before(now)
        }
    }

    fun logout() {
        // Clear the JWT token
        TokenManager.clearToken()
        // Clear user data
        _user.value = null
        _appointments.value = emptyList()
        allAppointments = emptyList()
    }
}
