package com.application.tm_application_for_tsd.viewModel

import androidx.compose.animation.core.spring
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.tm_application_for_tsd.network.Api
import com.application.tm_application_for_tsd.utils.InfoForCheckBox
import com.application.tm_application_for_tsd.viewModel.LduViewModel.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditLduViewModel @Inject constructor(
    val api: Api
) : ViewModel(){
    sealed class UiState {
        object Loading : UiState()
        data class Loaded(val actions: List<ActionItem>) : UiState()
        object Error : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    fun loadLduData(id: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = api.getLDU(id)

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
                            "Sortiruemyi_Tovar" -> result.sortiruemyiTovar
                            "Ne_Sortiruemyi_Tovar" -> result.neSortiruemyiTovar
                            "Produkty" -> result.produkty
                            "Opasnyi_Tovar" -> result.opasnyiTovar
                            "Zakrytaya_Zona" -> result.zakrytayaZona
                            "Krupnogabaritnyi_Tovar" -> result.krupnogabaritnyiTovar
                            "Yuvelirnye_Izdelia" -> result.yuvelirnyeIzdelia
                            "Pechat_Etiketki_s_SHK" -> result.pechatEtiketkiSHK
                            "Pechat_Etiketki_s_Opisaniem" -> result.pechatEtiketkiOpisaniem
                            else -> null
                        }
                    }
                    when (value) {
                        "V" -> {
                            ActionItem(name, "1")
                        }
                        null -> {
                            ActionItem(name, "0")

                        }
                        else -> {
                            ActionItem(name,value.toString())
                        }
                    }
                }

                _uiState.value = UiState.Loaded(actions)
            } catch (e: Exception) {
                _uiState.value = UiState.Error
            }
        }
    }


    fun incrementAction(index: Int) {
        if (_uiState.value is UiState.Loaded) {
            val actions = (_uiState.value as UiState.Loaded).actions.toMutableList()
            val currentAction = actions[index]
            val currentValue = currentAction.count.toIntOrNull() ?: 0
            actions[index] = currentAction.copy(count = (currentValue + 1).toString())
            _uiState.value = UiState.Loaded(actions)
        }
    }

    fun decrementAction(index: Int) {
        if (_uiState.value is UiState.Loaded) {
            val actions = (_uiState.value as UiState.Loaded).actions.toMutableList()
            val currentAction = actions[index]
            val currentValue = currentAction.count.toIntOrNull() ?: 0
            if (currentValue > 0) {
                actions[index] = currentAction.copy(count = (currentValue - 1).toString())
                _uiState.value = UiState.Loaded(actions)
            }
        }
    }
    fun saveActions( id: Long, onComplete: () -> Unit) {
        if (_uiState.value is UiState.Loaded) {
            val actions = (_uiState.value as UiState.Loaded).actions

            // Prepare request body with quantities
            val requestBody = mutableMapOf<String, String>().apply {
                actions.forEachIndexed { index, action ->
                    put(InfoForCheckBox.infoBoxToDB[index], action.count.toString())
                }
            }

            viewModelScope.launch {
                try {
                    api.updateCheckBox(id, requestBody)
                    onComplete()
                } catch (e: Exception) {
                    _uiState.value = UiState.Error
                }
            }
        }
    }

    fun setStatus(id: Long, stats: Int, onComplete: () -> Unit){
        viewModelScope.launch {
            try{
                val response = api.updateStatus(id, stats)
                if(response.success){
                    onComplete()
                }
            }catch (e: Exception) {
                _uiState.value = UiState.Error
            }
        }
    }

}