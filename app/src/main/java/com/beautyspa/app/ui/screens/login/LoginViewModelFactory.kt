package com.beautyspa.app.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beautyspa.app.data.repository.ApiRepository

class LoginViewModelFactory(private val apiRepository: ApiRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(apiRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

