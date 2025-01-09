package com.application.tm_application_for_tsd.screen.upakovka.wb

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.application.tm_application_for_tsd.utils.SPHelper
import com.application.tm_application_for_tsd.viewModel.ScannerViewModel
import com.application.tm_application_for_tsd.viewModel.WBViewModel
@Composable
fun WBBoxScreen(
    scanViewModel: ScannerViewModel = hiltViewModel(),
    spHelper: SPHelper,
    viewModel: WBViewModel = hiltViewModel(),
    toWriteVlozhennost: () -> Unit
) {
    val scanInput by scanViewModel.barcodeData.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var shouldNavigate by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() } // Состояние Snackbar

    // Обработчик штрих-кода
    LaunchedEffect(scanInput) {
        Log.d("WBBoxScreen", "LaunchedEffect triggered with scanInput: $scanInput")
        if (scanInput.isNotEmpty()) {
            spHelper.setSHKBox(scanInput)
            viewModel.checkWps(scanInput)
            scanViewModel.clearBarcode() // Сбрасываем после обработки
        }
    }

    // Обработчик состояния UI
    LaunchedEffect(uiState) {
        when (uiState) {
            is WBViewModel.UiState.Error -> {
                val message = (uiState as WBViewModel.UiState.Error).message
                snackbarHostState.showSnackbar(message) // Показываем Snackbar с сообщением об ошибке
            }
            is WBViewModel.UiState.Success -> {
                shouldNavigate = true
            }
            else -> Unit
        }
    }

    // Навигация
    if (shouldNavigate) {
        shouldNavigate = false
        Log.d("WBBoxScreen", "Navigating toWriteVlozhennost")
        toWriteVlozhennost() // Выполняем навигацию
    }

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Пожалуйста, отсканируйте короб",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // SnackbarHost для отображения Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter) // Позиционируем внизу экрана
        )
    }
}
