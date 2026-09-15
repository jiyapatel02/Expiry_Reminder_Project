package com.example.expiry_reminder_project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expiry_reminder_project.adapter.FolderAdapter
import com.example.expiry_reminder_project.model.Folder
import com.google.android.material.card.MaterialCardView
import org.json.JSONArray

class FoldersActivity : AppCompatActivity() {

    private lateinit var recyclerFolders: RecyclerView
    private lateinit var tvEmptyFolders: TextView
    private lateinit var tvFolderCount: TextView
    private lateinit var adapter: FolderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_folders)

        recyclerFolders = findViewById(R.id.recyclerFolders)
        tvEmptyFolders = findViewById(R.id.tvEmptyFolders)
        tvFolderCount = findViewById(R.id.tvFolderCount)

        setupRecyclerView()
        setupClicks()
        loadFolders()
    }

    private fun setupRecyclerView() {

        adapter = FolderAdapter(
            mutableListOf(),

            // Open folder
            { folder ->

                val intent = Intent(
                    this,
                    FolderDocumentsActivity::class.java
                )

                intent.putExtra(
                    "folder_id",
                    folder.id
                )

                intent.putExtra(
                    "folder_name",
                    folder.name
                )

                startActivity(intent)
            },

            // Edit folder
            { folder ->

                val intent = Intent(
                    this,
                    EditFolderActivity::class.java
                )

                intent.putExtra(
                    "folder_id",
                    folder.id
                )

                startActivity(intent)
            }
        )

        recyclerFolders.layoutManager =
            LinearLayoutManager(this)

        recyclerFolders.adapter = adapter
    }

    private fun setupClicks() {

        findViewById<TextView>(
            R.id.btnBack
        ).setOnClickListener {

            onBackPressedDispatcher.onBackPressed()
        }

        findViewById<MaterialCardView>(
            R.id.btnAddFolder
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    AddFolderActivity::class.java
                )
            )
        }
    }

    private fun loadFolders() {

        val folders = getFolders()

        adapter.updateList(folders)

        tvFolderCount.text =
            if (folders.size == 1) {
                "1 Folder"
            } else {
                "${folders.size} Folders"
            }

        if (folders.isEmpty()) {

            recyclerFolders.visibility =
                View.GONE

            tvEmptyFolders.visibility =
                View.VISIBLE

        } else {

            recyclerFolders.visibility =
                View.VISIBLE

            tvEmptyFolders.visibility =
                View.GONE
        }
    }

    private fun getFolders(): MutableList<Folder> {

        val folders =
            mutableListOf<Folder>()

        val preferences =
            getSharedPreferences(
                "ExpiryReminder",
                Context.MODE_PRIVATE
            )

        val data =
            preferences.getString(
                "folders",
                "[]"
            )

        try {

            val foldersArray =
                JSONArray(data)

            for (i in 0 until foldersArray.length()) {

                val folder =
                    foldersArray.getJSONObject(i)

                folders.add(
                    Folder(
                        id = folder.optString("id"),
                        name = folder.optString("name"),
                        description =
                            folder.optString(
                                "description"
                            )
                    )
                )
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }

        return folders
    }

    override fun onResume() {

        super.onResume()

        if (::adapter.isInitialized) {
            loadFolders()
        }
    }
}