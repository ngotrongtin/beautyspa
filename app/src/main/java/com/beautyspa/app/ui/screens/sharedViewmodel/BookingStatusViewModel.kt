package com.beautyspa.app.ui.screens.sharedViewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class BookingStatus{
    COMPLETED,
    FAILED,
}

class BookingStatusViewModel : ViewModel() {
    private val _bookingStatus = MutableStateFlow<BookingStatus?>(null)
    val bookingStatus: StateFlow<BookingStatus?> = _bookingStatus.asStateFlow()

    fun complete(){
        _bookingStatus.value = BookingStatus.COMPLETED
    }

    fun fail(){
        _bookingStatus.value = BookingStatus.FAILED
    }

    fun nulling(){
        _bookingStatus.value = null
    }
}