package com.application.tm_application_for_tsd.viewModel

import androidx.lifecycle.ViewModel
import com.application.tm_application_for_tsd.network.Api
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OtkazViewModel @Inject constructor(
    api: Api
) : ViewModel() {
}