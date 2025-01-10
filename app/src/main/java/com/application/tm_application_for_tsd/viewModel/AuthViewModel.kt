package com.application.tm_application_for_tsd.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.tm_application_for_tsd.repository.AuthRepository
import com.application.tm_application_for_tsd.utils.SPHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val spHelper: SPHelper,
    private val authRepository: AuthRepository,
) : ViewModel() {

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        data class Success(val username: String) : AuthState()
        data class Error(val error: String) : AuthState()
    }

    private val _authStatus = MutableStateFlow<AuthState>(AuthState.Idle)
    val authStatus: StateFlow<AuthState> get() = _authStatus

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> get() = _isAuthenticated.asStateFlow()

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading

    fun authenticate(barcode: String) {
        val trimmedBarcode = barcode.substring(1, barcode.length - 1)

        if (trimmedBarcode.isBlank()) {
            _authStatus.value = AuthState.Error(error = "Штрих-код не может быть пустым")
            return
        }

        viewModelScope.launch {
            _authStatus.value = AuthState.Loading
            _loading.postValue(true)

            try {
                val userResponse = authRepository.getEmployeeById(trimmedBarcode)

                if (userResponse.success) {
                    val user = userResponse.value?.firstOrNull()
                    if (user != null) {
                        val username = user.name
                        _authStatus.value = AuthState.Success(username = username)
                        _isAuthenticated.value = true
                    } else {
                        _authStatus.value = AuthState.Error(error = "Пользователь не найден")
                        _isAuthenticated.value = false
                    }
                } else {
                    val errorMessage = "Ошибка авторизации: код ${userResponse.errorCode}"
                    Log.e("AuthViewModel", errorMessage)
                    _authStatus.value = AuthState.Error(error = errorMessage)
                    _isAuthenticated.value = false
                }

            } catch (e: Exception) {
                val exceptionMessage = "Ошибка авторизации: ${e.localizedMessage ?: "Неизвестная ошибка"}"
                Log.e("AuthViewModel", exceptionMessage, e)
                _authStatus.value = AuthState.Error(error = exceptionMessage)
                _isAuthenticated.value = false

            } finally {
                _loading.postValue(false)
            }
        }
    }
}
