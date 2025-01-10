package com.application.tm_application_for_tsd.viewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.tm_application_for_tsd.network.Api
import com.application.tm_application_for_tsd.utils.SPHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class OzonViewModel @Inject constructor(
    private val api: Api,
    private val spHelper: SPHelper
) : ViewModel() {
    private val _vneshnost = MutableStateFlow("")
    val vneshnost: StateFlow<String> = _vneshnost.asStateFlow()

    private val _boxCount = MutableStateFlow("")
    val boxCount: StateFlow<String> = _boxCount.asStateFlow()

    private val _palletNumber = MutableStateFlow("")
    val palletNumber: StateFlow<String> = _palletNumber.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()
    private val _completionError = MutableStateFlow<String?>(null)
    val completionError: StateFlow<String?> = _completionError.asStateFlow()
    private val _isNonStandardAction = MutableStateFlow(false)
    val isNonStandardAction: StateFlow<Boolean> = _isNonStandardAction.asStateFlow()


    fun onVneshnostChange(value: String) {
        _vneshnost.value = value
    }

    fun onBoxCountChange(value: String) {
        _boxCount.value = value
    }

    fun onPalletNumberChange(value: String) {
        _palletNumber.value = value
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun sendFinishData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _successMessage.value = null

            try {
                val articulWork = spHelper.getArticuleWork() ?: throw Exception("Артикул не найден")
                val taskName = spHelper.getTaskName() ?: throw Exception("Название задания отсутствует")

                // Проверка введенных значений
                if (_boxCount.value.isEmpty() || _vneshnost.value.isEmpty() || _palletNumber.value.isEmpty()) {
                    _error.value = "Ошибка: одно или несколько полей пустые."
                    return@launch
                }

                val currentMesto = _boxCount.value.toIntOrNull() ?: 0
                val currentVlozhennost = _vneshnost.value.toIntOrNull() ?: 0
                val currentTotal = currentMesto * currentVlozhennost

                if (currentTotal <= 0) {
                    _error.value = "Ошибка: введенные значения должны быть больше нуля."
                    return@launch
                }

                // Запрос проверки состояния заказа
                val checkResponse = api.checkOZONComplect(taskName, articulWork)

                if (checkResponse.success) {
                    val errorMessage = checkResponse.value

                    // Разбираем ответ "Осталось: X"
                    val parts = errorMessage.split("Можно добавить:").last().trim()
                    val remaining = parts.toIntOrNull() ?: 0

                    if (remaining < 0) {
                        _error.value = "Ошибка: итоговый заказ уже переполнен. Остаток: $remaining. Проверьте данные."
                        return@launch
                    }

                    if (currentTotal == remaining) {
                        sendUpdateRequest(currentTotal)
                    } else {
                        _error.value = "Ошибка: можно добавить только $remaining единиц(ы)."
                    }
                    return@launch
                }
            } catch (e: Exception) {
                _error.value = "Ошибка: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun sendUpdateRequest(currentTotal: Int) {
        val currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"))

        val response = api.updateOzon(
            id = spHelper.getId(),
            mesto = _boxCount.value.toInt(),
            vlozhennost = _vneshnost.value.toInt(),
            palletNo = _palletNumber.value.toInt(),
            time = currentDateTime
        )

        if (response.success) {
            _successMessage.value = "Данные успешно отправлены ($currentTotal)."
        } else {
            throw Exception("Ошибка отправки данных.")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveNonStandardData(nested: String, place: String, pallet: String) {
        viewModelScope.launch {
            _isNonStandardAction.value = true  // Устанавливаем флаг для нестандартной вложенности
            try {
                val currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"))
                val response = api.getDuplicate(spHelper.getId(), place.toInt(), nested.toInt(), pallet.toInt(), currentDateTime)

                if (response!!.success) {
                    _successMessage.value = "Нестандартная вложенность сохранена"
                } else {
                    throw Exception("Неверные данные для отправки")
                }
            } catch (e: Exception) {
                _error.value = "Ошибка: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun excludeArticle(id: Long, reason: String, comment: String, size: Int) {
        viewModelScope.launch {
            try {
                val response = spHelper.getArticuleWork()?.toInt()?.let {
                    api.excludeArticle(
                        id,
                        reason,
                        comment,
                        size
                    )
                }
                if (response!!.success) {
                    _successMessage.value = "Артикул успешно исключён из обработки"
                } else {
                    _error.value = "Ошибка исключения артикула"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка: ${e.message}"
            }
        }
    }


    fun clearMessages() {
        _error.value = null
        _successMessage.value = null
        _isNonStandardAction.value = false  // Сброс флага после обработки
    }

}
