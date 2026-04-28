package com.expensetracker

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
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
            showLogoutConfirmation()
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

//                val intent = Intent(this, HistoryActivity::class.java)
//                intent.putExtra("category", selected.name)
//                startActivity(intent)
//            }
                // Step 1 → Filter transactions for this category
                val filteredTransactions = transactions.filter { transaction ->
                    transaction.category == selected.name
                }

                // Step 2 → Show dialog with filtered transactions
                if (filteredTransactions.isEmpty()) {
                    // Show message if no transactions found
                    Toast.makeText(
                        this,
                        "No transactions for ${selected.name}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Show the popup dialog ✅
                    showHistoryPopup(filteredTransactions.toMutableList())
                }
            }
    }

    //THIS FUNCTION WILL CREATE A DIALOG BOX WHICH WILL HOLD THE HISTORY OF PARTICULAR TRANSACTION
    private fun showHistoryPopup(transactions: List<Transaction>) {
        val dialog = Dialog(this, R.style.CustomDialogTheme)
        dialog.setContentView(R.layout.dialog_history)

        // Transparent background so only the CardView is visible
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Set Layout Params for a proper "Pop-up" feel
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val recyclerView = dialog.findViewById<RecyclerView>(R.id.historyRecyclerView)
        val closeBtn = dialog.findViewById<TextView>(R.id.closeHistoryBtn)
        val title = dialog.findViewById<TextView>(R.id.historyTitle)

        // Set up the RecyclerView with your existing HistoryAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = HistoryAdapter(transactions as MutableList<Transaction>)

        closeBtn.setOnClickListener {
            dialog.dismiss()
        }

        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val transactionToDelete = (recyclerView.adapter as HistoryAdapter).getItemAt(position)

                // 1. Call API to delete from MongoDB
                deleteFromBackend(transactionToDelete.id, position, recyclerView.adapter as HistoryAdapter)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        dialog.show()
    }

    private fun deleteFromBackend(id: String, position: Int, adapter: HistoryAdapter) {
        Log.d("DELETE_DEBUG", "Attempting to delete ID: $id")
        val sharedPref = getSharedPreferences("MyApp", MODE_PRIVATE)
        val token = sharedPref.getString("token", "")

        RetrofitClient.api.deleteTransaction("Bearer $token", id).enqueue(object : Callback<DeleteResponse> {
            override fun onResponse(call: Call<DeleteResponse>, response: Response<DeleteResponse>) {
                if (response.isSuccessful) {
                    Log.d("DELETE_DEBUG", "Backend Success!")
                    // 2. Remove from the Adapter list and Refresh UI
                    adapter.removeItem(position)
                    Toast.makeText(this@DashboardActivity, "Deleted successfully", Toast.LENGTH_SHORT).show()

                    // 3. Refresh the Dashboard totals (Balance/Expense)
                    loadTransactions()
                }else {
                    Log.e("DELETE_DEBUG", "Backend Error: ${response.code()}")
                    adapter.notifyItemChanged(position) // Snap it back
                }
            }

            override fun onFailure(call: Call<DeleteResponse>, t: Throwable) {
                Log.e("DELETE_DEBUG", "Network Failure: ${t.message}") // Bring the item back if it fails
                Toast.makeText(this@DashboardActivity, "Error deleting", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showEmptyState() {
        emptyStateText.visibility = View.VISIBLE
        categoryRecyclerView.visibility = View.GONE
    }

        override fun onResume() {
            super.onResume()
            loadTransactions()
        }

    //LOGOUT FUNCTION
    private fun showLogoutConfirmation() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout from ExpenseTracker?")

        builder.setPositiveButton("Logout") { _, _ ->
            performLogout()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()

        // Optional: Style the buttons to match your "Shine Black" theme
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY)
    }

    private fun performLogout() {
        // 1. Clear SharedPreferences
        val sharedPref = getSharedPreferences("MyApp", MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear() // Wipes token, name, and everything else
            apply()
        }

        // 2. Redirect to Auth/Login Screen
        val intent = Intent(this, AuthActivity::class.java)

        // CRITICAL: This clears the activity stack so the user
        // can't click "Back" to see the Dashboard again.
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }
}