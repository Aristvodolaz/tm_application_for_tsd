package com.application.tm_application_for_tsd.screen.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ExcludeArticleDialog(
    reasons: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (reason: String, comment: String) -> Unit
) {
    var selectedReason by remember { mutableStateOf(reasons.firstOrNull() ?: "") }
    var comment by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Причины исключения") },
        text = {
            Column {
                Text("Выберите причину:")
                Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Button(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedReason)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        reasons.forEach { reason ->
                            DropdownMenuItem(
                                text = { Text(reason) },
                                onClick = {
                                    selectedReason = reason
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Комментарий (необязательно):")
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    placeholder = { Text("Введите комментарий...") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(selectedReason, comment)
                onDismiss()
            }) {
                Text("ОК")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
