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
import com.google.android.material.bottomsheet.BottomSheetDialog

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        val sharedPref = getSharedPreferences("MyApp", MODE_PRIVATE)
        val name = sharedPref.getString("name", "User")

        // 2. Find the views that are ALREADY in activity_profile
        val nameTextView = findViewById<TextView>(R.id.profileName)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        // 3. Set the data
        nameTextView.text = name

        // 4. Handle Logout click
        btnLogout.setOnClickListener {
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

        // Optional: Style the buttons to match your "Shine Black" theme
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY)
    }

    private fun performLogout() {
        // 1. Clear SharedPreferences
        val sharedPref = getSharedPreferences("MyApp", MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear() // Wipes token, name, and everything else
            apply()
        }

        // 2. Redirect to Auth/Login Screen
        val intent = Intent(this, AuthActivity::class.java)

        // CRITICAL: This clears the activity stack so the user
        // can't click "Back" to see the Dashboard again.
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }
}