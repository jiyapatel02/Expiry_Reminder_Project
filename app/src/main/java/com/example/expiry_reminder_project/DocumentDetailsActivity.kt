package com.example.expiry_reminder_project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.example.expiry_reminder_project.utils.DateUtils
import org.json.JSONArray
import java.io.File


class DocumentDetailsActivity : AppCompatActivity() {

    private var documentId: String = ""

    private lateinit var tvDocumentName: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvDocumentNumber: TextView
    private lateinit var tvIssueDate: TextView
    private lateinit var tvExpiryDate: TextView
    private lateinit var tvNotes: TextView


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_document_details
        )

        documentId =
            intent.getStringExtra("document_id")
                ?: ""


        initializeViews()

        setupBackButton()

        setupButtons()

        loadDocument()
    }

    private fun initializeViews() {

        tvDocumentName =
            findViewById(R.id.tvDocumentName)

        tvCategory =
            findViewById(R.id.tvCategory)

        tvStatus =
            findViewById(R.id.tvStatus)

        tvDocumentNumber =
            findViewById(R.id.tvDocumentNumber)

        tvIssueDate =
            findViewById(R.id.tvIssueDate)

        tvExpiryDate =
            findViewById(R.id.tvExpiryDate)

        tvNotes =
            findViewById(R.id.tvNotes)
    }
    private fun setupBackButton() {

        findViewById<ImageButton>(
            R.id.btnBack
        ).setOnClickListener {

            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupButtons() {

        findViewById<MaterialCardView>(
            R.id.cardEdit
        ).setOnClickListener {

            val intent =
                Intent(
                    this,
                    EditDocumentActivity::class.java
                )

            intent.putExtra(
                "document_id",
                documentId
            )

            startActivity(intent)
        }
        findViewById<MaterialCardView>(
            R.id.cardReminder
        ).setOnClickListener {

            showReminderDialog()
        }
        findViewById<MaterialCardView>(
            R.id.cardDelete
        ).setOnClickListener {

            showDeleteConfirmation()
        }
    }

    private fun loadDocument() {

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

            val documents =
                JSONArray(data)


            var found = false


            for (i in 0 until documents.length()) {

                val document =
                    documents.getJSONObject(i)


                if (
                    document.optString("id")
                    == documentId
                ) {

                    found = true


                    val name =
                        document.optString(
                            "name",
                            "Document"
                        )


                    val category =
                        document.optString(
                            "type",
                            document.optString(
                                "category",
                                "Other"
                            )
                        )


                    val documentNumber =
                        document.optString(
                            "documentNumber",
                            ""
                        )


                    val issueDate =
                        document.optString(
                            "issueDate",
                            ""
                        )


                    val expiryDate =
                        document.optString(
                            "expiryDate",
                            ""
                        )


                    val notes =
                        document.optString(
                            "notes",
                            ""
                        )


                    tvDocumentName.text =
                        name


                    tvCategory.text =
                        category


                    tvDocumentNumber.text =
                        if (
                            documentNumber.isBlank()
                        ) {
                            "Not added"
                        } else {
                            documentNumber
                        }


                    tvIssueDate.text =
                        if (
                            issueDate.isBlank()
                        ) {
                            "Not available"
                        } else {
                            issueDate
                        }


                    tvExpiryDate.text =
                        if (
                            expiryDate.isBlank()
                        ) {
                            "Not available"
                        } else {
                            expiryDate
                        }


                    tvNotes.text =
                        if (
                            notes.isBlank()
                        ) {
                            "No notes added"
                        } else {
                            notes
                        }


                    // -----------------------------
                    // Status
                    // -----------------------------

                    val status =
                        if (expiryDate.isBlank()) {

                            "VALID"

                        } else {

                            DateUtils.getStatus(
                                expiryDate
                            )
                        }


                    tvStatus.text =
                        status


                    updateStatusColor(
                        status
                    )


                    return
                }
            }


            if (!found) {

                Toast.makeText(
                    this,
                    "Document not found",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }


        } catch (e: Exception) {

            e.printStackTrace()

            Toast.makeText(
                this,
                "Unable to load document",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private fun updateStatusColor(
        status: String
    ) {

        when (status.uppercase()) {

            "VALID" -> {

                tvStatus.setTextColor(
                    getColor(
                        R.color.status_valid
                    )
                )
            }


            "EXPIRING SOON" -> {

                tvStatus.setTextColor(
                    getColor(
                        R.color.status_due_soon
                    )
                )
            }


            "EXPIRED" -> {

                tvStatus.setTextColor(
                    getColor(
                        R.color.status_expired
                    )
                )
            }


            else -> {

                tvStatus.setTextColor(
                    getColor(
                        R.color.text_secondary
                    )
                )
            }
        }
    }

    private fun showReminderDialog() {

        val options =
            arrayOf(
                "7 days before",
                "14 days before",
                "30 days before",
                "3 months before",
                "6 months before"
            )


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


        var currentReminder =
            "7 days before"


        // Find currently saved reminder
        try {

            val documents =
                JSONArray(data)


            for (i in 0 until documents.length()) {

                val document =
                    documents.getJSONObject(i)


                if (
                    document.optString("id")
                    == documentId
                ) {

                    currentReminder =
                        document.optString(
                            "reminderPeriod",
                            "7 days before"
                        )

                    break
                }
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }


        var selectedIndex =
            options.indexOf(
                currentReminder
            )


        if (selectedIndex < 0) {
            selectedIndex = 0
        }


        AlertDialog.Builder(this)

            .setTitle(
                "Set Reminder"
            )

            .setSingleChoiceItems(
                options,
                selectedIndex
            ) { dialog, which ->

                val selectedReminder =
                    options[which]


                saveReminder(
                    selectedReminder
                )


                dialog.dismiss()
            }

            .setNegativeButton(
                "Cancel",
                null
            )

            .show()
    }

    private fun saveReminder(
        reminderPeriod: String
    ) {

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

            val documents =
                JSONArray(data)


            var updated = false


            for (i in 0 until documents.length()) {

                val document =
                    documents.getJSONObject(i)


                if (
                    document.optString("id")
                    == documentId
                ) {

                    document.put(
                        "reminderPeriod",
                        reminderPeriod
                    )


                    // Reset notification flag
                    document.put(
                        "lastReminderDate",
                        ""
                    )


                    updated = true

                    break
                }
            }


            if (updated) {

                preferences.edit()
                    .putString(
                        "documents",
                        documents.toString()
                    )
                    .apply()


                // Make sure daily checking is running
                ReminderScheduler
                    .scheduleDailyReminder(
                        this
                    )


                Toast.makeText(
                    this,
                    "Reminder set: $reminderPeriod",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                Toast.makeText(
                    this,
                    "Document not found",
                    Toast.LENGTH_SHORT
                ).show()
            }


        } catch (e: Exception) {

            e.printStackTrace()

            Toast.makeText(
                this,
                "Unable to set reminder",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showDeleteConfirmation() {

        AlertDialog.Builder(this)

            .setTitle(
                "Delete Document"
            )

            .setMessage(
                "Are you sure you want to delete this document?"
            )

            .setNegativeButton(
                "Cancel",
                null
            )

            .setPositiveButton(
                "Delete"
            ) { _, _ ->

                deleteDocument()
            }

            .show()
    }

    private fun deleteDocument() {

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

            val documents =
                JSONArray(data)


            val remainingDocuments =
                JSONArray()


            var deletedFilePath =
                ""


            for (i in 0 until documents.length()) {

                val document =
                    documents.getJSONObject(i)


                if (
                    document.optString("id")
                    == documentId
                ) {

                    // Get saved attachment path
                    deletedFilePath =
                        document.optString(
                            "filePath",
                            ""
                        )

                } else {

                    remainingDocuments.put(
                        document
                    )
                }
            }

            if (
                deletedFilePath.isNotBlank()
            ) {

                try {

                    val file =
                        File(
                            deletedFilePath
                        )

                    if (file.exists()) {
                        file.delete()
                    }

                } catch (e: Exception) {

                    e.printStackTrace()
                }
            }


            preferences.edit()
                .putString(
                    "documents",
                    remainingDocuments.toString()
                )
                .apply()


            Toast.makeText(
                this,
                "Document deleted",
                Toast.LENGTH_SHORT
            ).show()


            finish()


        } catch (e: Exception) {

            e.printStackTrace()

            Toast.makeText(
                this,
                "Unable to delete document",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onResume() {

        super.onResume()

        if (
            ::tvDocumentName.isInitialized
        ) {

            loadDocument()
        }
    }
}