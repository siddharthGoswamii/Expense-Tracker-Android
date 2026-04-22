package com.expensetracker.model

data class SignupRequest(
    val name: String,
    val email: String,
    val password: String
)

