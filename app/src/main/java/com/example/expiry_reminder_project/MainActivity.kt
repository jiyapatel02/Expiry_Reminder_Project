package com.example.expiry_reminder_project

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expiry_reminder_project.adapter.DocumentAdapter
import com.example.expiry_reminder_project.model.Document
import com.example.expiry_reminder_project.utils.DateUtils
import com.example.expiry_reminder_project.utils.StorageHelper
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerDocuments: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var etSearch: EditText

    private lateinit var adapter: DocumentAdapter

    private var allDocuments =
        mutableListOf<Document>()

    private var currentFilter = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        initializeViews()
        setupRecyclerView()
        setupClicks()
        setupSearch()

        loadDocuments()
    }

    private fun initializeViews() {

        recyclerDocuments =
            findViewById(R.id.recyclerDocuments)

        tvEmpty =
            findViewById(R.id.tvEmpty)

        etSearch =
            findViewById(R.id.etSearch)
    }

    private fun setupRecyclerView() {

        adapter =
            DocumentAdapter(
                this,
                mutableListOf()
            )

        recyclerDocuments.layoutManager =
            LinearLayoutManager(this)

        recyclerDocuments.adapter = adapter
    }

    private fun setupClicks() {

        // Add Document
        findViewById<MaterialCardView>(
            R.id.btnAddDocument
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    AddDocumentActivity::class.java
                )
            )
        }

        // Profile
        findViewById<MaterialCardView>(
            R.id.btnProfile
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ProfileActivity::class.java
                )
            )
        }

        // Folders
        findViewById<MaterialCardView>(
            R.id.cardFolders
        ).setOnClickListener {

            // We will create FoldersActivity
            // in the next step.
        }

        // All documents
        findViewById<MaterialCardView>(
            R.id.cardAllDocuments
        ).setOnClickListener {

            currentFilter = "All"

            updateFilterButton()

            applyFilters()
        }

        // Bottom Documents
        findViewById<TextView>(
            R.id.navDocuments
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    DocumentsActivity::class.java
                )
            )
        }

        // Bottom Reminders
        findViewById<TextView>(
            R.id.navReminders
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    RemindersActivity::class.java
                )
            )
        }

        // Bottom Settings
        findViewById<TextView>(
            R.id.navSettings
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    SettingsActivity::class.java
                )
            )
        }

        // Home
        findViewById<TextView>(
            R.id.navHome
        ).setOnClickListener {
            // Already on Home
        }

        // Filters
        findViewById<TextView>(
            R.id.filterAll
        ).setOnClickListener {

            currentFilter = "All"
            updateFilterButton()
            applyFilters()
        }

        findViewById<TextView>(
            R.id.filterValid
        ).setOnClickListener {

            currentFilter = "VALID"
            updateFilterButton()
            applyFilters()
        }

        findViewById<TextView>(
            R.id.filterSoon
        ).setOnClickListener {

            currentFilter = "EXPIRING SOON"
            updateFilterButton()
            applyFilters()
        }

        findViewById<TextView>(
            R.id.filterExpired
        ).setOnClickListener {

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

        allDocuments =
            StorageHelper.getDocuments(this)

        applyFilters()
    }

    private fun applyFilters() {

        val searchText =
            etSearch.text.toString()
                .trim()
                .lowercase()

        val filtered =
            allDocuments.filter { document ->

                val matchesSearch =
                    searchText.isEmpty() ||
                            document.name
                                .lowercase()
                                .contains(searchText) ||
                            document.type
                                .lowercase()
                                .contains(searchText) ||
                            document.documentNumber
                                .lowercase()
                                .contains(searchText)

                val status =
                    DateUtils.getStatus(
                        document.expiryDate
                    )

                val matchesFilter =
                    currentFilter == "All" ||
                            status == currentFilter

                matchesSearch && matchesFilter
            }.toMutableList()

        adapter.updateList(filtered)

        if (filtered.isEmpty()) {

            tvEmpty.visibility = android.view.View.VISIBLE

            recyclerDocuments.visibility =
                RecyclerView.GONE

        } else {

            tvEmpty.visibility = android.view.View.GONE

            recyclerDocuments.visibility =
                RecyclerView.VISIBLE
        }
    }

    private fun updateFilterButton() {

        val all =
            findViewById<TextView>(
                R.id.filterAll
            )

        val valid =
            findViewById<TextView>(
                R.id.filterValid
            )

        val soon =
            findViewById<TextView>(
                R.id.filterSoon
            )

        val expired =
            findViewById<TextView>(
                R.id.filterExpired
            )

        all.setTextColor(
            getColor(
                if (currentFilter == "All")
                    R.color.card_white
                else
                    R.color.text_secondary
            )
        )

        valid.setTextColor(
            getColor(
                if (currentFilter == "VALID")
                    R.color.card_white
                else
                    R.color.text_secondary
            )
        )

        soon.setTextColor(
            getColor(
                if (currentFilter == "EXPIRING SOON")
                    R.color.card_white
                else
                    R.color.text_secondary
            )
        )

        expired.setTextColor(
            getColor(
                if (currentFilter == "EXPIRED")
                    R.color.card_white
                else
                    R.color.text_secondary
            )
        )

        all.setBackgroundResource(
            if (currentFilter == "All")
                R.drawable.bg_filter_selected
            else
                R.drawable.bg_filter
        )

        valid.setBackgroundResource(
            if (currentFilter == "VALID")
                R.drawable.bg_filter_selected
            else
                R.drawable.bg_filter
        )

        soon.setBackgroundResource(
            if (currentFilter == "EXPIRING SOON")
                R.drawable.bg_filter_selected
            else
                R.drawable.bg_filter
        )

        expired.setBackgroundResource(
            if (currentFilter == "EXPIRED")
                R.drawable.bg_filter_selected
            else
                R.drawable.bg_filter
        )
    }

    override fun onResume() {
        super.onResume()

        loadDocuments()
    }
}