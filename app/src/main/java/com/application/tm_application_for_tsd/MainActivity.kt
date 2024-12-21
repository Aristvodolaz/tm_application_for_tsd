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
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.application.tm_application_for_tsd.network.request_response.Article
import com.application.tm_application_for_tsd.screen.AuthScreen
import com.application.tm_application_for_tsd.screen.TaskScreen
import com.application.tm_application_for_tsd.screen.edit.OzonEditScreen
import com.application.tm_application_for_tsd.screen.edit.ScanPalletScreen
import com.application.tm_application_for_tsd.screen.edit.WBEditScreen
import com.application.tm_application_for_tsd.screen.ldu.AddLduScreen
import com.application.tm_application_for_tsd.screen.ldu.EditLduScreen
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
    var showBottomBar by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
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
                    authViewModel = authViewModel,
                    spHelper
                )
            }
            composable("task") {
                TaskScreen(
                    onNavigateToObrabotka = { taskName ->
                        navController.navigate("obrabotka")
                        showBottomBar = true
                    },
                    spHelper = spHelper
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
                            article.id?.let { it1 -> spHelper.setId(it1) }
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
                    article.id?.let { spHelper.setId(it) }
                    InfoSyryoScreen(spHelper = spHelper, article = article, viewModel = articleViewModel,
                        onNavigateToNext = {
                            navController.navigate("write_ldu")
                        })
                } else {
                    Text("Ошибка: артикул не найден", modifier = Modifier.fillMaxSize(), color = Color.Red)
                }
            }
            composable("info_article_screen") { backStackEntry ->
                val article = navController.previousBackStackEntry?.savedStateHandle?.get<Article.Articuls>("article")
                if (article != null) {
                    article.id?.let { spHelper.setId(it) }
                    InfoArticleScreen(spHelper = spHelper, article = article, viewModel = articleViewModel,
                        onNavigateToNext = {
                            navController.navigate("check_shk")
                        })
                } else {
                    Text("Ошибка: артикул не найден", modifier = Modifier.fillMaxSize(), color = Color.Red)
                }
            }

            composable("check_shk"){
                CheckShkScreen(spHelper = spHelper, scanViewModel = scannerViewModel, onNavigateToNext = {
                    navController.navigate("write_ldu")
                })
            }

            composable("write_ldu"){
                spHelper.getTaskName()?.let { AddLduScreen( id =  spHelper.getId(),
                    onSaveSuccess = {
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
                            article.id?.let { it2 -> spHelper.setId(it2) }

                            spHelper.setNameStuffWork(article.nazvanieTovara.toString())
                            navController.currentBackStackEntry?.savedStateHandle?.set("article", article)
                            navController.navigate("upakovka_article_screen")
                        }) }

            }
            composable("upakovka_article_screen") {
                val article = navController.previousBackStackEntry?.savedStateHandle?.get<Article.Articuls>("article")
                if (article != null) {
                    article.id?.let { it1 -> spHelper.setId(it1.toLong()) }
                    spHelper.getTaskName()?.let {
                        article.id?.let { it1 ->
                            LduScreen(
                                it1,
                                toNextScreen = {
                                    if(spHelper.getPref() == "WB") navController.navigate("set_in_box_wb")
                                    else navController.navigate("set_in_box")
                                }
                            )
                        }
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
                if(spHelper.getPref() == "WB") {
                    spHelper.getTaskName()?.let { it1 ->
                        RedactorWBScreen(spHelper = spHelper, taskName = it1, onClick = {
                            spHelper.setSHKPallet(it.pallet)
                            spHelper.setSHKBox(it.shk)
                            spHelper.setId(it.id)
                            navController.navigate("wb_edit_screen")
                        })
                    }
                }
                 else {
                    spHelper.getTaskName()?.let {
                        RedactorScreen(taskName = spHelper.getTaskName()!!,
                            scannerViewModel = scannerViewModel,
                            onClickArticle = { article ->
                                spHelper.setArticuleWork(article.artikul.toString())
                                spHelper.setShkWork(article.shk.toString())
                                spHelper.setPref(article.pref.toString())
                                article.id?.let { it1 -> spHelper.setId(it1) }
                                spHelper.setNameStuffWork(article.nazvanieTovara.toString())
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "article",
                                    article
                                )
                               navController.navigate("redactor_other")
                            })
                    }
                }
            }

            composable("master"){
                spHelper.getTaskName()
                    ?.let { it1 -> RedactorForMasterScreen(it1,
                        scannerViewModel = scannerViewModel,
                        onClick = {
                            it.id?.let { it2 -> spHelper.setId(it2) }
                            Log.d("COOOONTROLLER" , "DDDDD")
                            navController.navigate("edit_ldu")
                    }) }
            }

            composable("edit_ldu"){
                spHelper.getTaskName()?.let { it1 ->
                    EditLduScreen(
                        spHelper.getId(),
                        toDone = {
                            navController.navigate("master")
                        }
                    )
                }
            }


            composable("wb_edit_screen"){
                WBEditScreen(spHelper = spHelper, toScanPallet = {
                    navController.navigate("to_scan_pallet_screen_edit_wb")
                }, toDone = {
                    navController.navigate("redactor")
                } )
            }

            composable("to_scan_pallet_screen_edit_wb"){
                ScanPalletScreen(scanViewModel = scannerViewModel, spHelper = spHelper, toDone = {
                    navController.navigate("redactor")
                })
            }

            composable("redactor_other"){
                OzonEditScreen(spHelper = spHelper, onDone = {
                    navController.navigate("master")
                })
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