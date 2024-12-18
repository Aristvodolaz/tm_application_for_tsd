package com.application.tm_application_for_tsd.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.tm_application_for_tsd.network.Api
import com.application.tm_application_for_tsd.network.request_response.Article
import com.application.tm_application_for_tsd.network.request_response.Data
import com.application.tm_application_for_tsd.network.request_response.Value
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class TaskViewModel @Inject constructor(
    private val api: Api
) : ViewModel() {

    private val _skladList = MutableStateFlow<List<Value>>(emptyList())
    val skladList: StateFlow<List<Value>> = _skladList

    private val _selectedSklad = MutableStateFlow<Value?>(null)
    val selectedSklad: StateFlow<Value?> = _selectedSklad

    private val _taskList = MutableStateFlow<List<Data>>(emptyList())
    val taskList: StateFlow<List<Data>> = _taskList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error


    init {
        fetchSklads()
    }

    suspend fun getTasksInWork(taskName: String, status: Int): Article{
        return api.getTasksInWork(taskName, status)
    }

    fun fetchSklads() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = api.getSklads()
                if (response.success && response.value.isNotEmpty()) {
                    _skladList.value = response.value
                } else {
                    _error.value = "Ошибка загрузки складов"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchTasks(skladPref: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = api.getTasks(skladPref)
                if (response.success) {
                    _taskList.value = response.value
                } else {
                    _error.value = "Ошибка загрузки заданий"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectSklad(sklad: Value) {
        _selectedSklad.value = sklad
        fetchTasks(sklad.pref) // Загружаем задания для выбранного склада
    }

    fun deleteArticle(id: Long, taskName: String) {
        viewModelScope.launch {
            try {
                api.deleteRecord(id, taskName )
//                api.deleteArticle(articleId) // Ваш API для удаления
            } catch (e: Exception) {
                // Обработать ошибку
            }
        }
    }

}
