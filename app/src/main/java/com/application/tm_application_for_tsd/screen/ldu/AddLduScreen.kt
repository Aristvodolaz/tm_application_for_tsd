package com.application.tm_application_for_tsd.screen.ldu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.tm_application_for_tsd.viewModel.LduViewModel
import com.application.tm_application_for_tsd.viewModel.ScannerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLduScreen(artikul: String, taskName: String, onSaveSuccess: () -> Unit, viewModel: LduViewModel = hiltViewModel()){
    val uiState = viewModel.uiState.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.loadLduData(artikul, taskName)
    }

    Scaffold(
        topBar = { AddLduTopBar() },
        content = { padding ->
            when (uiState) {
                is LduViewModel.UiState.Loading -> LoadingState()
                is LduViewModel.UiState.Loaded -> {
                    LazyColumn(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        items(uiState.actions.size) { index ->
                            ActionItem(
                                actionName = uiState.actions[index].name,
                                count = uiState.actions[index].count,
                                onIncrement = { viewModel.incrementAction(index) },
                                onDecrement = { viewModel.decrementAction(index) }
                            )
                        }
                    }
                }
                is LduViewModel.UiState.Error -> ErrorState()
            }
        },
        bottomBar = {
            if (uiState is LduViewModel.UiState.Loaded) {
                AddLduBottomBar {
                    viewModel.saveActions(artikul.toInt(), taskName) { onSaveSuccess() }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLduTopBar() {
    TopAppBar(
        title = { Text("Управление LDU",
            fontSize = 16.sp,
            style = MaterialTheme.typography.titleLarge,
        ) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xffffffff))
    )
}

@Composable
fun AddLduBottomBar(onSaveClick: () -> Unit) {
    BottomAppBar(modifier = Modifier.height(48.dp)) {
        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth().padding(4.dp)
        ) {
            Text("Сохранить и продолжить", fontSize = 14.sp)
        }
    }
}

@Composable
fun ActionItem(actionName: String, count: Int, onIncrement: () -> Unit, onDecrement: () -> Unit) {
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
                modifier = Modifier.weight(1f) // Занимает все свободное пространство
            )

            // Управляющие кнопки справа
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Button(
                    onClick = onDecrement,
                    enabled = count > 0
                ) {
                    Text("-")
                }
                Text(
                    text = count.toString(),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )
                Button(onClick = onIncrement) {
                    Text("+")
                }
            }
        }
    }
}

@Composable
fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Произошла ошибка!", color = Color.Red)
    }
}
