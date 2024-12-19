package com.application.tm_application_for_tsd.screen.upakovka.wb

import android.util.Log
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

@Composable
fun WBBoxScreen(
    scanViewModel: ScannerViewModel = hiltViewModel(),
    spHelper: SPHelper,
    toWriteVlozhennost: () -> Unit
) {
    val scanInput by scanViewModel.barcodeData.collectAsStateWithLifecycle()

    var shouldNavigate by remember { mutableStateOf(false) }

    LaunchedEffect(scanInput) {
        Log.d("WBBoxScreen", "LaunchedEffect triggered with scanInput: $scanInput")
        if (scanInput.isNotEmpty()) {
            spHelper.setSHKBox(scanInput)
            Log.d("WBBoxScreen", "Set SHKBox with scanInput: $scanInput")
            shouldNavigate = true
            scanViewModel.clearBarcode() // Сбрасываем после обработки
        }
    }

    if (shouldNavigate) {
        shouldNavigate = false
        Log.d("WBBoxScreen", "Navigating toWriteVlozhennost")
        toWriteVlozhennost() // Выполняем навигацию
    }


    // Отображение текста в центре экрана
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Пожалуйста, отсканируйте короб",
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
