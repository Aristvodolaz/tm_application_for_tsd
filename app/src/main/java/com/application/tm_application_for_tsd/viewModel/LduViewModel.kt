package com.application.tm_application_for_tsd.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.tm_application_for_tsd.network.Api
import com.application.tm_application_for_tsd.network.request_response.CheckBox
import com.application.tm_application_for_tsd.utils.InfoForCheckBox
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ActionItem(val name: String, var count: Int)

@HiltViewModel
class LduViewModel @Inject constructor(private val apiService: Api) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Loaded(val actions: List<ActionItem>) : UiState()
        object Error : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    fun loadLduData(artikul: String, taskName: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = apiService.getLDU(artikul.toInt(), taskName)

                // Преобразование данных с сервера
                val actions = InfoForCheckBox.infoBox.mapIndexed { index, name ->
                    val dbField = InfoForCheckBox.infoBoxToDB[index]
                    val value = response.value.firstOrNull()?.let { result ->
                        when (dbField) {
                            "Op_1_Bl_1_Sht" -> result.op1Bl1Sht
                            "Op_2_Bl_2_Sht" -> result.op2Bl2Sht
                            "Op_3_Bl_3_Sht" -> result.op3Bl3Sht
                            "Op_4_Bl_4_Sht" -> result.op4Bl4Sht
                            "Op_5_Bl_5_Sht" -> result.op5Bl5Sht
                            "Op_6_Blis_6_10_Sht" -> result.op6Blis610Sht
                            "Op_7_Pereschyot" -> result.op7Pereschyot
                            "Op_9_Fasovka_Sborka" -> result.op9FasovkaSborka
                            "Op_10_Markirovka_SHT" -> result.op10MarkirovkaSHT
                            "Op_11_Markirovka_Pром" -> result.op11MarkirovkaProm
                            "Op_13_Markirovka_Fabr" -> result.op13MarkirovkaFabr
                            "Op_14_TU_1_Sht" -> result.op14TU1Sht
                            "Op_15_TU_2_Sht" -> result.op15TU2Sht
                            "Op_16_TU_3_5" -> result.op16TU35
                            "Op_17_TU_6_8" -> result.op17TU68
                            "Op_468_Proverka_SHK" -> result.op468ProverkaSHK
                            "Op_469_Spetsifikatsiya_TM" -> result.op469SpetsifikatsiyaTM
                            "Op_470_Dop_Upakovka" -> result.op470DopUpakovka
                            else -> null
                        }
                    }
                    ActionItem(name, if (value == "V") 1 else 0)
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
                .filter { it.count >0 }
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
