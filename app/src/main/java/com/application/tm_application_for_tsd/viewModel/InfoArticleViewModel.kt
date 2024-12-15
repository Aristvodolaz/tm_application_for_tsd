package com.application.tm_application_for_tsd.viewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.tm_application_for_tsd.network.Api
import com.application.tm_application_for_tsd.network.request_response.SrokGodnosti
import com.application.tm_application_for_tsd.network.request_response.Status
import com.application.tm_application_for_tsd.utils.SPHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun changeStatusTask(article: String, status: Int) {
        viewModelScope.launch {
            updateState(isLoading = true)
            try {
                val currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"))
                val taskName = spHelper.getTaskName() ?: throw IllegalStateException("Task name not found")
                val response = api.changeStatus(Status(taskName, article,
                   currentDateTime, status, spHelper.getNameEmployer()!!
                ))
                if (response.success) {
                    updateState(
                        successMessage = "Артикул успешно взят в работу",
                        taskStatus = TaskStatus.IN_PROGRESS
                    )
                } else {
                    updateState(errorMessage = "Не удалось изменить статус задания. Попробуйте снова.")
                }
            } catch (e: Exception) {
                updateState(errorMessage = "Ошибка соединения: ${e.localizedMessage}")
            }
        }
    }

    fun findInExcel(shk: String, name: String) {
        viewModelScope.launch {
            updateState(isLoading = true)
            try {
                val response = api.getShk(name, shk)
                if (response.success && response.articuls.isNotEmpty()) {
                    val articul = response.articuls.first()
                    articul.shk?.let { spHelper.setShkWork(it) }
                    spHelper.setArticuleWork(articul.artikul.toString())
                    articul.nazvanieTovara?.let { spHelper.setNameStuffWork(it) }
                    updateState(successMessage = "Товар найден: ${articul.nazvanieTovara}")
                } else {
                    searchArticleInDb(spHelper.getArticuleWork())
                }
            } catch (e: Exception) {
                updateState(errorMessage = "Ошибка: ${e.localizedMessage}")
            }
        }
    }

    private fun searchArticleInDb(article: String?) {
        viewModelScope.launch {
            try {
                val response = article?.let { api.getArticulTask(spHelper.getTaskName() ?: "", it) }
                if (response != null && response.success && response.articuls.isEmpty()) {
                    val shk = response.articuls.first().shk.orEmpty()
                    spHelper.setShkWork(shk)
                    updateState(successMessage = "Штрихкод найден: $shk")
                } else {
                    updateState(errorMessage = "Артикул не найден в базе данных.")
                }
            } catch (e: Exception) {
                updateState(errorMessage = "Ошибка: ${e.localizedMessage}")
            }
        }
    }

    private fun updateState(
        isLoading: Boolean = false,
        taskStatus: TaskStatus? = null,
        errorMessage: String? = null,
        successMessage: String? = null,
        newShk: String? = null
    ) {
        _state.value = _state.value.copy(
            isLoading = isLoading,
            taskStatus = taskStatus ?: _state.value.taskStatus,
            errorMessage = errorMessage,
            successMessage = successMessage,
            newShk = newShk
        )
    }
}

data class InfoArticleState(
    val isLoading: Boolean = false,
    val taskStatus: TaskStatus = TaskStatus.NOT_STARTED,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val newShk: String? = null
)

enum class TaskStatus {
    NOT_STARTED,
    IN_PROGRESS,
    CANCELLED
}
