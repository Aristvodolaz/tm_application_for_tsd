package com.application.tm_application_for_tsd.screen.obrabotka

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.tm_application_for_tsd.utils.SPHelper
import com.application.tm_application_for_tsd.viewModel.InfoArticleViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoArticleScreen(
    viewModel: InfoArticleViewModel = hiltViewModel(),
    spHelper: SPHelper
) {
    val state by viewModel.state.collectAsState()
    var showExpirationFields by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Информация о товаре") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Полная информация об артикуле
                        Text(
                            text = "Артикул: ${spHelper.getArticuleWork() ?: "Не найден"}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "ШК: ${spHelper.getShkWork() ?: "Не указан"}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        state.successMessage?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        state.errorMessage?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        if (!showExpirationFields) {
                            Button(
                                onClick = {
                                    val article = spHelper.getArticuleWork()
                                    if (!article.isNullOrEmpty()) {
                                        viewModel.changeStatusTask(article, 1)
                                        showExpirationFields = true
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Взять в работу")
                            }
                        } else {
                            // Поля для проверки срока годности
                            OutlinedTextField(
                                value = "",
                                onValueChange = {},
                                label = { Text("Начальная дата") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = "",
                                onValueChange = {},
                                label = { Text("Срок в месяцах") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = "",
                                onValueChange = {},
                                label = { Text("Конечная дата") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = {
                                    // Логика проверки срока годности
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Text("Проверить срок годности")
                            }

                            Button(
                                onClick = {
                                    // Логика удаления из обработки
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Убрать из обработки")
                            }
                        }
                    }
                }
            }
        }
    )
}
