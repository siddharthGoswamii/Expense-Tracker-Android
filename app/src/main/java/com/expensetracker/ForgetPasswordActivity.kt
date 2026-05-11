package com.expensetracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.expensetracker.api.RetrofitClient
import com.expensetracker.model.ForgotPasswordRequest
import com.expensetracker.model.ForgotPasswordResponse
import com.expensetracker.model.OtpRequest
import com.expensetracker.model.OtpResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgetPasswordActivity : AppCompatActivity() {
    private lateinit var emailInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        val backBtn = findViewById<Button>(R.id.backBtn)
        backBtn.setOnClickListener {
            startActivityForResult(Intent(this, MainActivity::class.java), 100)
            finish()
        }
        val loginLink = findViewById<TextView>(R.id.loginLink)
        loginLink.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)

            // Ye flags stack ko clear kar denge
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

            startActivity(intent)
            finish() // Current activity ko destroy kar do
        }
        performOtpRequest()
    }

    private fun performOtpRequest() {
        val userEmail = emailInput.text.toString().trim()
        val otpReq = ForgotPasswordRequest(userEmail)

        RetrofitClient.api.forgotPassword(otpReq).enqueue(object : Callback<ForgotPasswordResponse> {
            override fun onResponse(call: Call<ForgotPasswordResponse>, response: Response<ForgotPasswordResponse>) {

                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@ForgetPasswordActivity, "OTP sent to your email", Toast.LENGTH_SHORT).show()

                } else {
                    val errorMsg = response.body()?.message ?: "User not found"
                    Toast.makeText(this@ForgetPasswordActivity, errorMsg, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                Toast.makeText(this@ForgetPasswordActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }
}
