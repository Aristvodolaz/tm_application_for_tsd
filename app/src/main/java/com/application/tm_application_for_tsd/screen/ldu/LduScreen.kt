package com.application.tm_application_for_tsd.screen.ldu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.tm_application_for_tsd.viewModel.LduViewModel

@Composable
fun LduScreen(
    artikul: String,
    taskName: String,
    viewModel: LduViewModel = hiltViewModel(),
    toNextScreen: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Загружаем данные при первом отображении экрана
    LaunchedEffect(Unit) {
        viewModel.loadLduData(artikul, taskName)
    }

    Scaffold(
        topBar = { LduTopBar() },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                when (uiState) {
                    is LduViewModel.UiState.Loading -> {
                        LoadingState(
                            modifier = Modifier
                                .weight(1f) // Занимает доступное пространство
                                .fillMaxWidth()
                        )
                    }
                    is LduViewModel.UiState.Loaded -> {
                        val actions = (uiState as LduViewModel.UiState.Loaded).actions
                            .filter { it.count.toInt() > 0 } // Отображаем только элементы с ненулевым количеством

                        if (actions.isNotEmpty()) {
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f) // Занимает оставшееся пространство
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                contentPadding = PaddingValues(8.dp)
                            ) {
                                items(actions) { action ->
                                    DisplayActionItem(
                                        actionName = action.name,
                                        count = action.count.toInt()
                                    )
                                }
                            }
                        } else {
                            // Если действий нет, показываем сообщение
                            EmptyStateMessage(
                                modifier = Modifier
                                    .weight(1f) // Занимает оставшееся пространство
                                    .fillMaxWidth()
                            )
                        }
                    }
                    is LduViewModel.UiState.Error -> {
                        ErrorState(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        )
                    }
                }

                // Кнопка для перехода на следующий экран
                Button(
                    onClick = toNextScreen,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .height(48.dp)
                ) {
                    Text("Продолжить")
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LduTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Управление LDU",
                fontSize = 16.sp,
                style = MaterialTheme.typography.titleLarge
            )
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun DisplayActionItem(actionName: String, count: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = actionName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = count.toString(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}



@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun EmptyStateMessage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Нет доступных действий для отображения.",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun ErrorState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Произошла ошибка при загрузке данных.",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.error
        )
    }
}