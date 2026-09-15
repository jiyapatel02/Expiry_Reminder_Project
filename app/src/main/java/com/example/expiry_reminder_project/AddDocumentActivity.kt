package com.example.expiry_reminder_project

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
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
import java.util.UUID

class AddDocumentActivity : AppCompatActivity() {

    private lateinit var etDocumentName: EditText
    private lateinit var etDocumentNumber: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerFolder: Spinner
    private lateinit var tvIssueDate: TextView
    private lateinit var tvExpiryDate: TextView
    private lateinit var etNotes: EditText

    private var issueDateMillis: Long = 0L
    private var expiryDateMillis: Long = 0L

    private val displayFormat =
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    private val preferencesName = "DocumentStorage"
    private val documentsKey = "documents"

    private val categories = arrayOf(
        "Government ID",
        "Identity Document",
        "Education",
        "Vehicle",
        "Insurance",
        "Financial",
        "Other"
    )

    private val folderIds = mutableListOf<String>()
    private val folderNames = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_document)

        initializeViews()
        setupCategorySpinner()
        loadFolders()
        setupDatePickers()
        setupClicks()
    }

    // ---------------------------------------------------------
    // INITIALIZE VIEWS
    // ---------------------------------------------------------

    private fun initializeViews() {

        etDocumentName =
            findViewById(R.id.etDocumentName)

        etDocumentNumber =
            findViewById(R.id.etDocumentNumber)

        spinnerCategory =
            findViewById(R.id.spinnerCategory)

        spinnerFolder =
            findViewById(R.id.spinnerFolder)

        tvIssueDate =
            findViewById(R.id.tvIssueDate)

        tvExpiryDate =
            findViewById(R.id.tvExpiryDate)

        etNotes =
            findViewById(R.id.etNotes)
    }

    // ---------------------------------------------------------
    // CATEGORY
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

        folderIds.clear()
        folderNames.clear()

        folderIds.add("")
        folderNames.add("No Folder")

        val preferences =
            getSharedPreferences(
                "ExpiryReminder",
                Context.MODE_PRIVATE
            )

        val data =
            preferences.getString(
                "folders",
                "[]"
            ) ?: "[]"

        try {

            val folders =
                JSONArray(data)

            for (i in 0 until folders.length()) {

                val folder =
                    folders.getJSONObject(i)

                folderIds.add(
                    folder.optString("id")
                )

                folderNames.add(
                    folder.optString(
                        "name",
                        "Folder"
                    )
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
    }

    // ---------------------------------------------------------
    // DATE PICKERS
    // ---------------------------------------------------------

    private fun setupDatePickers() {

        tvIssueDate.setOnClickListener {

            showDatePicker(true)
        }

        tvExpiryDate.setOnClickListener {

            showDatePicker(false)
        }
    }

    private fun showDatePicker(
        isIssueDate: Boolean
    ) {

        val calendar = Calendar.getInstance()

        val dialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->

                val selectedCalendar =
                    Calendar.getInstance()

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

                val millis =
                    selectedCalendar.timeInMillis

                if (isIssueDate) {

                    issueDateMillis = millis

                    tvIssueDate.text =
                        displayFormat.format(
                            selectedCalendar.time
                        )

                } else {

                    expiryDateMillis = millis

                    tvExpiryDate.text =
                        displayFormat.format(
                            selectedCalendar.time
                        )
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        dialog.show()
    }

    // ---------------------------------------------------------
    // BUTTONS
    // ---------------------------------------------------------

    private fun setupClicks() {

        findViewById<TextView>(
            R.id.btnBack
        ).setOnClickListener {

            onBackPressedDispatcher.onBackPressed()
        }

        findViewById<MaterialCardView>(
            R.id.btnSaveDocument
        ).setOnClickListener {

            saveDocument()
        }
    }

    // ---------------------------------------------------------
    // SAVE DOCUMENT
    // ---------------------------------------------------------

    private fun saveDocument() {

        val name =
            etDocumentName.text
                .toString()
                .trim()

        val documentNumber =
            etDocumentNumber.text
                .toString()
                .trim()

        val notes =
            etNotes.text
                .toString()
                .trim()

        // Validate name
        if (name.isEmpty()) {

            etDocumentName.error =
                "Enter document name"

            etDocumentName.requestFocus()

            return
        }

        // Validate issue date
        if (issueDateMillis == 0L) {

            Toast.makeText(
                this,
                "Please select issue date",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // Validate expiry date
        if (expiryDateMillis == 0L) {

            Toast.makeText(
                this,
                "Please select expiry date",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // Expiry must be after issue
        if (expiryDateMillis <= issueDateMillis) {

            Toast.makeText(
                this,
                "Expiry date must be after issue date",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val category =
            spinnerCategory
                .selectedItem
                .toString()

        val folderPosition =
            spinnerFolder.selectedItemPosition

        val folderId =
            if (
                folderPosition >= 0 &&
                folderPosition < folderIds.size
            ) {
                folderIds[folderPosition]
            } else {
                ""
            }

        val document =
            JSONObject()

        document.put(
            "id",
            UUID.randomUUID().toString()
        )

        document.put(
            "name",
            name
        )

        document.put(
            "type",
            category
        )

        // Keep category too for compatibility
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
            issueDateMillis.toString()
        )

        document.put(
            "expiryDate",
            expiryDateMillis.toString()
        )

        document.put(
            "folderId",
            folderId
        )

        document.put(
            "notes",
            notes
        )

        val preferences =
            getSharedPreferences(
                preferencesName,
                Context.MODE_PRIVATE
            )

        val oldData =
            preferences.getString(
                documentsKey,
                "[]"
            ) ?: "[]"

        val documents = try {

            JSONArray(oldData)

        } catch (e: Exception) {

            JSONArray()
        }

        documents.put(document)

        // Save document
        preferences.edit()
            .putString(
                documentsKey,
                documents.toString()
            )
            .apply()

        // Update reminder schedule
        ReminderScheduler.schedule(this)

        Toast.makeText(
            this,
            "Document added successfully",
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }
}