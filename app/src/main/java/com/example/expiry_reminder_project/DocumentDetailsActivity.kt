package com.example.expiry_reminder_project

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import java.util.Calendar

class DocumentDetailsActivity : AppCompatActivity() {

    private var documentId: String = ""

    private val preferencesName =
        "DocumentStorage"

    private val documentsKey =
        "documents"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_document_details
        )

        documentId =
            intent.getStringExtra(
                "documentId"
            ).orEmpty()

        val btnBack =
            findViewById<Button>(
                R.id.btnBack
            )

        val cardEdit =
            findViewById<View>(
                R.id.cardEdit
            )

        val cardDelete =
            findViewById<View>(
                R.id.cardDelete
            )


        btnBack.setOnClickListener {
            finish()
        }


        loadDocument()


        cardEdit.setOnClickListener {
            showEditDialog()
        }


        cardDelete.setOnClickListener {
            showDeleteDialog()
        }
    }


    private fun loadDocument() {

        val documents =
            getDocuments()

        for (i in 0 until documents.length()) {

            val document =
                documents.getJSONObject(i)

            if (
                document.optString("id")
                == documentId
            ) {

                findViewById<TextView>(
                    R.id.tvDocumentName
                ).text =
                    document.optString("name")

                findViewById<TextView>(
                    R.id.tvCategory
                ).text =
                    document.optString("category")

                findViewById<TextView>(
                    R.id.tvIssueDate
                ).text =
                    "Issue Date: ${
                        document.optString(
                            "issueDate"
                        )
                    }"

                findViewById<TextView>(
                    R.id.tvExpiryDate
                ).text =
                    "Expiry Date: ${
                        document.optString(
                            "expiryDate"
                        )
                    }"

                findViewById<TextView>(
                    R.id.tvNotes
                ).text =
                    document.optString(
                        "notes",
                        "No notes"
                    )

                return
            }
        }
    }


    private fun showEditDialog() {

        val documents =
            getDocuments()

        for (i in 0 until documents.length()) {

            val document =
                documents.getJSONObject(i)

            if (
                document.optString("id")
                == documentId
            ) {

                val input =
                    EditText(this)

                input.setText(
                    document.optString("name")
                )

                input.setTextColor(
                    getColor(
                        R.color.text_primary
                    )
                )

                input.hint =
                    "Document name"


                AlertDialog.Builder(this)
                    .setTitle("Edit Document Name")
                    .setView(input)
                    .setNegativeButton(
                        "Cancel",
                        null
                    )
                    .setPositiveButton(
                        "Save"
                    ) { _, _ ->

                        val newName =
                            input.text
                                .toString()
                                .trim()

                        if (newName.isNotEmpty()) {

                            document.put(
                                "name",
                                newName
                            )

                            saveDocuments(
                                documents
                            )

                            loadDocument()
                        }
                    }
                    .show()

                return
            }
        }
    }


    private fun showDeleteDialog() {

        AlertDialog.Builder(this)
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

        val documents =
            getDocuments()

        val updatedDocuments =
            JSONArray()

        for (i in 0 until documents.length()) {

            val document =
                documents.getJSONObject(i)

            if (
                document.optString("id")
                != documentId
            ) {

                updatedDocuments.put(
                    document
                )
            }
        }

        saveDocuments(
            updatedDocuments
        )

        finish()
    }


    private fun getDocuments(): JSONArray {

        val preferences =
            getSharedPreferences(
                preferencesName,
                MODE_PRIVATE
            )

        return JSONArray(
            preferences.getString(
                documentsKey,
                "[]"
            )
        )
    }


    private fun saveDocuments(
        documents: JSONArray
    ) {

        getSharedPreferences(
            preferencesName,
            MODE_PRIVATE
        )
            .edit()
            .putString(
                documentsKey,
                documents.toString()
            )
            .apply()
    }
}