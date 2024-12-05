package com.application.tm_application_for_tsd

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.application.tm_application_for_tsd.screen.AuthScreen
import dagger.hilt.android.AndroidEntryPoint

import com.application.tm_application_for_tsd.utils.DataWedgeManager
import com.application.tm_application_for_tsd.viewModel.ScannerViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val scannerViewModel: ScannerViewModel by viewModels()
    @Inject
    lateinit var dataWedgeManager: DataWedgeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Теперь Hilt автоматически инжектирует dataWedgeManager
        dataWedgeManager.createAndConfigureProfile(
            profileName = "MyDataWedgeProfile",
            packageName = packageName,
            intentAction = "com.symbol.datawedge.api.RESULT_ACTION"
        )

        setContent {
            val navController = rememberNavController()
            NavigationGraph(navController = navController, scannerViewModel = scannerViewModel)
        }

        // Регистрируем BroadcastReceiver
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.symbol.datawedge.api.RESULT_ACTION")
        registerReceiver(scanReceiver, intentFilter)
    }


    private val scanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if ("com.symbol.datawedge.api.RESULT_ACTION" == intent.action) {
                val scannedData = intent.getStringExtra("com.symbol.datawedge.data_string")
                val error = intent.getStringExtra("com.symbol.datawedge.result")

                Log.d("ScanReceiver", "Intent received: $intent")
                Log.d("ScanReceiver", "Scanned Data: $scannedData")
                Log.d("ScanReceiver", "Error: $error")

                if (!scannedData.isNullOrEmpty()) {
                    Log.d("ScanReceiver", "Sending data to ViewModel: $scannedData")
                    scannerViewModel.onBarcodeScanned(scannedData)
                } else if (!error.isNullOrEmpty()) {
                    Log.d("ScanReceiver", "Sending error to ViewModel: $error")
                    scannerViewModel.onScanError(error)
                }
            }
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(scanReceiver)
    }
}
//
//@Composable
//fun AppWithBottomNavigation() {
//    val navController = rememberNavController()
//    val items = listOf(
//        BottomNavItem(
//            route = "scanner",
//            icon = Icons.Filled.Home, // Material Design Home icon
//            label = "Сортировка"
//        ),
//        BottomNavItem(
//            route = "list_for_pallet",
//            icon = Icons.Filled.List, // Material Design List icon
//            label = "Список"
//        ),
//        BottomNavItem(
//            route = "settings",
//            icon = Icons.Filled.Settings, // Material Design Settings icon
//            label = "Настройки"
//        )
//    )
//
//    Scaffold(
//        bottomBar = {
//            if (navController.currentBackStackEntry?.destination?.route != "auth") {
//                BottomNavigationBar(navController, items)
//            }
//        }
//    ) { innerPadding ->
//        Box(modifier = Modifier.padding(innerPadding)) {
//            NavigationGraph(navController)
//        }
//    }
//}

@Composable
fun BottomNavigationBar(navController: NavHostController, items: List<BottomNavItem>) {
    NavigationBar(   containerColor = Color(0xFFE1E1E1), // Adjust background color
        tonalElevation = 4.dp, // Optional, gives elevation effect
        modifier = Modifier.height(56.dp)) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp) // Adjust icon size
                    )
                },
                label = {
                    Text(
                        item.label,
                        style = MaterialTheme.typography.bodySmall, // Smaller text style
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center // Center align text
                    )
                },
                alwaysShowLabel = true // Ensures label is always visible
            )
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, scannerViewModel: ScannerViewModel) {
    NavHost(navController, startDestination = "auth") {
        composable("auth") {
            Log.d("NavigationGraph", "Navigating to AuthScreen")

            AuthScreen(
                navController = navController,
                scannerViewModel = scannerViewModel,
                authViewModel = hiltViewModel()
            )
        }

    }
}

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

fun sampleBoxes(): List<String> {
    return listOf("Box 1", "Box 2", "Box 3", "Box 4", "Box 5")
}
