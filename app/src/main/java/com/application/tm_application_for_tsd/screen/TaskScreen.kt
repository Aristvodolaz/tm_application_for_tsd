package com.application.tm_application_for_tsd.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.tm_application_for_tsd.utils.SPHelper
import com.application.tm_application_for_tsd.viewModel.TaskViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    onNavigateToObrabotka: (String) -> Unit, // Функция для навигации
    viewModel: TaskViewModel = hiltViewModel(),
    spHelper: SPHelper) {
    val skladList by viewModel.skladList.collectAsState(initial = emptyList())
    val selectedSklad by viewModel.selectedSklad.collectAsState()
    val taskList by viewModel.taskList.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var searchQuery by remember { mutableStateOf("") } // Состояние для строки поиска

    var isDialogOpen by remember { mutableStateOf(false) }

    val filteredTaskList = taskList.filter {
        it.Nazvanie_Zadaniya.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp)
            .padding(horizontal = 8.dp)
            .background(Color(0xffffffff))
    ) {
        // Поле выбора склада с обработкой кликов
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .clickable { isDialogOpen = true } // Открытие диалога при нажатии
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    shape = MaterialTheme.shapes.small
                )
                .padding(8.dp),

        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedSklad?.name ?: "Выберите склад",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
            }
        }

        // AlertDialog для выбора склада
        if (isDialogOpen) {
            AlertDialog(
                onDismissRequest = { isDialogOpen = false },
                title = { Text("Выберите склад") },
                text = {
                    Column {
                        if (skladList.isEmpty()) {
                            Text("Список складов пуст")
                        } else {
                            skladList.forEach { sklad ->
                                Text(
                                    text = sklad.name ?: "Нет имени",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.selectSklad(sklad)
                                            viewModel.fetchTasks(sklad.pref) // Загрузка заданий для выбранного склада
                                            isDialogOpen = false
                                        }
                                        .padding(vertical = 8.dp),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { isDialogOpen = false }) {
                        Text("Закрыть")
                    }
                }
            )
        }

        // Поле ввода для поиска
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Поиск по названию задания") },
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Ошибка загрузки
        error?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        // Индикатор загрузки
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            // Список заданий
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredTaskList) { task ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(bottom = 3.dp) // Отступы между карточками
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                shape = MaterialTheme.shapes.small
                            )
                            .clickable {
                                if (task.Nazvanie_Zadaniya.isNotEmpty()) {
                                    spHelper.saveTaskName(task.Nazvanie_Zadaniya)
                                    onNavigateToObrabotka(task.Nazvanie_Zadaniya) // Передача параметра taskName
                                } else {
                                    Log.e("TaskScreen", "Пустое название задания")
                                }
                            },
                        elevation = CardDefaults.elevatedCardElevation(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White // Динамический фон
                        )
                    ) {
                        Text(
                            text = task.Nazvanie_Zadaniya,
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
