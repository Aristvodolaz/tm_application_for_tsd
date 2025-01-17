package com.application.tm_application_for_tsd.screen.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun ExcludeArticleDialog(
    reasons: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (reason: String, comment: String, quantity: String) -> Unit
) {
    var selectedReason by remember { mutableStateOf(reasons.firstOrNull() ?: "") }
    var comment by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // Ошибки для каждого поля
    var reasonError by remember { mutableStateOf(false) }
    var quantityError by remember { mutableStateOf(false) }
    var commentError by remember { mutableStateOf(false) }

    // Функция для проверки всех полей
    fun validateFields(): Boolean {
        // Проверка всех полей
        val isReasonValid = selectedReason.isNotEmpty()
        val isQuantityValid = quantity.isNotEmpty() && quantity.toIntOrNull() != null && quantity.toInt() > 0
        val isCommentValid = comment.isNotEmpty()

        reasonError = !isReasonValid
        quantityError = !isQuantityValid
        commentError = !isCommentValid

        return isReasonValid && isQuantityValid && isCommentValid
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Причины исключения") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Поле для выбора причины исключения
                ReasonSelector(
                    reasons = reasons,
                    selectedReason = selectedReason,
                    onReasonChange = { selectedReason = it },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    isError = reasonError
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Поле для ввода количества
                QuantityInput(
                    quantity = quantity,
                    onQuantityChange = { quantity = it },
                    isError = quantityError
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Поле для комментария
                CommentInput(
                    comment = comment,
                    onCommentChange = { comment = it },
                    isError = commentError
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (validateFields()) {
                        onConfirm(selectedReason, comment, quantity)
                        onDismiss()
                    }
                }
            ) {
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

@Composable
fun ReasonSelector(
    reasons: List<String>,
    selectedReason: String,
    onReasonChange: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    isError: Boolean
) {
    Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        OutlinedTextField(
            value = selectedReason,
            onValueChange = {},
            readOnly = true,
            label = { Text("Причина") },
            trailingIcon = {
                IconButton(onClick = { onExpandedChange(!expanded) }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            },
            isError = isError,
            modifier = Modifier.fillMaxWidth()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            reasons.forEach { reason ->
                DropdownMenuItem(
                    text = { Text(reason) },
                    onClick = {
                        onReasonChange(reason)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
    if (isError) {
        Text(
            text = "Выберите причину!",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun QuantityInput(
    quantity: String,
    onQuantityChange: (String) -> Unit,
    isError: Boolean
) {
    OutlinedTextField(
        value = quantity,
        onValueChange = onQuantityChange,
        label = { Text("Количество (обязательно)") },
        placeholder = { Text("Введите количество...") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        isError = isError,
        modifier = Modifier.fillMaxWidth()
    )
    if (isError) {
        Text(
            text = "Введите количество (целое число больше 0)!",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun CommentInput(
    comment: String,
    onCommentChange: (String) -> Unit,
    isError: Boolean
) {
    OutlinedTextField(
        value = comment,
        onValueChange = onCommentChange,
        label = { Text("Комментарий (необязательно)") },
        placeholder = { Text("Введите комментарий...") },
        isError = isError,
        modifier = Modifier.fillMaxWidth()
    )
    if (isError) {
        Text(
            text = "Комментарий не может быть пустым!",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
