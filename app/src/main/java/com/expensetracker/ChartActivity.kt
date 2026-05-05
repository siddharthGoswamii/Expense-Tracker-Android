package com.expensetracker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.expensetracker.api.RetrofitClient
import com.expensetracker.model.BreakdownResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback

class ChartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chart)

        // 2. Chart Click Listener for Drill-down
        pieChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Map.Entry?, h: Highlight?) {
                val selectedLabel = (e as PieEntry).label
                if (selectedLabel == "Expense" || selectedLabel == "Income") {
                    // Category breakdown fetch karo based on slice clicked
                    fetchCategoryData(selectedLabel)
                }
            }
            override fun onNothingSelected() {}
        })
    }
    private fun loadMainOverview() {
        // Yahan tum dashboard waali calculation logic use kar sakte ho
        // ya phir ek separate total-overview API call kar sakte ho.
    }

    private fun fetchCategoryData(type: String) {
        val token = getSharedPreferences("MyApp", MODE_PRIVATE).getString("token", "")
        RetrofitClient.api.getCategoryBreakdown("Bearer $token", type).enqueue(object : Callback<BreakdownResponse> {
            override fun onResponse(call: Call<BreakdownResponse>, response: Response<BreakdownResponse>) {
                if (response.isSuccessful) {
                    updateChartWithCategories(response.body()?.data, type)
                }
            }
            override fun onFailure(call: Call<BreakdownResponse>, t: Throwable) {}
        })
    }
}