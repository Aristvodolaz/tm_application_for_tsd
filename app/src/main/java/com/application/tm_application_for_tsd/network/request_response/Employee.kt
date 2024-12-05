package com.application.tm_application_for_tsd.network.request_response

data class EmployeeResponse(
    val id: String,
    val name: String,
    val position: String,
    val isAuthorized: Boolean
)

data class Employee(
    val id: String,
    val name: String,
    val position: String
)
