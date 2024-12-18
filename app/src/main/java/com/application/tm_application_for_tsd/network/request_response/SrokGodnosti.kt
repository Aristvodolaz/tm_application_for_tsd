package com.application.tm_application_for_tsd.network.request_response
import com.google.gson.annotations.SerializedName

data class SrokGodnosti (
    val name: String,
    val artikul: Int,
    val srok_godnosti: String
)

data class UpdateSrokGodnosti(
    @SerializedName("srokGodnosti") val srok: String,
    @SerializedName("persent") val persent: String,
    @SerializedName("articul") val articul: String,
    @SerializedName("taskName") val taskName: String
)
