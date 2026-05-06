package com.expensetracker.model

import com.google.gson.annotations.SerializedName

data class CategoryBreakdown(
    @SerializedName("_id")
    val id: String,
    val totalAmount: Int
)

data class BreakdownResponse(
    val success: Boolean,
    val data: List<CategoryBreakdown>
)