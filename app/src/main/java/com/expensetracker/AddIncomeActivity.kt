package com.expensetracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.expensetracker.api.RetrofitClient
import com.expensetracker.model.AddExpenseRequest
import com.expensetracker.model.AddExpenseResponse
import com.expensetracker.model.AddIncomeRequest
import com.expensetracker.model.AddIncomeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
            val type: String = "Income"

            if (amount.isEmpty()) {
                Toast.makeText(this, "Enter amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val sharedPref = getSharedPreferences("MyApp", MODE_PRIVATE)
            val token = sharedPref.getString("token", "")

            val request = AddIncomeRequest(category, amount.toInt(), type)

            RetrofitClient.api.addTransaction("Bearer $token", request)
                .enqueue(object : Callback<AddIncomeResponse> {
                    override fun onResponse(call: Call<AddIncomeResponse>, response: Response<AddIncomeResponse>) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            setResult(RESULT_OK)
                            finish()
                        } else {
                            Toast.makeText(this@AddIncomeActivity, "Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<AddIncomeResponse>, t: Throwable) {
                        Log.d("FLOW", "8 - FAILURE: ${t.javaClass.name} - ${t.message}")
                    }
                })
            finish()
        }
    }

    private fun saveTransaction(amount: Double, category: String, type: String) {
        // Tumhara existing Retrofit logic yahan aayega
    }
}