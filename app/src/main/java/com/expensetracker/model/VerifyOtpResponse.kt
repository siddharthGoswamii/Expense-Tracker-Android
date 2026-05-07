package com.expensetracker.model

data class VerifyOtpResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    val name: String? = null
)