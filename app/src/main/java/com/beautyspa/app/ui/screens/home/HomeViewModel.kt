package com.beautyspa.app.ui.screens.home

import androidx.lifecycle.ViewModel
import com.beautyspa.app.data.model.Service
import com.beautyspa.app.data.model.ServiceCategory
import com.beautyspa.app.data.repository.ServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CategoryItem(
    val category: ServiceCategory,
    val serviceCount: Int
)

class HomeViewModel : ViewModel() {
    
    private val repository = ServiceRepository()
    
    private val _featuredServices = MutableStateFlow<List<Service>>(emptyList())
    val featuredServices: StateFlow<List<Service>> = _featuredServices.asStateFlow()
    
    private val _categories = MutableStateFlow<List<CategoryItem>>(emptyList())
    val categories: StateFlow<List<CategoryItem>> = _categories.asStateFlow()
    
    fun loadData() {
        _featuredServices.value = repository.getFeaturedServices()
        
        val allServices = repository.getAllServices()
        _categories.value = ServiceCategory.entries.map { category ->
            CategoryItem(
                category = category,
                serviceCount = allServices.count { it.category == category }
            )
        }
    }
}
