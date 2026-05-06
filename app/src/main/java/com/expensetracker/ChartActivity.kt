package com.expensetracker

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.expensetracker.api.RetrofitClient
import com.expensetracker.model.BreakdownResponse
import com.expensetracker.model.CategoryBreakdown
import com.expensetracker.model.SummaryResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet

class ChartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chart)
        val pieChart=findViewById<PieChart>(R.id.pieChart)
        loadMainOverview()

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
        val token = getSharedPreferences("MyApp", MODE_PRIVATE).getString("token", "")

        RetrofitClient.api.getSummary("Bearer $token").enqueue(object : Callback<SummaryResponse> {
            override fun onResponse(call: Call<SummaryResponse>, response: Response<SummaryResponse>) {
                if (response.isSuccessful) {
                    val summaryData = response.body()?.data
                    if (summaryData != null) {
                        val entries = ArrayList<PieEntry>()

                        // Backend se aayi hui values ko chart me dalo
                        entries.add(PieEntry(summaryData.totalIncome.toFloat(), "Income"))
                        entries.add(PieEntry(summaryData.totalExpense.toFloat(), "Expense"))

                        val dataSet = PieDataSet(entries, "Overview")
                        dataSet.colors = arrayListOf(Color.GRAY, Color.RED)

                        val pieChart = findViewById<PieChart>(R.id.pieChart)
                        pieChart.data = PieData(dataSet)
                        pieChart.centerText = "Total Summary"
                        pieChart.invalidate()
                    }
                }
            }

            override fun onFailure(call: Call<SummaryResponse>, t: Throwable) {
                Toast.makeText(this@ChartActivity, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
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