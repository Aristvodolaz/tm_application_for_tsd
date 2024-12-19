package com.application.tm_application_for_tsd

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInbox
import androidx.compose.material.icons.filled.ChecklistRtl
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ManageAccounts
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
import com.application.tm_application_for_tsd.network.request_response.Article
import com.application.tm_application_for_tsd.screen.AuthScreen
import com.application.tm_application_for_tsd.screen.TaskScreen
import com.application.tm_application_for_tsd.screen.edit.OzonEditScreen
import com.application.tm_application_for_tsd.screen.ldu.AddLduScreen
import com.application.tm_application_for_tsd.screen.ldu.LduScreen
import com.application.tm_application_for_tsd.screen.navigation.ObraborkaScreen
import com.application.tm_application_for_tsd.screen.navigation.PalletScreen
import com.application.tm_application_for_tsd.screen.navigation.RedactorForMasterScreen
import com.application.tm_application_for_tsd.screen.navigation.RedactorScreen
import com.application.tm_application_for_tsd.screen.navigation.RedactorWBScreen
import com.application.tm_application_for_tsd.screen.navigation.UpakovkaScreen
import com.application.tm_application_for_tsd.screen.obrabotka.CheckShkScreen
import com.application.tm_application_for_tsd.screen.obrabotka.InfoArticleScreen
import com.application.tm_application_for_tsd.screen.obrabotka.InfoSyryoScreen
import com.application.tm_application_for_tsd.screen.upakovka.OzonScreen
import com.application.tm_application_for_tsd.screen.upakovka.wb.WBBoxScreen
import com.application.tm_application_for_tsd.screen.upakovka.wb.WBListScreen
import com.application.tm_application_for_tsd.screen.upakovka.wb.WBPalletScreen
import com.application.tm_application_for_tsd.screen.upakovka.wb.WBVlozhScreen
import dagger.hilt.android.AndroidEntryPoint

