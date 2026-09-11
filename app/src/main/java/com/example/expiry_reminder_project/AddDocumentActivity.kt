package com.example.expiry_reminder_project

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AddDocumentActivity : AppCompatActivity() {

    private lateinit var etDocumentName: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var tvIssueDate: TextView
    private lateinit var tvExpiryDate: TextView
    private lateinit var etNotes: EditText
    private lateinit var btnSave: TextView
    private lateinit var btnBack: TextView

    private var issueDateMillis: Long? = null
    private var expiryDateMillis: Long? = null

    private val displayFormat =
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_document)

        etDocumentName = findViewById(R.id.etDocumentName)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        tvIssueDate = findViewById(R.id.tvIssueDate)
        tvExpiryDate = findViewById(R.id.tvExpiryDate)
        etNotes = findViewById(R.id.etNotes)
        btnSave = findViewById(R.id.btnSave)
        btnBack = findViewById(R.id.btnBack)

        setupCategorySpinner()

        btnBack.setOnClickListener {
            finish()
        }

        tvIssueDate.setOnClickListener {
            showDatePicker(true)
        }

        tvExpiryDate.setOnClickListener {
            showDatePicker(false)
        }

        btnSave.setOnClickListener {
            saveDocument()
        }
    }

    private fun setupCategorySpinner() {

        val categories = arrayOf(
            "Select Category",
            "Government ID",
            "Identity Document",
            "Education",
            "Vehicle",
            "Insurance",
            "Financial",
            "Other"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerCategory.adapter = adapter
    }

    private fun showDatePicker(isIssueDate: Boolean) {

        val calendar = Calendar.getInstance()

        val selectedDate = if (isIssueDate) {
            issueDateMillis
        } else {
            expiryDateMillis
        }

        if (selectedDate != null) {
            calendar.timeInMillis = selectedDate
        }

        val dialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->

                val selectedCalendar = Calendar.getInstance()

                selectedCalendar.set(
                    year,
                    month,
                    dayOfMonth,
                    0,
                    0,
                    0
                )

                selectedCalendar.set(
                    Calendar.MILLISECOND,
                    0
                )

                val millis = selectedCalendar.timeInMillis

                if (isIssueDate) {

                    if (expiryDateMillis != null &&
                        millis >= expiryDateMillis!!
                    ) {

                        Toast.makeText(
                            this,
                            "Issue date must be before expiry date",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@DatePickerDialog
                    }

                    issueDateMillis = millis

                    tvIssueDate.text =
                        displayFormat.format(selectedCalendar.time)

                } else {

                    if (issueDateMillis != null &&
                        millis <= issueDateMillis!!
                    ) {

                        Toast.makeText(
                            this,
                            "Expiry date must be after issue date",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@DatePickerDialog
                    }

                    expiryDateMillis = millis

                    tvExpiryDate.text =
                        displayFormat.format(selectedCalendar.time)
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        dialog.show()
    }

    private fun saveDocument() {

        val name = etDocumentName.text.toString().trim()
        val category = spinnerCategory.selectedItem.toString()
        val notes = etNotes.text.toString().trim()

        if (name.isEmpty()) {
            etDocumentName.error = "Enter document name"
            etDocumentName.requestFocus()
            return
        }

        if (spinnerCategory.selectedItemPosition == 0) {

            Toast.makeText(
                this,
                "Please select a category",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (issueDateMillis == null) {

            Toast.makeText(
                this,
                "Please select issue date",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (expiryDateMillis == null) {

            Toast.makeText(
                this,
                "Please select expiry date",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val document = JSONObject()

        document.put(
            "id",
            UUID.randomUUID().toString()
        )

        document.put(
            "name",
            name
        )

        document.put(
            "category",
            category
        )

        document.put(
            "issueDate",
            issueDateMillis
        )

        document.put(
            "expiryDate",
            expiryDateMillis
        )

        document.put(
            "notes",
            notes
        )

        val preferences = getSharedPreferences(
            "DocumentStorage",
            MODE_PRIVATE
        )

        val existingDocuments = JSONArray(
            preferences.getString(
                "documents",
                "[]"
            )
        )

        existingDocuments.put(document)

        preferences.edit()
            .putString(
                "documents",
                existingDocuments.toString()
            )
            .apply()

        Toast.makeText(
            this,
            "Document saved successfully",
            Toast.LENGTH_SHORT
        ).show()

        val intent = Intent(
            this,
            DocumentsActivity::class.java
        )

        startActivity(intent)

        finish()
    }
}