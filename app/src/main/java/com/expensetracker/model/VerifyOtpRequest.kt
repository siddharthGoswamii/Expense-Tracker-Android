package com.expensetracker.model

data class VerifyOtpRequest(
    val email: String,
    val otp: String,
    val name: String,
    val password: String,
    val avatar: String
)