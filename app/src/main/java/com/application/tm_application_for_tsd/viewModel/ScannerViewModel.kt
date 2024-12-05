package com.application.tm_application_for_tsd.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor() : ViewModel() {

    private val _barcodeData = MutableStateFlow("")
    val barcodeData: StateFlow<String> get() = _barcodeData

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> get() = _error

    fun onBarcodeScanned(data: String) {
        Log.d("ScannerViewModel", "Updating barcode data: $data")
        _barcodeData.value = data // Установка нового значения
    }

    fun onScanError(errorMessage: String) {
        Log.d("ScannerViewModel", "Updating error: $errorMessage")
        _error.value = errorMessage
    }
}
