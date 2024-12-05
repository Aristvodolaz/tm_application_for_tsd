package com.application.tm_application_for_tsd.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.application.tm_application_for_tsd.R
import com.application.tm_application_for_tsd.viewModel.AuthViewModel

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.fragment.app.activityViewModels
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.application.tm_application_for_tsd.ui.theme.RedAccentButton
import com.application.tm_application_for_tsd.viewModel.ScannerViewModel
import dagger.hilt.android.AndroidEntryPoint
@Composable
fun AuthScreen(
    navController: NavController,
    scannerViewModel: ScannerViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // Состояния
    val isLoading by authViewModel.loading.observeAsState(false)
    val authState by authViewModel.authStatus.collectAsStateWithLifecycle()
    val barcodeData by scannerViewModel.barcodeData.collectAsStateWithLifecycle()
    val error by scannerViewModel.error.collectAsStateWithLifecycle()

    Log.d("AuthScreen", "Current barcodeData: $barcodeData")

    // Обработка изменения barcodeData
    LaunchedEffect(barcodeData) {
        if (barcodeData.isNotEmpty()) {
            Log.d("AuthScreen", "Authenticating barcode: $barcodeData")
            authViewModel.authenticate(barcodeData)
        }
    }

    // Обработка состояния авторизации
    LaunchedEffect(authState) {
        when (authState) {
            is AuthViewModel.AuthState.Success -> {
                val username = (authState as AuthViewModel.AuthState.Success).username
                Log.d("AuthScreen", "Authentication success. Navigating to home: $username")
                Toast.makeText(context, "Добро пожаловать, $username", Toast.LENGTH_SHORT).show()
                navController.navigate("home") {
                    popUpTo("auth") { inclusive = true }
                }
            }
            is AuthViewModel.AuthState.Error -> {
                val errorMessage = (authState as AuthViewModel.AuthState.Error).error
                Log.e("AuthScreen", "Authentication error: $errorMessage")
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    AuthScreenContent(isLoading = isLoading)
}

@Composable
fun AuthScreenContent(
    isLoading: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Авторизация",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = "Пожалуйста, отсканируйте ваш штрих-код для авторизации.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
        } else {
            Text(
                text = "Сканер активен.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF008000)
            )
        }
    }
}