import com.application.tm_application_for_tsd.utils.DataWedgeManager
import com.application.tm_application_for_tsd.utils.SPHelper
import com.application.tm_application_for_tsd.viewModel.AuthViewModel
import com.application.tm_application_for_tsd.viewModel.InfoArticleViewModel
import com.application.tm_application_for_tsd.viewModel.ScannerViewModel
import com.google.gson.Gson
import javax.inject.Inject
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataWedgeManager: DataWedgeManager
    val scannerViewModel: ScannerViewModel by viewModels()
    val authViewModel: AuthViewModel by viewModels()
    val articleViewModel: InfoArticleViewModel by viewModels()
    @Inject
    lateinit var spHelper: SPHelper

    @RequiresApi(Build.VERSION_CODES.O)
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
            TSDApplication(navController, scannerViewModel, articleViewModel, authViewModel, spHelper)
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
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TSDApplication(
    navController: NavHostController,
    scannerViewModel: ScannerViewModel,
    articleViewModel: InfoArticleViewModel,
    authViewModel: AuthViewModel,
    spHelper: SPHelper
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
                    onNavigateToInfo = { navController.navigate("info") },
                    onNavigateToMaster = {navController.navigate("master")}
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
                    onNavigateToObrabotka = { taskName ->
                        navController.navigate("obrabotka")
                        showBottomBar = true
                    },
                    spHelper = spHelper // Передаем SPHelper
                )
            }
            composable(
                route = "obrabotka",
            ) {
                spHelper.getTaskName()?.let { taskName ->
                    ObraborkaScreen(
                        taskName = taskName,
                        scannerViewModel = scannerViewModel,
                        onArticleClick = { article ->
                            spHelper.setArticuleWork(article.artikul.toString())
                            spHelper.setShkWork(article.shk.toString())
                            spHelper.setPref(article.pref.toString())
                            spHelper.setNameStuffWork(article.nazvanieTovara.toString())
                            navController.currentBackStackEntry?.savedStateHandle?.set("article", article)
                            val destination = if (article.kolVoSyrya != null) {
                                "info_syryo_screen"
                            } else {
                                "info_article_screen"
                            }
                            navController.navigate(destination)
                        }
                    )
                }
            }


            composable("info_syryo_screen"){
                val article = navController.previousBackStackEntry?.savedStateHandle?.get<Article.Articuls>("article")
                if (article != null) {
                    InfoSyryoScreen(spHelper = spHelper, article = article, viewModel = articleViewModel,
                        onNavigateToNext = {
                            // Здесь реализуйте переход на новый экран
                            navController.navigate("write_ldu")
                        })
                } else {
                    // Отобразить ошибку, если объект article равен null
                    Text("Ошибка: артикул не найден", modifier = Modifier.fillMaxSize(), color = Color.Red)
                }
            }
            composable("info_article_screen") { backStackEntry ->
                val article = navController.previousBackStackEntry?.savedStateHandle?.get<Article.Articuls>("article")
                if (article != null) {
                    InfoArticleScreen(spHelper = spHelper, article = article, viewModel = articleViewModel,
                        onNavigateToNext = {
                            // Здесь реализуйте переход на новый экран
                            navController.navigate("check_shk")
                        })
                } else {
                    // Отобразить ошибку, если объект article равен null
                    Text("Ошибка: артикул не найден", modifier = Modifier.fillMaxSize(), color = Color.Red)
                }
            }

            composable("check_shk"){
                CheckShkScreen(spHelper = spHelper, scanViewModel = scannerViewModel, onNavigateToNext = {
                    navController.navigate("write_ldu")
                })
            }

            composable("write_ldu"){
                spHelper.getTaskName()?.let { AddLduScreen(spHelper.getArticuleWork()!!, it,{
                    navController.navigate("obrabotka")
                }) }
            }



            composable("upakovka") {
                spHelper.getTaskName()
                    ?.let { it1 -> UpakovkaScreen(taskName = it1, scannerViewModel = scannerViewModel,
                        onArticleClick = { article ->
                            spHelper.setArticuleWork(article.artikul.toString())
                            spHelper.setShkWork(article.shk.toString())
                            spHelper.setPref(article.pref.toString())
                            spHelper.setNameStuffWork(article.nazvanieTovara.toString())
                            navController.currentBackStackEntry?.savedStateHandle?.set("article", article)
                            navController.navigate("upakovka_article_screen")
                        }) }

            }
            composable("upakovka_article_screen") {
                val article = navController.previousBackStackEntry?.savedStateHandle?.get<Article.Articuls>("article")
                if (article != null) {
                    spHelper.getTaskName()?.let {
                        LduScreen(
                            spHelper.getArticuleWork()!!,
                            spHelper.getTaskName()!!,
                            toNextScreen = {
//                                if(spHelper.getPref() == "WB")
                                    navController.navigate("set_in_box_wb")
//                                else navController.navigate("set_in_box")
                            }
                        )
                    }
                }
            }

            composable("set_in_box"){
                OzonScreen(spHelper = spHelper)
            }

            composable("set_in_box_wb"){
                WBListScreen(spHelper = spHelper, toScanBox = {
                    navController.navigate("scan_box_to_wb")
                }, toDone ={
                    navController.navigate("upakovka")
                } )
            }

            composable("write_vlozh_to_wb"){
                WBVlozhScreen(spHelper) {
                    navController.navigate("scan_pallet_wb")
                }
            }

            composable("scan_box_to_wb"){
                WBBoxScreen(spHelper=spHelper, scanViewModel = scannerViewModel, toWriteVlozhennost = {
                    Log.d("Navigation", "Navigating to write_vlozh_to_wb")
                    navController.navigate("write_vlozh_to_wb")
                }
                )
            }



            composable("scan_pallet_wb"){
                WBPalletScreen(spHelper = spHelper,scanViewModel = scannerViewModel,){
                    navController.navigate("set_in_box_wb")
                }
            }
            composable("redactor") {
                spHelper.getTaskName()?.let { RedactorScreen(taskName = spHelper.getTaskName()!!, scannerViewModel = scannerViewModel,
                    onClickArticle = {article ->
                    spHelper.setArticuleWork(article.artikul.toString())
                    spHelper.setShkWork(article.shk.toString())
                    spHelper.setPref(article.pref.toString())
                    spHelper.setNameStuffWork(article.nazvanieTovara.toString())
                    navController.currentBackStackEntry?.savedStateHandle?.set("article", article)
                        val pref = article.pref.toString() == "WB"
                        if(pref) navController.navigate("redactor_wb")
                        else navController.navigate("redactor_other")
                    })
                }
            }

            composable("master"){
                spHelper.getTaskName()
                    ?.let { it1 -> RedactorForMasterScreen(it1,
                        scannerViewModel = scannerViewModel,
                        onClick = {
                            navController.navigate("edit_ldu")
                    }) }
            }

            composable("edit_ldu"){
                spHelper.getTaskName()?.let { it1 ->
                    AddLduScreen(
                        spHelper.getArticuleWork()!!, it1,
                        onSaveSuccess = {
                            navController.navigate("master")
                        })
                }
            }

            composable("redactor_wb"){
                RedactorWBScreen()
            }

            composable("redactor_other"){
                OzonEditScreen()
            }
            composable("info") {
                spHelper.getTaskName()?.let { PalletScreen(taskName = spHelper.getTaskName()!!) } // Передаем SPHelper
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
    onNavigateToInfo: () -> Unit,
    onNavigateToMaster: () -> Unit

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
            icon = { Icon(Icons.Filled.ManageAccounts, contentDescription = "master") },
            selected = currentDestination == "master",
            onClick = onNavigateToMaster,
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