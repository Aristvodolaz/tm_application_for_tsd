package com.application.tm_application_for_tsd

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInbox
import androidx.compose.material.icons.filled.ChecklistRtl
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.application.tm_application_for_tsd.screen.AuthScreen
import com.application.tm_application_for_tsd.screen.TaskScreen
import dagger.hilt.android.AndroidEntryPoint

import com.application.tm_application_for_tsd.utils.DataWedgeManager
import com.application.tm_application_for_tsd.viewModel.AuthViewModel
import com.application.tm_application_for_tsd.viewModel.ScannerViewModel
import javax.inject.Inject
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataWedgeManager: DataWedgeManager
    val scannerViewModel: ScannerViewModel by viewModels()
    val authViewModel: AuthViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Настройка DataWedge
        dataWedgeManager.createAndConfigureProfile(
            profileName = "MyDataWedgeProfile",
            packageName = packageName,
            intentAction = "com.symbol.datawedge.api.RESULT_ACTION"
        )

        setContent {
            val navController = rememberNavController()
            TSDApplication(navController, scannerViewModel, authViewModel)
        }
        // Регистрация BroadcastReceiver
        val intentFilter = IntentFilter("com.symbol.datawedge.api.RESULT_ACTION")
        registerReceiver(scanReceiver, intentFilter)
    }

    private val scanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val scannedData = intent.getStringExtra("com.symbol.datawedge.data_string")
            if (!scannedData.isNullOrEmpty()) {
                scannerViewModel.onBarcodeScanned(scannedData)
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(scanReceiver)
    }
}
@Composable
fun TSDApplication(
    navController: NavHostController,
    scannerViewModel: ScannerViewModel,
    authViewModel: AuthViewModel
) {
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val currentBackStackEntry by navController.currentBackStackEntryFlow.collectAsState(null)
    val currentDestination = currentBackStackEntry?.destination?.route ?: "auth"
    var showBottomBar by remember { mutableStateOf(false) } // Состояние для показа нижнего меню

    Scaffold(
        bottomBar = {
            if (showBottomBar) { // Показываем меню только если разрешено
                BottomNavigationBar(
                    currentDestination = currentDestination,
                    onNavigateToScanner = { navController.navigate("obrabotka") },
                    onNavigateToList = { navController.navigate("upakovka") },
                    onNavigateToSettings = { navController.navigate("redactor") },
                    onNavigateToInfo = { navController.navigate("info") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (isAuthenticated) "task" else "auth",
            Modifier.padding(innerPadding)
        ) {
            composable("auth") {
                AuthScreen(
                    navController = navController,
                    scannerViewModel = scannerViewModel,
                    authViewModel = authViewModel
                )
            }
            composable("task") {
                TaskScreen(
                    onTaskSelected = {
                        showBottomBar = true // Включаем нижнее меню после выбора задания
                        navController.navigate("obrabotka") // Переход на главный экран
                    }
                )
            }
            composable("obrabotka") {
                Text("Экран обработки задания")
            }
            composable("upakovka") {
                Text("Экран списка для паллет")
            }
            composable("redactor") {
                Text("Экран настроек")
            }
            composable("info") {
                Text("Экран информации")
            }
        }
    }
}




@Composable
fun BottomNavigationBar(
    currentDestination: String,
    onNavigateToScanner: () -> Unit,
    onNavigateToList: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToInfo: () -> Unit

) {
    NavigationBar(
        containerColor = Color.LightGray,
        contentColor = Color.White,
        modifier = Modifier.height(56.dp)
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.ChecklistRtl, contentDescription = "obrabotka") },
            selected = currentDestination == "obrabotka",
            onClick = onNavigateToScanner,
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.White, // Цвет выделенного фона
            )

        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.AllInbox, contentDescription = "upakovka") },
            selected = currentDestination == "upakovka",
            onClick = onNavigateToList,
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.White, // Цвет выделенного фона
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.EditNote, contentDescription = "redactor") },
            selected = currentDestination == "redactor",
            onClick = onNavigateToSettings,
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.White, // Цвет выделенного фона
            )
        )

        NavigationBarItem(
            icon = { Icon(Icons.Filled.Info, contentDescription = "info") },
            selected = currentDestination == "info",
            onClick = onNavigateToInfo,
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.White, // Цвет выделенного фона
            )
        )
    }
}