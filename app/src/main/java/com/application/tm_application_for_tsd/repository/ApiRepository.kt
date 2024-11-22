package com.application.tm_application_for_tsd.repository

import com.application.tm_application_for_tsd.network.Api
import com.application.tm_application_for_tsd.network.request_response.ValidateBoxRequest
import com.application.tm_application_for_tsd.network.request_response.ValidateBoxResponse
import javax.inject.Inject

class ApiRepository @Inject constructor(private val api: Api) {

        suspend fun checkValidateBox(sscc: String, pallet: String): ValidateBoxResponse {
            val request = ValidateBoxRequest(sscc, pallet)
            val response = api.checkValidateBox(request)

            if (response.isSuccessful) {
                return response.body() ?: ValidateBoxResponse("Unknown error",false)
            } else {
                throw Exception("Error: ${response.code()} - ${response.message()}")
            }
        }
    }
