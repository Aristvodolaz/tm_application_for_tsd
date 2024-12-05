    package com.application.tm_application_for_tsd.screen

    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.foundation.lazy.items
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.ArrowDropDown
    import androidx.compose.material3.*
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.unit.dp
    import androidx.hilt.navigation.compose.hiltViewModel
    import com.application.tm_application_for_tsd.viewModel.TaskViewModel

    import androidx.compose.ui.Modifier


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TaskScreen(onTaskSelected: () -> Unit, viewModel: TaskViewModel = hiltViewModel()) {
        val skladList = viewModel.skladList
        val selectedSklad by viewModel.selectedSklad
        val taskList by viewModel.taskList
        val isLoading by viewModel.isLoading
        val error by viewModel.error

        var expanded by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Dropdown for sklad selection
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedSklad ?: "",
                    onValueChange = { },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Выберите склад") },
                    trailingIcon = {
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                    },
                    readOnly = true
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    skladList.forEach { sklad ->
                        DropdownMenuItem(
                            onClick = {
                                viewModel.selectedSklad.value = sklad
                                viewModel.fetchTasks(sklad) // Загружаем задания для склада
                                expanded = false
                            },
                            text = { Text(sklad) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Error message
            if (error != null) {
                Text(text = error!!, color = MaterialTheme.colorScheme.error)
            }

            // Loading indicator
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            // LazyColumn for tasks
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(taskList) { task ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                // Выбор задания
                                onTaskSelected()
                            },
                        elevation = CardDefaults.elevatedCardElevation()
                    ) {
                        Text(
                            text = task,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
