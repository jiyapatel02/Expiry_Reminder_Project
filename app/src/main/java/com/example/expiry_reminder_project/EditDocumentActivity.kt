package com.example.expiry_reminder_project

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditDocumentActivity : AppCompatActivity() {

    private lateinit var etDocumentName: EditText
    private lateinit var etDocumentNumber: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerFolder: Spinner
    private lateinit var tvIssueDate: TextView
    private lateinit var tvExpiryDate: TextView
    private lateinit var etNotes: EditText
    private lateinit var btnUpdateDocument: MaterialCardView

    private var documentId: String = ""

    private val categories = arrayOf(
        "Select Category",
        "Identity",
        "Education",
        "Finance",
        "Insurance",
        "Vehicle",
        "Medical",
        "Employment",
        "Property",
        "Other"
    )

    private val dateFormat = SimpleDateFormat(
        "dd/MM/yyyy",
        Locale.getDefault()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_document)

        documentId = intent.getStringExtra("document_id") ?: ""

        initializeViews()
        setupBackButton()
        setupCategorySpinner()
        loadFolders()
        setupDatePickers()
        loadDocument()
        setupUpdateButton()
    }

    // ---------------------------------------------------------
    // INITIALIZE VIEWS
    // ---------------------------------------------------------

    private fun initializeViews() {

        etDocumentName = findViewById(R.id.etDocumentName)
        etDocumentNumber = findViewById(R.id.etDocumentNumber)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        spinnerFolder = findViewById(R.id.spinnerFolder)
        tvIssueDate = findViewById(R.id.tvIssueDate)
        tvExpiryDate = findViewById(R.id.tvExpiryDate)
        etNotes = findViewById(R.id.etNotes)
        btnUpdateDocument = findViewById(R.id.btnUpdateDocument)
    }

    // ---------------------------------------------------------
    // BACK BUTTON
    // ---------------------------------------------------------

    private fun setupBackButton() {

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    // ---------------------------------------------------------
    // CATEGORY SPINNER
    // ---------------------------------------------------------

    private fun setupCategorySpinner() {

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

    // ---------------------------------------------------------
    // LOAD FOLDERS
    // ---------------------------------------------------------

    private fun loadFolders() {

        val folderNames = ArrayList<String>()
        val folderIds = ArrayList<String>()

        folderNames.add("No Folder")
        folderIds.add("")

        val preferences = getSharedPreferences(
            "ExpiryReminder",
            Context.MODE_PRIVATE
        )

        val data = preferences.getString(
            "folders",
            "[]"
        ) ?: "[]"

        try {

            val folders = JSONArray(data)

            for (i in 0 until folders.length()) {

                val folder = folders.getJSONObject(i)

                folderNames.add(
                    folder.optString(
                        "name",
                        "Folder"
                    )
                )

                folderIds.add(
                    folder.optString("id")
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            folderNames
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerFolder.adapter = adapter

        spinnerFolder.tag = folderIds
    }

    // ---------------------------------------------------------
    // LOAD EXISTING DOCUMENT
    // ---------------------------------------------------------

    private fun loadDocument() {

        if (documentId.isEmpty()) {
            Toast.makeText(
                this,
                "Document not found",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        val preferences = getSharedPreferences(
            "DocumentStorage",
            Context.MODE_PRIVATE
        )

        val data = preferences.getString(
            "documents",
            "[]"
        ) ?: "[]"

        try {

            val documents = JSONArray(data)

            for (i in 0 until documents.length()) {

                val document = documents.getJSONObject(i)

                if (document.optString("id") == documentId) {

                    etDocumentName.setText(
                        document.optString("name")
                    )

                    etDocumentNumber.setText(
                        document.optString("documentNumber")
                    )

                    etNotes.setText(
                        document.optString("notes")
                    )

                    val category = document.optString(
                        "type",
                        document.optString("category")
                    )

                    setCategorySelection(category)

                    val folderId = document.optString(
                        "folderId"
                    )

                    setFolderSelection(folderId)

                    val issueDate = document.optString(
                        "issueDate"
                    )

                    if (issueDate.isNotEmpty()) {
                        tvIssueDate.text = issueDate
                    }

                    val expiryDate = document.optString(
                        "expiryDate"
                    )

                    if (expiryDate.isNotEmpty()) {
                        tvExpiryDate.text = expiryDate
                    }

                    return
                }
            }

            Toast.makeText(
                this,
                "Document not found",
                Toast.LENGTH_SHORT
            ).show()

            finish()

        } catch (e: Exception) {

            e.printStackTrace()

            Toast.makeText(
                this,
                "Unable to load document",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // ---------------------------------------------------------
    // CATEGORY SELECTION
    // ---------------------------------------------------------

    private fun setCategorySelection(category: String) {

        for (i in categories.indices) {

            if (categories[i].equals(
                    category,
                    ignoreCase = true
                )
            ) {

                spinnerCategory.setSelection(i)
                return
            }
        }
    }

    // ---------------------------------------------------------
    // FOLDER SELECTION
    // ---------------------------------------------------------

    private fun setFolderSelection(folderId: String) {

        val ids = spinnerFolder.tag as? ArrayList<String>
            ?: return

        for (i in ids.indices) {

            if (ids[i] == folderId) {

                spinnerFolder.setSelection(i)
                return
            }
        }
    }

    // ---------------------------------------------------------
    // DATE PICKERS
    // ---------------------------------------------------------

    private fun setupDatePickers() {

        tvIssueDate.setOnClickListener {
            showDatePicker(tvIssueDate)
        }

        tvExpiryDate.setOnClickListener {
            showDatePicker(tvExpiryDate)
        }
    }

    private fun showDatePicker(target: TextView) {

        val calendar = Calendar.getInstance()

        val currentText = target.text.toString()

        if (
            currentText.isNotEmpty() &&
            currentText != "Select issue date" &&
            currentText != "Select expiry date"
        ) {

            try {

                val date = dateFormat.parse(currentText)

                if (date != null) {
                    calendar.time = date
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->

                val selectedCalendar = Calendar.getInstance()

                selectedCalendar.set(
                    selectedYear,
                    selectedMonth,
                    selectedDay
                )

                target.text = dateFormat.format(
                    selectedCalendar.time
                )

            },
            year,
            month,
            day
        ).show()
    }

    // ---------------------------------------------------------
    // UPDATE BUTTON
    // ---------------------------------------------------------

    private fun setupUpdateButton() {

        btnUpdateDocument.setOnClickListener {
            updateDocument()
        }
    }

    // ---------------------------------------------------------
    // UPDATE DOCUMENT
    // ---------------------------------------------------------

    private fun updateDocument() {

        val name = etDocumentName.text.toString().trim()

        val documentNumber =
            etDocumentNumber.text.toString().trim()

        val category =
            spinnerCategory.selectedItem.toString()

        val issueDate =
            tvIssueDate.text.toString().trim()

        val expiryDate =
            tvExpiryDate.text.toString().trim()

        val notes =
            etNotes.text.toString().trim()

        // -----------------------------------------------------
        // VALIDATION
        // -----------------------------------------------------

        if (name.isEmpty()) {

            etDocumentName.error =
                "Enter document name"

            etDocumentName.requestFocus()
            return
        }

        if (documentNumber.isEmpty()) {

            etDocumentNumber.error =
                "Enter document number"

            etDocumentNumber.requestFocus()
            return
        }

        if (
            category.isEmpty() ||
            category == "Select Category"
        ) {

            Toast.makeText(
                this,
                "Please select a category",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (
            issueDate.isEmpty() ||
            issueDate == "Select issue date"
        ) {

            Toast.makeText(
                this,
                "Please select issue date",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (
            expiryDate.isEmpty() ||
            expiryDate == "Select expiry date"
        ) {

            Toast.makeText(
                this,
                "Please select expiry date",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // -----------------------------------------------------
        // DATE VALIDATION
        // -----------------------------------------------------

        try {

            val issue =
                dateFormat.parse(issueDate)

            val expiry =
                dateFormat.parse(expiryDate)

            if (
                issue != null &&
                expiry != null &&
                expiry.before(issue)
            ) {

                Toast.makeText(
                    this,
                    "Expiry date must be after issue date",
                    Toast.LENGTH_LONG
                ).show()

                return
            }

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Invalid date",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // -----------------------------------------------------
        // GET SELECTED FOLDER
        // -----------------------------------------------------

        val folderIds =
            spinnerFolder.tag as? ArrayList<String>

        val selectedFolderPosition =
            spinnerFolder.selectedItemPosition

        val folderId =
            if (
                folderIds != null &&
                selectedFolderPosition >= 0 &&
                selectedFolderPosition < folderIds.size
            ) {
                folderIds[selectedFolderPosition]
            } else {
                ""
            }

        // -----------------------------------------------------
        // UPDATE JSON
        // -----------------------------------------------------

        val preferences = getSharedPreferences(
            "DocumentStorage",
            Context.MODE_PRIVATE
        )

        val data = preferences.getString(
            "documents",
            "[]"
        ) ?: "[]"

        try {

            val documents = JSONArray(data)

            var updated = false

            for (i in 0 until documents.length()) {

                val document =
                    documents.getJSONObject(i)

                if (
                    document.optString("id") ==
                    documentId
                ) {

                    document.put(
                        "name",
                        name
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
                        "documentNumber",
                        documentNumber
                    )

                    document.put(
                        "issueDate",
                        issueDate
                    )

                    document.put(
                        "expiryDate",
                        expiryDate
                    )

                    document.put(
                        "folderId",
                        folderId
                    )

                    document.put(
                        "notes",
                        notes
                    )

                    updated = true
                    break
                }
            }

            if (!updated) {

                Toast.makeText(
                    this,
                    "Document not found",
                    Toast.LENGTH_SHORT
                ).show()

                return
            }

            preferences.edit()
                .putString(
                    "documents",
                    documents.toString()
                )
                .apply()

            Toast.makeText(
                this,
                "Document updated successfully",
                Toast.LENGTH_SHORT
            ).show()

            finish()

        } catch (e: Exception) {

            e.printStackTrace()

            Toast.makeText(
                this,
                "Failed to update document",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}