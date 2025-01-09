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
fun RedactorForMasterScreen(
    taskName: String,
    viewModel: TaskViewModel = hiltViewModel(),
    scannerViewModel: ScannerViewModel = hiltViewModel(),
    onClick: (Article.Articuls) -> Unit
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
                it.shk?.contains(scannedBarcode) == true ||
                        it.shkSyrya?.contains(scannedBarcode) == true ||
                        it.shkWps?.contains(scannedBarcode) == true
            }
            scannerViewModel.clearBarcode()
        }
    }

    // Фильтрация по поисковому запросу
    LaunchedEffect(searchQuery) {
        val query = searchQuery.trim()
        filteredArticles = articles.filter {
            it.nazvanieTovara?.contains(query, ignoreCase = true) == true ||
                    it.artikulSyrya?.contains(query, ignoreCase = true) == true ||
                    it.artikul?.toString()?.contains(query) == true ||
                    it.shk?.contains(query) == true ||
                    it.shkSyrya?.contains(query) == true ||
                    it.shkWps?.contains(query) == true
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
                        ArticleCardWithDelete(
                            article = article,
                            onClick = { onClick(article) },
                            onDelete = { selectedArticle ->
                                handleDeleteArticle(
                                    context = context,
                                    viewModel = viewModel,
                                    selectedArticle = selectedArticle,
                                    taskName = taskName,
                                    articles = articles,
                                    filteredArticles = filteredArticles,
                                    onArticlesUpdate = { updatedArticles ->
                                        articles = updatedArticles
                                        filteredArticles = updatedArticles
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleCardWithDelete(
    article: Article.Articuls,
    onClick: (Article.Articuls) -> Unit,
    onDelete: (Article.Articuls) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        onClick = { onClick(article) }
    ) {
        Box(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Название: ${article.nazvanieTovara ?: "Не указано"}",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Артикул: ${article.artikul ?: "Не указан"}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "ШК: ${article.shk ?: "Не указан"}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "Место: ${article.mesto ?: "Не указано"}", style = MaterialTheme.typography.bodyMedium)
                }

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

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        TextButton(onClick = {
                            onDelete(article)
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

fun handleDeleteArticle(
    context: Context,
    viewModel: TaskViewModel,
    selectedArticle: Article.Articuls,
    taskName: String,
    articles: List<Article.Articuls>,
    filteredArticles: List<Article.Articuls>,
    onArticlesUpdate: (List<Article.Articuls>) -> Unit
) {
    if (selectedArticle.pref == "WB") {
        viewModel.resetWB(
            id = selectedArticle.id?.toLong() ?: 0,
            taskName = taskName,
             articul = selectedArticle.artikul.toString(),
            onSuccess = {
                Toast.makeText(context, "Удаление успешно", Toast.LENGTH_SHORT).show()
                val updatedArticles = articles.filter { it.id != selectedArticle.id }
                onArticlesUpdate(updatedArticles)
            },
            onError = { error ->
                Toast.makeText(context, "Ошибка удаления: $error", Toast.LENGTH_SHORT).show()
            }
        )
    } else {
        viewModel.resetOzon(
            articul = selectedArticle.artikul.toString(),
            taskName = taskName,
            onSuccess = {
                Toast.makeText(context, "Удаление успешно", Toast.LENGTH_SHORT).show()
                val updatedArticles = articles.filter { it.id != selectedArticle.id }
                onArticlesUpdate(updatedArticles)
            },
            onError = { error ->
                Toast.makeText(context, "Ошибка удаления: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
