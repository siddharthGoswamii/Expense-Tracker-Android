package com.expensetracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ForgetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        val backBtn = findViewById<Button>(R.id.backBtn)
        backBtn.setOnClickListener {
            startActivityForResult(Intent(this, MainActivity::class.java), 100)
            finish()
        }
        val loginLink = findViewById<TextView>(R.id.loginLink)
        loginLink.setOnClickListener {
            startActivityForResult(Intent(this, MainActivity::class.java), 100)
            finish()
        }
    }
}