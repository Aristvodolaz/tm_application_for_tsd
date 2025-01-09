package com.application.tm_application_for_tsd.screen.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.application.tm_application_for_tsd.network.request_response.WBData
import com.application.tm_application_for_tsd.network.request_response.WBItem
import com.application.tm_application_for_tsd.utils.SPHelper
import com.application.tm_application_for_tsd.viewModel.RedactorWBViewModel
import com.application.tm_application_for_tsd.viewModel.ScannerViewModel
@Composable
fun RedactorWBScreen(
    taskName: String,
    redactorWBViewModel: RedactorWBViewModel = hiltViewModel(),
    onClick: (WBItem) -> Unit,
    scannerViewModel: ScannerViewModel = hiltViewModel()
) {
    val uiState by redactorWBViewModel.uiState.collectAsState()
    val scannedBarcode by scannerViewModel.barcodeData.collectAsStateWithLifecycle()
    var articles by remember { mutableStateOf<List<WBItem>>(emptyList()) }
    var filteredArticles by remember { mutableStateOf<List<WBItem>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }

    // Загрузка данных
    LaunchedEffect(taskName) {
        redactorWBViewModel.getData(taskName)
    }

    // Обновляем filteredArticles при изменении articles, searchQuery или scannedBarcode
    LaunchedEffect(articles, searchQuery, scannedBarcode) {
        filteredArticles = articles.filter { article ->
            (scannedBarcode.isNotEmpty() && (
                    article.shk?.contains(scannedBarcode, ignoreCase = true) == true ||
                            article.pallet?.contains(scannedBarcode, ignoreCase = true) == true
                    )) || (searchQuery.isNotEmpty() && (
                    article.pallet?.contains(searchQuery, ignoreCase = true) == true ||
                            article.artikul?.toString()?.contains(searchQuery, ignoreCase = true) == true ||
                            article.shk?.contains(searchQuery, ignoreCase = true) == true
                    )) || (searchQuery.isEmpty() && scannedBarcode.isEmpty()) // Отображаем всё, если фильтры пустые
        }
    }

    Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Поиск") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            when (uiState) {
                is RedactorWBViewModel.UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is RedactorWBViewModel.UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = (uiState as RedactorWBViewModel.UiState.Error).message,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                is RedactorWBViewModel.UiState.Empty -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Пусто!",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
                is RedactorWBViewModel.UiState.Success -> {
                    val successState = uiState as RedactorWBViewModel.UiState.Success
                    articles = successState.items

                    // Если filteredArticles пуст, показываем сообщение или список
                    if (filteredArticles.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Нет совпадений",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(filteredArticles) { item ->
                                ArticleRedactorForWBCard(item, onClicks = {
                                    onClick(item)
                                }, onDelete = {
                                    redactorWBViewModel.deleteArticle(item.id, item.name)
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleRedactorForWBCard(
    article: WBItem,
    onClicks: (WBItem) -> Unit,
    onDelete: (WBItem) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFAFAFA)
        ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        onClick = { onClicks(article) }
    ) {
        Box(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Столбец с информацией об артикуле
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = ("Aртикул: " + article.artikul.toString()) ?: "Не указано",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ШК: ${article.shk ?: "Не указан"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Паллет: ${article.pallet ?: "Не указан"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Вложеноость: ${article.kolvo}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                // Иконка удаления
                IconButton(
                    onClick = { showDialog = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                        contentDescription = "Удалить",
                        tint = Color.Red
                    )
                }
            }

            // Показываем диалог подтверждения удаления
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        TextButton(onClick = {
                            onDelete(article) // Вызов обработчика удаления
                            showDialog = false
                        }) {
                            Text("Удалить", color = Color.Red)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Отмена")
                        }
                    },
                    title = { Text("Удаление") },
                    text = { Text("Вы уверены, что хотите удалить этот элемент?") }
                )
            }
        }
    }
}
