package com.application.tm_application_for_tsd.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.tm_application_for_tsd.network.Api
import com.application.tm_application_for_tsd.network.request_response.WBItem
import com.application.tm_application_for_tsd.utils.SPHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class RedactorWBViewModel @Inject constructor(
    private val api: Api
) : ViewModel() {
    sealed class UiState {
        object Loading : UiState()
        data class Success(val items: List<WBItem>) : UiState()
        data class Error(val message: String) : UiState()
        object Empty : UiState() // New state for empty results
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    fun getData(task: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = api.getDataWb(task)
                if (response.success) {
                    if (response.value.isNullOrEmpty()) {
                        _uiState.value = UiState.Empty
                    } else {
                        _uiState.value = UiState.Success(response.value)
                    }
                } else {
                    _uiState.value = UiState.Error("An unknown error occurred.")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Пусто!")
            }
        }
    }

    fun deleteArticle(id: Long, taskName: String) {
        viewModelScope.launch {
            try {
                api.deleteRecord(id, taskName )
                getData(taskName)
            } catch (e: Exception) {
                // Обработать ошибку
            }
        }
    }
}
