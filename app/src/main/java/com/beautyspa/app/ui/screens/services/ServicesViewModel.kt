package com.beautyspa.app.ui.screens.services

import androidx.lifecycle.ViewModel
import com.beautyspa.app.data.model.Service
import com.beautyspa.app.data.model.ServiceCategory
import com.beautyspa.app.data.repository.ServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ServicesViewModel : ViewModel() {
    
    private val repository = ServiceRepository()
    private var allServices: List<Service> = emptyList()
    
    private val _services = MutableStateFlow<List<Service>>(emptyList())
    val services: StateFlow<List<Service>> = _services.asStateFlow()
    
    private val _selectedCategory = MutableStateFlow<ServiceCategory?>(null)
    val selectedCategory: StateFlow<ServiceCategory?> = _selectedCategory.asStateFlow()
    
    fun loadServices() {
        allServices = repository.getAllServices()
        _services.value = allServices
    }
    
    fun filterByCategory(category: ServiceCategory?) {
        _selectedCategory.value = category
        _services.value = if (category == null) {
            allServices
        } else {
            allServices.filter { it.category == category }
        }
    }
}
