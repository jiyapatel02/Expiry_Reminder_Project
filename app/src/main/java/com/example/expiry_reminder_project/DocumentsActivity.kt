package com.example.expiry_reminder_project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expiry_reminder_project.adapter.DocumentAdapter
import com.example.expiry_reminder_project.model.Document
import com.example.expiry_reminder_project.utils.DateUtils
import com.google.android.material.card.MaterialCardView
import org.json.JSONArray

class DocumentsActivity : AppCompatActivity() {

    private lateinit var recyclerDocuments: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var etSearch: EditText
    private lateinit var adapter: DocumentAdapter

    private val allDocuments =
        mutableListOf<Document>()

    private var currentFilter = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_documents)

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

        updateFilterStyle()
    }
    private fun setupRecyclerView() {

        adapter = DocumentAdapter(
            this,
            mutableListOf()
        )

        recyclerDocuments.layoutManager =
            LinearLayoutManager(this)

        recyclerDocuments.adapter =
            adapter
    }

    private fun setupClicks() {

        // Back button
        findViewById<ImageButton>(
            R.id.btnBack
        ).setOnClickListener {

            onBackPressedDispatcher
                .onBackPressed()
        }

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

        findViewById<TextView>(
            R.id.filterAll
        ).setOnClickListener {

            currentFilter = "All"

            updateFilterStyle()
            applyFilters()
        }

        findViewById<TextView>(
            R.id.filterValid
        ).setOnClickListener {

            currentFilter = "VALID"

            updateFilterStyle()
            applyFilters()
        }

        findViewById<TextView>(
            R.id.filterSoon
        ).setOnClickListener {

            currentFilter = "EXPIRING SOON"

            updateFilterStyle()
            applyFilters()
        }

        findViewById<TextView>(
            R.id.filterExpired
        ).setOnClickListener {

            currentFilter = "EXPIRED"

            updateFilterStyle()
            applyFilters()
        }

        findViewById<TextView>(
            R.id.navHome
        ).setOnClickListener {

            val intent =
                Intent(
                    this,
                    MainActivity::class.java
                )

            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP

            startActivity(intent)
            finish()
        }

        findViewById<TextView>(
            R.id.navDocuments
        ).setOnClickListener {
            // Already on Documents
        }

        findViewById<TextView>(
            R.id.navRenewal
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    RenewalActivity::class.java
                )
            )
        }

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

        allDocuments.clear()

        val preferences =
            getSharedPreferences(
                "DocumentStorage",
                Context.MODE_PRIVATE
            )

        val data =
            preferences.getString(
                "documents",
                "[]"
            ) ?: "[]"

        try {

            val jsonArray =
                JSONArray(data)

            for (i in 0 until jsonArray.length()) {

                val obj =
                    jsonArray.getJSONObject(i)

                val document =
                    Document(

                        id = obj.optString(
                            "id"
                        ),

                        name = obj.optString(
                            "name"
                        ),

                        type = obj.optString(
                            "type",
                            obj.optString(
                                "category"
                            )
                        ),

                        documentNumber =
                            obj.optString(
                                "documentNumber"
                            ),

                        issueDate =
                            obj.optString(
                                "issueDate"
                            ),

                        expiryDate =
                            obj.optString(
                                "expiryDate"
                            ),

                        folderId =
                            obj.optString(
                                "folderId"
                            ),

                        notes =
                            obj.optString(
                                "notes"
                            )
                    )

                allDocuments.add(
                    document
                )
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }

        applyFilters()
    }

    private fun applyFilters() {

        val searchText =
            etSearch.text
                .toString()
                .trim()
                .lowercase()

        val filteredDocuments =
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

                matchesSearch &&
                        matchesFilter

            }.toMutableList()

        adapter.updateList(
            filteredDocuments
        )

        updateEmptyState(
            filteredDocuments.isEmpty()
        )
    }

    private fun updateEmptyState(
        isEmpty: Boolean
    ) {

        if (isEmpty) {

            recyclerDocuments.visibility =
                View.GONE

            tvEmpty.visibility =
                View.VISIBLE

            tvEmpty.text =
                if (allDocuments.isEmpty()) {
                    "No documents added yet"
                } else {
                    "No documents found"
                }

        } else {

            recyclerDocuments.visibility =
                View.VISIBLE

            tvEmpty.visibility =
                View.GONE
        }
    }

    private fun updateFilterStyle() {

        val filterAll =
            findViewById<TextView>(
                R.id.filterAll
            )

        val filterValid =
            findViewById<TextView>(
                R.id.filterValid
            )

        val filterSoon =
            findViewById<TextView>(
                R.id.filterSoon
            )

        val filterExpired =
            findViewById<TextView>(
                R.id.filterExpired
            )

        setFilterStyle(
            filterAll,
            currentFilter == "All"
        )

        setFilterStyle(
            filterValid,
            currentFilter == "VALID"
        )

        setFilterStyle(
            filterSoon,
            currentFilter == "EXPIRING SOON"
        )

        setFilterStyle(
            filterExpired,
            currentFilter == "EXPIRED"
        )
    }

    private fun setFilterStyle(
        view: TextView,
        selected: Boolean
    ) {

        if (selected) {

            view.setTextColor(
                getColor(
                    R.color.card_white
                )
            )

            view.setBackgroundResource(
                R.drawable.bg_filter_selected
            )

        } else {

            view.setTextColor(
                getColor(
                    R.color.text_secondary
                )
            )

            view.setBackgroundResource(
                R.drawable.bg_filter
            )
        }
    }

    override fun onResume() {

        super.onResume()

        if (::adapter.isInitialized) {
            loadDocuments()
        }
    }
}