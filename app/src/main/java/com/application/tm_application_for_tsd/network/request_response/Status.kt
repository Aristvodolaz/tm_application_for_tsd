package com.application.tm_application_for_tsd.network.request_response

import com.google.gson.annotations.SerializedName

data class Status(
    @SerializedName("taskName") val taskName: String,
    @SerializedName("articul") val articul: String,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("status") val status: Int,
    @SerializedName("ispolnitel") val ispolnitel: String
)