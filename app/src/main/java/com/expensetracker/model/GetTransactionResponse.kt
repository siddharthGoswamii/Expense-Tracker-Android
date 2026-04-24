package com.expensetracker.model

data class GetTransactionResponse(
    val success: Boolean,
    val data: List<Transaction>,
    val pagination: Pagination
)

data class Transaction(
    val category: String,
    val amount: Int
)
data class Pagination(
    val total: Int,
    val page: Int,
    val limit: Int,
    val totalPages: Int
)