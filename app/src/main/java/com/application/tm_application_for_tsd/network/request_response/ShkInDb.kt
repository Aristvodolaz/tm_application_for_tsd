package com.application.tm_application_for_tsd.network.request_response

import com.google.gson.annotations.SerializedName

data class ShkInDb(
    @SerializedName("success") val success: Boolean,
    @SerializedName("value") val value: List<ShkInfo>,
    @SerializedName("errorCode") val errorCode: Int
) {
    data class ShkInfo(
        @SerializedName("ID") val article: String,
        @SerializedName("ARTICLE_MEASURE_ID") val articleMeasure: String,
        @SerializedName("IS_ACTIVE") val status: Int,
        @SerializedName("NAME") val name: String,
        @SerializedName("PIECE_GTIN") val shk: String,
        @SerializedName("FPACK_GTIN") val shkFP: String,
        @SerializedName("IS_VALID_PERIOD_WATCH") val periodWatch: Int,
        @SerializedName("VALID_PERIOD_DAYS") val periodDays: Int
    )
}
