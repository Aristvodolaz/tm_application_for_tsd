package com.application.tm_application_for_tsd.viewModel

import androidx.lifecycle.viewModelScope
import com.application.tm_application_for_tsd.network.Api
import com.application.tm_application_for_tsd.utils.SPHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.ViewModel
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
                    searchArticleInDb(shk)
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
                val response = api.getArticulTask(spHelper.getTaskName() ?: "", shk)
                if (response.success && response.articuls.isEmpty()) {
                    // Если ШК не найден, показываем диалог
                    updateState(showRewriteDialog = true, isLoading = false)
                } else {
                    updateState(errorMessage = "Артикул не найден в базе данных.", isLoading = false)
                }
            } catch (e: Exception) {
                updateState(errorMessage = "Ошибка: ${e.localizedMessage}", isLoading = false)
            }
        }
    }

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
                updateState(successMessage = "ШК успешно перезаписан.", showRewriteDialog = false)
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
