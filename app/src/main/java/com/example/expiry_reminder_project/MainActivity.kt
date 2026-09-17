package com.example.expiry_reminder_project

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expiry_reminder_project.adapter.DocumentAdapter
import com.example.expiry_reminder_project.model.Document
import com.example.expiry_reminder_project.utils.DateUtils
import com.google.android.material.card.MaterialCardView
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerDocuments: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var etSearch: EditText
    private lateinit var adapter: DocumentAdapter

    private var allDocuments = mutableListOf<Document>()
    private var currentFilter = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupRecyclerView()
        setupClicks()
        setupSearch()

        loadDocuments()

        NotificationHelper.createChannel(this)
        ReminderScheduler.scheduleDailyReminder(this)

        requestNotificationPermission()
    }

    private fun initializeViews() {

        recyclerDocuments = findViewById(R.id.recyclerDocuments)
        tvEmpty = findViewById(R.id.tvEmpty)
        etSearch = findViewById(R.id.etSearch)

        updateFilterButton()
    }

    private fun setupRecyclerView() {

        adapter = DocumentAdapter(this, mutableListOf())

        recyclerDocuments.layoutManager =
            LinearLayoutManager(this)

        recyclerDocuments.adapter = adapter

        recyclerDocuments.setHasFixedSize(false)
    }

    private fun setupClicks() {

        findViewById<TextView>(R.id.tvProfile).setOnClickListener {
            startActivity(
                Intent(this, ProfileActivity::class.java)
            )
        }

        findViewById<MaterialCardView>(R.id.cardAddDocument).setOnClickListener {
            startActivity(
                Intent(this, AddDocumentActivity::class.java)
            )
        }

        findViewById<MaterialCardView>(R.id.cardAllDocuments).setOnClickListener {
            startActivity(
                Intent(this, DocumentsActivity::class.java)
            )
        }

        findViewById<MaterialCardView>(R.id.cardFolders).setOnClickListener {
            startActivity(
                Intent(this, FoldersActivity::class.java)
            )
        }

        findViewById<TextView>(R.id.navHome).setOnClickListener {
            // Already on Home
        }

        findViewById<TextView>(R.id.navDocuments).setOnClickListener {
            startActivity(
                Intent(this, DocumentsActivity::class.java)
            )
        }

        findViewById<TextView>(R.id.navRenewal).setOnClickListener {
            startActivity(
                Intent(this, RenewalActivity::class.java)
            )
        }

        findViewById<TextView>(R.id.navSettings).setOnClickListener {
            startActivity(
                Intent(this, SettingsActivity::class.java)
            )
        }

        findViewById<TextView>(R.id.filterAll).setOnClickListener {
            currentFilter = "All"
            updateFilterButton()
            applyFilters()
        }

        findViewById<TextView>(R.id.filterValid).setOnClickListener {
            currentFilter = "VALID"
            updateFilterButton()
            applyFilters()
        }

        findViewById<TextView>(R.id.filterSoon).setOnClickListener {
            currentFilter = "EXPIRING SOON"
            updateFilterButton()
            applyFilters()
        }

        findViewById<TextView>(R.id.filterExpired).setOnClickListener {
            currentFilter = "EXPIRED"
            updateFilterButton()
            applyFilters()
        }
    }

    private fun setupSearch() {

        etSearch.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    applyFilters()
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )
    }

    private fun loadDocuments() {

        val preferences = getSharedPreferences(
            "DocumentStorage",
            Context.MODE_PRIVATE
        )

        val data = preferences.getString(
            "documents",
            "[]"
        ) ?: "[]"

        allDocuments.clear()

        try {

            val documents = JSONArray(data)

            for (i in 0 until documents.length()) {

                val obj = documents.getJSONObject(i)

                allDocuments.add(
                    Document(
                        id = obj.optString("id"),
                        name = obj.optString("name"),
                        type = obj.optString("type"),
                        documentNumber =
                            obj.optString("documentNumber"),
                        issueDate =
                            obj.optString("issueDate"),
                        expiryDate =
                            obj.optString("expiryDate"),
                        folderId =
                            obj.optString("folderId"),
                        notes =
                            obj.optString("notes")
                    )
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        applyFilters()
    }

    private fun applyFilters() {

        val searchText =
            etSearch.text.toString()
                .trim()
                .lowercase()

        val filteredDocuments =
            allDocuments.filter { document ->

                val matchesSearch =
                    searchText.isEmpty() ||
                            document.name.lowercase()
                                .contains(searchText) ||
                            document.type.lowercase()
                                .contains(searchText) ||
                            document.documentNumber.lowercase()
                                .contains(searchText)

                val matchesFilter =
                    currentFilter == "All" ||
                            DateUtils.getStatus(
                                document.expiryDate
                            ) == currentFilter

                matchesSearch && matchesFilter
            }

        adapter.updateList(
            filteredDocuments.toMutableList()
        )

        if (filteredDocuments.isEmpty()) {

            tvEmpty.visibility = View.VISIBLE
            recyclerDocuments.visibility = View.GONE

        } else {

            tvEmpty.visibility = View.GONE
            recyclerDocuments.visibility = View.VISIBLE
        }
    }

    private fun updateFilterButton() {

        val filterAll =
            findViewById<TextView>(R.id.filterAll)

        val filterValid =
            findViewById<TextView>(R.id.filterValid)

        val filterSoon =
            findViewById<TextView>(R.id.filterSoon)

        val filterExpired =
            findViewById<TextView>(R.id.filterExpired)

        filterAll.setBackgroundResource(
            if (currentFilter == "All")
                R.drawable.bg_filter_selected
            else
                R.drawable.bg_filter
        )

        filterValid.setBackgroundResource(
            if (currentFilter == "VALID")
                R.drawable.bg_filter_selected
            else
                R.drawable.bg_filter
        )

        filterSoon.setBackgroundResource(
            if (currentFilter == "EXPIRING SOON")
                R.drawable.bg_filter_selected
            else
                R.drawable.bg_filter
        )

        filterExpired.setBackgroundResource(
            if (currentFilter == "EXPIRED")
                R.drawable.bg_filter_selected
            else
                R.drawable.bg_filter
        )

        filterAll.setTextColor(
            if (currentFilter == "All")
                ContextCompat.getColor(
                    this,
                    R.color.card_white
                )
            else
                ContextCompat.getColor(
                    this,
                    R.color.text_primary
                )
        )

        filterValid.setTextColor(
            if (currentFilter == "VALID")
                ContextCompat.getColor(
                    this,
                    R.color.card_white
                )
            else
                ContextCompat.getColor(
                    this,
                    R.color.text_primary
                )
        )

        filterSoon.setTextColor(
            if (currentFilter == "EXPIRING SOON")
                ContextCompat.getColor(
                    this,
                    R.color.card_white
                )
            else
                ContextCompat.getColor(
                    this,
                    R.color.text_primary
                )
        )

        filterExpired.setTextColor(
            if (currentFilter == "EXPIRED")
                ContextCompat.getColor(
                    this,
                    R.color.card_white
                )
            else
                ContextCompat.getColor(
                    this,
                    R.color.text_primary
                )
        )
    }

    private fun requestNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS
                    ),
                    100
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (::recyclerDocuments.isInitialized) {

            loadDocuments()

            ReminderScheduler.scheduleDailyReminder(this)
        }
    }
}