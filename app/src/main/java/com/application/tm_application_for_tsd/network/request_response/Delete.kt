package com.application.tm_application_for_tsd.network.request_response

data class DeleteResponse(
    val success: Boolean,
    val value: String?,
    val errorCode: Int
)