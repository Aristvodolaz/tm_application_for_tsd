package com.application.tm_application_for_tsd.screen.edit

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
fun ScanPalletScreen(wbViewModel: WBViewModel = hiltViewModel(),
                     scanViewModel: ScannerViewModel = hiltViewModel(),
                     spHelper: SPHelper,
                     toDone: () -> Unit
) {
    val scanInput by scanViewModel.barcodeData.collectAsStateWithLifecycle()
    val uiState by wbViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(scanInput) {
        if (scanInput.isNotEmpty()) {
            spHelper.setSHKPallet(scanInput)
            spHelper.getSHKBox()?.let { wbViewModel.updateData(spHelper.getId(), spHelper.getVlozh(), scanInput) }
            scanViewModel.clearBarcode() // Сброс после обработки
        }
    }

    // Переход на следующий экран при успешной отправке
    LaunchedEffect(uiState) {
        if (uiState is WBViewModel.UiState.Success) {
            wbViewModel.resetSuccessState() // Сбрасываем состояние успеха
            toDone() // Переход на следующий экран
        }
    }

    // Основной UI
    when (uiState) {
        is WBViewModel.UiState.Loading -> {
            // Отображение индикатора загрузки
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }
        is WBViewModel.UiState.Error -> {
            // Отображение ошибки
            val error = (uiState as WBViewModel.UiState.Error).message
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
        else -> {
            // Текстовое сообщение при ожидании сканирования
            Text(
                text = "Пожалуйста, отсканируйте паллет",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }
    }
}
