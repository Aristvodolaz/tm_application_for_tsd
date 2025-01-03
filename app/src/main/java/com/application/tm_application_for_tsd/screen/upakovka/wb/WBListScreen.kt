package com.application.tm_application_for_tsd.screen.upakovka.wb

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.tm_application_for_tsd.R
import com.application.tm_application_for_tsd.screen.dialog.ExcludeArticleDialog
import com.application.tm_application_for_tsd.utils.SPHelper
import com.application.tm_application_for_tsd.viewModel.WBViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WBListScreen(
    wbViewModel: WBViewModel = hiltViewModel(),
    spHelper: SPHelper,
    toScanBox: () -> Unit,
    toDone: () -> Unit
) {
    val uiState by wbViewModel.uiState.collectAsState()
    var showExcludeDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val reasons = context.resources.getStringArray(R.array.cancel_reasons).toList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Список коробов", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is WBViewModel.UiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is WBViewModel.UiState.Error -> {
                    Text(
                        text = (uiState as WBViewModel.UiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is WBViewModel.UiState.Loaded -> {
                    val loadedState = uiState as WBViewModel.UiState.Loaded

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
                    ) {
                        // Название товара и количество
                        item {
                            Column {
                                Text(
                                    text = loadedState.productName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Text(
                                    text = "Количество штук: ${loadedState.totalCount}",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                            }
                        }

                        // Список коробов
                        items(loadedState.boxes) { box ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 2.dp),
                                elevation = CardDefaults.elevatedCardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(4.dp)) {
                                    Text(
                                        text = "Короб: ${box.shkWps}",
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Паллет: ${box.palletNo}",
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Вложенность: ${box.kolvoTovarov}",
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        // Кнопки управления
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { toScanBox() },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                ) {
                                    Text("Добавить короб", color = Color.White, fontSize = 14.sp)
                                }

                                Button(
                                    onClick = {
                                        wbViewModel.closeTask()
                                        toDone()
                                              },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                                ) {
                                    Text("Закрыть задание", color = Color.White, fontSize = 14.sp)
                                }

                                Button(
                                    onClick = { showExcludeDialog = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                ) {
                                    Text("Исключить артикул из обработки", color = Color.White, fontSize = 14.sp, textAlign = TextAlign.Center)
                                }
                            }
                        }
                    }
                }

                WBViewModel.UiState.Success -> {
                    // Успешное завершение действия
                }
            }

            if (showExcludeDialog) {
                ExcludeArticleDialog(
                    reasons = reasons,
                    onDismiss = { showExcludeDialog = false },
                    onConfirm = { reason, comment, size ->
                        wbViewModel.excludeArticle(spHelper.getId(),reason, comment, size.toInt()) // todo сюда надо добавить количество
                        showExcludeDialog = false
                    }
                )
            }
        }
    }
}
