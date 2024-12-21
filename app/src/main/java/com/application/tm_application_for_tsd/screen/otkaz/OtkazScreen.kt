package com.application.tm_application_for_tsd.screen.otkaz

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.tm_application_for_tsd.utils.SPHelper
import com.application.tm_application_for_tsd.viewModel.OtkazViewModel

@Composable
fun OtkazScreen(
    otkazViewModel: OtkazViewModel = hiltViewModel(),
    spHelper: SPHelper,
    toNextScreen: () -> Unit
) {

    LaunchedEffect(spHelper.getId()){

    }
}