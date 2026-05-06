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
import com.github.mikephil.charting.utils.ColorTemplate

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
            override fun onNothingSelected() {
                loadMainOverview()
            }
        })
    }
    private fun loadMainOverview() {
        val token = getSharedPreferences("MyApp", MODE_PRIVATE).getString("token", "")

        RetrofitClient.api.getSummary("Bearer $token").enqueue(object : Callback<SummaryResponse> {
            override fun onResponse(call: Call<SummaryResponse>, response: Response<SummaryResponse>) {
                if (response.isSuccessful) {
                    val summaryData = response.body()?.data
                    android.util.Log.d("CHART_DATA", "Income: ${summaryData?.totalIncome}, Expense: ${summaryData?.totalExpense}")
                    if (summaryData != null) {
                        val entries = ArrayList<PieEntry>()

                        // Backend se aayi hui values ko chart me dalo
                        entries.add(PieEntry(summaryData.totalIncome.toFloat(), "Income"))
                        entries.add(PieEntry(summaryData.totalExpense.toFloat(), "Expense"))

                        val dataSet = PieDataSet(entries, "Overview")
                        val colors = ArrayList<Int>()
                        colors.add(android.graphics.Color.parseColor("#00C853")) // Neon Green for Income
                        colors.add(android.graphics.Color.parseColor("#FF3D00")) // Electric Red for Expense
                        dataSet.colors = colors

                        dataSet.sliceSpace = 5f            // Slices ke beech ka gap
                        dataSet.selectionShift = 12f       // Click karne par slice thoda bahar aayega (Glow effect)
                        dataSet.valueLineColor = android.graphics.Color.WHITE
                        dataSet.setDrawValues(true)

                        // Chart ke beech mein hole ko transparent aur stylish banao
                        val pieChart = findViewById<PieChart>(R.id.pieChart)
                        pieChart.isDrawHoleEnabled = true
                        pieChart.setHoleColor(android.graphics.Color.TRANSPARENT) // Dark theme ke liye
                        pieChart.holeRadius = 60f // Inner circle size
                        pieChart.transparentCircleRadius = 65f

// Center Text (Dynamic summary)
                        pieChart.centerText = "Overview"
                        pieChart.setCenterTextColor(android.graphics.Color.WHITE)
                        pieChart.setCenterTextSize(20f)

// Animation: Chart ko ghumte hue load hone do
                        pieChart.animateY(1400, com.github.mikephil.charting.animation.Easing.EaseInOutQuad)

// Legend (Niche jo labels aate hain unhe style karo)
                        val l = pieChart.legend
                        l.verticalAlignment = com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.BOTTOM
                        l.horizontalAlignment = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER
                        l.textColor = android.graphics.Color.WHITE
                        l.textSize = 12f

                        pieChart.data = PieData(dataSet)
                        val balance = summaryData.totalIncome - summaryData.totalExpense
                        pieChart.centerText = "Balance\n₹$balance"
                        pieChart.invalidate()
                    }
                }
            }

            override fun onFailure(call: Call<SummaryResponse>, t: Throwable) {
                Toast.makeText(this@ChartActivity, "Network Error",     Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchCategoryData(type: String) {
        val token = getSharedPreferences("MyApp", MODE_PRIVATE).getString("token", "")
        RetrofitClient.api.getCategoryBreakdown("Bearer $token", type).enqueue(object : Callback<BreakdownResponse> {
            override fun onResponse(call: Call<BreakdownResponse>, response: Response<BreakdownResponse>) {
//                if (response.isSuccessful) {
//                    updateChartWithCategories(response.body()?.data, type)
//                }
                if (response.isSuccessful) {
                    val dataList = response.body()?.data
                    if (!dataList.isNullOrEmpty()) {
                        updateChartWithCategories(dataList, type)
                    } else {
                        Toast.makeText(this@ChartActivity, "No categories found for $type", Toast.LENGTH_SHORT).show()
                    }
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

        val dataSet = PieDataSet(entries, "$type Breakdown")

        // Vibrant colors for different categories
        dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        dataSet.sliceSpace = 3f
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 14f

        val data = PieData(dataSet)
        pieChart.data = data

        // Center text ko change karke batao ki hum kya dekh rahe hain
        pieChart.centerText = "$type\nBreakdown"
        pieChart.setCenterTextColor(if(type == "Income") Color.GREEN else Color.RED)

        // Smooth transition animation
        pieChart.animateXY(800, 800)
        pieChart.invalidate()
//        val dataSet = com.github.mikephil.charting.data.PieDataSet(entries, "$type Breakdown")
//        dataSet.colors = com.github.mikephil.charting.utils.ColorTemplate.MATERIAL_COLORS.toList()
//
//        val data = com.github.mikephil.charting.data.PieData(dataSet)
//        pieChart.data = data
//        pieChart.centerText = type
//        pieChart.animateY(1000)
//        pieChart.invalidate()
    }
}