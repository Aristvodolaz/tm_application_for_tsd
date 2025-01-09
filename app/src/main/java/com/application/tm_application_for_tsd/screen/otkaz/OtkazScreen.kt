package com.application.tm_application_for_tsd.screen.otkaz

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
    toNextScreen: () -> Unit
) {
    var vlozhennost by remember { mutableStateOf("") }
    var isInputValid by remember { mutableStateOf(true) }
    val otkazState by otkazViewModel.otkazState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Show Snackbar messages based on state
    LaunchedEffect(otkazState) {
        when (val state = otkazState) {
            is OtkazViewModel.OtkazState.Error -> {
                snackbarHostState.showSnackbar(state.message)
            }
            is OtkazViewModel.OtkazState.Success -> {
                snackbarHostState.showSnackbar(state.message)
                toNextScreen() // Proceed to the next screen upon success
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ВП ${article.vp}", fontSize = 16.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {

            Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                // Display Article Information
                Text("${article.nazvanieTovara}", fontSize = 18.sp, maxLines = 2, fontWeight = FontWeight.Bold)
                Text("Артикул: ${article.artikul}", fontSize = 16.sp)
                Text("ШК: ${article.shk}", fontSize = 16.sp)

                Spacer(modifier = Modifier.height(16.dp))

                // Display planned quantity information
                Text("Количество ПЛАН (ожидаемое):", fontSize = 16.sp, color = Color.Gray)
                article.planOtkaz?.let { Text(it, fontSize = 18.sp) }
//                if (article.kolVoSyrya != null) {
//                    Text(article.kolVoSyrya.toString(), fontSize = 18.sp)
//                } else {
//                    Text(article.itogZakaz.toString(), fontSize = 18.sp)
//                }

                Spacer(modifier = Modifier.height(12.dp))

                // Input for actual quantity
                Text("Количество ФАКТ (фактическое):", fontSize = 16.sp, color = Color.Gray)
                OutlinedTextField(
                    value = vlozhennost,
                    onValueChange = {
                        vlozhennost = it
                        isInputValid = it.toIntOrNull() != null && it.toInt() > 0
                    },
                    placeholder = { Text("Например: 5") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = !isInputValid,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .padding(top = 8.dp),
                )

                // Display error message if input is invalid
                if (!isInputValid && vlozhennost.isNotEmpty()) {
                    Text(
                        text = "Пожалуйста, введите корректное количество.",
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (isInputValid) {
//                            article.vp?.let {
//                                otkazViewModel.getFactVp(spHelper.getId(), vlozhennost.toInt(),
//                                    it, article.artikul.toString()
//                                )
//                            }
                            otkazViewModel.sendFactSize(spHelper.getId(), vlozhennost.toInt())
                        }
                    },
                    enabled = vlozhennost.isNotEmpty() && isInputValid,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Завершить приемку", fontSize = 16.sp)
                }
            }
        }
    }
}
