package com.application.tm_application_for_tsd.screen.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.tm_application_for_tsd.network.request_response.WBItem
import com.application.tm_application_for_tsd.utils.SPHelper
import com.application.tm_application_for_tsd.viewModel.RedactorWBViewModel
@Composable
fun RedactorWBScreen(
    taskName: String,
    redactorWBViewModel: RedactorWBViewModel = hiltViewModel(),
    onClick: (WBItem) -> Unit,
    spHelper: SPHelper
) {
    val uiState = redactorWBViewModel.uiState.collectAsState()

    // Загружаем данные при первом отображении экрана
    var isDataLoaded by remember { mutableStateOf(false) }
    LaunchedEffect(taskName) {
        if (!isDataLoaded) {
            redactorWBViewModel.getData(taskName)
            isDataLoaded = true
        }
    }

    when (val state = uiState.value) {
        is RedactorWBViewModel.UiState.Loading -> {
            // Отображение индикатора загрузки
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is RedactorWBViewModel.UiState.Error -> {
            // Отображение сообщения об ошибке
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(text = "Ошибка: ${state.message}", fontSize = 16.sp)
            }
        }

        is RedactorWBViewModel.UiState.Success -> {
            // Отображение списка элементов
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.items) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = CardDefaults.elevatedCardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                                .clickable { onClick(item) }
                        ) {
                            Text(text = "Артикул: ${item.artikul}", fontSize = 16.sp)
                            Text(text = "Паллет: ${item.pallet}", fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
