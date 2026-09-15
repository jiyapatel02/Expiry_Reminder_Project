package com.example.expiry_reminder_project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.expiry_reminder_project.utils.DateUtils
import com.google.android.material.card.MaterialCardView
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DocumentDetailsActivity : AppCompatActivity() {

    private var documentId = ""

    private val preferencesName = "DocumentStorage"
    private val documentsKey = "documents"

    private val displayFormat =
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_document_details)

        documentId =
            intent.getStringExtra("document_id")
                ?: intent.getStringExtra("documentId")
                        ?: ""

        findViewById<TextView>(R.id.btnBack)
            .setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

        findViewById<MaterialCardView>(R.id.cardEdit)
            .setOnClickListener {

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

        findViewById<MaterialCardView>(R.id.cardDelete)
            .setOnClickListener {
                showDeleteDialog()
            }

        loadDocument()
    }

    private fun loadDocument() {

        val documents = getDocuments()

        for (i in 0 until documents.length()) {

            val document =
                documents.getJSONObject(i)

            if (document.optString("id") == documentId) {

                val name =
                    document.optString(
                        "name",
                        "Unnamed Document"
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
                    formatDate(
                        document.optString(
                            "issueDate"
                        )
                    )

                val expiryDate =
                    formatDate(
                        document.optString(
                            "expiryDate"
                        )
                    )

                val notes =
                    document.optString(
                        "notes",
                        ""
                    )

                val status =
                    DateUtils.getStatus(
                        document.optString(
                            "expiryDate"
                        )
                    )

                findViewById<TextView>(
                    R.id.tvDocumentName
                ).text = name

                findViewById<TextView>(
                    R.id.tvCategory
                ).text = category

                findViewById<TextView>(
                    R.id.tvDocumentNumber
                ).text =
                    if (documentNumber.isBlank()) {
                        "Not added"
                    } else {
                        documentNumber
                    }

                findViewById<TextView>(
                    R.id.tvIssueDate
                ).text = issueDate

                findViewById<TextView>(
                    R.id.tvExpiryDate
                ).text = expiryDate

                findViewById<TextView>(
                    R.id.tvNotes
                ).text =
                    if (notes.isBlank()) {
                        "No notes added"
                    } else {
                        notes
                    }

                updateStatus(status)

                return
            }
        }

        Toast.makeText(
            this,
            "Document not found",
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }

    private fun updateStatus(status: String) {

        val tvStatus =
            findViewById<TextView>(
                R.id.tvStatus
            )

        when (status) {

            "VALID" -> {

                tvStatus.text = "VALID"

                tvStatus.setTextColor(
                    getColor(
                        R.color.status_valid
                    )
                )

                tvStatus.setBackgroundResource(
                    R.drawable.bg_status_valid
                )
            }

            "EXPIRING SOON" -> {

                tvStatus.text =
                    "EXPIRING SOON"

                tvStatus.setTextColor(
                    getColor(
                        R.color.status_due_soon
                    )
                )

                tvStatus.setBackgroundResource(
                    R.drawable.bg_status_soon
                )
            }

            "EXPIRED" -> {

                tvStatus.text = "EXPIRED"

                tvStatus.setTextColor(
                    getColor(
                        R.color.status_expired
                    )
                )

                tvStatus.setBackgroundResource(
                    R.drawable.bg_status_expired
                )
            }

            else -> {

                tvStatus.text = status

                tvStatus.setTextColor(
                    getColor(
                        R.color.text_secondary
                    )
                )
            }
        }
    }

    private fun formatDate(value: String): String {

        if (value.isBlank()) {
            return "Not available"
        }

        return try {

            val millis =
                value.toLong()

            displayFormat.format(
                Date(millis)
            )

        } catch (e: Exception) {

            value
        }
    }

    private fun getDocuments(): JSONArray {

        val preferences =
            getSharedPreferences(
                preferencesName,
                Context.MODE_PRIVATE
            )

        val data =
            preferences.getString(
                documentsKey,
                "[]"
            )

        return try {
            JSONArray(data)
        } catch (e: Exception) {
            JSONArray()
        }
    }

    private fun saveDocuments(
        documents: JSONArray
    ) {

        getSharedPreferences(
            preferencesName,
            Context.MODE_PRIVATE
        )
            .edit()
            .putString(
                documentsKey,
                documents.toString()
            )
            .apply()
    }

    private fun showDeleteDialog() {

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Delete Document")
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

        val oldDocuments =
            getDocuments()

        val newDocuments =
            JSONArray()

        for (i in 0 until oldDocuments.length()) {

            val document =
                oldDocuments.getJSONObject(i)

            if (
                document.optString("id")
                != documentId
            ) {
                newDocuments.put(document)
            }
        }

        saveDocuments(newDocuments)

        Toast.makeText(
            this,
            "Document deleted",
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }

    override fun onResume() {
        super.onResume()

        if (documentId.isNotBlank()) {
            loadDocument()
        }
    }
}