package com.expensetracker.model

data class OtpRequest(
    val email: String
)

data class VerifyOtpRequest(
    val email: String, val otp: String
)
data class GenericResponse (
    val success: Boolean,
    val message: String
)