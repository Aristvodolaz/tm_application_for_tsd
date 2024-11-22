package com.application.tm_application_for_tsd.network.request_response

import com.google.gson.annotations.SerializedName

data class ValidateBoxRequest (
    @SerializedName("sscc") val sscc: String,
    @SerializedName("pallet_scanned") val pallet: String
)

data class ValidateBoxResponse(
    @SerializedName("message") val message: String,
    @SerializedName("valid") val valid: Boolean
)