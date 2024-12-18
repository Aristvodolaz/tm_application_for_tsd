package com.application.tm_application_for_tsd.screen.ldu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LduScreen(
    artikul: String,
    taskName: String,
    viewModel: LduViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadLduData(artikul, taskName)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Управление LDU",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.titleLarge,
                ) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xffffffff))
            )
        },
        content = { padding ->
            when (uiState) {
                is LduViewModel.UiState.Loading -> LoadingState()
                is LduViewModel.UiState.Loaded -> {
                    val actions = (uiState as LduViewModel.UiState.Loaded).actions
                        .filter { it.count > 0 } // Отображаем только с ненулевым count

                    LazyColumn(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize()
                            .background(Color(0xffffffff)),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        contentPadding = PaddingValues(2.dp)
                    ) {
                        items(actions) { action ->
                            DisplayActionItem(
                                actionName = action.name,
                                count = action.count
                            )
                        }
                    }
                }
                is LduViewModel.UiState.Error -> ErrorState()
            }
        }
    )
}
@Composable
fun DisplayActionItem(actionName: String, count: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .background(Color(0xFFffffff)),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFffffff)
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
