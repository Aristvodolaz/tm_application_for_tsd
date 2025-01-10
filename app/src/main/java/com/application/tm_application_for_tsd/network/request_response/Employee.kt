package com.application.tm_application_for_tsd.network.request_response

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("value") val value: List<Value>?,
    @SerializedName("errorCode") val errorCode: Int
) {
    data class Value(
        @SerializedName("ID") val id: Int,
        @SerializedName("FULL_NAME") val name: String
    )
}

