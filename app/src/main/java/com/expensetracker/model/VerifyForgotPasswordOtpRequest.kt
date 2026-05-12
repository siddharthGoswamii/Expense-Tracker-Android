package com.expensetracker.model

data class VerifyForgotPasswordOtpRequest(
    val email: String,
    val otp: String
)