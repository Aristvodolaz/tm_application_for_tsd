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
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.lifecycle.viewModelScope
import com.application.tm_application_for_tsd.network.request_response.Article
import com.application.tm_application_for_tsd.network.request_response.WBItem
import com.application.tm_application_for_tsd.viewModel.ScannerViewModel
import com.application.tm_application_for_tsd.viewModel.TaskViewModel
import kotlinx.coroutines.launch
@Composable
fun RedactorScreen(
    taskName: String,
    viewModel: TaskViewModel = hiltViewModel(),
    scannerViewModel: ScannerViewModel = hiltViewModel(),
    onClickArticle: (Article.Articuls) -> Unit
) {
    var articles by remember { mutableStateOf<List<Article.Articuls>>(emptyList()) }
    var filteredArticles by remember { mutableStateOf<List<Article.Articuls>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scannedBarcode by scannerViewModel.barcodeData.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Загрузка данных при инициализации
    LaunchedEffect(taskName) {
        loadArticles(taskName, viewModel, context) { loadedArticles ->
            articles = loadedArticles
            filteredArticles = loadedArticles
        }
    }

    // Обновляем список по введенному тексту или отсканированному штрих-коду
    LaunchedEffect(searchQuery, scannedBarcode) {
        filteredArticles = if (scannedBarcode.isNotEmpty()) {
            articles.filter { it.shk == scannedBarcode || it.artikul.toString() == scannedBarcode || it.shkSyrya == scannedBarcode }
        } else {
            articles.filter {
                it.nazvanieTovara?.contains(searchQuery, ignoreCase = true) == true ||
                        it.artikul.toString()?.contains(searchQuery, ignoreCase = true) == true ||
                        it.shkSyrya?.contains(searchQuery, ignoreCase = true) == true
            }
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
            label = { Text("Поиск по названию или артикулу") },
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
                        ArticleRedactorForOzonCard(
                            article = article,
                            onClicks = { onClickArticle(article) },
                                )
                    }
                }
            }
        }
    }
}

// Вспомогательная функция для загрузки статей
private fun loadArticles(
    taskName: String,
    viewModel: TaskViewModel,
    context: Context,
    onLoaded: (List<Article.Articuls>) -> Unit
) {
    viewModel.viewModelScope.launch {
        try {
            val result = viewModel.getTasksInWork(taskName, 2).articuls
            onLoaded(result)
        } catch (e: Exception) {
            Toast.makeText(context, "Ошибка загрузки: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun ArticleRedactorForOzonCard(
    article: Article.Articuls,
    onClicks: (Article.Articuls) -> Unit,
) {

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
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Название: ${article.nazvanieTovara ?: "Не указано"}",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Артикул: ${article.artikul ?: "Не указан"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ШК: ${article.shk ?: "Не указан"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Место: ${article.mesto ?: "Не указано"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Вложенность: ${article.vlozhennost ?: "Не указано"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Паллет: ${article.palletNo ?: "Не указано"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                }
        }
    }
}
