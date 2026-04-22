package com.expensetracker.api

import com.expensetracker.model.AddExpenseRequest
import com.expensetracker.model.AddExpenseResponse
import com.expensetracker.model.GetTransactionResponse
import com.expensetracker.model.LoginRequest
import com.expensetracker.model.LoginResponse
import com.expensetracker.model.SignupRequest
import com.expensetracker.model.SignupResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
    @POST("auth/signup")
    fun signup(@Body request: SignupRequest): Call<SignupResponse>
    @POST("transactions/add")
    fun addTransaction(
        @Header("Authorization") token: String,
        @Body request: AddExpenseRequest
    ): Call<AddExpenseResponse>


    @GET("transactions/all")
    fun getTransactions(
        @Header("Authorization") token: String
    ): Call<GetTransactionResponse>
}