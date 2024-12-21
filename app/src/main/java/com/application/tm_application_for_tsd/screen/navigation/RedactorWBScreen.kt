package com.application.tm_application_for_tsd.screen.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
    val uiState by redactorWBViewModel.uiState.collectAsState()

    LaunchedEffect(taskName) {
        redactorWBViewModel.getData(taskName) // Triggers data load when `taskName` changes
    }

    Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        when (uiState) {
            is RedactorWBViewModel.UiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
            }

            is RedactorWBViewModel.UiState.Error -> {
                Text(
                    text = (uiState as RedactorWBViewModel.UiState.Error).message,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
                )
            }

            is RedactorWBViewModel.UiState.Empty -> {
                Text(
                    text = "No items found for the current task.",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
                )
            }

            is RedactorWBViewModel.UiState.Success -> {
                val items = (uiState as RedactorWBViewModel.UiState.Success).items
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(items) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onClick(item) }
                                .padding(8.dp),
                            elevation = CardDefaults.elevatedCardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(text = "Артикул: ${item.artikul}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Паллет: ${item.pallet}", fontSize = 14.sp)
                                Text(text = "Короб: ${item.shk}", fontSize = 14.sp)
                                Text(text = "Вложенность: ${item.kolvo}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
