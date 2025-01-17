package com.application.tm_application_for_tsd.viewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.tm_application_for_tsd.network.Api
import com.application.tm_application_for_tsd.network.request_response.SrokGodnosti
import com.application.tm_application_for_tsd.network.request_response.UpdateStatusRequest
import com.application.tm_application_for_tsd.utils.SPHelper
import com.application.tm_application_for_tsd.viewModel.LduViewModel.UiState
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
@HiltViewModel
class InfoArticleViewModel @Inject constructor(
    private val api: Api,
    private val spHelper: SPHelper
) : ViewModel() {

    private val _state = MutableStateFlow(InfoArticleState())
    val state: StateFlow<InfoArticleState> get() = _state

    private val _navigateToNextScreen = MutableStateFlow(false)
    val navigateToNextScreen: StateFlow<Boolean> = _navigateToNextScreen

    @RequiresApi(Build.VERSION_CODES.O)
    fun changeStatusTask() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"))
                val response = api.changeStatus(spHelper.getId(), 1, currentDateTime,
                   spHelper.getNameEmployer()!!
                )

                if (response.success) {
                    _state.value = _state.value.copy(
                        successMessage = "Артикул успешно взят в работу",
                        isTaskInProgress = true,
                        isLoading = false
                    )
                } else {
                    _state.value = _state.value.copy(
                        errorMessage = "Не удалось изменить статус задания. Попробуйте снова.",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = "Ошибка соединения: ${e.localizedMessage}",
                    isLoading = false
                )
            }
        }
    }
    fun clearSuccessMessage() {
        updateState(successMessage = null)
    }

    fun clearErrorMessage() {
        updateState(errorMessage = null)
    }

    fun addExpirationData(persent: String, endDate: String) {
        viewModelScope.launch {
            try {
                val response = api.sendSrokGodnosti(spHelper.getId(), endDate, persent)

                if (response.success) {
                    if(spHelper.getPref()=="WB"){
                        addSrokForWB(endDate)
                    }else{
                        _state.value = _state.value.copy(successMessage = "Срок годности успешно обновлен!")
                        _navigateToNextScreen.value = true // Устанавливаем флаг для навигации

                    }
                } else {
                    _state.value = _state.value.copy(errorMessage = "Ошибка обновления срока годности.")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Ошибка: ${e.localizedMessage}")
            }
        }
    }

    fun addSrokForWB(data: String){
        viewModelScope.launch {
            try {
                val response = spHelper.getTaskName()
                    ?.let { SrokGodnosti(it,spHelper.getArticuleWork()!!.toInt(),data) }
                    ?.let { api.addSrokGodnostiForWb(it) }
                if(response!!.success){
                    _state.value = _state.value.copy(successMessage = "Срок годности успешно обновлен!")
                    _navigateToNextScreen.value = true // Устанавливаем флаг для навигации

                } else  _state.value = _state.value.copy(errorMessage = "Ошибка обновления срока годности.")

            }catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Ошибка: ${e.localizedMessage}")
            }
        }
    }

    private fun updateState(
        isLoading: Boolean = false,
        taskStatus: TaskStatus? = null,
        errorMessage: String? = null,
        successMessage: String? = null,
        newShk: String? = null,
        isTaskInProgress: Boolean = false
    ) {
        _state.value = _state.value.copy(
            isLoading = isLoading,
            taskStatus = taskStatus ?: _state.value.taskStatus,
            errorMessage = errorMessage,
            successMessage = successMessage,
            newShk = newShk,
            isTaskInProgress = isTaskInProgress
        )
    }

    fun resetNavigation() {
        _navigateToNextScreen.value = false
    }

    fun triggerNavigation() {
        _navigateToNextScreen.value = true
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun excludeArticle(reason: String, comment: String, count: Int) {
        viewModelScope.launch {
            // Устанавливаем состояние загрузки
            _state.value = _state.value.copy(isLoading = true)

            try {
                // Отправляем запрос на исключение артикула
                val response = spHelper.getArticuleWork()?.toInt()?.let {
                    api.excludeArticle(spHelper.getId(), reason, comment, count)
                }

                if (response?.success == true) {
                    // Логика для экстренного исключения
                    if (reason == "Убрать из обработки(экстренно)") {
                        spHelper.getNameEmployer()?.let { setEndStatus(spHelper.getId(), it) }
                    }
                    // Устанавливаем успешное сообщение
                    _state.value = _state.value.copy(
                        successMessage = "Артикул успешно исключён из обработки!",
                        errorMessage = null, // Сбрасываем ошибку
                        isLoading = false    // Останавливаем индикатор загрузки
                    )
                } else {
                    // Обработка ошибки при исключении артикула
                    _state.value = _state.value.copy(
                        errorMessage = "Ошибка исключения артикула.",
                        successMessage = null, // Сбрасываем успех
                        isLoading = false      // Останавливаем индикатор загрузки
                    )
                }
            } catch (e: Exception) {
                // Обработка исключений
                _state.value = _state.value.copy(
                    errorMessage = "Ошибка: ${e.localizedMessage}",
                    successMessage = null, // Сбрасываем успех
                    isLoading = false      // Останавливаем индикатор загрузки
                )
            }
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun setEndStatus(id: Long, name: String) {
        viewModelScope.launch {
            // Устанавливаем состояние загрузки
            _state.value = _state.value.copy(isLoading = true)

            try {
                // Форматируем текущую дату и время
                val currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"))

                // Отправляем запрос на обновление статуса
                val response = api.addEndStatus(UpdateStatusRequest(id, currentDateTime, name))

                if (response.success) {
                    // Обновляем состояние с успешным сообщением
                    _state.value = _state.value.copy(
                        successMessage = "Артикул успешно исключён из обработки!",
                        errorMessage = null, // Сбрасываем ошибку
                        isLoading = false    // Останавливаем индикатор загрузки
                    )
                } else {
                    // Обработка ошибки на уровне сервера (если success == false)
                    _state.value = _state.value.copy(
                        errorMessage = "Не удалось обновить статус артикула. Попробуйте снова.",
                        successMessage = null, // Сбрасываем успех
                        isLoading = false      // Останавливаем индикатор загрузки
                    )
                }
            } catch (e: Exception) {
                // Обработка исключений (например, ошибка соединения)
                _state.value = _state.value.copy(
                    errorMessage = "Ошибка: ${e.localizedMessage}",
                    successMessage = null, // Сбрасываем успех
                    isLoading = false      // Останавливаем индикатор загрузки
                )
            }
        }
    }


}
data class InfoArticleState(
    val isLoading: Boolean = false,
    val taskStatus: TaskStatus = TaskStatus.NOT_STARTED,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val newShk: String? = null,
    val isTaskInProgress: Boolean = false
) {
    val isSuccess: Boolean get() = !successMessage.isNullOrEmpty()
    val isError: Boolean get() = !errorMessage.isNullOrEmpty()
}

enum class TaskStatus {
    NOT_STARTED,
    IN_PROGRESS,
    CANCELLED
}

