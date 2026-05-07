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

        RetrofitClient.api.requestOTP(otpReq).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
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

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                signupBtn.isEnabled = true
                signupBtn.text = "Signup"
                Toast.makeText(this@SignupActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}


//class SignupActivity : AppCompatActivity() {
//
//    lateinit var step1Layout: LinearLayout
//    lateinit var step2Layout: LinearLayout
//
//    lateinit var name: EditText
//    lateinit var email: EditText
//    lateinit var password: EditText
//
//    lateinit var nextBtn: Button
//    lateinit var signupBtn: Button
//
//    lateinit var avatar1: ImageView
//    lateinit var avatar2: ImageView
//
//    var selectedAvatar = ""
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_signup)
//
//        step1Layout = findViewById(R.id.step1Layout)
//        step2Layout = findViewById(R.id.step2Layout)
//
//        name = findViewById(R.id.name)
//        email = findViewById(R.id.email)
//        password = findViewById(R.id.password)
//
//        nextBtn = findViewById(R.id.nextBtn)
//        signupBtn = findViewById(R.id.signupBtn)
//
//        avatar1 = findViewById(R.id.avatar1)
//        avatar2 = findViewById(R.id.avatar2)
//
//        nextBtn.setOnClickListener {
//            val userName = name.text.toString().trim()
//            val userEmail = email.text.toString().trim()
//            val userPassword = password.text.toString().trim()
//
//            if (userName.isEmpty() || userEmail.isEmpty()) {
//                Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//            if (userPassword.length < 6) {
//                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT)
//                    .show()
//                return@setOnClickListener
//            }
//
//            // Move to step 2
//            step1Layout.visibility = View.GONE
//            step2Layout.visibility = View.VISIBLE
//        }
//
////AVATAR SELECTION
//        avatar1.setOnClickListener {
//            selectedAvatar = "avatar1"
//            Toast.makeText(this, "Avatar 1 selected", Toast.LENGTH_SHORT).show()
//        }
//
//        avatar2.setOnClickListener {
//            selectedAvatar = "avatar2"
//            Toast.makeText(this, "Avatar 2 selected", Toast.LENGTH_SHORT).show()
//        }
//
////SIGNUP BUTTON
//        signupBtn.setOnClickListener {
//
//            if (selectedAvatar.isEmpty()) {
//                Toast.makeText(this, "Select avatar", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            val userName = name.text.toString().trim()
//            val userEmail = email.text.toString().trim()
//            val userPassword = password.text.toString().trim()
//            val userAvatar = selectedAvatar
//
//            val request = SignupRequest(
//                userName,
//                userEmail,
//                userPassword,
//
//            )
//
//            val otpReq = OtpRequest(email)
//            RetrofitClient.api.requestOTP(otpReq).enqueue(object : Callback<GenericResponse> {
//                override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
//                    if (response.isSuccessful && response.body()?.success == true) {
//                        // OTP chala gaya! Ab user ko OTP screen par bhejo
//                        val intent = Intent(this@SignupActivity, OtpActivity::class.java)
//                        intent.putExtra("EMAIL_KEY", email) // <--- Ye line sabse important hai
//                        startActivity(intent)
//                    } else {
//                        Toast.makeText(this@SignupActivity, "Failed to send OTP", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            val intent = Intent(this, OtpActivity::class.java)
//            intent.putExtra("EMAIL_KEY", emailField.text.toString())
//            startActivity(intent)
//
//            RetrofitClient.api.signup(request).enqueue(object : Callback<SignupResponse> {
//
//                override fun onResponse(
//                    call: Call<SignupResponse>,
//                    response: Response<SignupResponse>
//                ) {
//
//                    if (response.isSuccessful && response.body()?.success == true) {
//
//                        val token = response.body()?.token   //
//
//                        val sharedPref = getSharedPreferences("MyApp", MODE_PRIVATE)
//                        val editor = sharedPref.edit()
//                        editor.putString("token", token)
//                        editor.putString("name", userName) // Add this line to save the name!
//                        editor.apply()
////                        sharedPref.edit().putString("token", token).apply()
//
//                        Toast.makeText(this@SignupActivity, "Signup Successful", Toast.LENGTH_SHORT)
//                            .show()
//
//                        val intent = Intent(this@SignupActivity, DashboardActivity::class.java)
//                        intent.putExtra("name", userName)
//
//                        startActivity(intent)
//                        finish()
//
//                    } else {
//                        Toast.makeText(this@SignupActivity, "User already existed", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                }
//
//                override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
//                    Toast.makeText(this@SignupActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
//                        .show()
//                }
//            })
//        }
//    }
//}