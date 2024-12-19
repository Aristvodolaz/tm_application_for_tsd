package com.application.tm_application_for_tsd.screen.obrabotka

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.tm_application_for_tsd.R
import com.application.tm_application_for_tsd.network.request_response.Article
import com.application.tm_application_for_tsd.screen.dialog.ExcludeArticleDialog
import com.application.tm_application_for_tsd.utils.SPHelper
import com.application.tm_application_for_tsd.viewModel.InfoArticleViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoSyryoScreen (
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
    var showExpirationButton by remember { mutableStateOf(article.status == 1) }
    var showIsklDialog by remember { mutableStateOf(false) } // Для отображения диалога
    val context = LocalContext.current
    val reasons = context.resources.getStringArray(R.array.cancel_reasons).toList()
    var showExpirationDialog by remember { mutableStateOf(false) }
    var expirationPercentage by remember { mutableStateOf(0.0) }
    var endDates by remember { mutableStateOf("") }

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
        },
        modifier = Modifier.height(48.dp)
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            Column(modifier = Modifier.padding(8.dp).fillMaxSize()) {
                Text("${article.nazvanieTovara}", fontSize = 16.sp, maxLines = 2)
                Text("Артикул: ${article.artikul}", fontSize = 14.sp)
                Text("ШК сырья: ${article.shkSyrya}", fontSize = 14.sp)
                Text("Кол-во сырья: ${article.kolVoSyrya}", fontSize = 14.sp)

                Spacer(modifier = Modifier.height(8.dp))
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
                                    if(spHelper.getPref()=="WB") viewModel.addSrokForWB(calculatedEndDate)
                                    viewModel.addExpirationData(
                                        persent = "%.2f".format(percentage),
                                        endDate = calculatedEndDate
                                    )
                                }
                            } ?: Toast.makeText(context, "Некорректные данные", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Введите даты в формате dd.MM.yyyy", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
                }
                if (showExpirationDialog) {
                    ShowExpirationDialog(
                        percentagePassed = expirationPercentage,
                        onConfirm = {
                            if(spHelper.getPref()=="WB") viewModel.addSrokForWB(endDates)
                            viewModel.addExpirationData(
                                persent = "%.2f".format(expirationPercentage),
                                endDate = endDates
                            )
                        },
                        onCancel = {
                            showExpirationDialog = false
                            Toast.makeText(context, "Работа с товаром прекращена", Toast.LENGTH_SHORT).show()
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
