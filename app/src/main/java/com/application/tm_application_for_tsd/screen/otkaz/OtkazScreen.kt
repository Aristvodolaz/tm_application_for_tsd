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
import androidx.compose.ui.text.style.TextAlign
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
    toNextScreen: () -> Unit
) {
    var vlozhennost by remember { mutableStateOf("") }
    var summa by remember { mutableStateOf("") }
    var artikul by remember { mutableStateOf("") }
    var isInputValid by remember { mutableStateOf(true) }
    val otkazState by otkazViewModel.otkazState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        artikul = article.artikulSyrya?.toString() ?: article.artikul.toString()
        otkazViewModel.getTransferSize(article.vp.toString(), artikul)
    }

    LaunchedEffect(otkazState) {
        when (val state = otkazState) {
            is OtkazViewModel.OtkazState.Error -> snackbarHostState.showSnackbar(state.message)
            is OtkazViewModel.OtkazState.Success -> {
                snackbarHostState.showSnackbar(state.message)
                toNextScreen()
            }
            is OtkazViewModel.OtkazState.SuccessVP -> summa = state.sum.toString()
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Приемка: ВП ${article.vp}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF5F5F5))
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Информация о товаре
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = article.nazvanieTovara ?: "Нет названия",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Артикул: ${article.artikul}", fontSize = 16.sp, color = Color.Gray)
                        Text("ШК: ${article.shk}", fontSize = 16.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Text("Количество по ВП:", fontSize = 16.sp, color = Color.Gray)
                        Text(summa, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        var plans = if(article.kolVoSyrya!=null)
                            article.kolVoSyrya.toInt()
                        else article.itogZakaz!!.toInt()

                        Text(
                            text = "План заявки (ожидаемое): ${plans ?: "Нет данных"}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Ввод фактического количества
                OutlinedTextField(
                    value = vlozhennost,
                    onValueChange = {
                        vlozhennost = it
                        isInputValid = it.toIntOrNull() != null && it.toInt() > 0
                    },
                    placeholder = { Text("Введите количество, например: 5") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = !isInputValid,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp)),
                )

                if (!isInputValid && vlozhennost.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Ошибка",
                            tint = Color.Red
                        )
                        Text(
                            text = "Введите корректное количество.",
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Кнопка подтверждения
                Button(
                    onClick = {
                        if (isInputValid) {
                            article.nazvanieZadaniya?.let { name ->
                                article.vp?.let { vp ->
                                    otkazViewModel.addInfoVP(name, artikul, vp, summa.toInt(), vlozhennost.toInt(), article.id!!.toLong())
                                }
                            }
                        }
                    },
                    enabled = vlozhennost.isNotEmpty() && isInputValid,
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                ) {
                    Text("Завершить приемку", fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}
