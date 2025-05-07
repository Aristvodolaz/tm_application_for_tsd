package com.application.tm_application_for_tsd.screen.ldu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.tm_application_for_tsd.viewModel.EditLduViewModel

@Composable
fun EditLduScreen (
    id: Long,
    viewModel: EditLduViewModel = hiltViewModel(),
    toDone: () -> Unit
){
    val uiState = viewModel.uiState.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.loadLduData(id)
    }

    Scaffold(
        topBar = { AddLduTopBar() },
        content = { padding ->
            when (uiState) {
                is EditLduViewModel.UiState.Loading -> LoadingState()
                is EditLduViewModel.UiState.Loaded -> {
                    LazyColumn(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        items(uiState.actions.size) { index ->
                            EditActionItem(
                                actionName = uiState.actions[index].name,
                                count = uiState.actions[index].count,
                                onIncrement = { viewModel.incrementAction(index) },
                                onDecrement = { viewModel.decrementAction(index) },
                                isStringValue = uiState.actions[index].isStringValue
                            )
                        }
                    }
                }
                is EditLduViewModel.UiState.Error -> ErrorState()
            }
        },
        bottomBar = {
            if (uiState is EditLduViewModel.UiState.Loaded) {
                AddLduBottomBar {
                    viewModel.saveActions(id) { toDone() }
                }
            }
        }
    )
}

@Composable
fun EditActionItem(actionName: String, count: String, onIncrement: () -> Unit, onDecrement: () -> Unit, isStringValue: Boolean = false) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Название действия слева
            Text(
                text = actionName,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )

            if (!isStringValue) {
                // Управляющие кнопки справа для числовых значений
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Button(
                        onClick = onDecrement,
                        enabled = count.toIntOrNull() ?: 0 > 0
                    ) {
                        Text("-")
                    }
                    Text(
                        text = count,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                    Button(onClick = onIncrement) {
                        Text("+")
                    }
                }
            } else {
                // Просто текст для строковых значений
                Text(
                    text = count,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}