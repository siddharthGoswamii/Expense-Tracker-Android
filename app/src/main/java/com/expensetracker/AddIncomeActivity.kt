package com.expensetracker

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddIncomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_income)
        val etAmount = findViewById<EditText>(R.id.etIncomeAmount)
        val spinner = findViewById<Spinner>(R.id.incomeCategorySpinner)
        val btnSave = findViewById<Button>(R.id.btnSaveIncome)

        // Income Categories set karo
        val categories = arrayOf("Salary", "Freelance", "Gift", "Investment", "Others")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spinner.adapter = adapter

        btnSave.setOnClickListener {
            val amount = etAmount.text.toString()
            val category = spinner.selectedItem.toString()

            if (amount.isNotEmpty()) {
                // Yahan apna Retrofit/API call dalo
                // bas body mein: type = "Income" pass karna
                saveTransaction(amount.toDouble(), category, "Income")
            }
        }
    }

    private fun saveTransaction(amount: Double, category: String, type: String) {
        // Tumhara existing Retrofit logic yahan aayega
    }
}