package com.application.tm_application_for_tsd.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.tm_application_for_tsd.network.Api
import com.application.tm_application_for_tsd.network.request_response.Articles
import com.application.tm_application_for_tsd.network.request_response.Pallets
import com.application.tm_application_for_tsd.network.request_response.ValuePallet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PalletViewModel @Inject constructor(
    private val api: Api // Внедряем API
) : ViewModel() {

    private val _state = MutableStateFlow(PalletScreenState())
    val state: StateFlow<PalletScreenState> = _state

    fun loadPallets(taskName: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                val response = api.getPallets(taskName)
                val palletList = response.value.pallet_no
                val totalPlaces = response.value.total

                _state.value = _state.value.copy(
                    pallets = palletList,
                    totalPlaces = totalPlaces,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Ошибка загрузки паллетов: ${e.message}"
                )
            }
        }
    }



    // Загрузка списка статей для выбранного паллета
    fun loadArticles(taskName: String, palletNo: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                val response = api.getArticleOnPallet(pallet = palletNo, taskName = taskName)
                _state.value = _state.value.copy(
                    selectedPallet = palletNo,
                    articles = response.value.articles,
                    totalPlaces = response.value.totalBox,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Ошибка загрузки содержимого паллета: ${e.message}"
                )
            }
        }
    }

    // Сброс выбора паллета
    fun deselectPallet() {
        _state.value = _state.value.copy(
            selectedPallet = null,
            articles = emptyList(),
            totalPlaces = 0
        )
    }
}

// Состояние экрана
data class PalletScreenState(
    val isLoading: Boolean = false,
    val pallets: List<Pallets> = emptyList(),
    val selectedPallet: String? = null,
    val articles: List<Articles> = emptyList(),
    val totalPlaces: Int = 0,
    val errorMessage: String? = null
)
