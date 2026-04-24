package com.expensetracker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.expensetracker.api.RetrofitClient
import com.expensetracker.model.*

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity : AppCompatActivity() {

    private lateinit var welcomeText: TextView
    private lateinit var totalBalanceText: TextView
    private lateinit var expenseTotalText: TextView
    private lateinit var incomeText: TextView
    private lateinit var balanceText: TextView
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var emptyStateText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        initViews()
        setupNavigation()
        loadTransactions()
    }

    private fun initViews() {
        welcomeText = findViewById(R.id.welcomeText)
        totalBalanceText = findViewById(R.id.totalBalanceText)
        expenseTotalText = findViewById(R.id.expenseTotalText)
        incomeText = findViewById(R.id.incomeText)
        balanceText = findViewById(R.id.balanceText)
        categoryRecyclerView = findViewById(R.id.categoryRecyclerView)
        emptyStateText = findViewById(R.id.emptyStateText)

        val name = intent.getStringExtra("name") ?: "User"
        welcomeText.text = "Hi, $name"
    }

    private fun setupNavigation() {

        val navAdd = findViewById<FloatingActionButton>(R.id.navAdd)
        navAdd.setOnClickListener {
            startActivityForResult(Intent(this, AddExpenseActivity::class.java), 100)
        }

        findViewById<TextView>(R.id.navRecords).setOnClickListener {
            Toast.makeText(this, "Opening Records...", Toast.LENGTH_SHORT).show()
        }

        findViewById<TextView>(R.id.navProfile).setOnClickListener {
            Toast.makeText(this, "Opening Profile...", Toast.LENGTH_SHORT).show()
        }
    }

    fun loadTransactions() {

        val sharedPref = getSharedPreferences("MyApp", MODE_PRIVATE)
        val token = sharedPref.getString("token", "")

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_LONG).show()
            return
        }

        val limit = 5
        val allTransactions = mutableListOf<Transaction>()

        fun fetchPage(page: Int) {

            RetrofitClient.api.getTransactions("Bearer $token", page, limit)
                .enqueue(object : Callback<GetTransactionResponse> {

                    override fun onResponse(
                        call: Call<GetTransactionResponse>,
                        response: Response<GetTransactionResponse>
                    ) {
                        if (response.isSuccessful && response.body()?.success == true) {

                            val body = response.body()!!
                            val transactions = body.data
                            val pagination = body.pagination

                            // 🔥 Add current page data
                            allTransactions.addAll(transactions)

                            // 🔁 Fetch next page
                            if (page < pagination.totalPages) {
                                fetchPage(page + 1)
                            } else {
                                processAllTransactions(allTransactions)
                            }

                        } else {
                            showEmptyState()
                        }
                    }

                    override fun onFailure(
                        call: Call<GetTransactionResponse>,
                        t: Throwable
                    ) {
                        Log.e("API_ERROR", "Failed: ${t.message}")
                    }
                })
        }

        fetchPage(1)
    }

    private fun processAllTransactions(transactions: List<Transaction>) {

        if (transactions.isEmpty()) {
            showEmptyState()
            return
        }

        var totalExpense = 0
        val categoryTotals = HashMap<String, Int>()

        for (item in transactions) {
            totalExpense += item.amount
            val current = categoryTotals.getOrDefault(item.category, 0)
            categoryTotals[item.category] = current + item.amount
        }

        // Update UI
        expenseTotalText.text = "Expense\n₹$totalExpense"
        incomeText.text = "Income\n₹0"
        balanceText.text = "Balance\n-₹$totalExpense"
        totalBalanceText.text = "₹$totalExpense"

        val summaryList = categoryTotals.map {
            CategorySummary(it.key, it.value)
        }

        categoryRecyclerView.visibility = View.VISIBLE
        emptyStateText.visibility = View.GONE

        categoryRecyclerView.layoutManager = LinearLayoutManager(this)

        categoryRecyclerView.adapter =
            CategoryAdapter(summaryList) { selected ->

                val intent = Intent(this, HistoryActivity::class.java)
                intent.putExtra("category", selected.name)
                startActivity(intent)
            }
    }

    private fun showEmptyState() {
        emptyStateText.visibility = View.VISIBLE
        categoryRecyclerView.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        loadTransactions()
    }
}


