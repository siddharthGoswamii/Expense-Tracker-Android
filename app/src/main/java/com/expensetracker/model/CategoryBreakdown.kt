package com.expensetracker.model

data class CategoryBreakdown(
    @com.google.gson.annotations.SerializedName("_id")
    val id: String,
    @com.google.gson.annotations.SerializedName("totalAmount")
    val totalAmount: Int
)

data class BreakdownResponse(
    val success: Boolean,
    val data: List<CategoryBreakdown>
)