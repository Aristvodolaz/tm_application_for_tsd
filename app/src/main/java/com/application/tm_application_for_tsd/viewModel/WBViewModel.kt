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
        data class Error(val message: String) : UiState() // Это будет использоваться для всплывающих ошибок
        object Success : UiState() // Новое состояние для успешного действия
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadData()
    }

    fun resetError() {
        _errorMessage.value = null
    }

    fun checkOrderCompletionBeforeClosing() {
        viewModelScope.launch {
            try {
                val taskName = spHelper.getTaskName() ?: "Unknown Task"
                val artikul = spHelper.getArticuleWork()?.toIntOrNull() ?: -1

                if (artikul == -1) {
                    _errorMessage.value = "Invalid artikul"
                    return@launch
                }

                // Проверка WB или OZON комплектации через API
                val response = api.checkWBComplect(taskName, artikul.toString())
                if (response.success) {
                    closeTask() // Если проверка успешна, закрываем задание
                } else {
                    // Если проверка не пройдена, показываем ошибку, но не меняем состояние списка коробов
                    _errorMessage.value = response.value
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка проверки: ${e.message}"
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val taskName = spHelper.getTaskName() ?: "Неизвестное название задания"
                val artikul = spHelper.getArticuleWork()?.toIntOrNull() ?: -1

                if (artikul == -1) {
                    _uiState.value = UiState.Error("Ошибка артикула")
                    return@launch
                }

                val response = api.getBoxList(taskName, artikul)
                val boxes = response.value ?: emptyList()
                val totalCount = response.value?.size ?: 0

                _uiState.value = UiState.Loaded(boxes, spHelper.getNameStuffWork().toString(),  totalCount)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Ошибка: ${e.localizedMessage}")
            }
        }
    }

    fun addBox(vlozh: Int, wps: String, pallet: String) {
        viewModelScope.launch {
            try {
                val taskName = spHelper.getTaskName() ?: "Неизвестное название задания"
                val artikul = spHelper.getArticuleWork()?.toIntOrNull() ?: -1

                if (artikul == -1) {
                    _uiState.value = UiState.Error("Ошибка артикула")
                    return@launch
                }

                api.addBox( AddBox( taskName, artikul,vlozh, pallet,wps ))
                _uiState.value = UiState.Success
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Ошибка: ${e.localizedMessage}")
            }
        }
    }

    fun checkWps(shk: String){
        viewModelScope.launch {
            try {
                val taskName = spHelper.getTaskName() ?: "Unknown Task"
                val response = api.checkWps(taskName, shk)
                if(!response.success) _uiState.value = UiState.Error("Данный ШК ВПС уже используется в заказе!")
                else _uiState.value = UiState.Success

            } catch (e: Exception) {
                _uiState.value = UiState.Error("Ошибка добавления короба: ${e.message}")
            }
        }
    }

    fun updateData(id: Long, vlozh: Int, pallet: String){
        viewModelScope.launch {
            try{
                val response = api.updateWB(id, pallet, vlozh)
                if(response.success){
                    _uiState.value = UiState.Success
                }else _uiState.value = UiState.Error("Ошибка добавления короба")

            }catch (e: Exception) {
                _uiState.value = UiState.Error("Ошибка добавления короба: ${e.message}")
            }
        }
    }

    fun closeTask() {
        viewModelScope.launch {
            try {
                api.endStatusWb(spHelper.getId())
                _uiState.value = UiState.Success // Reset UI state after closing the task
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Ошибка закрытия задания: ${e.localizedMessage}")
            }
        }
    }

    fun excludeArticle(id: Long, reason: String, comment: String, count: Int) {
        viewModelScope.launch {
            try {
                val taskName = spHelper.getTaskName() ?: "Unknown Task"
                val artikul = spHelper.getArticuleWork()?.toIntOrNull() ?: -1

                if (artikul == -1) {
                    _uiState.value = UiState.Error("Invalid artikul")
                    return@launch
                }

                api.excludeArticle(id, reason, comment, count)
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
