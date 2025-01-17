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
    val errorMessage by wbViewModel.errorMessage.collectAsState()
    val context = LocalContext.current
    val reasons = context.resources.getStringArray(R.array.cancel_reasons).toList()
    val snackbarHostState = remember { SnackbarHostState() }
    var showExcludeDialog by remember { mutableStateOf(false) }
    var isNavigating by remember { mutableStateOf(false) }

    LaunchedEffect(wbViewModel.successMessage.collectAsState().value) {
        wbViewModel.successMessage.value?.let { successMessage ->
            snackbarHostState.showSnackbar(successMessage)
            toScanBox()
            wbViewModel.resetsuccessMessage()
        }
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is WBViewModel.UiState.Success -> {
                if (!isNavigating) {
                    isNavigating = true
                    snackbarHostState.showSnackbar("Задание успешно закрыто!")
                    toDone()
                    wbViewModel.resetSuccessState()
                }
            }
            is WBViewModel.UiState.Error -> {
                val error = (uiState as WBViewModel.UiState.Error).message
                snackbarHostState.showSnackbar(error)
                wbViewModel.resetError()
            }
            else -> Unit
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            wbViewModel.resetError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Список коробов", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                is WBViewModel.UiState.Loaded -> {
                    val loadedState = uiState as WBViewModel.UiState.Loaded

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
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
                                    text = "Количество коробов: ${loadedState.totalCount}",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )

                                Text(
                                    text = "Количество: ${loadedState.totalCountKolvo} из ${spHelper.getVlozhFull()}",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                        }

                        items(loadedState.boxes) { box ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 2.dp),
                                elevation = CardDefaults.elevatedCardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(4.dp)) {
                                    Text("Короб: ${box.shkWps}", fontSize = 14.sp)
                                    Text("Паллет: ${box.palletNo}", fontSize = 14.sp)
                                    Text("Вложенность: ${box.kolvoTovarov}", fontSize = 14.sp)
                                }
                            }
                        }

                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { wbViewModel.checkBox() },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                ) {
                                    Text("Добавить короб", color = Color.White, fontSize = 14.sp)
                                }

                                Button(
                                    onClick = { wbViewModel.checkOrderCompletionBeforeClosing() },
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
                                    Text("Исключить артикул из обработки", color = Color.White, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
                else -> {}
            }

            if (showExcludeDialog) {
                ExcludeArticleDialog(
                    reasons = reasons,
                    onDismiss = { showExcludeDialog = false },
                    onConfirm = { reason, comment, size ->
                        wbViewModel.excludeArticle(spHelper.getId(), reason, comment, size.toInt())
                        showExcludeDialog = false
                    }
                )
            }
        }
    }
}
