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
import com.expensetracker.model.VerifyOtpResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtpActivity : AppCompatActivity() {
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
        val userName = intent.getStringExtra("NAME_KEY") ?: ""
        val userPass = intent.getStringExtra("PASSWORD_KEY") ?: ""
        val userAvatar = intent.getStringExtra("AVATAR_KEY") ?: ""
        val userEmail = intent.getStringExtra("EMAIL_KEY") ?: ""
        
        val request = VerifyOtpRequest(userEmail, otp, userName, userPass, userAvatar)

        RetrofitClient.api.verifyOTP(request).enqueue(object : Callback<VerifyOtpResponse> {
            override fun onResponse(call: Call<VerifyOtpResponse>, response: Response<VerifyOtpResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val token = response.body()?.token
                    val userName = intent.getStringExtra("NAME_KEY") // Signup screen se laya hua naam

                    val sharedPref = getSharedPreferences("MyApp", MODE_PRIVATE)
                    val editor = sharedPref.edit()
                    editor.putString("token", token)
                    editor.putString("name", userName)
                    editor.apply()

                    Toast.makeText(this@OtpActivity, "Account Created Successfully!", Toast.LENGTH_SHORT).show()
                    val flow = intent.getStringExtra("flow")

                    if (flow == "forgot") {
                        // Agar forgot flow hai toh Reset Password page par bhejo
                        val intent = Intent(this@OtpActivity, ResetPasswordActivity::class.java)
                        intent.putExtra("email", userEmail)
                        intent.putExtra("otp", otp) // OTP bhi pass kar dena reset page ke liye
                        startActivity(intent)
                        finish()
                    } else {
                        // Purana Signup flow: Dashboard par bhejo
                        val intent = Intent(this@OtpActivity, DashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Toast.makeText(this@OtpActivity, "Invalid OTP", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<VerifyOtpResponse>, t: Throwable) {
                Toast.makeText(this@OtpActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}