package com.beautyspa.app.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beautyspa.app.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ChatMessage(val text: String, val isUser: Boolean)

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
)

class ChatViewModel : ViewModel() {
    private val repo = ChatRepository()

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState

    fun ask(question: String) {
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + ChatMessage(question, true),
            loading = true,
            error = null
        )
        viewModelScope.launch {
            val answer = repo.ask(question)
            val botText = if (answer.isNullOrBlank()) "(no response)" else answer
            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages + ChatMessage(botText, false),
                loading = false,
                error = if (answer.isNullOrBlank()) "Failed to get answer" else null
            )
        }
    }
}
