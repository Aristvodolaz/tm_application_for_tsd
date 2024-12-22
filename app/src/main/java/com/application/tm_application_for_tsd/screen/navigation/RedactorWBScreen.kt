package com.application.tm_application_for_tsd.screen.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.tm_application_for_tsd.network.request_response.Article
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
        onClick ={ onClicks(article)}

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
