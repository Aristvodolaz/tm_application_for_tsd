package com.application.tm_application_for_tsd.network.request_response

import com.google.gson.annotations.SerializedName

data class FinishOzon (
    @SerializedName("taskName") val taskName: String,
    @SerializedName("shk") val shk: String,
    @SerializedName("mesto") val mesto: String,
    @SerializedName("vlozhennost") val vlozhennost: String,
    @SerializedName("palletNo") val palletNo: String,
    @SerializedName("timeEnd") val timeEnd: String
)