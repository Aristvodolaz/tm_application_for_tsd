package com.application.tm_application_for_tsd.screen.upakovka

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.tm_application_for_tsd.R
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.tm_application_for_tsd.screen.dialog.ExcludeArticleDialog
import com.application.tm_application_for_tsd.utils.SPHelper
import com.application.tm_application_for_tsd.viewModel.OzonViewModel
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OzonScreen(
    viewModel: OzonViewModel = hiltViewModel(),
    spHelper: SPHelper
) {
    val vneshnost by viewModel.vneshnost.collectAsState()
    val boxCount by viewModel.boxCount.collectAsState()
    val palletNumber by viewModel.palletNumber.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    var showDialog by remember { mutableStateOf(false) } // Для отображения диалога
    var showIsklDialog by remember { mutableStateOf(false) } // Для отображения диалога

    val context = LocalContext.current

    val reasons = context.resources.getStringArray(R.array.cancel_reasons).toList()

    val snackbarHostState = remember { SnackbarHostState() }

    // Обработка ошибок и успеха
    LaunchedEffect(error, successMessage) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Товарная информация", fontSize = 18.sp, color = Color.Black) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        // Оборачиваем содержимое в вертикальную прокрутку
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState()), // Добавлена прокрутка
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Отображение товарной информации
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column {
                    Text("${spHelper.getNameStuffWork()}", fontSize = 16.sp)
                    Text("Артикул: ${spHelper.getArticuleWork()} ", fontSize = 14.sp)
                    Text("ШК: ${spHelper.getShkWork()}", fontSize = 14.sp)
                }
            }

            // Поля ввода
            CustomTextField(
                label = "Вложенность",
                example = "Пример: 1",
                state = vneshnost,
                onValueChange = viewModel::onVneshnostChange
            )
            CustomTextField(
                label = "Количество коробов",
                example = "Пример: 6",
                state = boxCount,
                onValueChange = viewModel::onBoxCountChange
            )
            CustomTextField(
                label = "Номер палета",
                example = "Пример: 4",
                state = palletNumber,
                onValueChange = viewModel::onPalletNumberChange
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Кнопки
            Button(
                onClick = { viewModel.sendFinishData() },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Записать", color = Color.White, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { showDialog = true },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text("Нестандартная вложенность", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { showIsklDialog = true },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text("Исключить артикул из обработки", color = Color.White, fontSize = 16.sp, textAlign = TextAlign.Center)
            }
        }

        // Диалоговые окна
        if (showDialog) {
            NonStandardDialog(
                onDismiss = { showDialog = false },
                onConfirm = { nested, place, pallet ->
                    viewModel.saveNonStandardData(nested, place, pallet)
                    showDialog = false
                }
            )
        }

        if (showIsklDialog) {
            ExcludeArticleDialog(
                reasons = reasons,
                onDismiss = { showIsklDialog = false },
                onConfirm = { reason, comment, size ->
                    viewModel.excludeArticle(reason, comment) // todo сюда надо добавить количество
                    showIsklDialog = false
                }
            )
        }
    }
}


@Composable
fun NonStandardDialog(
    onDismiss: () -> Unit,
    onConfirm: (nested: String, place: String, pallet: String) -> Unit
) {
    var nested by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("") }
    var pallet by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Нестандартная вложенность") },
        text = {
            Column {
                CustomTextField(
                    label = "Вложенность",
                    example = "Пример: 1",
                    state = nested,
                    onValueChange = { nested = it }
                )
                CustomTextField(
                    label = "Место",
                    example = "Пример: 2",
                    state = place,
                    onValueChange = { place = it }
                )
                CustomTextField(
                    label = "Паллет",
                    example = "Пример: 3",
                    state = pallet,
                    onValueChange = { pallet = it }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(nested, place, pallet) }) {
                Text("Записать")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Отменить")
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    label: String,
    example: String,
    state: String,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth() // Исправлено, чтобы поле занимало всю ширину
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp) // Отступ между меткой и полем ввода
        )
        TextField(
            value = state,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = example,
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                unfocusedTextColor = Color.Black,
                focusedTextColor = Color.Black, // Убедитесь, что текст черного цвета
                containerColor = Color.White, // Белый фон
                focusedIndicatorColor = Color.Blue, // Цвет индикатора при фокусе
                unfocusedIndicatorColor = Color.Gray, // Цвет индикатора без фокуса
                focusedPlaceholderColor = Color.LightGray, // Цвет текста в placeholder
                unfocusedPlaceholderColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(4.dp)) // Убедитесь, что фон белый
        )
    }
}
