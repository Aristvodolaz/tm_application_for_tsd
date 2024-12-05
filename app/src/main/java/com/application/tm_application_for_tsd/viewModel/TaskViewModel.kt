package com.application.tm_application_for_tsd.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class TaskViewModel @Inject constructor() : ViewModel() {
    val skladList = listOf("MSC-Polaris", "Warehouse-1", "Warehouse-2") // Пример складов
    val selectedSklad = mutableStateOf<String?>(null)
    val taskList = mutableStateOf<List<String>>(emptyList())
    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)

    fun fetchTasks(skladPref: String) {
        isLoading.value = true
        error.value = null

        viewModelScope.launch {
            try {
                // Здесь вы делаете запрос на сервер, замените на ваш API вызов
                val tasks = mockFetchTasksFromServer(skladPref)
                taskList.value = tasks
            } catch (e: Exception) {
                error.value = "Ошибка загрузки: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    // Имитация запроса к серверу
    private suspend fun mockFetchTasksFromServer(skladPref: String): List<String> {
        // Симуляция задержки для загрузки данных
        kotlinx.coroutines.delay(1000)
        return listOf("Task 1", "Task 2", "Task 3") // Пример данных
    }
}
