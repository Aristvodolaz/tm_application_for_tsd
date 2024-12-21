package com.application.tm_application_for_tsd.viewModel

import androidx.lifecycle.viewModelScope
import com.application.tm_application_for_tsd.network.Api
import com.application.tm_application_for_tsd.utils.SPHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.tm_application_for_tsd.network.request_response.Article
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class CheckShkViewModel @Inject constructor(
    private val api: Api,
    private val spHelper: SPHelper
) : ViewModel() {

    private val _state = MutableStateFlow(CheckShkState())
    val state: StateFlow<CheckShkState> get() = _state

    /**
     * Основная функция поиска в Excel
     */
    fun findInExcel(shk: String, name: String) {
        viewModelScope.launch {
            updateState(isLoading = true, successMessage = null, errorMessage = null, showRewriteDialog = false)
            try {
                val response = api.getShk(name, shk)
                if (response.success && response.articuls.isNotEmpty()) {
                    val articul = response.articuls.first()
                    saveArticleData(articul)
                    updateState(successMessage = "Товар найден: ${articul.nazvanieTovara}", isLoading = false)
                } else {
                    searchArticleInDb(spHelper.getArticuleWork().toString())
                }
            } catch (e: Exception) {
                updateState(errorMessage = "Ошибка: ${e.localizedMessage}", isLoading = false)
            }
        }
    }

    /**
     * Функция поиска артикула в базе данных
     */
    private fun searchArticleInDb(shk: String) {
        viewModelScope.launch {
            try {
                val response = api.searchInDbForArticule(shk)
                if (response.success && response.value!=null) {
                    if(response.value[0].shk==null || response.value[0].shk.equals("null")){
                        updateState(showRewriteDialog = true, isLoading = false)
                    } else {
                        spHelper.setShkWork(response.value[0].shk)
                        updateShk(response.value[0].shk)
                    }
                } else {
                    updateState(showRewriteDialog = true, isLoading = false)
                }
            } catch (e: Exception) {
                updateState(errorMessage = "Ошибка: ${e.localizedMessage}", isLoading = false)
            }
        }
    }

    fun updateShk(shk: String){
        viewModelScope.launch {
            try {
                val response = api.updateShk(spHelper.getId(), shk)
                if(response!!.success){
                    updateState(successMessage = "ШК успешно перезаписан.", showRewriteDialog = false)
                } else  updateState(errorMessage = "Ошибка перезаписи ШК.", showRewriteDialog = false)


            }catch (e: Exception) {
                updateState(errorMessage = "Ошибка: ${e.localizedMessage}", isLoading = false)
            }
        }

    }
//
//    fun searchArticleInDbForSG(article: String) {
//        viewModelScope.launch {
//            try {
//                val response = api.searchInDbForArticule(article)
//                if(response.success && response.value!=null){
//                    if(response.value[0].periodWatch == 1 && response.value[0].periodDays>0){
//                        view.checkLastPeriodDate(response.value[0].periodDays)
//                    } else{
//                        view.writeLastDate()
//                    }
//                } else {
//                    view.errorMessage( "Артикул не найден!")
//                }
//            }
//        }
//    }
    /**
     * Сохранение данных артикула в SharedPreferences
     */
    private fun saveArticleData(articul: Article.Articuls) {
        articul.shk?.let { spHelper.setShkWork(it) }
        spHelper.setArticuleWork(articul.artikul.toString())
        articul.nazvanieTovara?.let { spHelper.setNameStuffWork(it) }
    }

    /**
     * Подтверждение перезаписи ШК
     */
    fun confirmRewriteShk(newShk: String) {
        viewModelScope.launch {
            try {
                spHelper.setShkWork(newShk)
                updateShk(newShk)
            } catch (e: Exception) {
                updateState(errorMessage = "Ошибка перезаписи ШК.", showRewriteDialog = false)
            }
        }
    }

    /**
     * Отмена перезаписи ШК
     */
    fun cancelRewriteShk() {
        updateState(showRewriteDialog = false)
    }

    /**
     * Обновление состояния
     */
    private fun updateState(
        isLoading: Boolean = false,
        successMessage: String? = null,
        errorMessage: String? = null,
        showRewriteDialog: Boolean = false
    ) {
        _state.value = _state.value.copy(
            isLoading = isLoading,
            successMessage = successMessage,
            errorMessage = errorMessage,
            showRewriteDialog = showRewriteDialog
        )
    }

    /**
     * Сброс состояния
     */
    fun resetState() {
        _state.value = CheckShkState()
    }
}

data class CheckShkState(
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val showRewriteDialog: Boolean = false
)
