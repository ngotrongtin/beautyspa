package com.beautyspa.app.ui.screens.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.beautyspa.app.data.model.BookingState
import com.beautyspa.app.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ChatMessage(val text: String, val isUser: Boolean)

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val paymentState: BookingState? = null
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = ChatRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState

    fun ask(question: String) {
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + ChatMessage(question, true),
            loading = true,
            error = null
        )
        viewModelScope.launch {
            val response = repo.ask(question)
            val botText = if (response.message.isBlank()) "(no response)" else response.message

            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages + ChatMessage(botText, false),
                loading = false,
                error = if (response.message.isBlank()) "Failed to get answer" else null,
                paymentState = response.state
            )
        }
    }

    fun clearPaymentState() {
        _uiState.value = _uiState.value.copy(paymentState = null)
    }
}
