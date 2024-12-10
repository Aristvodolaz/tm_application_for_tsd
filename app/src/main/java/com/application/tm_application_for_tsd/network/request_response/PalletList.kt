package com.application.tm_application_for_tsd.network.request_response

import com.google.gson.annotations.SerializedName

data class PalletList(
    @SerializedName("success") val success: Boolean,
    @SerializedName("value") val value: ValuePallet,
    @SerializedName("errorCode") val errorCode: Int
)

data class ValuePallet(
    @SerializedName("pallets") val pallet_no: List<Pallets>,
    @SerializedName("totalPlaces") val total: Int
)

data class Pallets(
    @SerializedName("Pallet_No") val pallet_no: String,
    @SerializedName("Total_Kolvo") val total: Int
)