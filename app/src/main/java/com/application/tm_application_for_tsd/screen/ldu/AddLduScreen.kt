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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.tm_application_for_tsd.viewModel.LduViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLduScreen(artikul: Int, taskName: String, onSaveSuccess: () -> Unit) {
    val viewModel: LduViewModel = viewModel()
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
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(16.dp)
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
                    viewModel.saveActions(artikul, taskName) { onSaveSuccess() }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLduTopBar() {
    TopAppBar(
        title = { Text("Управление LDU", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
    )
}

@Composable
fun AddLduBottomBar(onSaveClick: () -> Unit) {
    BottomAppBar {
        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text("Сохранить и продолжить", fontSize = 16.sp)
        }
    }
}

@Composable
fun ActionItem(actionName: String, count: Int, onIncrement: () -> Unit, onDecrement: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(actionName, fontSize = 18.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = onDecrement, enabled = count > 0) { Text("-") }
                Text(count.toString(), modifier = Modifier.padding(horizontal = 8.dp))
                Button(onClick = onIncrement) { Text("+") }
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
