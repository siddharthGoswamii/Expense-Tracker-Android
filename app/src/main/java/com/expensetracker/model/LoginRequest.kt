package com.expensetracker.model

data class LoginRequest(
    val email: String,
    val password: String
)