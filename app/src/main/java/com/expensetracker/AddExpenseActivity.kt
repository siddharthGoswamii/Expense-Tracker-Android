package com.expensetracker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.util.Log
import android.widget.Toast
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.expensetracker.api.RetrofitClient
import com.expensetracker.model.AddExpenseRequest
import com.expensetracker.model.AddExpenseResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AddExpenseActivity : AppCompatActivity() {

    lateinit var spinner: Spinner
    lateinit var amount: EditText
    lateinit var saveBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_add_expense)
        Log.d("FLOW", "onCreate started")

        spinner = findViewById(R.id.categorySpinner)
        Log.d("FLOW", "spinner found: $spinner")

        amount = findViewById(R.id.amount)
        Log.d("FLOW", "amount found: $amount")

        saveBtn = findViewById(R.id.saveBtn)
        Log.d("FLOW", "saveBtn found: $saveBtn")

        // Categories
        val categories = arrayOf("Food", "Travel", "Misc")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )

        spinner.adapter = adapter

        saveBtn.setOnClickListener {

            Log.d("FLOW", "1 - button clicked")

            val selectedCategory = spinner.selectedItem.toString()
            Log.d("FLOW", "2 - category: $selectedCategory")

            val enteredAmount = amount.text.toString()
            Log.d("FLOW", "3 - amount: $enteredAmount")

            if (enteredAmount.isEmpty()) {
                Toast.makeText(this, "Enter amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("FLOW", "4 - getting token")
            val sharedPref = getSharedPreferences("MyApp", MODE_PRIVATE)
            val token = sharedPref.getString("token", "")
            Log.d("FLOW", "5 - token: $token")

            Log.d("FLOW", "6 - creating request")
            val request = AddExpenseRequest(selectedCategory, enteredAmount.toInt())
            Log.d("FLOW", "7 - calling API")

            RetrofitClient.api.addTransaction("Bearer $token", request)
                .enqueue(object : Callback<AddExpenseResponse> {
                    override fun onResponse(call: Call<AddExpenseResponse>, response: Response<AddExpenseResponse>) {
                        Log.d("FLOW", "8 - response code: ${response.code()}")
                        Log.d("FLOW", "9 - error body: ${response.errorBody()?.string()}")
                        if (response.isSuccessful && response.body()?.success == true) {
                            setResult(RESULT_OK)
                            finish()
                        } else {
                            Toast.makeText(this@AddExpenseActivity, "Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<AddExpenseResponse>, t: Throwable) {
                        Log.d("FLOW", "8 - FAILURE: ${t.javaClass.name} - ${t.message}")
                    }
                })
            finish()
        }
    }
}