package com.expensetracker.model

data class LoginResponse(
    val success: Boolean,
    val token: String,
    val user: User
)

data class User(
    val name: String,
    val email: String
)
