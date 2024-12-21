package com.application.tm_application_for_tsd.viewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.tm_application_for_tsd.network.Api
import com.application.tm_application_for_tsd.network.request_response.WBItem
import com.application.tm_application_for_tsd.viewModel.RedactorWBViewModel.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class OzonEditViewModel @Inject constructor(
    val api: Api
): ViewModel(){
    sealed class UiState {
        object Loading : UiState()
        data class Success(val message: String) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    @RequiresApi(Build.VERSION_CODES.O)
    fun editItem(id: Long, vlozh: Int, mesto: Int, pallet: Int ){
        viewModelScope.launch {
            try{
                val currentDateTime = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"))
                val response = api.updateOzon(id, mesto, vlozh, pallet, currentDateTime)
                if(response.success){
                    _uiState.value = UiState.Success("Изменения сохранены!")
                } else {
                    _uiState.value = UiState.Error("Unknown error")
                }
            }catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to fetch data: ${e.message}")
            }
        }
    }
}