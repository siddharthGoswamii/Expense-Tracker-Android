package com.expensetracker.model

data class SummaryResponse(
    val success: Boolean,
    val data: SummaryData
)
data class SummaryData(
    val totalIncome: Int,
    val totalExpense: Int
)