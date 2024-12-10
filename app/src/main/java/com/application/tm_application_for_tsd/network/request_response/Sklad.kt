package com.application.tm_application_for_tsd.network.request_response

import com.google.gson.annotations.SerializedName

data class Sklad(
    @SerializedName("success") val success: Boolean,
    @SerializedName("value") val value: List<Value>,
    @SerializedName("errorCode") val code: Int
)

data class Value(
    @SerializedName("Pref") val pref: String,
    @SerializedName("Name") val name: String,
    @SerializedName("City") val city: String
)