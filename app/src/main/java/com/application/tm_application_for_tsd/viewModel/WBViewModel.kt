package com.application.tm_application_for_tsd.viewModel

import android.annotation.SuppressLint
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class WBViewModel @Inject constructor(
    private val api: Api,
    private val spHelper: SPHelper
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Loaded(val boxes: List<Box>, val productName: String, val totalCount: Int, val totalCountKolvo: Int) : UiState()
        data class Error(val message: String) : UiState() // Это будет использоваться для всплывающих ошибок
        data class SuccessMsg(val message: String) : UiState() // Это будет использоваться для всплывающих ошибок
        object Success : UiState() // Новое состояние для успешного действия
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    private val _isClosingTask = MutableStateFlow(false)
    val isClosingTask: StateFlow<Boolean> = _isClosingTask

    init {
        loadData()
    }

    fun resetError() {
        _errorMessage.value = null
    }
    fun resetsuccessMessage() {
        _successMessage.value = null
    }

    fun checkBox() {
        viewModelScope.launch {
            try {
                val taskName = spHelper.getTaskName() ?: "Unknown Task"
                val artikul = spHelper.getArticuleWork()?.toIntOrNull() ?: -1

                if (artikul == -1) {
                    _errorMessage.value = "Некорректный артикул"
                    return@launch
                }

                val response = api.checkOrderCompletionWBBox(taskName, artikul.toString())
                if (response.success) {
                    _successMessage.value = response.value
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка: ${e.message}"
            }
        }
    }


    fun checkOrderCompletionBeforeClosing() {
        viewModelScope.launch {
            if (_isClosingTask.value) return@launch // Проверяем, чтобы избежать повторного вызова
            _isClosingTask.value = true
            try {
                val taskName = spHelper.getTaskName() ?: "Unknown Task"
                val artikul = spHelper.getArticuleWork()?.toIntOrNull() ?: -1

                if (artikul == -1) {
                    _errorMessage.value = "Invalid artikul"
                    return@launch
                }

                val response = api.checkWBComplect(taskName, artikul.toString())
                if (response.success) {
                    closeTask()
                } else {
                    _errorMessage.value = response.value
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка проверки: ${e.message}"
            } finally {
                _isClosingTask.value = false
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
                val totalCountValue = boxes.sumBy { it.kolvoTovarov }

                _uiState.value = UiState.Loaded(boxes, spHelper.getNameStuffWork().toString(),  totalCount, totalCountValue)
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

    @SuppressLint("NewApi")
    fun closeTask() {
        viewModelScope.launch {
            try {
                val currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"))
                val response = api.endStatusWb(spHelper.getId(), currentDateTime)
                if (response.success) {
                    _uiState.value = UiState.Success
                } else {
                    _uiState.value = UiState.Error("Ошибка закрытия задания!")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Ошибка закрытия задания: ${e.localizedMessage}")
            }
        }
    }


    fun excludeArticle(id: Long, reason: String, comment: String, count: Int) {
        viewModelScope.launch {
            try {
                val artikul = spHelper.getArticuleWork()?.toIntOrNull() ?: -1

                if (artikul == -1) {
                    _uiState.value = UiState.Error("Invalid artikul")
                    return@launch
                }

                val response = api.excludeArticle(id, reason, comment, count)
                if(response.success)
                _successMessage.value = response.value
                else _errorMessage.value = response.value
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to exclude article: ${e.message}")
            }
        }
    }

    fun resetSuccessState() {
        _uiState.value = UiState.Loading
        _isClosingTask.value = false
    }

}
