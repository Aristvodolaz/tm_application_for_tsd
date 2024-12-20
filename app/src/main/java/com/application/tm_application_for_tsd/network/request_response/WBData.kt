package com.application.tm_application_for_tsd.network.request_response

import com.google.gson.annotations.SerializedName

data class WBData(
    @SerializedName("success") val success: Boolean,
    @SerializedName("value") val value: List<WBItem>,
    @SerializedName("errorCode") val code: Int
)
data class WBItem (
    @SerializedName("Nazvanie_Zadaniya") val name: String,
    @SerializedName("Artikul") val  artikul: Int,
    @SerializedName("Srok_Godnosti") val sg: String,
    @SerializedName("SHK_WPS") val shk: String,
    @SerializedName("Pallet_No") val pallet: String,
    @SerializedName("Kolvo_Tovarov") val kolvo: Int
)


