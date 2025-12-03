package com.beautyspa.app.ui.screens.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beautyspa.app.data.model.Service
import com.beautyspa.app.data.model.Specialist
import com.beautyspa.app.data.repository.ApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class BookingViewModel : ViewModel() {
    
    private val repository = ApiRepository()

    private val _services = MutableStateFlow<List<Service>>(emptyList())
    val services: StateFlow<List<Service>> = _services.asStateFlow()
    
    private val _timeSlots = MutableStateFlow<List<String>>(emptyList())
    val timeSlots: StateFlow<List<String>> = _timeSlots.asStateFlow()
    
    private val _specialists = MutableStateFlow<List<Specialist>>(emptyList())
    val specialists: StateFlow<List<Specialist>> = _specialists.asStateFlow()
    
    private val _selectedService = MutableStateFlow<Service?>(null)
    val selectedService: StateFlow<Service?> = _selectedService.asStateFlow()
    
    private val _selectedDate = MutableStateFlow<Calendar?>(null)
    val selectedDate: StateFlow<Calendar?> = _selectedDate.asStateFlow()
    
    private val _selectedTimeSlot = MutableStateFlow<String?>(null)
    val selectedTimeSlot: StateFlow<String?> = _selectedTimeSlot.asStateFlow()
    
    private val _selectedSpecialist = MutableStateFlow<Specialist?>(null)
    val selectedSpecialist: StateFlow<Specialist?> = _selectedSpecialist.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _paymentClientSecret = MutableStateFlow<String?>(null)
    val paymentClientSecret: StateFlow<String?> = _paymentClientSecret.asStateFlow()

    fun clearClientSecret() {
        _paymentClientSecret.value = null
    }

    fun loadData() {
        if (_isLoading.value) return
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val services = repository.fetchServices()
                val specialists = repository.fetchSpecialists()
                _services.value = services
                _specialists.value = specialists
                _timeSlots.value = defaultTimeSlots()
            } catch (t: Throwable) {
                _error.value = t.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun selectService(service: Service) {
        _selectedService.value = service
    }

    fun selectDate(date: java.util.Date) {
        val calendar = Calendar.getInstance()
        calendar.time = date
        _selectedDate.value = calendar
    }
    
    fun selectTimeSlot(timeSlot: String) {
        _selectedTimeSlot.value = timeSlot
    }
    
    fun selectSpecialist(specialist: Specialist) {
        _selectedSpecialist.value = specialist
    }
    
    fun isBookingComplete(): Boolean {
        return _selectedService.value != null &&
                _selectedDate.value != null &&
                _selectedTimeSlot.value != null &&
                _selectedSpecialist.value != null
    }

    // Create payment intent per backend API. For demo we use a fake user id "demo-user".
    fun createPaymentIntent() {
        val service = _selectedService.value ?: run { _error.value = "Select service"; return }
        val date = _selectedDate.value ?: run { _error.value = "Select date"; return }
        val timeSlot = _selectedTimeSlot.value ?: run { _error.value = "Select time"; return }
        val specialist = _selectedSpecialist.value ?: run { _error.value = "Select specialist"; return }
        if (_isLoading.value) return
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val iso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                    timeZone = java.util.TimeZone.getTimeZone("UTC")
                }.format(date.time)
                val idemp = UUID.randomUUID().toString()
                val resp = repository.createPaymentIntent(
                    userId = "u101",
                    serviceId = service.id,
                    specialistId = specialist.id,
                    dateIso = iso,
                    timeSlot = timeSlot,
                    amount = service.price,
                    currency = "usd",
                    idempotencyKey = idemp
                )
                _paymentClientSecret.value = resp.clientSecret
            } catch (t: Throwable) {
                _error.value = t.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun defaultTimeSlots(): List<String> {
        return listOf(
            "9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM",
            "11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM",
            "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM",
            "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM",
            "5:00 PM", "5:30 PM", "6:00 PM"
        )
    }
}
