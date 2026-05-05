package com.expensetracker.model

import androidx.lifecycle.LiveData

data class ProfileResponse(
    val success: Boolean,
    val data: UserData
)

data class UserData(
    val id: String,
    val name: String,
    val email: String,
    val joinedAt: String, // Backend se ISO date string aayegi
    val profileImage: String? = null
)