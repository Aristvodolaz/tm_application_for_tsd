package com.application.tm_application_for_tsd.network.request_response

data class GetSizeOtkaz(
    val success: Boolean,
    val value: Int,
    val errorCode: Int
)