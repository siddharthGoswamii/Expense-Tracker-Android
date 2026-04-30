package com.expensetracker

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.card.MaterialCardView

class ChoiceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_choice)
        val cardIncome = findViewById<MaterialCardView>(R.id.cardAddIncome)
        val cardExpense = findViewById<MaterialCardView>(R.id.cardAddExpense)

        cardIncome.setOnClickListener {
            startActivity(Intent(this, AddIncomeActivity::class.java))
            finish() // Choice page ko band kar dega
        }

        cardExpense.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
            finish()
        }
    }
}