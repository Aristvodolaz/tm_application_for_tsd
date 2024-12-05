package com.application.tm_application_for_tsd.network

import com.application.tm_application_for_tsd.network.request_response.EmployeeResponse
import com.application.tm_application_for_tsd.network.request_response.ValidateBoxRequest
import com.application.tm_application_for_tsd.network.request_response.ValidateBoxResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface Api {
    @POST("/api/tm/validateBox")
    suspend fun checkValidateBox(@Body barcodeData: ValidateBoxRequest): Response<ValidateBoxResponse>

    @GET("/api/employee/{id}")
    suspend fun getEmployeeDetails(@Path("id") id: String): EmployeeResponse
}