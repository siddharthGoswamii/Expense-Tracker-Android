package com.expensetracker.model

data class CategoryBreakdown(
    val id: String,
    val totalAmount: Int
)

data class BreakdownResponse(
    val success: Boolean,
    val data: List<CategoryBreakdown>
)