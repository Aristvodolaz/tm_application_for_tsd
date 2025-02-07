import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.tm_application_for_tsd.network.request_response.Article
import com.application.tm_application_for_tsd.utils.SPHelper
import com.application.tm_application_for_tsd.viewModel.OtkazViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtkazScreen(
    article: Article.Articuls,
    otkazViewModel: OtkazViewModel = hiltViewModel(),
    spHelper: SPHelper,
    toNextScreen: () -> Unit,
) {
    var vlozhennost by remember { mutableStateOf("") }
    var summa by remember { mutableStateOf("0") }
    var prinyato by remember { mutableStateOf("0") }
    var factSum by remember { mutableStateOf("0") }
    var artikul by remember { mutableStateOf("") }
    var isInputValid by remember { mutableStateOf(true) }

    val otkazState by otkazViewModel.otkazState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Загружаем данные при открытии экрана
    LaunchedEffect(Unit) {
        artikul = (article.artikulSyrya ?: article.artikul).toString()
        otkazViewModel.getTransferSize(article.vp.orEmpty(), artikul)
    }

    // Реакция на изменение состояния (успехи, ошибки)
    LaunchedEffect(otkazState) {
        when (val state = otkazState) {
            is OtkazViewModel.OtkazState.Error -> snackbarHostState.showSnackbar(state.message)
            is OtkazViewModel.OtkazState.Success -> snackbarHostState.showSnackbar(state.message)
            is OtkazViewModel.OtkazState.SuccessVP -> {
                summa = state.sum.toString()
                factSum = state.factSum.toString()
                prinyato = state.prinyato.toString()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Приемка: ВП ${article.vp.orEmpty()}", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF5F5F5))
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(modifier = Modifier.padding(16.dp)) { Text(it.visuals.message) }
            }
        }
    ) { padding ->

    Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Карточка с информацией
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(article.nazvanieTovara ?: "Нет названия", fontSize = 20.sp, fontWeight = FontWeight.Bold, maxLines = 2)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Артикул: ${article.artikul}", fontSize = 16.sp, color = Color.Gray)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("Количество по ВП: $summa", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Принято: $prinyato", fontSize = 20.sp, color = Color.Black)
                    Text("Осталось принять: $factSum", fontSize = 20.sp, color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Поле ввода количества
            OutlinedTextField(
                value = vlozhennost,
                onValueChange = { vlozhennost = it },
                label = { Text("Фактическое количество") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = vlozhennost.isNotEmpty() && vlozhennost.toIntOrNull() == null,
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp)
            )


            if (!isInputValid && vlozhennost.isNotEmpty()) {
                Text("Введите корректное количество", color = Color.Red, fontSize = 14.sp)
            }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
                onClick = {
                    if (isInputValid) {
                        article.nazvanieZadaniya?.let { name ->
                            article.vp?.let { vp ->
                                otkazViewModel.addInfoVP(
                                    name, artikul, vp, summa.toIntOrNull() ?: 0, vlozhennost.toInt()
                                )
                            }
                        }
                    }
                },
                enabled = vlozhennost.isNotEmpty() && isInputValid,
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp)
            ) {
                Text("Добавить")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Кнопка "Перейти к упаковке"
            Button(
                onClick = { toNextScreen() },
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp)
            ) {
                Text("Перейти к упаковке")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Кнопка "Завершить приемку"
        Button(
            onClick = {
                article.id?.let {
                    otkazViewModel.setStatus(it, 3, toNextScreen)
                }
            },
            enabled = prinyato.toIntOrNull() ?: 0 > 0, // Кнопка активна, если есть принятый товар
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
        ) {
            Text("Завершить приемку", fontSize = 16.sp, color = Color.White)
        }
        }
    }
}
