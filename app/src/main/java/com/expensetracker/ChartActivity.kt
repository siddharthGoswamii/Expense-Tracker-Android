package com.expensetracker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.expensetracker.api.RetrofitClient
import com.expensetracker.model.BreakdownResponse
import com.expensetracker.model.CategoryBreakdown
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.charts.PieChart

class ChartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chart)
        val pieChart=findViewById<PieChart>(R.id.pieChart)

        // 2. Chart Click Listener for Drill-down
        pieChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
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

    private fun updateChartWithCategories(dataList: List<CategoryBreakdown>?, type: String) {
        val pieChart = findViewById<com.github.mikephil.charting.charts.PieChart>(R.id.pieChart)

        if (dataList.isNullOrEmpty()) return

        val entries = ArrayList<com.github.mikephil.charting.data.PieEntry>()
        dataList.forEach {
            // Backend se '_id' mein category name aa raha hai
            entries.add(com.github.mikephil.charting.data.PieEntry(it.totalAmount.toFloat(), it.id))
        }

        val dataSet = com.github.mikephil.charting.data.PieDataSet(entries, "$type Breakdown")
        dataSet.colors = com.github.mikephil.charting.utils.ColorTemplate.MATERIAL_COLORS.toList()

        val data = com.github.mikephil.charting.data.PieData(dataSet)
        pieChart.data = data
        pieChart.centerText = type
        pieChart.animateY(1000)
        pieChart.invalidate()
    }
}