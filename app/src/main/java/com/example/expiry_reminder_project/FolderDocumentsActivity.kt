package com.example.expiry_reminder_project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expiry_reminder_project.adapter.DocumentAdapter
import com.example.expiry_reminder_project.model.Document
import com.example.expiry_reminder_project.utils.DateUtils
import org.json.JSONArray

class FolderDocumentsActivity : AppCompatActivity() {

    private lateinit var recyclerDocuments: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var tvFolderName: TextView
    private lateinit var adapter: DocumentAdapter

    private var folderId = ""
    private var folderName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_folder_documents)

        folderId =
            intent.getStringExtra("folder_id").orEmpty()

        folderName =
            intent.getStringExtra("folder_name").orEmpty()

        initializeViews()
        setupRecyclerView()
        setupBackButton()
        loadFolderDocuments()
    }

    private fun initializeViews() {

        recyclerDocuments =
            findViewById(R.id.recyclerDocuments)

        tvEmpty =
            findViewById(R.id.tvEmpty)

        tvFolderName =
            findViewById(R.id.tvFolderName)

        tvFolderName.text =
            if (folderName.isBlank()) {
                "Folder Documents"
            } else {
                folderName
            }
    }

    private fun setupRecyclerView() {

        adapter =
            DocumentAdapter(
                this,
                mutableListOf()
            )

        recyclerDocuments.layoutManager =
            LinearLayoutManager(this)

        recyclerDocuments.adapter =
            adapter
    }

    private fun setupBackButton() {

        findViewById<TextView>(
            R.id.btnBack
        ).setOnClickListener {

            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun loadFolderDocuments() {

        val allDocuments =
            getDocuments()

        val folderDocuments =
            allDocuments.filter { document ->

                document.folderId == folderId

            }.toMutableList()

        adapter.updateList(folderDocuments)

        if (folderDocuments.isEmpty()) {

            recyclerDocuments.visibility =
                View.GONE

            tvEmpty.visibility =
                View.VISIBLE

        } else {

            recyclerDocuments.visibility =
                View.VISIBLE

            tvEmpty.visibility =
                View.GONE
        }
    }

    private fun getDocuments(): MutableList<Document> {

        val documents =
            mutableListOf<Document>()

        val preferences =
            getSharedPreferences(
                "DocumentStorage",
                Context.MODE_PRIVATE
            )

        val data =
            preferences.getString(
                "documents",
                "[]"
            )

        try {

            val jsonArray =
                JSONArray(data)

            for (i in 0 until jsonArray.length()) {

                val obj =
                    jsonArray.getJSONObject(i)

                documents.add(
                    Document(
                        id = obj.optString("id"),
                        name = obj.optString("name"),
                        type = obj.optString(
                            "type",
                            obj.optString("category")
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
                )
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }

        return documents
    }

    override fun onResume() {

        super.onResume()

        if (::adapter.isInitialized) {
            loadFolderDocuments()
        }
    }
}