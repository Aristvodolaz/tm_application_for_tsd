package com.application.tm_application_for_tsd.network.request_response

import com.google.gson.annotations.SerializedName

data class WBRequest(
    @SerializedName("Nazvanie_Zadaniya") val nazvanieZadaniya: String,
    @SerializedName("Artikul") val artikul: Int,
    @SerializedName("Kolvo_Tovarov") val kolvoTovarov: Int,
    @SerializedName("SHK_WPS") val shkWps: String,
    @SerializedName("Pallet_No") val palletNo: String
)
