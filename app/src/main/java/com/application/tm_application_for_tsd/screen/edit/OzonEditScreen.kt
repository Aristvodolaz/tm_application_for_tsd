package com.application.tm_application_for_tsd.screen.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.tm_application_for_tsd.utils.SPHelper
import com.application.tm_application_for_tsd.viewModel.OzonEditViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OzonEditScreen(
    ozonEditViewModel: OzonEditViewModel = hiltViewModel(),
    spHelper: SPHelper,
    onDone: () -> Unit
) {
    var pallet by remember { mutableStateOf("") }
    var nestedness by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Редактировать данные", fontSize = 20.sp) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Поле ввода для паллета
                OutlinedTextField(
                    value = pallet,
                    onValueChange = { pallet = it },
                    label = { Text("Паллет") },
                    placeholder = { Text("Введите номер паллета") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Поле ввода для вложенности
                OutlinedTextField(
                    value = nestedness,
                    onValueChange = { nestedness = it },
                    label = { Text("Вложенность") },
                    placeholder = { Text("Введите вложенность") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Поле ввода для места
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Место") },
                    placeholder = { Text("Введите место") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Кнопка изменения
                Button(
                    onClick = {
                        ozonEditViewModel.editItem(spHelper.getId(), vlozh = nestedness.toInt(), mesto = location.toInt(), pallet = pallet.toInt())
                        onDone() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Изменить")
                }
            }
        }
    )
}