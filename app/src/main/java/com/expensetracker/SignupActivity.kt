package com.expensetracker

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.Button
import android.view.View
import android.content.Intent
import com.expensetracker.api.RetrofitClient
import com.expensetracker.model.SignupRequest
import com.expensetracker.model.SignupResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SignupActivity : AppCompatActivity() {

    lateinit var step1Layout: LinearLayout
    lateinit var step2Layout: LinearLayout

    lateinit var name: EditText
    lateinit var email: EditText
    lateinit var password: EditText

    lateinit var nextBtn: Button
    lateinit var signupBtn: Button

    lateinit var avatar1: ImageView
    lateinit var avatar2: ImageView

    var selectedAvatar = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        step1Layout = findViewById(R.id.step1Layout)
        step2Layout = findViewById(R.id.step2Layout)

        name = findViewById(R.id.name)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)

        nextBtn = findViewById(R.id.nextBtn)
        signupBtn = findViewById(R.id.signupBtn)

        avatar1 = findViewById(R.id.avatar1)
        avatar2 = findViewById(R.id.avatar2)

        nextBtn.setOnClickListener {
            val userName = name.text.toString().trim()
            val userEmail = email.text.toString().trim()
            val userPassword = password.text.toString().trim()

            if (userName.isEmpty() || userEmail.isEmpty()) {
                Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (userPassword.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Move to step 2
            step1Layout.visibility = View.GONE
            step2Layout.visibility = View.VISIBLE
        }

//AVATAR SELECTION
        avatar1.setOnClickListener {
            selectedAvatar = "avatar1"
            Toast.makeText(this, "Avatar 1 selected", Toast.LENGTH_SHORT).show()
        }

        avatar2.setOnClickListener {
            selectedAvatar = "avatar2"
            Toast.makeText(this, "Avatar 2 selected", Toast.LENGTH_SHORT).show()
        }

//SIGNUP BUTTON
        signupBtn.setOnClickListener {

            if (selectedAvatar.isEmpty()) {
                Toast.makeText(this, "Select avatar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userName = name.text.toString().trim()
            val userEmail = email.text.toString().trim()
            val userPassword = password.text.toString().trim()
            val userAvatar = selectedAvatar

            val request = SignupRequest(
                userName,
                userEmail,
                userPassword,

            )

            RetrofitClient.api.signup(request).enqueue(object : Callback<SignupResponse> {

                override fun onResponse(
                    call: Call<SignupResponse>,
                    response: Response<SignupResponse>
                ) {

                    if (response.isSuccessful && response.body()?.success == true) {

                        val token = response.body()?.token   //

                        val sharedPref = getSharedPreferences("MyApp", MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        editor.putString("token", token)
                        editor.putString("name", userName) // Add this line to save the name!
                        editor.apply()
//                        sharedPref.edit().putString("token", token).apply()

                        Toast.makeText(this@SignupActivity, "Signup Successful", Toast.LENGTH_SHORT)
                            .show()

                        val intent = Intent(this@SignupActivity, DashboardActivity::class.java)
                        intent.putExtra("name", userName)

                        startActivity(intent)
                        finish()

                    } else {
                        Toast.makeText(this@SignupActivity, "User already existed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                    Toast.makeText(this@SignupActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }

//            Toast.makeText(this, "Signup flow ready", Toast.LENGTH_SHORT).show()
    }
}