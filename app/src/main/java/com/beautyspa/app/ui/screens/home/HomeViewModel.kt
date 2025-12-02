package com.beautyspa.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beautyspa.app.data.model.Service
import com.beautyspa.app.data.model.ServiceCategory
import com.beautyspa.app.data.repository.ApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CategoryItem(
    val category: ServiceCategory,
    val serviceCount: Int
)

class HomeViewModel : ViewModel() {
    
    private val repository = ApiRepository()

    private val _featuredServices = MutableStateFlow<List<Service>>(emptyList())
    val featuredServices: StateFlow<List<Service>> = _featuredServices.asStateFlow()
    
    private val _categories = MutableStateFlow<List<CategoryItem>>(emptyList())
    val categories: StateFlow<List<CategoryItem>> = _categories.asStateFlow()

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
                _featuredServices.value = services.filter { it.isFeatured }
                _categories.value = ServiceCategory.entries.map { category ->
                    CategoryItem(
                        category = category,
                        serviceCount = services.count { it.category == category }
                    )
                }
            } catch (t: Throwable) {
                _error.value = t.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
