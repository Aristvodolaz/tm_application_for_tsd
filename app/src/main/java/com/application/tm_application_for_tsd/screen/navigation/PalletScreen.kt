package com.application.tm_application_for_tsd.screen.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.application.tm_application_for_tsd.network.request_response.Article
import com.application.tm_application_for_tsd.network.request_response.Pallets
import com.application.tm_application_for_tsd.viewModel.PalletViewModel
import com.application.tm_application_for_tsd.viewModel.ScannerViewModel

@Composable
fun PalletScreen(
    taskName: String,
    viewModel: PalletViewModel = hiltViewModel(),
    scannerViewModel: ScannerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val scannedBarcode by scannerViewModel.barcodeData.collectAsStateWithLifecycle()
    var filteredPallet by remember { mutableStateOf<List<Pallets>>(emptyList()) }
    var pallets by remember { mutableStateOf<List<Pallets>>(emptyList()) }

    // Загружаем паллеты при открытии экрана
    LaunchedEffect(taskName) {
        viewModel.loadPallets(taskName)
    }
    // Фильтрация при изменении штрих-кода
    LaunchedEffect(scannedBarcode) {
        if (scannedBarcode.isNotEmpty()) {
            filteredPallet = pallets.filter {
                it.pallet_no.contains(scannedBarcode)
            }
        }
    }

    // Фильтрация при изменении поискового запроса
    LaunchedEffect(searchQuery) {
        filteredPallet = pallets.filter {
            it.pallet_no.contains(searchQuery)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text(
            text = "$taskName",
            fontSize = 16.sp,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp),
            maxLines = 1
        )

        when {
            state.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            state.errorMessage != null -> {
                Text(
                    text = state.errorMessage ?: "Ошибка",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }
            state.selectedPallet == null -> {
                // Отображение списка паллетов
                LazyColumn {
                    items(state.pallets.size) { index ->
                        val pallet = state.pallets[index]
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    viewModel.loadArticles(taskName, pallet.pallet_no)
                                },
                            elevation = CardDefaults.elevatedCardElevation(),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Text(
                                text = "Паллет: ${pallet.pallet_no}",
                                modifier = Modifier.padding(top = 8.dp, start = 8.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Количество: ${pallet.total}",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(bottom = 8.dp, start = 8.dp),

                                )
                        }
                    }
                }
            }
            else -> {
                // Отображение статей для выбранного паллета
                LazyColumn {
                    items(state.articles.size) { index ->
                        val article = state.articles[index]
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.elevatedCardElevation(),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            if (taskName.contains("WB", ignoreCase = true)) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(text = "Артикул: ${article.articul}")
                                    Text(text = "Вложенность: ${article.kolvo}")
                                    Text(text = "Места: 1")
                                }
                            } else {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = article.nazvanieTovara ?: "Не указано",
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                        fontSize = 16.sp,
                                        maxLines = 2,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(text = "Артикул: ${article.articul}",
                                            style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray)
                                    Text(text = "Места: ${article.mesto}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray)
                                    Text(text = "Вложенность: ${article.vlozhennost}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray)
                                }
                            }
                        }
                    }
                }

                // Общее количество мест
                Text(
                    text = "Общее количество мест: ${state.totalPlaces}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )

                // Кнопка назад
                Button(
                    onClick = { viewModel.deselectPallet() },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Назад к списку паллетов")
                }
            }
        }
    }
}
