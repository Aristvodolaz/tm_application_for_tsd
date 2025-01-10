package com.application.tm_application_for_tsd.repository

import com.application.tm_application_for_tsd.network.Api
import com.application.tm_application_for_tsd.network.request_response.AuthResponse
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authService: Api
) {

    suspend fun getEmployeeById(id: String): AuthResponse {
        val response = authService.getEmployeeDetails(id)
        return if (response.success) {
            response
        } else {
            throw Exception("Ошибка авторизации: код ${response.errorCode}")
        }
    }
}
