package com.application.tm_application_for_tsd.screen.navigation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.application.tm_application_for_tsd.network.request_response.Article
import com.application.tm_application_for_tsd.viewModel.ScannerViewModel
import com.application.tm_application_for_tsd.viewModel.TaskViewModel

@Composable
fun RedactorForMasterScreen (
    taskName: String,
    viewModel: TaskViewModel = hiltViewModel(),
    scannerViewModel: ScannerViewModel = hiltViewModel(),
    onClick:(Article.Articuls) -> Unit
) {
    var articles by remember { mutableStateOf<List<Article.Articuls>>(emptyList()) }
    var filteredArticles by remember { mutableStateOf<List<Article.Articuls>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    val scannedBarcode by scannerViewModel.barcodeData.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Загрузка данных при инициализации
    LaunchedEffect(taskName) {
        try {
            isLoading = true
            articles = viewModel.getTasksInWork(taskName, 2).articuls
            filteredArticles = articles
        } catch (e: Exception) {
            Toast.makeText(context, "Ошибка загрузки: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    // Фильтрация по штрих-коду
    LaunchedEffect(scannedBarcode) {
        if (scannedBarcode.isNotEmpty()) {
            filteredArticles = articles.filter {
                it.shk?.contains(scannedBarcode) == true || it.shkSyrya?.contains(scannedBarcode) == true
            }
            scannerViewModel.clearBarcode()

        }
    }

    // Фильтрация по поисковому запросу
    LaunchedEffect(searchQuery) {
        val query = searchQuery
        filteredArticles = articles.filter {
            it.nazvanieTovara?.contains(query, ignoreCase = true) == true ||
                    it.artikulSyrya?.contains(query, ignoreCase = true) == true ||
                    it.artikul?.toString()?.contains(query) == true ||
                    it.shk?.contains(query) == true ||
                    it.shkSyrya?.contains(query) == true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .background(Color.White),
    ) {
        Text(
            text = taskName,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 12.dp),
            fontSize = 16.sp,
            maxLines = 1
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Поиск") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            if (filteredArticles.isEmpty()) {
                Text(
                    text = "Нет совпадений",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredArticles) { article ->
                        ArticleRedactorCard(
                            article = article,
                            onDelete = { selectedArticle ->
                                // Удаление из списка
                                selectedArticle.id?.let { selectedArticle.nazvanieZadaniya?.let { it1 ->
                                    viewModel.deleteArticle(it,
                                        it1
                                    )
                                } }
                                Toast.makeText(
                                    context,
                                    "Удалено: ${selectedArticle.nazvanieTovara}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                articles = articles - selectedArticle
                                filteredArticles = filteredArticles - selectedArticle
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun confirmDeletion(
    article: Article.Articuls,
    viewModel: TaskViewModel,
    context: Context,
    onDeleteConfirmed: () -> Unit
) {
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Подтверждение удаления") },
            text = { Text("Вы уверены, что хотите удалить ${article.nazvanieTovara}?") },
            confirmButton = {
                TextButton(onClick = {
                    article.id?.let { article.nazvanieZadaniya?.let { it1 ->
                        viewModel.deleteArticle(it,
                            it1
                        )
                    } }
                    Toast.makeText(context, "Удалено: ${article.nazvanieTovara}", Toast.LENGTH_SHORT).show()
                    onDeleteConfirmed()
                    showDialog = false
                }) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleRedactorCard(
    article: Article.Articuls,
    onDelete: (Article.Articuls) -> Unit
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
        elevation = CardDefaults.elevatedCardElevation(4.dp)

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
                        text = article.nazvanieTovara ?: "Не указано",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Артикул: ${article.artikul ?: "Не указан"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (!article.artikulSyrya.isNullOrEmpty()) {
                        Text(
                            text = "Артикул сырья: ${article.artikulSyrya}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    Text(
                        text = "ШК: ${article.shk ?: "Не указан"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (!article.shkSyrya.isNullOrEmpty()) {
                        Text(
                            text = "ШК сырья: ${article.shkSyrya}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
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
