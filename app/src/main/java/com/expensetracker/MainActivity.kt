package com.expensetracker

import android.R.attr.name
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.expensetracker.api.RetrofitClient
import com.expensetracker.model.LoginRequest
import com.expensetracker.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Intent
import android.util.Log
import android.widget.Toast
import kotlin.text.lowercase

class MainActivity : AppCompatActivity() {

    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var loginBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        loginBtn = findViewById(R.id.loginBtn)
        email.setHintTextColor(android.graphics.Color.parseColor("#555555"))
        password.setHintTextColor(android.graphics.Color.parseColor("#555555"))
        val backBtn = findViewById<TextView>(R.id.backBtn)
        backBtn.setOnClickListener {
            finish() // goes back to previous screen
        }


        loginBtn.setOnClickListener {

            val userEmail = email.text.toString().trim().lowercase()
            val userPassword = password.text.toString().trim()

            if (userEmail.isEmpty() || userPassword.isEmpty()) {
                Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(userEmail, userPassword)
        }
    }

    private fun loginUser(email: String, password: String) {




        val request = LoginRequest(email, password)

        RetrofitClient.api.login(request).enqueue(object : Callback<LoginResponse> {

            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful && response.body()?.success==true) {
                    Log.d("LOGIN_DEBUG", response.body().toString())

                    val token = response.body()?.token
                    val userName = response.body()?.user?.name

                    val sharedPref = getSharedPreferences("MyApp", MODE_PRIVATE)
                    sharedPref.edit().putString("token", token).apply()


//                    Toast.makeText(this@MainActivity, "Login Success", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MainActivity, DashboardActivity::class.java)
                    intent.putExtra("name", userName)

                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intent)
                    finish()

                    println("TOKEN: $token")

                } else {
                    Toast.makeText(this@MainActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}