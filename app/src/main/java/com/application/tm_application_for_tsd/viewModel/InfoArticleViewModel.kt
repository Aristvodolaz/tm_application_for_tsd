package com.application.tm_application_for_tsd.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.tm_application_for_tsd.network.Api
import com.application.tm_application_for_tsd.network.request_response.Status
import com.application.tm_application_for_tsd.utils.SPHelper
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InfoArticleViewModel(
    private val api: Api
) : ViewModel() {

    private val _state = MutableStateFlow(InfoArticleState())
    val state: StateFlow<InfoArticleState> = _state

    fun changeStatusTask(article: String, status: Int) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                val response = api.changeStatus(Status(SPHelper.getNameTask(), article, status, SPHelper.getNameEmployer()))
                if (response.isSuccess) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = "Артикул взят в работу",
                        taskStatus = TaskStatus.IN_PROGRESS
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Статус задания не изменен, повторите попытку сканирования"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Ошибка соединения: ${e.localizedMessage}"
                )
            }
        }
    }

    fun setInSharedPref(shk: String, article: String, name: String) {
        SPHelper.setShkWork(shk)
        SPHelper.setArticuleWork(article)
        SPHelper.setNameStuffWork(name)
    }

    fun findInExcel(shk: String, name: String) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                val response = api.findShkInExcel(name, shk)
                if (response.isSuccess) {
                    val articul = response.articuls[0]
                    setInSharedPref(articul.shk, articul.artikul.toString(), articul.nazvanieTovara)
                    updateShk(shk)
                } else {
                    searchArticleInDb(SPHelper.getArticuleWork())
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Ошибка: ${e.localizedMessage}"
                )
            }
        }
    }

    fun searchArticleInDb(article: String) {
        viewModelScope.launch {
            try {
                val response =api.findShkInDBWithArticule(article)
                if (response.isSuccess && response.value != null) {
                    val shk = response.value[0].shk
                    if (shk.isNullOrBlank()) {
                        _state.value = _state.value.copy(newShk = SPHelper.getShkWork())
                    } else {
                        SPHelper.setShkWork(shk)
                        updateShk(shk)
                    }
                } else {
                    _state.value = _state.value.copy(newShk = SPHelper.getShkWork())
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Ошибка: ${e.localizedMessage}")
            }
        }
    }

    fun updateShk(shk: String) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                val response = model.updateShk(shk)
                if (response.isSuccess) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = "ШК успешно обновлён"
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Ошибка обновления ШК, попробуйте позже"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Ошибка соединения: ${e.localizedMessage}"
                )
            }
        }
    }

    fun sendSrokGodnosti(date: String, persent: String) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                val response = taskModel.sendSrokGodnosti(date, persent)
                if (response.isSuccess) {
                    if (SPHelper.getPrefics() == "WB") {
                        sendWBSrok(date)
                    } else {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            successMessage = "Срок годности успешно записан"
                        )
                    }
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Ошибка записи срока годности"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Ошибка соединения: ${e.localizedMessage}"
                )
            }
        }
    }

    private suspend fun sendWBSrok(date: String) {
        withContext(Dispatchers.IO) {
            taskModel.addSrokForWB(date)
        }
        _state.value = _state.value.copy(
            isLoading = false,
            successMessage = "Срок годности успешно записан"
        )
    }

    fun cancelTask(reason: String, comment: String) {
        viewModelScope.launch {
            try {
                val response = taskModel.calncelTask(reason, comment)
                if (response.isSuccess) {
                    _state.value = _state.value.copy(successMessage = "Задача успешно отменена")
                } else {
                    _state.value = _state.value.copy(errorMessage = "Ошибка отмены задачи")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Ошибка соединения: ${e.localizedMessage}")
            }
        }
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
