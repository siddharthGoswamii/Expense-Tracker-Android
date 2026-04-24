package com.expensetracker

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.expensetracker.api.RetrofitClient
import com.expensetracker.model.GetTransactionResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        recyclerView = findViewById(R.id.historyRecyclerView)
        emptyText = findViewById(R.id.emptyHistoryText)

        val category = intent.getStringExtra("category")

        loadHistory(category)
    }

    private fun loadHistory(category: String?) {

        val sharedPref = getSharedPreferences("MyApp", MODE_PRIVATE)
        val token = sharedPref.getString("token", "")

        RetrofitClient.api.getTransactions("Bearer $token", 1, 10)
            .enqueue(object : Callback<GetTransactionResponse> {

                override fun onResponse(
                    call: Call<GetTransactionResponse>,
                    response: Response<GetTransactionResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {

                        val allData = response.body()?.data ?: emptyList()

                        // 🔥 FILTER BY CATEGORY
                        val filtered = allData.filter {
                            it.category == category
                        }

                        if (filtered.isEmpty()) {
                            emptyText.visibility = View.VISIBLE
                        } else {
                            emptyText.visibility = View.GONE

                            recyclerView.layoutManager = LinearLayoutManager(this@HistoryActivity)
                            recyclerView.adapter = HistoryAdapter(filtered)
                        }
                    }
                }

                override fun onFailure(call: Call<GetTransactionResponse>, t: Throwable) {
                    Toast.makeText(this@HistoryActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}