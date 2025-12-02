package com.beautyspa.app.ui.screens.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beautyspa.app.data.model.Service
import com.beautyspa.app.data.model.ServiceCategory
import com.beautyspa.app.data.repository.ApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ServicesViewModel : ViewModel() {
    
    private val repository = ApiRepository()
    private var allServices: List<Service> = emptyList()
    
    private val _services = MutableStateFlow<List<Service>>(emptyList())
    val services: StateFlow<List<Service>> = _services.asStateFlow()
    
    private val _selectedCategory = MutableStateFlow<ServiceCategory?>(null)
    val selectedCategory: StateFlow<ServiceCategory?> = _selectedCategory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadServices() {
        if (_isLoading.value) return
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                allServices = repository.fetchServices()
                applyFilter()
            } catch (t: Throwable) {
                _error.value = t.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun filterByCategory(category: ServiceCategory?) {
        _selectedCategory.value = category
        applyFilter()
    }

    private fun applyFilter() {
        val category = _selectedCategory.value
        _services.value = if (category == null) {
            allServices
        } else {
            allServices.filter { it.category == category }
        }
    }
}
