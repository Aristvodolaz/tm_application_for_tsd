package com.application.tm_application_for_tsd.network.request_response

data class GetSizeOtkaz(
    val success: Boolean,
    val value: Int,
    val errorCode: Int
)

data class GetTransferNumsDataRequest(
    val transferNums: List<Long> // Список номеров transfer_nums
)

data class GetTransferNumsDataResponse(
    val success: Boolean,          // Успех выполнения
    val value: List<TransferData>, // Список данных из таблицы
    val sum: Int,
    val factSum: Int,
    val errorCode: Int             // Код ошибки или 200 при успехе
)

data class TransferData(
    val transferNum: Long,         // TRANSFER_NUM
    val itemNum: String,           // ITEM_NUM
    val qtyOrdered: Int,           // QTY_ORDERED
    // Добавьте другие поля таблицы, если они присутствуют в ответе
    val qtyCommitted: Int?,        // QTY_COMMITTED (может быть null)
    val chz: String?               // CHZ (может быть null)
)

data class AddTaskDataRequest(
    val Nazvanie_Zadaniya: String, // Название задания
    val VP: String,              // VP
    val Artikul: String,         // Артикул
    val Plans: Int,              // План (целое число)
    val Fact: Int                // Факт (целое число)
)

data class AddTaskDataResponse(
    val success: Boolean,        // Успех выполнения
    val value: String?,          // Сообщение об успехе или null
    val errorCode: Int           // Код ошибки или 200 при успехе
)
