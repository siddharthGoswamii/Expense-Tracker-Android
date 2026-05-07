package com.expensetracker.api

import com.expensetracker.model.AddExpenseRequest
import com.expensetracker.model.AddExpenseResponse
import com.expensetracker.model.AddIncomeRequest
import com.expensetracker.model.AddIncomeResponse
import com.expensetracker.model.BreakdownResponse
import com.expensetracker.model.DeleteResponse
import com.expensetracker.model.GetTransactionResponse
import com.expensetracker.model.LoginRequest
import com.expensetracker.model.LoginResponse
import com.expensetracker.model.ProfileResponse
import com.expensetracker.model.SignupRequest
import com.expensetracker.model.SignupResponse
import com.expensetracker.model.SummaryResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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

    @POST("transactions/add")
    fun addTransaction(
        @Header("Authorization") token: String,
        @Body request: AddIncomeRequest
    ): Call<AddIncomeResponse>


    @GET("transactions/all")
    fun getTransactions(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Call<GetTransactionResponse>

    @DELETE("transactions/{id}")
    fun deleteTransaction(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<DeleteResponse>

    @GET("transactions/categories")
    fun getCategoryBreakdown(
        @Header("Authorization") token: String,
        @Query("type") type: String
    ): Call<BreakdownResponse>

    @GET("auth/profile") // Path yahan dalo (backend ke routes ke hisaab se)
    fun getProfile(
        @Header("Authorization") token: String
    ): Call<ProfileResponse>

    @GET("transactions/summary") // Is path ko apne backend routes se verify kar lena
    fun getSummary(
        @Header("Authorization") token: String
    ): Call<SummaryResponse>

    interface ApiService {
        // 1. OTP Request bhejne ke liye
        @POST("api/auth/request-otp")
        fun requestOTP(@Body request: Map<String, String>): Call<GenericResponse>

        // 2. OTP Verify karne ke liye
        @POST("api/auth/verify-otp")
        fun verifyOTP(@Body request: Map<String, String>): Call<GenericResponse>
    }
}