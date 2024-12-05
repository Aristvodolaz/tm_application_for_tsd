package com.application.tm_application_for_tsd.screen.List

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// Task List Screen
@Composable
fun TaskListScreen(
    taskList: List<String>,
    onTaskSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Task List",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(taskList) { task ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 4.dp,
                    onClick = { onTaskSelected(task) }
                ) {
                    Text(
                        text = task,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp),
                        maxLines = 1
                    )
                }
            }
        }
    }
}


// Navigation Graph
@Composable
fun NavigationGraph(
    navController: NavHostController,
    taskList: List<String>,
    palletMap: Map<String, List<String>>,
    boxMap: Map<String, List<String>>
) {
    NavHost(navController, startDestination = "taskList") {
        composable("taskList") {
            TaskListScreen(taskList) { selectedTask ->
                navController.navigate("palletList/$selectedTask")
            }
        }
        composable("palletList/{taskName}") { backStackEntry ->
            val taskName = backStackEntry.arguments?.getString("taskName") ?: return@composable
            val pallets = palletMap[taskName] ?: emptyList()
            PalletListScreen(taskName, pallets) { selectedPallet ->
                navController.navigate("boxList/$selectedPallet")
            }
        }
        composable("boxList/{palletNumber}") { backStackEntry ->
            val palletNumber = backStackEntry.arguments?.getString("palletNumber") ?: return@composable
            val boxes = boxMap[palletNumber] ?: emptyList()
            BoxListScreen(palletNumber, boxes)
        }
    }
}

// Sample Usage in Main App
@Composable
fun AppWithTaskNavigation() {
    val navController = rememberNavController()
    val taskList = listOf("Task 1", "Task 2", "Task 3")
    val palletMap = mapOf(
        "Task 1" to listOf("Pallet A1", "Pallet A2"),
        "Task 2" to listOf("Pallet B1", "Pallet B2"),
        "Task 3" to listOf("Pallet C1", "Pallet C2")
    )
    val boxMap = mapOf(
        "Pallet A1" to listOf("Box A1-1", "Box A1-2"),
        "Pallet A2" to listOf("Box A2-1", "Box A2-2"),
        "Pallet B1" to listOf("Box B1-1", "Box B1-2")
    )

    Scaffold {
        Box(modifier = Modifier.padding(it)) {
            NavigationGraph(navController, taskList, palletMap, boxMap)
        }
    }
}
