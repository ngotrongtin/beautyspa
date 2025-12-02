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
import java.util.Calendar

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
    
    fun selectDate(year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
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
