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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.tm_application_for_tsd.network.request_response.Article
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
    onNavigateToNext: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val navigateToNext by viewModel.navigateToNextScreen.collectAsState()
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var durationInMonths by remember { mutableStateOf("") }
    val context = LocalContext.current
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
                Text("Товар: ${article.nazvanieTovara}", fontSize = 16.sp)
                Text("Артикул: ${article.artikul}", fontSize = 14.sp)
                Text("ШК: ${article.shk}", fontSize = 14.sp)

                Spacer(modifier = Modifier.height(16.dp))

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
                                viewModel.addExpirationData(
                                    persent = "%.2f".format(percentage),
                                    endDate = calculatedEndDate
                                )
                                Toast.makeText(context, "Осталось: ${"%.2f".format(percentage)}%", Toast.LENGTH_SHORT).show()
                            } ?: Toast.makeText(context, "Некорректные данные", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Введите даты в формате dd.MM.yyyy", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                if (!state.isTaskInProgress) {
                    Button(
                        onClick = { viewModel.changeStatusTask(article.artikul.toString(), 1) },
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    ) {
                        Text("Взять в работу", fontSize = 16.sp)
                    }
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

        val end = if (endDate.isNotEmpty()) {
            LocalDate.parse(endDate, dateFormatter)
        } else if (durationInMonths.isNotEmpty()) {
            start.plusMonths(durationInMonths.toLong())
        } else return null

        val today = LocalDate.now()
        if (end.isBefore(start) || end.isBefore(today)) return null

        val totalDays = ChronoUnit.DAYS.between(start, end).toDouble()
        val remainingDays = ChronoUnit.DAYS.between(today, end).toDouble()
        val percentage = (remainingDays / totalDays * 100).coerceAtLeast(0.0)

        Pair(percentage, end.format(dateFormatter))
    } catch (e: Exception) {
        null
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
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = startDate,
            onValueChange = onStartDateChange,
            label = { Text("Начальная") },
            placeholder = { Text("dd.MM.yyyy") },
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = durationInMonths,
            onValueChange = onDurationChange,
            label = { Text("Срок (мес)") },
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = endDate,
            onValueChange = onEndDateChange,
            label = { Text("Конечная") },
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
