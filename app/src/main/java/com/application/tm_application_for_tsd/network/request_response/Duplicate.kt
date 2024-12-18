package com.application.tm_application_for_tsd.network.request_response

import com.google.gson.annotations.SerializedName

data class Duplicate(
    @SerializedName("taskName") val taskName: String,
    @SerializedName("articul") val articul: String?,
    @SerializedName("mesto") val mesto: String,
    @SerializedName("vlozhennost") val vlozhennost: String,
    @SerializedName("palletNo") val palletNo: String
)
