package com.expensetracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.expensetracker.api.RetrofitClient
import com.expensetracker.model.GenericResponse
import com.expensetracker.model.OtpRequest
import com.expensetracker.model.OtpResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {

    private lateinit var step1Layout: LinearLayout
    private lateinit var step2Layout: LinearLayout

    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText

    private lateinit var nextBtn: Button
    private lateinit var signupBtn: Button

    private lateinit var avatar1: ImageView
    private lateinit var avatar2: ImageView

    private var selectedAvatar = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize UI Elements
        step1Layout = findViewById(R.id.step1Layout)
        step2Layout = findViewById(R.id.step2Layout)

        name = findViewById(R.id.name)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)

        nextBtn = findViewById(R.id.nextBtn)
        signupBtn = findViewById(R.id.signupBtn)

        avatar1 = findViewById(R.id.avatar1)
        avatar2 = findViewById(R.id.avatar2)

        // --- STEP 1: VALIDATION & NAVIGATION ---
        nextBtn.setOnClickListener {
            val userName = name.text.toString().trim()
            val userEmail = email.text.toString().trim()
            val userPassword = password.text.toString().trim()

            if (userName.isEmpty() || userEmail.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (userPassword.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Move to Avatar selection
            step1Layout.visibility = View.GONE
            step2Layout.visibility = View.VISIBLE
        }

        // --- STEP 2: AVATAR SELECTION ---
        avatar1.setOnClickListener {
            selectedAvatar = "avatar1"
            Toast.makeText(this, "Avatar 1 selected", Toast.LENGTH_SHORT).show()
        }

        avatar2.setOnClickListener {
            selectedAvatar = "avatar2"
            Toast.makeText(this, "Avatar 2 selected", Toast.LENGTH_SHORT).show()
        }

        // --- FINAL STEP: REQUEST OTP ---
        signupBtn.setOnClickListener {
            if (selectedAvatar.isEmpty()) {
                Toast.makeText(this, "Please select an avatar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            performOtpRequest()
        }
    }

    private fun performOtpRequest() {
        val userEmail = email.text.toString().trim()
        val userName = name.text.toString().trim()
        val userPassword = password.text.toString().trim()

        // Disable button to prevent multiple clicks
        signupBtn.isEnabled = false
        signupBtn.text = "Sending OTP..."

        val otpReq = OtpRequest(userEmail)

        RetrofitClient.api.requestOTP(otpReq).enqueue(object : Callback<OtpResponse> {
            override fun onResponse(call: Call<OtpResponse>, response: Response<OtpResponse>) {
                signupBtn.isEnabled = true
                signupBtn.text = "Signup"

                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@SignupActivity, "OTP sent to your email", Toast.LENGTH_SHORT).show()

                    // Move to OtpActivity and pass ALL data needed for eventual Signup
                    val intent = Intent(this@SignupActivity, OtpActivity::class.java)
                    intent.putExtra("EMAIL_KEY", userEmail)
                    intent.putExtra("NAME_KEY", userName)
                    intent.putExtra("PASSWORD_KEY", userPassword)
                    intent.putExtra("AVATAR_KEY", selectedAvatar)

                    startActivity(intent)
                } else {
                    Toast.makeText(this@SignupActivity, "Failed to send OTP. Check email or try again.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<OtpResponse>, t: Throwable) {
                signupBtn.isEnabled = true
                signupBtn.text = "Signup"
                Toast.makeText(this@SignupActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}