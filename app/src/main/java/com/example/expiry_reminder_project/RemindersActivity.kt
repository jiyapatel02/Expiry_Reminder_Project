package com.example.expiry_reminder_project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expiry_reminder_project.adapter.DocumentAdapter
import com.example.expiry_reminder_project.model.Document
import com.example.expiry_reminder_project.utils.DateUtils
import org.json.JSONArray

class RemindersActivity : AppCompatActivity() {

    private lateinit var recyclerReminders: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: DocumentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_reminders)

        // ---------------------------------------------------------
        // BACK BUTTON
        // ---------------------------------------------------------

        findViewById<ImageButton>(
            R.id.btnBack
        ).setOnClickListener {

            onBackPressedDispatcher.onBackPressed()
        }


        // ---------------------------------------------------------
        // RECYCLER VIEW
        // ---------------------------------------------------------

        recyclerReminders =
            findViewById(R.id.recyclerReminders)

        tvEmpty =
            findViewById(R.id.tvEmpty)

        adapter =
            DocumentAdapter(
                this,
                mutableListOf()
            )

        recyclerReminders.layoutManager =
            LinearLayoutManager(this)

        recyclerReminders.adapter =
            adapter


        // ---------------------------------------------------------
        // LOAD REMINDERS
        // ---------------------------------------------------------

        loadReminders()


        // ---------------------------------------------------------
        // BOTTOM NAVIGATION - HOME
        // ---------------------------------------------------------

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


        // ---------------------------------------------------------
        // BOTTOM NAVIGATION - DOCUMENTS
        // ---------------------------------------------------------

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


        // ---------------------------------------------------------
        // BOTTOM NAVIGATION - RENEWAL
        // ---------------------------------------------------------

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


        // ---------------------------------------------------------
        // BOTTOM NAVIGATION - SETTINGS
        // ---------------------------------------------------------

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


    // ---------------------------------------------------------
    // LOAD REMINDERS
    // ---------------------------------------------------------

    private fun loadReminders() {

        val reminders =
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

                val status =
                    DateUtils.getStatus(
                        document.expiryDate
                    )

                // Show only documents that need attention
                if (
                    status == "EXPIRED" ||
                    status == "EXPIRING SOON"
                ) {

                    reminders.add(
                        document
                    )
                }
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }


        // ---------------------------------------------------------
        // EMPTY STATE
        // ---------------------------------------------------------

        if (reminders.isEmpty()) {

            recyclerReminders.visibility =
                View.GONE

            tvEmpty.visibility =
                View.VISIBLE

            tvEmpty.text =
                "No reminders at the moment"

        } else {

            recyclerReminders.visibility =
                View.VISIBLE

            tvEmpty.visibility =
                View.GONE

            adapter.updateList(
                reminders
            )
        }
    }


    // ---------------------------------------------------------
    // REFRESH
    // ---------------------------------------------------------

    override fun onResume() {

        super.onResume()

        if (::adapter.isInitialized) {

            loadReminders()
        }
    }
}