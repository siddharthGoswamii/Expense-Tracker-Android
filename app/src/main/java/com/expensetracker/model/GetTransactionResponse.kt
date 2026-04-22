package com.expensetracker.model

data class GetTransactionResponse(
    val success: Boolean,
    val data: List<Transaction>
)

data class Transaction(
    val category: String,
    val amount: Int
)