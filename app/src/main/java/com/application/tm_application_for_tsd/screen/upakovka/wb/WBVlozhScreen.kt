package com.application.tm_application_for_tsd.screen.upakovka.wb

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
import com.application.tm_application_for_tsd.utils.SPHelper

@Composable
fun WBVlozhScreen(
    spHelper: SPHelper,
    toScanPallet: () -> Unit
) {
    var vlozhennost by remember { mutableStateOf("") }
    var isInputValid by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Заголовок
        Text(
            text = "Введите вложенность",
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Поле для ввода вложенности
        OutlinedTextField(
            value = vlozhennost,
            onValueChange = {
                vlozhennost = it
                isInputValid = it.toIntOrNull() != null && it.toInt() > 0
            },
            label = { Text("Вложенность") },
            placeholder = { Text("Например: 5") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = !isInputValid,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        if (!isInputValid) {
            Text(
                text = "Введите положительное число",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .align(Alignment.Start)
                    .padding(start = 32.dp) // Уступ для выравнивания с полем
            )
        }

        // Примечание под полем
        Text(
            text = "Укажите, сколько предметов содержится в коробе.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Кнопка "Продолжить"
        Button(
            onClick = {
                spHelper.setVlozh(vlozhennost.toInt())
                toScanPallet()
            },
            enabled = vlozhennost.isNotEmpty() && isInputValid,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text("Продолжить", fontSize = 16.sp)
        }
    }
}
