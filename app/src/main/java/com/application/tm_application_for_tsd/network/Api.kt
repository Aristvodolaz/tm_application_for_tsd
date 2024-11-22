package com.application.tm_application_for_tsd.network

import com.application.tm_application_for_tsd.network.request_response.ValidateBoxRequest
import com.application.tm_application_for_tsd.network.request_response.ValidateBoxResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface Api {
    @POST("api/tm/validateBox")
    suspend fun checkValidateBox(@Body barcodeData: ValidateBoxRequest): Response<ValidateBoxResponse>
}