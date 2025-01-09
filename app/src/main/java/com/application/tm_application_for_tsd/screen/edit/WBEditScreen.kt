package com.application.tm_application_for_tsd.screen.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.tm_application_for_tsd.utils.SPHelper
import com.application.tm_application_for_tsd.viewModel.WBViewModel

@Composable
fun WBEditScreen (
    spHelper: SPHelper,
    toScanPallet: () -> Unit,
    wbViewModel: WBViewModel = hiltViewModel(),
    toDone: () -> Unit
) {
    var vlozhennost by remember { mutableStateOf(spHelper.getSize().toString()) }
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
                spHelper.getSHKPallet()?.let {
                    wbViewModel.updateData(spHelper.getId(), vlozh = vlozhennost.toInt(),
                        it
                    )
                }
                toDone()
            },
            enabled = vlozhennost.isNotEmpty() && isInputValid,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text("Cохранить", fontSize = 16.sp)
        }


        Button(
            onClick = {
                spHelper.setVlozh(vlozhennost.toInt())
                toScanPallet()
            },
            enabled = vlozhennost.isNotEmpty() && isInputValid,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(Color.Gray)
        ) {
            Text("Изменить паллет", fontSize = 16.sp)
        }
    }
}
