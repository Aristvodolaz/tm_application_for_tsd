package com.application.tm_application_for_tsd.screen.obrabotka

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.tm_application_for_tsd.R
import com.application.tm_application_for_tsd.network.request_response.Article
import com.application.tm_application_for_tsd.screen.dialog.ExcludeArticleDialog
import com.application.tm_application_for_tsd.screen.upakovka.OzonScreen
import com.application.tm_application_for_tsd.utils.SPHelper
import com.application.tm_application_for_tsd.viewModel.InfoArticleViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InfoArticleScreen(
    viewModel: InfoArticleViewModel = hiltViewModel(),
    spHelper: SPHelper,
    article: Article.Articuls,
    onNavigateToNext: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val navigateToNext by viewModel.navigateToNextScreen.collectAsState()
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var durationInMonths by remember { mutableStateOf("") }
    val context = LocalContext.current
    var showExpirationButton by remember { mutableStateOf(article.status == 1) }
    var showIsklDialog by remember { mutableStateOf(false) } // Для отображения диалога
    val reasons = context.resources.getStringArray(R.array.cancel_reasons).toList()
    var showExpirationDialog by remember { mutableStateOf(false) }
    var expirationPercentage by remember { mutableStateOf(0.0) }
    // Обработка навигации
    LaunchedEffect(navigateToNext) {
        if (navigateToNext) {
            onNavigateToNext()
            viewModel.resetNavigation() // Сбросить флаг навигации
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Информация о товаре", fontSize = 16.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            Column(modifier = Modifier.padding(8.dp).fillMaxSize()) {
                Text("${article.nazvanieTovara}", fontSize = 16.sp, maxLines = 2)
                Text("Артикул: ${article.artikul}", fontSize = 14.sp)
                Text("ШК: ${article.shk}", fontSize = 14.sp)
                Text("Количество: ${article.itogZakaz}", fontSize = 14.sp)

                Spacer(modifier = Modifier.height(16.dp))

                // Отображение кнопок в зависимости от статуса
                if (!showExpirationButton) {
                    Button(
                        onClick = {
                            viewModel.changeStatusTask(article.artikul.toString(), 1)
                            showExpirationButton = true
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    ) {
                        Text("Взять в работу", fontSize = 16.sp)
                    }
                } else {
                    ExpirationDateFields(
                        startDate = startDate,
                        onStartDateChange = { startDate = it },
                        durationInMonths = durationInMonths,
                        onDurationChange = { durationInMonths = it },
                        endDate = endDate,
                        onEndDateChange = { endDate = it },
                        onCheckDates = {
                            if (isValidDateFormat(startDate) && (isValidDateFormat(endDate) || durationInMonths.isNotEmpty())) {
                                val result = validateAndCalculateExpiration(startDate, endDate, durationInMonths)
                                result?.let { (percentage, calculatedEndDate) ->
                                    expirationPercentage = percentage
                                    if (percentage < 75) {
                                        showExpirationDialog = true
                                    } else {
                                        viewModel.addExpirationData(
                                            persent = "%.2f".format(percentage),
                                            endDate = calculatedEndDate
                                        )
                                        Toast.makeText(context, "Осталось: ${"%.2f".format(percentage)}%", Toast.LENGTH_SHORT).show()
                                    }
                                } ?: Toast.makeText(context, "Некорректные данные", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Введите даты в формате dd.MM.yyyy", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }

                Button(
                    onClick = { showIsklDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text("Исключить артикул из обработки", color = Color.White, fontSize = 14.sp, textAlign = TextAlign.Center)
                }

                if (showIsklDialog) {
                    ExcludeArticleDialog(
                        reasons = reasons,
                        onDismiss = { showIsklDialog = false },
                        onConfirm = { reason, comment, size ->
                            viewModel.excludeArticle(reason, comment) // todo сюда надо добавить количество
                            showIsklDialog = false
                        }
                    )
                }

                if (showExpirationDialog) {
                    ShowExpirationDialog(
                        percentagePassed = expirationPercentage,
                        onConfirm = {
                            showExpirationDialog = false
                            Toast.makeText(context, "Работа с товаром продолжена", Toast.LENGTH_SHORT).show()
                        },
                        onCancel = {
                            showExpirationDialog = false
                            Toast.makeText(context, "Работа с товаром прекращена", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                state.successMessage?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }

                state.errorMessage?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun validateAndCalculateExpiration(startDate: String, endDate: String, durationInMonths: String): Pair<Double, String>? {
    return try {
        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val start = LocalDate.parse(startDate, dateFormatter)

        val end = when {
            endDate.isNotEmpty() -> LocalDate.parse(endDate, dateFormatter)
            durationInMonths.isNotEmpty() -> start.plusMonths(durationInMonths.toLong())
            else -> return null
        }

        val today = LocalDate.now()

        // Проверка на корректность дат
        if (end.isBefore(start)) {
            throw IllegalArgumentException("Конечная дата не может быть раньше начальной")
        }

        // Расчёт общих и оставшихся дней
        val totalDays = ChronoUnit.DAYS.between(start, end).toDouble()
        val remainingDays = ChronoUnit.DAYS.between(today, end).toDouble().coerceAtLeast(0.0)

        // Расчёт процента
        val percentage = if (totalDays > 0) (remainingDays / totalDays * 100) else 0.0

        Pair(percentage, end.format(dateFormatter))
    } catch (e: Exception) {
        null // Обработка ошибок
    }
}
@Composable
fun ShowExpirationDialog(
    percentagePassed: Double,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val per = 100 - percentagePassed
    if (percentagePassed < 75) {
        AlertDialog(
            onDismissRequest = onCancel,
            title = { Text("Внимание") },
            text = { Text("Срок использования товара $per%, что превышает 75%. Продолжить?") },
            confirmButton = {
                Button(onClick = onConfirm) {
                    Text("Да")
                }
            },
            dismissButton = {
                Button(onClick = onCancel) {
                    Text("Нет")
                }
            }
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun isValidDateFormat(date: String): Boolean {
    return try {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        LocalDate.parse(date, formatter)
        true
    } catch (e: DateTimeParseException) {
        false
    }
}

@Composable
fun ExpirationDateFields(
    startDate: String,
    onStartDateChange: (String) -> Unit,
    durationInMonths: String,
    onDurationChange: (String) -> Unit,
    endDate: String,
    onEndDateChange: (String) -> Unit,
    onCheckDates: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        OutlinedTextField(
            value = startDate,
            onValueChange = onStartDateChange,
            label = { Text("Начальная", fontSize = 14.sp) },
            placeholder = { Text("dd.MM.yyyy") },
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = durationInMonths,
            onValueChange = onDurationChange,
            label = { Text("Срок (мес)", fontSize = 14.sp) },
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = endDate,
            onValueChange = onEndDateChange,
            label = { Text("Конечная", fontSize = 14.sp) },
            placeholder = { Text("dd.MM.yyyy") },
            modifier = Modifier.weight(1f)
        )
    }

    Button(
        onClick = onCheckDates,
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
    ) {
        Text("Проверить срок годности")
    }
}
