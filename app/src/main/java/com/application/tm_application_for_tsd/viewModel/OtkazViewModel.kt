package com.application.tm_application_for_tsd.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.tm_application_for_tsd.network.Api
import com.application.tm_application_for_tsd.network.request_response.AddTaskDataRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtkazViewModel @Inject constructor(
    private val api: Api
) : ViewModel() {

    sealed class OtkazState {
        object Loading : OtkazState()
        data class Success(val message: String) : OtkazState()
        data class SuccessVP(val message: String, val sum: Int, val factSum: Int, val prinyato: Int) : OtkazState()
        data class Error(val message: String) : OtkazState()
    }

    private val _otkazState = MutableStateFlow<OtkazState>(OtkazState.Loading)
    val otkazState: StateFlow<OtkazState> = _otkazState

    /**
     * Загружаем данные при открытии экрана
     */
    fun getTransferSize(vp: String, artikul: String) {
        viewModelScope.launch {
            try {
                _otkazState.value = OtkazState.Loading
                val response = api.getTransferNumsData(vp, artikul)
                if (response.success) {
                    val prinyato = response.sum - response.factSum
                    _otkazState.value = OtkazState.SuccessVP(
                        message = "Данные успешно загружены",
                        sum = response.sum,
                        factSum = response.factSum,
                        prinyato = prinyato
                    )
                } else {
                    _otkazState.value = OtkazState.Error("Ошибка получения данных")
                }
            } catch (e: Exception) {
                _otkazState.value = OtkazState.Error("Ошибка: ${e.localizedMessage}")
            }
        }
    }

    /**
     * ЧАСТИЧНАЯ ПРИЕМКА: Добавляем данные и сразу обновляем UI
     */
    fun addInfoVP(name: String, artikul: String, vp: String, plan: Int, fact: Int) {
        viewModelScope.launch {
            try {
                _otkazState.value = OtkazState.Loading
                val response = api.addTaskData(AddTaskDataRequest(name, vp, artikul, plan, fact))
                if (response.success) {
                    // После частичной приемки обновляем UI
                    getTransferSize(vp, artikul)
                } else {
                    _otkazState.value = OtkazState.Error("Ошибка добавления данных")
                }
            } catch (e: Exception) {
                _otkazState.value = OtkazState.Error("Ошибка: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Завершение приемки: Устанавливаем статус и закрываем экран
     */
    fun setStatus(id: Long, status: Int, toNextScreen: () -> Unit) {
        viewModelScope.launch {
            try {
                _otkazState.value = OtkazState.Loading
                val response = api.updateStatus(id, status)
                if (response.success) {
                    toNextScreen()
                } else {
                    _otkazState.value = OtkazState.Error("Ошибка обновления статуса")
                }
            } catch (e: Exception) {
                _otkazState.value = OtkazState.Error("Ошибка: ${e.localizedMessage}")
            }
        }
    }
}
