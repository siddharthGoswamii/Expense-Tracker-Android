package com.expensetracker.model

data class AddIncomeRequest(
    val category: String,
    val amount: Int
)