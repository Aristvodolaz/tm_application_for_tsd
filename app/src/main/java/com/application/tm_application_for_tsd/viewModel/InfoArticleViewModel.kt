package com.application.tm_application_for_tsd.viewModel
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.tm_application_for_tsd.network.Api
import com.application.tm_application_for_tsd.network.request_response.SrokGodnosti
import com.application.tm_application_for_tsd.network.request_response.Status
import com.application.tm_application_for_tsd.network.request_response.UpdateSrokGodnosti
import com.application.tm_application_for_tsd.utils.SPHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
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
    fun changeStatusTask(article: String, status: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"))
                val taskName = spHelper.getTaskName() ?: throw IllegalStateException("Task name not found")
                val response = api.changeStatus(Status(taskName, article, currentDateTime, status, "TEST"))

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

    fun addExpirationData(persent: String, endDate: String) {
        viewModelScope.launch {
            try {
                val response = api.sendSrokGodnosti(
                    UpdateSrokGodnosti(
                        srok = endDate,
                        persent = persent,
                        articul = spHelper.getArticuleWork() ?: "",
                        taskName = spHelper.getTaskName() ?: ""
                    )
                )
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
                    ?.let { api.addSrokGodnosti(it) }
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


}

data class InfoArticleState(
    val isLoading: Boolean = false,
    val taskStatus: TaskStatus = TaskStatus.NOT_STARTED,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val newShk: String? = null,
    val isTaskInProgress: Boolean = false // Новый флаг
)


enum class TaskStatus {
    NOT_STARTED,
    IN_PROGRESS,
    CANCELLED
}
