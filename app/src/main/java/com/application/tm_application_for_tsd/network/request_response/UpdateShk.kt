package com.application.tm_application_for_tsd.network.request_response

import com.google.gson.annotations.SerializedName

data class UpdateShk (
        @SerializedName("taskName") val taskName: String,
        @SerializedName("articul") val articul: String,
        @SerializedName("newSHK") val newShk: String
)