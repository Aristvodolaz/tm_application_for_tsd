package com.application.tm_application_for_tsd.network.request_response

import com.google.gson.annotations.SerializedName

data class  Universal(
    @SerializedName("success") val success: Boolean,
    @SerializedName("value") val value: String,
    @SerializedName("errorCode") val errorCode: Int
)

