package com.application.tm_application_for_tsd.screen.old

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.PopupProperties
@Composable
fun SettingsScreen() {
    var selectedWarehouse by remember { mutableStateOf("Warehouse A") }
    val warehouses = listOf("Warehouse A", "Warehouse B", "Warehouse C")
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Выбор склада",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Dropdown container
        Box(modifier = Modifier.fillMaxWidth()) {
            // Editable TextField as dropdown selector
            OutlinedTextField(
                value = selectedWarehouse,
                onValueChange = {},
                label = { Text("Склад") },
                readOnly = true, // Prevent direct typing
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                trailingIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Toggle dropdown"
                        )
                    }
                }
            )

            // Dropdown Menu positioned under the TextField
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(),
                properties = PopupProperties(focusable = true)
            ) {
                warehouses.forEach { warehouse ->
                    DropdownMenuItem(
                        text = { Text(warehouse) },
                        onClick = {
                            selectedWarehouse = warehouse
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
