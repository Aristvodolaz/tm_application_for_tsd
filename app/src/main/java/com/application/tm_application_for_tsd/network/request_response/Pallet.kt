package com.application.tm_application_for_tsd.network.request_response

import com.google.gson.annotations.SerializedName

data class Pallet(
    @SerializedName("success") val success: Boolean,
    @SerializedName("value") val value: ArticleForPallet,
    @SerializedName("errorCode") val errorCode: Int
)

data class ArticleForPallet(
    @SerializedName("articles") val articles: List<Articles>,
    @SerializedName("totalPlaces") val totalBox: Int
)

data class Articles(
    @SerializedName("Artikul") val articul: Int,
    @SerializedName("Kolvo_Tovarov") val nazvanieTovara: Int,
    @SerializedName("Mesto") val mesto: Int
)

