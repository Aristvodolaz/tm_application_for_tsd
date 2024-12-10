package com.application.tm_application_for_tsd.network.request_response
data  class Task (
    val success: Boolean,
    val value: List<Data>,
    val errorCode: Int
)

data class Data(
    val Nazvanie_Zadaniya: String,
    val Scklad_Pref: String
)