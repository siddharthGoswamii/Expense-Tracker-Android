package com.expensetracker.model

data class SignupResponse(
    val success: Boolean,
    val message: String,
    val token: String
)