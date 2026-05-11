package com.expensetracker

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val loginLink = findViewById<TextView>(R.id.loginLink)
        loginLink.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)

            // Ye flags stack ko clear kar denge
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

            startActivity(intent)
            finish() // Current activity ko destroy kar do
        }
    }
}