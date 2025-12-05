package com.beautyspa.app.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beautyspa.app.data.TokenManager
import com.beautyspa.app.data.model.AuthResponse
import com.beautyspa.app.data.repository.ApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val apiRepository: ApiRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun onGoogleSignInResult(idToken: String?) {
        if (idToken == null) {
            _loginState.value = LoginState.Error("Google Sign-In failed.")
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val authResponse = apiRepository.googleSignIn(idToken)

                // Save the JWT token if login was successful and token is not empty
                if (authResponse.token.isNotBlank()) {
                    TokenManager.saveToken(authResponse.token)
                    _loginState.value = LoginState.Success(authResponse)
                } else {
                    _loginState.value = LoginState.Error("Login failed: No token received from server")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "An unknown error occurred.")
            }
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val authResponse: AuthResponse) : LoginState()
    data class Error(val message: String) : LoginState()
}

