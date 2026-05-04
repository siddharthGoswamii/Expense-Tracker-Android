package com.expensetracker.model

data class AddExpenseRequest(
    val category: String,
    val amount: Int,
    val type: String
)