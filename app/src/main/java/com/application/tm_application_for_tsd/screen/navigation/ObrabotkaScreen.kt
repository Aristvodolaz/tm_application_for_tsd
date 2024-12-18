package com.application.tm_application_for_tsd.screen.navigation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.application.tm_application_for_tsd.network.request_response.Article
import com.application.tm_application_for_tsd.viewModel.ScannerViewModel
import com.application.tm_application_for_tsd.viewModel.TaskViewModel
@Composable
fun ObraborkaScreen(
    taskName: String,
    viewModel: TaskViewModel = hiltViewModel(),
    scannerViewModel: ScannerViewModel = hiltViewModel(),
    onArticleClick: (Article.Articuls) -> Unit
) {
    var articles by remember { mutableStateOf<List<Article.Articuls>>(emptyList()) }
    var filteredArticles by remember { mutableStateOf<List<Article.Articuls>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    val scannedBarcode by scannerViewModel.barcodeData.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Загружаем данные при запуске экрана
    LaunchedEffect(taskName) {
        try {
            isLoading = true
            articles = viewModel.getTasksInWork(taskName, 0).articuls
            filteredArticles = articles
        } catch (e: Exception) {
            Toast.makeText(context, "Ошибка загрузки: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    // Фильтрация данных по штрих-коду
    LaunchedEffect(scannedBarcode) {
        if (scannedBarcode.isNotEmpty()) {
            filteredArticles = articles.filter {
                it.shk?.contains(scannedBarcode) == true || it.shkSyrya?.contains(scannedBarcode) == true
            }
        }
    }

    // Фильтрация по поисковому запросу
    LaunchedEffect(searchQuery) {
        filteredArticles = articles.filter { article ->
            article.nazvanieTovara?.contains(searchQuery, ignoreCase = true) == true ||
                    article.artikul?.toString()?.contains(searchQuery) == true ||
                    article.shk?.contains(searchQuery) == true ||
                    article.shkSyrya?.contains(searchQuery) == true
        }
    }

    // Основной UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .background(Color.White),
    ) {
        // Заголовок
        Text(
            text = taskName,
            style = MaterialTheme.typography.titleLarge,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp),
            maxLines = 1
        )

        // Поле поиска
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Поиск ") },
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
                        ArticleCard(
                            article = article,
                            onClick = { onArticleClick(article) }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ArticleCard(article: Article.Articuls, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 4.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFAFAFA)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = article.nazvanieTovara ?: "Не указано",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.padding(top = 4.dp))
            Text(
                text = "Артикул: ${article.artikul ?: "Не указан"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
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
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (!article.shkSyrya.isNullOrEmpty()) {
                Text(
                    text = "ШК сырья: ${article.shkSyrya}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}
