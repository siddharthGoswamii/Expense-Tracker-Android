package com.expensetracker

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.expensetracker.api.RetrofitClient
import com.expensetracker.model.ForgotPasswordResponse
import com.expensetracker.model.GenericResponse
import com.expensetracker.model.ResetPasswordRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var etNewPass: EditText
    private lateinit var etConfirmPass: EditText
    private lateinit var btnReset: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        // Intent se data nikaalo
        val email = intent.getStringExtra("email") ?: ""
        val otp = intent.getStringExtra("otp") ?: "" // OtpActivity se pass karwa lena

        etNewPass = findViewById(R.id.etNewPassword)
        etConfirmPass = findViewById(R.id.etConfirmPassword)
        btnReset = findViewById(R.id.btnReset)

        btnReset.setOnClickListener {
            val pass1 = etNewPass.text.toString().trim()
            val pass2 = etConfirmPass.text.toString().trim()

            // Validation: Matching check
            if (pass1.isEmpty() || pass2.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else if (pass1 != pass2) {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
            } else if (pass1.length < 6) {
                Toast.makeText(this, "Password too short (min 6 chars)", Toast.LENGTH_SHORT).show()
            } else {
                performReset(email, otp, pass1)
            }
        }

        val loginLink = findViewById<TextView>(R.id.loginLink)
        loginLink.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)

            // Ye flags stack ko clear kar denge
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

            startActivity(intent)
            finish()
        }
    }

    private fun performReset(email: String, otp: String, pass: String) {
        btnReset.isEnabled = false
        btnReset.text = "Updating..."

        val request = ResetPasswordRequest(email, otp, pass)

        RetrofitClient.api.resetPassword(request).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(
                call: Call<GenericResponse>,
                response: Response<GenericResponse>
            ) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(
                        this@ResetPasswordActivity,
                        "Password reset successful! Please Login.",
                        Toast.LENGTH_LONG
                    ).show()

                        // Direct to Login and Clear Stack
                    val intent = Intent(this@ResetPasswordActivity, MainActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                } else {
                    btnReset.isEnabled = true
                    btnReset.text = "RESET PASSWORD"
                        Toast.makeText(
                            this@ResetPasswordActivity,
                            "Error: ${response.body()?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                btnReset.isEnabled = true
                btnReset.text = "RESET PASSWORD"
                Toast.makeText(
                    this@ResetPasswordActivity,
                    "Network Failure",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
