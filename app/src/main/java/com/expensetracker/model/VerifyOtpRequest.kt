package com.expensetracker.model

data class VerifyOtpRequest(
    val email: String,
    val otp: String
)