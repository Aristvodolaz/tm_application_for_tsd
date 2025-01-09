package com.application.tm_application_for_tsd.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.tm_application_for_tsd.network.Api
import com.application.tm_application_for_tsd.network.request_response.Universal
import com.application.tm_application_for_tsd.viewModel.OzonEditViewModel.UiState
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
        data class Error(val message: String) : OtkazState()
    }

    private val _otkazState = MutableStateFlow<OtkazState>(OtkazState.Loading)
    val otkazState: StateFlow<OtkazState> = _otkazState

    // Отправка данных о фактическом количестве
    fun sendFactSize(id: Long, count: Int
//                     , fact: Int
    ) {
        viewModelScope.launch {
            try {
                // Устанавливаем состояние в "Loading" перед началом запроса
//                _otkazState.value = OtkazState.Loading

                val response = api.setFactSize(id, count
//                    , fact
                )

                // Обновляем состояние после выполнения запроса
                if (response.success) {
                    _otkazState.value = OtkazState.Success("Приемка успешно завершена!")
                } else {
                    _otkazState.value = OtkazState.Error("Ошибка в завершении приемки!")
                }
            } catch (e: Exception) {
                // В случае ошибки устанавливаем состояние Error
                _otkazState.value = OtkazState.Error("Ошибка: ${e.localizedMessage}")
            }
        }
    }

//    fun getFactVp(id: Long, count: Int, vp: String, art: String){
//        viewModelScope.launch {
//            try {
//                val response = api.getVPSize(art, vp)
//                if(response.success)  {
//                    sendFactSize(id, count, response.value)
//                } else _otkazState.value = OtkazState.Error("Ошибка в получении данных!")
//
//            } catch (e: Exception) {
//                // В случае ошибки устанавливаем состояние Error
//                _otkazState.value = OtkazState.Error("Ошибка: ${e.localizedMessage}")
//            }
//        }
//    }
}
