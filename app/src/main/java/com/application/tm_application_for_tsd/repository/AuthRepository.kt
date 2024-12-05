package com.application.tm_application_for_tsd.repository

import com.application.tm_application_for_tsd.network.Api
import com.application.tm_application_for_tsd.network.request_response.Employee
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authService: Api
) {

    suspend fun getEmployeeById(id: String): Employee {
        val response = authService.getEmployeeDetails(id)
        if (response.isAuthorized) {
            return Employee(
                id = response.id,
                name = response.name,
                position = response.position
            )
        } else {
            throw Exception("Employee not authorized")
        }
    }
}
