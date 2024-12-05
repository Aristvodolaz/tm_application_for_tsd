package com.application.tm_application_for_tsd.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.tm_application_for_tsd.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
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
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading

    fun authenticate(barcode: String) {
        viewModelScope.launch {
            _authStatus.value = AuthState.Loading
            _loading.postValue(true)

            try {
                val user = authRepository.getEmployeeById(barcode)
                _authStatus.value = AuthState.Success(username = user.name)
                _isAuthenticated.value = true

            } catch (e: Exception) {
                _authStatus.value = AuthState.Error(error = "Ошибка авторизации")
                _isAuthenticated.value = true

            } finally {
                _loading.postValue(false)
            }
        }
    }
}
