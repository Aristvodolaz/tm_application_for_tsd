package com.application.tm_application_for_tsd.network.request_response

import com.google.gson.annotations.SerializedName

data class UpdateStatusRequest(
    @SerializedName("id") val id: Long,             // Идентификатор задачи
    @SerializedName("endTime") val endTime: String,  // Время завершения задачи
    @SerializedName("ispolnitel") val ispolnitel: String  // Исполнитель задачи
)
