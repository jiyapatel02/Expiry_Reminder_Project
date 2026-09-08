package com.example.expiry_reminder_project

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddDocumentActivity : AppCompatActivity() {

    private lateinit var etDocumentName: EditText
    private lateinit var spCategory: Spinner
    private lateinit var etIssueDate: EditText
    private lateinit var etExpiryDate: EditText
    private lateinit var etNotes: EditText
    private lateinit var btnSaveDocument: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_document)
        val btnBack = findViewById<Button>(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }

        etDocumentName = findViewById(R.id.etDocumentName)
        spCategory = findViewById(R.id.spCategory)
        etIssueDate = findViewById(R.id.etIssueDate)
        etExpiryDate = findViewById(R.id.etExpiryDate)
        etNotes = findViewById(R.id.etNotes)
        btnSaveDocument = findViewById(R.id.btnSaveDocument)

        setupCategorySpinner()

        etIssueDate.setOnClickListener {
            showDatePicker(etIssueDate)
        }

        etExpiryDate.setOnClickListener {
            showDatePicker(etExpiryDate)
        }

        btnSaveDocument.setOnClickListener {
            saveDocument()
        }
    }

    private fun setupCategorySpinner() {

        val categories = arrayOf(
            "Select Category",
            "Government ID",
            "Education",
            "Transport",
            "Travel",
            "Financial",
            "Other"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )

        spCategory.adapter = adapter
    }

    private fun showDatePicker(editText: EditText) {

        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->

                val selectedDate = Calendar.getInstance()

                selectedDate.set(
                    year,
                    month,
                    dayOfMonth
                )

                val format = SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.getDefault()
                )

                editText.setText(
                    format.format(selectedDate.time)
                )
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.show()
    }

    private fun saveDocument() {

        val name = etDocumentName.text.toString().trim()
        val category = spCategory.selectedItem.toString()
        val issueDate = etIssueDate.text.toString().trim()
        val expiryDate = etExpiryDate.text.toString().trim()

        if (name.isEmpty()) {
            etDocumentName.error = "Enter document name"
            return
        }

        if (category == "Select Category") {
            Toast.makeText(
                this,
                "Please select a category",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (issueDate.isEmpty()) {
            Toast.makeText(
                this,
                "Select issue date",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (expiryDate.isEmpty()) {
            Toast.makeText(
                this,
                "Select expiry date",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        Toast.makeText(
            this,
            "Document saved successfully!",
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }
}