package com.expensetracker

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.expensetracker.api.RetrofitClient
import com.expensetracker.model.ProfileResponse
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.expensetracker.databinding.ActivityProfileBinding



class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Setup View Binding
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        // 2. Fetch data from Backend (Priority)
        fetchUserProfile()

        // 3. Optional: Initial data from SharedPreferences (for instant loading)
        val sharedPref = getSharedPreferences("MyApp", MODE_PRIVATE)
        binding.profileName.text = sharedPref.getString("name", "User")
        binding.profileEmail.text = sharedPref.getString("userEmail", "Email not found")

        // 4. Handle Logout click using binding
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun showLogoutConfirmation() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout from ExpenseTracker?")

        builder.setPositiveButton("Logout") { _, _ ->
            performLogout()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()

        // Styling the buttons
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY)
    }

    private fun performLogout() {
        // 1. Clear SharedPreferences
        val sharedPref = getSharedPreferences("MyApp", MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear()
            apply()
        }

        // 2. Redirect to Auth Screen and clear stack
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }

    private fun fetchUserProfile() {
        // Use the same SharedPreferences name consistently
        val token = getSharedPreferences("MyApp", MODE_PRIVATE).getString("token", "")

        if (token.isNullOrEmpty()) return

        RetrofitClient.api.getProfile("Bearer $token").enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful) {
                    val user = response.body()?.data
                    // Update UI using binding
                    binding.profileName.text = user?.name
                    binding.profileEmail.text = user?.email
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Toast.makeText(this@ProfileActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

//class ProfileActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityProfileBinding
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
//        binding = ActivityProfileBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        enableEdgeToEdge()
////        setContentView(R.layout.activity_profile)
//
//        val sharedPref = getSharedPreferences("MyApp", MODE_PRIVATE)
//        val name = sharedPref.getString("name", "User")
//        val email = sharedPref.getString("userEmail", "Email not found")
//
//        // 2. Find the views that are ALREADY in activity_profile
//        val nameTextView = findViewById<TextView>(R.id.profileName)
//        val btnLogout = findViewById<Button>(R.id.btnLogout)
//        findViewById<TextView>(R.id.profileEmail).text = email
//
//        // 3. Set the data
//        nameTextView.text = name
//
//        // 4. Handle Logout click
//        btnLogout.setOnClickListener {
//            showLogoutConfirmation()
//        }
//
//    }
//
//    private fun showLogoutConfirmation() {
//        val builder = android.app.AlertDialog.Builder(this)
//        builder.setTitle("Logout")
//        builder.setMessage("Are you sure you want to logout from ExpenseTracker?")
//
//        builder.setPositiveButton("Logout") { _, _ ->
//            performLogout()
//        }
//
//        builder.setNegativeButton("Cancel") { dialog, _ ->
//            dialog.dismiss()
//        }
//
//        val dialog = builder.create()
//        dialog.show()
//
//        // Optional: Style the buttons to match your "Shine Black" theme
//        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
//        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY)
//    }
//
//    private fun performLogout() {
//        // 1. Clear SharedPreferences
//        val sharedPref = getSharedPreferences("MyApp", MODE_PRIVATE)
//        with(sharedPref.edit()) {
//            clear() // Wipes token, name, and everything else
//            apply()
//        }
//
//        // 2. Redirect to Auth/Login Screen
//        val intent = Intent(this, AuthActivity::class.java)
//
//        // CRITICAL: This clears the activity stack so the user
//        // can't click "Back" to see the Dashboard again.
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//
//        startActivity(intent)
//        finish()
//
//        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
//    }
//
//    private fun fetchUserProfile() {
//        val token = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("token", "")
//
//        RetrofitClient.api.getProfile("Bearer $token").enqueue(object : Callback<ProfileResponse> {
//            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
//                if (response.isSuccessful) {
//                    val user = response.body()?.data
//                    binding.tvUserName.text = user?.name
//                    binding.tvUserEmail.text = user?.email
//                }
//            }
//            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
//                Toast.makeText(this@ProfileActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//}