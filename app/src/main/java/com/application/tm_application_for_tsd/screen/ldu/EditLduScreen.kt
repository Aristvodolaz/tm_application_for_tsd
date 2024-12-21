package com.application.tm_application_for_tsd.screen.ldu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.tm_application_for_tsd.network.Api
import com.application.tm_application_for_tsd.utils.SPHelper
import com.application.tm_application_for_tsd.viewModel.EditLduViewModel
import com.application.tm_application_for_tsd.viewModel.LduViewModel

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
                            ActionItem(
                                actionName = uiState.actions[index].name,
                                count = uiState.actions[index].count.toInt(),
                                onIncrement = { viewModel.incrementAction(index) },
                                onDecrement = { viewModel.decrementAction(index) }
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