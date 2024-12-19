package com.application.tm_application_for_tsd.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.tm_application_for_tsd.network.Api
import com.application.tm_application_for_tsd.network.request_response.AddBox
import com.application.tm_application_for_tsd.network.request_response.Box
import com.application.tm_application_for_tsd.utils.SPHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WBViewModel @Inject constructor(
    private val api: Api,
    private val spHelper: SPHelper
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Loaded(val boxes: List<Box>, val productName: String, val totalCount: Int) : UiState()
        data class Error(val message: String) : UiState()
        object Success : UiState() // Новое состояние для успешного действия

    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val taskName = spHelper.getTaskName() ?: "Unknown Task"
                val artikul = spHelper.getArticuleWork()?.toIntOrNull() ?: -1

                if (artikul == -1) {
                    _uiState.value = UiState.Error("Invalid artikul")
                    return@launch
                }

                val response = api.getBoxList(taskName, artikul)
                val boxes = response.value ?: emptyList()
                val totalCount = response.value?.size ?: 0

                _uiState.value = UiState.Loaded(boxes, spHelper.getNameStuffWork().toString(),  totalCount)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to load data: ${e.message}")
            }
        }
    }

    fun addBox(vlozh: Int, wps: String, pallet: String) {
        viewModelScope.launch {
            try {
                val taskName = spHelper.getTaskName() ?: "Unknown Task"
                val artikul = spHelper.getArticuleWork()?.toIntOrNull() ?: -1

                if (artikul == -1) {
                    _uiState.value = UiState.Error("Invalid artikul")
                    return@launch
                }

                api.addBox( AddBox( taskName, artikul,vlozh, pallet,wps ))
                _uiState.value = UiState.Success
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to add box: ${e.message}")
            }
        }
    }

    fun checkWps(shk: String){
        viewModelScope.launch {
            try {
                val taskName = spHelper.getTaskName() ?: "Unknown Task"
                val response = api.checkWps(taskName, shk)
                if(!response.success) _uiState.value = UiState.Error("Данный ШК ВПС уже используется в заказе!")

            } catch (e: Exception) {
                _uiState.value = UiState.Error("Ошибка добавления короба: ${e.message}")
            }
        }
    }

    fun closeTask() {
        viewModelScope.launch {
            try {
                val taskName = spHelper.getTaskName() ?: "Unknown Task"
                val artikul = spHelper.getArticuleWork()?.toIntOrNull() ?: -1

                if (artikul == -1) {
                    _uiState.value = UiState.Error("Invalid artikul")
                    return@launch
                }

                api.endStatusWb(taskName, artikul)
                _uiState.value = UiState.Loading // Reset UI state after closing the task
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to close task: ${e.message}")
            }
        }
    }

    fun excludeArticle(reason: String, comment: String) {
        viewModelScope.launch {
            try {
                val taskName = spHelper.getTaskName() ?: "Unknown Task"
                val artikul = spHelper.getArticuleWork()?.toIntOrNull() ?: -1

                if (artikul == -1) {
                    _uiState.value = UiState.Error("Invalid artikul")
                    return@launch
                }

                api.excludeArticle(taskName, artikul, reason, comment)
                _uiState.value = UiState.Loading // Reset UI state after excluding article
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to exclude article: ${e.message}")
            }
        }
    }

    fun resetSuccessState() {
        if (_uiState.value is UiState.Success) {
            loadData()
        }
    }
}
