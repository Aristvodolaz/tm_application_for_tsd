package com.application.tm_application_for_tsd.screen.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ApproveShkDialog(
    shk: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = { onConfirm(shk) }) {
                Text("Да")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Нет")
            }
        },
        title = { Text("Изменения ШК") },
        text = { Text("Подтверждаете ли вы перезапись ШК: $shk?") }
    )
}
