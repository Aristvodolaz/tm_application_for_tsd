package com.application.tm_application_for_tsd.screen.navigation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.application.tm_application_for_tsd.network.request_response.Article
import com.application.tm_application_for_tsd.viewModel.ScannerViewModel
import com.application.tm_application_for_tsd.viewModel.TaskViewModel

@Composable
fun UpakovkaScreen(
    taskName: String,
    viewModel: TaskViewModel = hiltViewModel(),
    scannerViewModel: ScannerViewModel = hiltViewModel()
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
            articles = viewModel.getTasksInWork(taskName, 3).articuls
            filteredArticles = articles // Изначально отображаем все записи
        } catch (e: Exception) {
            Toast.makeText(context, "Ошибка загрузки: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    // Фильтрация при изменении штрих-кода
    LaunchedEffect(scannedBarcode) {
        if (scannedBarcode.isNotEmpty()) {
            filteredArticles = articles.filter {
                it.shk?.contains(scannedBarcode) == true || it.shkSyrya?.contains(scannedBarcode) == true
            }
        }
    }

    // Фильтрация при изменении поискового запроса
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

    // UI отображение
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .background(Color(0xffffffff)),
    ) {
        Text(
            text = taskName,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 12.dp),
            fontSize = 14.sp,
            maxLines = 1
        )

        // Поле ввода для поиска
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
                        ArticleCard(article = article, {})
                    }
                }
            }
        }
    }
}