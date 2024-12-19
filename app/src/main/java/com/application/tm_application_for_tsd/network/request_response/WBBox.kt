package com.application.tm_application_for_tsd.network.request_response

import com.google.gson.annotations.SerializedName

data class WBBox(
    @SerializedName("success") val success: Boolean,
    @SerializedName("value") val value: List<Box>,
    @SerializedName("errorCode") val errorCode: Int

)

data class Box(
    @SerializedName("Nazvanie_Zadaniya") val nazvanieZadaniya: String,
    @SerializedName("Artikul") val artikul: Int,
    @SerializedName("Srok_Godnosti") val srokGodnosti: String,
    @SerializedName("SHK_WPS") val shkWps: String,
    @SerializedName("Pallet_No") val palletNo: String,
    @SerializedName("Kolvo_Tovarov") val kolvoTovarov: Int
)

data class WBPrivyazka(
    @SerializedName("success") val success: Boolean,
    @SerializedName("value") val msg: String,
    @SerializedName("errorCode") val code: Int
)

data class AddBox(
    val name: String,
    val artikul: Int,
    val kolvo: Int,
    val pallet: String,
    val shk: String
)
