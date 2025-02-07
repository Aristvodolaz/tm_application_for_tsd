package com.application.tm_application_for_tsd.network.request_response

import com.google.gson.annotations.SerializedName

data class OzonRequest(
    @SerializedName("ID") val id: Long?,
    @SerializedName("Nazvanie_Zadaniya") val nazvanieZadaniya: String,
    @SerializedName("Artikul") val artikul: Int,
    @SerializedName("Mesto") val mesto: Int,
    @SerializedName("Vlozhennost") val vlozhennost: Int,
    @SerializedName("Pallet_No") val palletNo: Int,
    @SerializedName("Time_End") val timeEnd: String
)
