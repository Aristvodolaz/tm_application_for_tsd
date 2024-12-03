package com.application.tm_application_for_tsd.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.tm_application_for_tsd.network.request_response.ValidateBoxResponse
import com.application.tm_application_for_tsd.repository.ApiRepository
import com.application.tm_application_for_tsd.utils.ScannerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val repository: ApiRepository,
    private val scannerController: ScannerController
) : ViewModel() {

    private val _barcodeData = MutableLiveData<String>()
    val barcodeData: LiveData<String> get() = _barcodeData

    private val _apiResponse = MutableLiveData<ValidateBoxResponse>()
    val apiResponse: LiveData<ValidateBoxResponse> get() = _apiResponse

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    init {
        // Устанавливаем коллбек для получения данных сканера
        scannerController.callback = object : ScannerController.ScannerCallback {
            override fun onDataReceived(barcodeData: String) {
                _barcodeData.postValue(barcodeData)
                checkValidateBox(barcodeData, "default_pallet") // Пример: автоматическая отправка
            }

            override fun onScanFailed(errorMessage: String) {
                _error.postValue(errorMessage)
            }
        }
    }


    fun checkValidateBox(sscc: String, pallet: String) {
        viewModelScope.launch {
            _loading.postValue(true)
            try {
                val response = repository.checkValidateBox(sscc, pallet)
                _apiResponse.postValue(response)
            } catch (e: Exception) {
                _error.postValue("Ошибка проверки коробки: ${e.localizedMessage}")
            } finally {
                _loading.postValue(false)
            }
        }
    }
    fun startScanning() {
        try {
            scannerController.startScanning()
        } catch (e: Exception) {
            _error.postValue("Ошибка запуска сканирования: ${e.localizedMessage}")
        }
    }

    fun stopScanning() {
        try {
            scannerController.releaseScanner()
        } catch (e: Exception) {
            _error.postValue("Ошибка остановки сканирования: ${e.localizedMessage}")
        }
    }


    override fun onCleared() {
        super.onCleared()
        scannerController.releaseScanner()
    }
}
