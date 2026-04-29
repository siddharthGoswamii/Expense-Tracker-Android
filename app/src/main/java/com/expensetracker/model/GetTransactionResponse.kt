package com.expensetracker.model

data class GetTransactionResponse(
    val success: Boolean,
    val data: List<Transaction>,
    val pagination: Pagination
)

data class Transaction(
    @com.google.gson.annotations.SerializedName("_id") val id: String,
    val category: String,
    val amount: Int,
    val type: String
)
data class Pagination(
    val total: Int,
    val page: Int,
    val limit: Int,
    val totalPages: Int
)