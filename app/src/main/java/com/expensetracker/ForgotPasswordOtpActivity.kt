package com.expensetracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.expensetracker.api.RetrofitClient
import com.expensetracker.model.VerifyForgotPasswordOtpRequest
import com.expensetracker.model.VerifyForgotPasswordOtpResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordOtpActivity : AppCompatActivity() {

    private lateinit var btnVerify: Button
    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp) // Purana layout reuse kar lo ya naya wala dalo

        // 1. ForgotPasswordActivity se bheja hua email catch karo
        userEmail = intent.getStringExtra("email")
        val otp1 = findViewById<EditText>(R.id.otp1)
        val otp2 = findViewById<EditText>(R.id.otp2)
        val otp3 = findViewById<EditText>(R.id.otp3)
        val otp4 = findViewById<EditText>(R.id.otp4)

        val btnVerify = findViewById<Button>(R.id.btnVerify)
        otp1.addTextChangedListener {
            if (it?.length == 1) otp2.requestFocus()
        }
        otp2.addTextChangedListener {
            if (it?.length == 1) otp3.requestFocus()
            else if (it?.isEmpty() == true) otp1.requestFocus()
        }
        otp3.addTextChangedListener {
            if (it?.length == 1) otp4.requestFocus()
            else if (it?.isEmpty() == true) otp2.requestFocus()
        }
        otp4.addTextChangedListener {
            if (it?.isEmpty() == true) otp3.requestFocus()
        }

        btnVerify.setOnClickListener {
            val otp = "${otp1.text}${otp2.text}${otp3.text}${otp4.text}"
            if (otp.length == 4) {
                performOtpVerification(otp)
            } else {
                Toast.makeText(this, "Enter 4 digits", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performOtpVerification(otp: String) {
        btnVerify.isEnabled = false
        btnVerify.text = "Verifying..."

        val request = VerifyForgotPasswordOtpRequest(userEmail ?: "", otp)

        RetrofitClient.api.verifyForgotPasswordOTP(request).enqueue(object : Callback<VerifyForgotPasswordOtpResponse> {
            override fun onResponse(call: Call<VerifyForgotPasswordOtpResponse>, response: Response<VerifyForgotPasswordOtpResponse>) {

                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@ForgotPasswordOtpActivity, "OTP Verified!", Toast.LENGTH_SHORT).show()

                    // SUCCESS: Ab Reset Password page par bhejo
                    val intent = Intent(this@ForgotPasswordOtpActivity, ResetPasswordActivity::class.java)
                    intent.putExtra("email", userEmail)
                    intent.putExtra("otp", otp) // Reset page pe final verification ke liye bhej rahe hain
                    startActivity(intent)
                    finish()
                } else {
                    btnVerify.isEnabled = true
                    btnVerify.text = "VERIFY"
                    val errorMsg = response.body()?.message ?: "Invalid OTP"
                    Toast.makeText(this@ForgotPasswordOtpActivity, errorMsg, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<VerifyForgotPasswordOtpResponse>, t: Throwable) {
                btnVerify.isEnabled = true
                btnVerify.text = "VERIFY"
                Toast.makeText(this@ForgotPasswordOtpActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}