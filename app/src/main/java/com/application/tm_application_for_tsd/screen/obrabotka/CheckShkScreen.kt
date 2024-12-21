package com.application.tm_application_for_tsd.screen.obrabotka

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.application.tm_application_for_tsd.utils.SPHelper
import com.application.tm_application_for_tsd.viewModel.CheckShkState
import com.application.tm_application_for_tsd.viewModel.CheckShkViewModel
import com.application.tm_application_for_tsd.viewModel.ScannerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckShkScreen(
    scanViewModel: ScannerViewModel = hiltViewModel(),
    checkShkViewModel: CheckShkViewModel = hiltViewModel(),
    spHelper: SPHelper,
    onNavigateToNext: () -> Unit
) {
    val state by checkShkViewModel.state.collectAsStateWithLifecycle()
    val scanInput by scanViewModel.barcodeData.collectAsStateWithLifecycle()
    val error by scanViewModel.error.collectAsStateWithLifecycle()

    // Effect to handle barcode scanning
    LaunchedEffect(scanInput) {
        if (scanInput.isNotEmpty()) {
            spHelper.setShkWork(scanInput)
            Log.d("CheckShkScreen", "Barcode scanned: $scanInput")
            checkShkViewModel.findInExcel(scanInput, spHelper.getTaskName().orEmpty())
            scanViewModel.clearBarcode() // Сброс после обработки
        }
    }

    // Trigger navigation on success
    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null) {
            onNavigateToNext()
        }
    }

    if (state.showRewriteDialog) {
        spHelper.getShkWork()?.let {
            RewriteDialog(
                shk = it,
                onConfirm = {
                    spHelper.getShkWork()?.let { checkShkViewModel.confirmRewriteShk(it) }
                },
                onDismiss = {
                    checkShkViewModel.cancelRewriteShk()
                }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Сканирование ШК товара", fontSize = 16.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            MainContent(state = state)
        }
    }
}

@Composable
fun MainContent(state: CheckShkState) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Отсканируйте ШК товара",
            fontSize = 20.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when {
            state.isLoading -> CircularProgressIndicator()
            state.successMessage != null -> {
                Text(
                    text = state.successMessage.orEmpty(),
                    color = Color.Green,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            state.errorMessage != null -> {
                Text(
                    text = state.errorMessage.orEmpty(),
                    color = Color.Red,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun RewriteDialog(shk: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Перезаписать ШК") },
        text = { Text("ШК не найден. Перезаписать ШК  $shk в базе данных?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Да")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
