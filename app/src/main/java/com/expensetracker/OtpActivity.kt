package com.expensetracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.expensetracker.api.RetrofitClient
import com.expensetracker.model.GenericResponse
import com.expensetracker.model.VerifyOtpRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtpActivity : AppCompatActivity() {

    // 1. Variable yahan declare karo
    private lateinit var userEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        // 2. Intent se email receive karo (Jo SignupActivity se bheja tha)
        userEmail = intent.getStringExtra("EMAIL_KEY") ?: ""

        val etOtp = findViewById<EditText>(R.id.etOtp)
        val btnVerify = findViewById<Button>(R.id.btnVerify)

        btnVerify.setOnClickListener {
            val code = etOtp.text.toString()
            if (code.length == 4) {
                verifyCode(code)
            } else {
                Toast.makeText(this, "Enter 4 digits", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun verifyCode(otp: String) {
        // Ab userEmail error nahi dega
        val request = VerifyOtpRequest(userEmail, otp)

        // Interface mein check karo 'verifyOTP' hi naam hai na?
        RetrofitClient.api.verifyOTP(request).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@OtpActivity, "Verified!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@OtpActivity, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@OtpActivity, "Invalid OTP", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                Toast.makeText(this@OtpActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}