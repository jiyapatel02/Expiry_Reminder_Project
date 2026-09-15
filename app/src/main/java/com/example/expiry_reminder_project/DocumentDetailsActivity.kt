package com.example.expiry_reminder_project

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.expiry_reminder_project.utils.DateUtils
import com.google.android.material.card.MaterialCardView
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Calendar
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

        findViewById<TextView>(R.id.btnBack).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        findViewById<MaterialCardView>(R.id.cardEdit)
            .setOnClickListener {
                showEditDialog()
            }

        findViewById<MaterialCardView>(R.id.cardDelete)
            .setOnClickListener {
                showDeleteDialog()
            }

        loadDocument()
    }

    // ---------------------------------------------------------
    // LOAD DOCUMENT
    // ---------------------------------------------------------

    private fun loadDocument() {

        val documents = getDocuments()

        for (i in 0 until documents.length()) {

            val document = documents.getJSONObject(i)

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
                        document.optString("issueDate")
                    )

                val expiryDate =
                    formatDate(
                        document.optString("expiryDate")
                    )

                val notes =
                    document.optString(
                        "notes",
                        ""
                    )

                val status =
                    DateUtils.getStatus(
                        document.optString("expiryDate")
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

    // ---------------------------------------------------------
    // STATUS
    // ---------------------------------------------------------

    private fun updateStatus(status: String) {

        val tvStatus =
            findViewById<TextView>(R.id.tvStatus)

        when (status) {

            "VALID" -> {

                tvStatus.text = "VALID"

                tvStatus.setTextColor(
                    getColor(R.color.status_valid)
                )

                tvStatus.setBackgroundColor(
                    getColor(R.color.primary_light)
                )
            }

            "EXPIRING SOON" -> {

                tvStatus.text = "EXPIRING SOON"

                tvStatus.setTextColor(
                    getColor(R.color.status_due_soon)
                )

                tvStatus.setBackgroundColor(
                    getColor(R.color.primary_light)
                )
            }

            "EXPIRED" -> {

                tvStatus.text = "EXPIRED"

                tvStatus.setTextColor(
                    getColor(R.color.status_expired)
                )

                tvStatus.setBackgroundColor(
                    getColor(R.color.primary_light)
                )
            }

            else -> {
                tvStatus.text = status
            }
        }
    }

    // ---------------------------------------------------------
    // DATE FORMAT
    // ---------------------------------------------------------

    private fun formatDate(value: String): String {

        if (value.isBlank()) {
            return "Not available"
        }

        return try {

            val millis = value.toLong()

            displayFormat.format(
                Date(millis)
            )

        } catch (e: Exception) {

            value
        }
    }

    // ---------------------------------------------------------
    // FULL EDIT DOCUMENT
    // ---------------------------------------------------------

    private fun showEditDialog() {

        val documents = getDocuments()

        for (i in 0 until documents.length()) {

            val document =
                documents.getJSONObject(i)

            if (document.optString("id") == documentId) {

                showEditForm(document, documents)

                return
            }
        }

        Toast.makeText(
            this,
            "Document not found",
            Toast.LENGTH_SHORT
        ).show()
    }

    // ---------------------------------------------------------
    // EDIT FORM
    // ---------------------------------------------------------

    private fun showEditForm(
        document: org.json.JSONObject,
        documents: JSONArray
    ) {

        val dialogView =
            layoutInflater.inflate(
                R.layout.dialog_edit_document,
                null
            )

        val etName =
            dialogView.findViewById<EditText>(
                R.id.etEditDocumentName
            )

        val etNumber =
            dialogView.findViewById<EditText>(
                R.id.etEditDocumentNumber
            )

        val spinnerCategory =
            dialogView.findViewById<Spinner>(
                R.id.spinnerEditCategory
            )

        val tvIssueDate =
            dialogView.findViewById<TextView>(
                R.id.tvEditIssueDate
            )

        val tvExpiryDate =
            dialogView.findViewById<TextView>(
                R.id.tvEditExpiryDate
            )

        val etNotes =
            dialogView.findViewById<EditText>(
                R.id.etEditNotes
            )

        // Existing values
        etName.setText(
            document.optString("name")
        )

        etNumber.setText(
            document.optString("documentNumber")
        )

        etNotes.setText(
            document.optString("notes")
        )

        // Make typed text clearly visible
        etName.setTextColor(
            getColor(R.color.text_primary)
        )

        etNumber.setTextColor(
            getColor(R.color.text_primary)
        )

        etNotes.setTextColor(
            getColor(R.color.text_primary)
        )

        etName.setHintTextColor(
            getColor(R.color.text_hint)
        )

        etNumber.setHintTextColor(
            getColor(R.color.text_hint)
        )

        etNotes.setHintTextColor(
            getColor(R.color.text_hint)
        )

        // -----------------------------------------------------
        // CATEGORY
        // -----------------------------------------------------

        val categories = arrayOf(
            "Government ID",
            "Identity Document",
            "Education",
            "Vehicle",
            "Insurance",
            "Financial",
            "Other"
        )

        val categoryAdapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                categories
            )

        categoryAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerCategory.adapter =
            categoryAdapter

        val currentCategory =
            document.optString(
                "type",
                document.optString(
                    "category",
                    "Other"
                )
            )

        val categoryIndex =
            categories.indexOf(currentCategory)

        if (categoryIndex >= 0) {
            spinnerCategory.setSelection(
                categoryIndex
            )
        }

        // -----------------------------------------------------
        // EXISTING DATES
        // -----------------------------------------------------

        var issueDateMillis =
            document.optString("issueDate")
                .toLongOrNull()

        var expiryDateMillis =
            document.optString("expiryDate")
                .toLongOrNull()

        if (issueDateMillis != null) {

            tvIssueDate.text =
                formatDate(
                    issueDateMillis.toString()
                )

            tvIssueDate.setTextColor(
                getColor(R.color.text_primary)
            )
        }

        if (expiryDateMillis != null) {

            tvExpiryDate.text =
                formatDate(
                    expiryDateMillis.toString()
                )

            tvExpiryDate.setTextColor(
                getColor(R.color.text_primary)
            )
        }

        // -----------------------------------------------------
        // DATE CLICK
        // -----------------------------------------------------

        tvIssueDate.setOnClickListener {

            showDatePicker(
                tvIssueDate,
                true,
                issueDateMillis,
                expiryDateMillis
            ) { selectedMillis ->

                issueDateMillis =
                    selectedMillis
            }
        }

        tvExpiryDate.setOnClickListener {

            showDatePicker(
                tvExpiryDate,
                false,
                issueDateMillis,
                expiryDateMillis
            ) { selectedMillis ->

                expiryDateMillis =
                    selectedMillis
            }
        }

        // -----------------------------------------------------
        // DIALOG
        // -----------------------------------------------------

        val dialog =
            AlertDialog.Builder(this)
                .setTitle("Edit Document")
                .setView(dialogView)
                .setNegativeButton(
                    "Cancel",
                    null
                )
                .setPositiveButton(
                    "Save",
                    null
                )
                .create()

        dialog.setOnShowListener {

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val name =
                    etName.text
                        .toString()
                        .trim()

                val number =
                    etNumber.text
                        .toString()
                        .trim()

                val category =
                    spinnerCategory.selectedItem
                        .toString()

                val notes =
                    etNotes.text
                        .toString()
                        .trim()

                // Validation
                if (name.isEmpty()) {

                    etName.error =
                        "Enter document name"

                    etName.requestFocus()

                    return@setOnClickListener
                }

                if (issueDateMillis == null) {

                    Toast.makeText(
                        this,
                        "Please select issue date",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                if (expiryDateMillis == null) {

                    Toast.makeText(
                        this,
                        "Please select expiry date",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                if (expiryDateMillis!! <= issueDateMillis!!) {

                    Toast.makeText(
                        this,
                        "Expiry date must be after issue date",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                // -------------------------------------------------
                // UPDATE JSON
                // -------------------------------------------------

                document.put(
                    "name",
                    name
                )

                document.put(
                    "documentNumber",
                    number
                )

                document.put(
                    "type",
                    category
                )

                document.put(
                    "category",
                    category
                )

                document.put(
                    "issueDate",
                    issueDateMillis.toString()
                )

                document.put(
                    "expiryDate",
                    expiryDateMillis.toString()
                )

                document.put(
                    "notes",
                    notes
                )

                saveDocuments(documents)

                loadDocument()

                Toast.makeText(
                    this,
                    "Document updated successfully",
                    Toast.LENGTH_SHORT
                ).show()

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    // ---------------------------------------------------------
    // DATE PICKER
    // ---------------------------------------------------------

    private fun showDatePicker(
        textView: TextView,
        isIssueDate: Boolean,
        currentMillis: Long?,
        otherDateMillis: Long?,
        onSelected: (Long) -> Unit
    ) {

        val calendar =
            Calendar.getInstance()

        if (currentMillis != null) {
            calendar.timeInMillis =
                currentMillis
        }

        val dialog =
            DatePickerDialog(
                this,
                { _, year, month, day ->

                    val selectedCalendar =
                        Calendar.getInstance()

                    selectedCalendar.set(
                        year,
                        month,
                        day,
                        0,
                        0,
                        0
                    )

                    selectedCalendar.set(
                        Calendar.MILLISECOND,
                        0
                    )

                    val selectedMillis =
                        selectedCalendar.timeInMillis

                    // Issue date must be before expiry
                    if (
                        isIssueDate &&
                        otherDateMillis != null &&
                        selectedMillis >= otherDateMillis
                    ) {

                        Toast.makeText(
                            this,
                            "Issue date must be before expiry date",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@DatePickerDialog
                    }

                    // Expiry date must be after issue
                    if (
                        !isIssueDate &&
                        otherDateMillis != null &&
                        selectedMillis <= otherDateMillis
                    ) {

                        Toast.makeText(
                            this,
                            "Expiry date must be after issue date",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@DatePickerDialog
                    }

                    textView.text =
                        displayFormat.format(
                            selectedCalendar.time
                        )

                    textView.setTextColor(
                        getColor(
                            R.color.text_primary
                        )
                    )

                    onSelected(
                        selectedMillis
                    )
                },
                calendar.get(
                    Calendar.YEAR
                ),
                calendar.get(
                    Calendar.MONTH
                ),
                calendar.get(
                    Calendar.DAY_OF_MONTH
                )
            )

        dialog.show()
    }

    // ---------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------

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

        Toast.makeText(
            this,
            "Document deleted",
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }

    // ---------------------------------------------------------
    // STORAGE
    // ---------------------------------------------------------

    private fun getDocuments(): JSONArray {

        val preferences =
            getSharedPreferences(
                preferencesName,
                Context.MODE_PRIVATE
            )

        return try {

            JSONArray(
                preferences.getString(
                    documentsKey,
                    "[]"
                )
            )

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
}