//package com.expensetracker
//
//import android.annotation.SuppressLint
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.expensetracker.api.RetrofitClient
//import com.expensetracker.model.GetTransactionResponse
//import com.google.android.material.floatingactionbutton.FloatingActionButton
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import androidx.recyclerview.widget.RecyclerView
//import androidx.recyclerview.widget.LinearLayoutManager
//import android.view.View
//import com.expensetracker.model.CategorySummary
//import com.expensetracker.model.Transaction
//
//class DashboardActivity : AppCompatActivity() {
//
//    // Professional UI references
//    private lateinit var welcomeText: TextView
//    private lateinit var totalBalanceText: TextView
//    private lateinit var expenseTotalText: TextView
//    private lateinit var incomeText: TextView
//    private lateinit var balanceText: TextView
//    private lateinit var categoryRecyclerView: RecyclerView
//    private lateinit var emptyStateText: TextView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_dashboard)
//
//        // 1. Initialize all views first
//        initViews()
//
//        // 2. Setup button clicks
//        setupNavigation()
//
//        // 3. Load initial data
//        loadTransactions()
//    }
//
//    private fun initViews() {
//        welcomeText = findViewById(R.id.welcomeText)
//        totalBalanceText = findViewById(R.id.totalBalanceText)
//        expenseTotalText = findViewById(R.id.expenseTotalText)
//        incomeText = findViewById(R.id.incomeText)
//        balanceText = findViewById(R.id.balanceText)
//        categoryRecyclerView = findViewById(R.id.categoryRecyclerView)
//        emptyStateText = findViewById(R.id.emptyStateText)
//
//        val name = intent.getStringExtra("name") ?: "User"
//        welcomeText.text = "Hi, $name"
//    }
//
//    private fun setupNavigation() {
////
//        val navAdd = findViewById<FloatingActionButton>(R.id.navAdd) // <-- Updated Cast
//        navAdd.setOnClickListener {
//            startActivityForResult(Intent(this, AddExpenseActivity::class.java), 100)
//        }
//
//        findViewById<TextView>(R.id.navRecords).setOnClickListener {
//            Toast.makeText(this, "Opening Records...", Toast.LENGTH_SHORT).show()
//        }
//
//        findViewById<TextView>(R.id.navProfile).setOnClickListener {
//            Toast.makeText(this, "Opening Profile...", Toast.LENGTH_SHORT).show()
//        }
//
//        // Add listeners for navCharts and navReports as needed
//    }
//
//    fun loadTransactions() {
//        val sharedPref = getSharedPreferences("MyApp", MODE_PRIVATE)
//        val token = sharedPref.getString("token", "")
//        var currentPage = 1
//        val limit = 5
//        var totalPages = 1
//
//        val allTransactions = mutableListOf<Transaction>()
//
//        if (token.isNullOrEmpty()) {
//            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_LONG).show()
//            return
//        }
//
//        RetrofitClient.api.getTransactions("Bearer $token", 1, 10)
//            .enqueue(object : Callback<GetTransactionResponse> {
//
//    override fun onResponse(call: Call<GetTransactionResponse>, response: Response<GetTransactionResponse>) {
//    if (response.isSuccessful) {
//        val body = response.body()
//
//        if (body != null && body.success == true && body.data != null) {
//            val transactions = body.data
//
//            var totalExpense = 0
//            val categoryTotals = HashMap<String, Int>()
//
//            // 1. Grouping Logic stays the same
//            for (item in transactions) {
//                totalExpense += item.amount
//                val currentTotal = categoryTotals.getOrDefault(item.category, 0)
//                categoryTotals[item.category] = currentTotal + item.amount
//            }
//
//            // 2. Update your Header Texts
//            expenseTotalText.text = "Expense\n₹$totalExpense"
//            incomeText.text = "Income\n₹0" // Assuming income is 0 for now
//            balanceText.text = "Balance\n-₹$totalExpense"
//            totalBalanceText.text = "₹$totalExpense"
//
//            // 3. Setup the RecyclerView Data
//            if (transactions.isEmpty()) {
//                // Show empty state, hide list
//                emptyStateText.visibility = android.view.View.VISIBLE
//                categoryRecyclerView.visibility = android.view.View.GONE
//            } else {
//                // Hide empty state, show list
//                emptyStateText.visibility = android.view.View.GONE
//                categoryRecyclerView.visibility = android.view.View.VISIBLE
//
//                // 4. Convert the Map to a List for the Adapter
//                val summaryList = categoryTotals.map {
//                    com.expensetracker.model.CategorySummary(it.key, it.value)
//                }
//
//                // 5. Connect the Adapter
//                categoryRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@DashboardActivity)
//                categoryRecyclerView.adapter = CategoryAdapter(summaryList) { selected ->
//                    // This is where you will open the 'History' screen later
////                    android.widget.Toast.makeText(this@DashboardActivity, "Clicked ${selected.name}", android.widget.Toast.LENGTH_SHORT).show()
//                    val intent = Intent(this@DashboardActivity, HistoryActivity::class.java)
//                    intent.putExtra("category", selected.name)
//                    startActivity(intent)
//                }
//            }
//
//        } else {
//            emptyStateText.visibility = android.view.View.VISIBLE
//            categoryRecyclerView.visibility = android.view.View.GONE
//        }
//    }
//}
//
//                override fun onFailure(call: Call<GetTransactionResponse>, t: Throwable) {
//                    Log.e("API_ERROR", "Failed: ${t.message}")
//                }
//            })
//    }
//
//    override fun onResume() {
//        super.onResume()
//        // Auto-refresh data when returning from 'Add Expense' screen
//        loadTransactions()
//    }
//}