package com.application.tm_application_for_tsd.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.tm_application_for_tsd.network.Api
import com.application.tm_application_for_tsd.network.request_response.CheckBox
import com.application.tm_application_for_tsd.utils.InfoForCheckBox
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ActionItem(val name: String, var count: Int)

class LduViewModel(private val apiService: Api) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Loaded(val actions: List<ActionItem>) : UiState()
        object Error : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    fun loadLduData(artikul: Int, taskName: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = apiService.getLDU(artikul, taskName)

                // Используем infoBox и infoBoxToDB для связывания
                val actions = InfoForCheckBox.infoBox.mapIndexed { index, displayName ->
                    val dbValue = response.value.firstOrNull()?.let { value ->
                        when (InfoForCheckBox.infoBoxToDB[index]) {
                            "Op_1_Bl_1_Sht" -> value.op1Bl1Sht
                            "Op_2_Bl_2_Sht" -> value.op2Bl2Sht
                            "Op_3_Bl_3_Sht" -> value.op3Bl3Sht
                            "Op_4_Bl_4_Sht" -> value.op4Bl4Sht
                            "Op_5_Bl_5_Sht" -> value.op5Bl5Sht
                            "Op_6_Blis_6_10_Sht" -> value.op6Blis610Sht
                            "Op_7_Pereschyot" -> value.op7Pereschyot
                            "Op_9_Fasovka_Sborka" -> value.op9FasovkaSborka
                            "Op_10_Markirovka_SHT" -> value.op10MarkirovkaSHT
                            "Op_11_Markirovka_Prom" -> value.op11MarkirovkaProm
                            "Op_13_Markirovka_Fabr" -> value.op13MarkirovkaFabr
                            "Op_14_TU_1_Sht" -> value.op14TU1Sht
                            "Op_15_TU_2_Sht" -> value.op15TU2Sht
                            "Op_16_TU_3_5" -> value.op16TU35
                            "Op_17_TU_6_8" -> value.op17TU68
                            "Op_468_Proverka_SHK" -> value.op468ProverkaSHK
                            "Op_469_Spetsifikatsiya_TM" -> value.op469SpetsifikatsiyaTM
                            "Op_470_Dop_Upakovka" -> value.op470DopUpakovka
                            else -> null
                        }
                    }
                    ActionItem(displayName, dbValue?.toIntOrNull() ?: 0)
                }

                _uiState.value = UiState.Loaded(actions)
            } catch (e: Exception) {
                _uiState.value = UiState.Error
            }
        }
    }

    fun incrementAction(index: Int) {
        if (_uiState.value is UiState.Loaded) {
            val actions = (_uiState.value as UiState.Loaded).actions
            _uiState.value = UiState.Loaded(actions.mapIndexed { i, action ->
                if (i == index) action.copy(count = action.count + 1) else action
            })
        }
    }

    fun decrementAction(index: Int) {
        if (_uiState.value is UiState.Loaded) {
            val actions = (_uiState.value as UiState.Loaded).actions
            _uiState.value = UiState.Loaded(actions.mapIndexed { i, action ->
                if (i == index) action.copy(count = maxOf(0, action.count - 1)) else action
            })
        }
    }

    fun saveActions(artikul: Int, taskName: String, onComplete: () -> Unit) {
        if (_uiState.value is UiState.Loaded) {
            val actions = (_uiState.value as UiState.Loaded).actions
            val columnsToUpdate = actions
                .filter { it.count > 0 }
                .mapIndexedNotNull { index, _ -> InfoForCheckBox.infoBoxToDB[index] }

            val request = CheckBox(taskName, artikul, columnsToUpdate)
            viewModelScope.launch {
                try {
                    apiService.updateCheckBox(request)
                    onComplete()
                } catch (e: Exception) {
                    // Handle error
                }
            }
        }
    }
}
