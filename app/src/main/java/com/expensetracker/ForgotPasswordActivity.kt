package com.expensetracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.expensetracker.api.RetrofitClient
import com.expensetracker.model.ForgotPasswordRequest
import com.expensetracker.model.ForgotPasswordResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var emailInput: EditText
    private lateinit var sendOtpBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val backBtn = findViewById<Button>(R.id.backBtn)
        backBtn.setOnClickListener {
            startActivityForResult(Intent(this, MainActivity::class.java), 100)
            finish()
        }

        emailInput = findViewById(R.id.emailInput)
        val sendOtpBtn = findViewById<Button>(R.id.sendOtpBtn)
        sendOtpBtn.setOnClickListener {
            val email = emailInput.text.toString().trim()
            if (email.isNotEmpty()) {
                sendOtpBtn.isEnabled = false
                sendOtpBtn.text = "Sending OTP..."
                performOtpRequest(email)
            } else {
                Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show()
            }
        }

        val loginLink = findViewById<TextView>(R.id.loginLink)
        loginLink.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)

            // Ye flags stack ko clear kar denge
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

            startActivity(intent)
            finish() // Current activity ko destroy kar do
        }
    }

    private fun performOtpRequest(string: String) {
        val otpReq = ForgotPasswordRequest(string)

        RetrofitClient.api.forgotPassword(otpReq).enqueue(object : Callback<ForgotPasswordResponse> {
            override fun onResponse(call: Call<ForgotPasswordResponse>, response: Response<ForgotPasswordResponse>) {

                // Response aate hi button handling (Optional: agar next screen pe ja rahe ho toh zaroorat nahi, par error ke liye chahiye)
                if (!(response.isSuccessful && response.body()?.success == true)) {
                    sendOtpBtn.isEnabled = true
                    sendOtpBtn.text = "Send OTP"
                }

                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@ForgotPasswordActivity, "OTP sent to your email", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java)
                    intent.putExtra("email", string)
                    startActivity(intent)
                    finish()

                } else {
                    val errorMsg = response.body()?.message ?: "User not found"
                    Toast.makeText(this@ForgotPasswordActivity, errorMsg, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                sendOtpBtn.isEnabled = true
                sendOtpBtn.text = "Send OTP"
                Toast.makeText(this@ForgotPasswordActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }
}
