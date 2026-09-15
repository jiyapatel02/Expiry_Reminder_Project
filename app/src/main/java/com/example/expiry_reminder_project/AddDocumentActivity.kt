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
import com.example.expiry_reminder_project.model.Document
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

    private var issueDateMillis: Long? = null
    private var expiryDateMillis: Long? = null

    private val displayFormat =
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    private val folderIds = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_document)

        initializeViews()
        setupCategorySpinner()
        setupFolderSpinner()
        setupClicks()
    }

    private fun initializeViews() {

        etDocumentName = findViewById(R.id.etDocumentName)
        etDocumentNumber = findViewById(R.id.etDocumentNumber)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        spinnerFolder = findViewById(R.id.spinnerFolder)

        tvIssueDate = findViewById(R.id.tvIssueDate)
        tvExpiryDate = findViewById(R.id.tvExpiryDate)

        etNotes = findViewById(R.id.etNotes)
    }

    private fun setupClicks() {

        findViewById<TextView>(R.id.btnBack).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        tvIssueDate.setOnClickListener {
            showDatePicker(true)
        }

        tvExpiryDate.setOnClickListener {
            showDatePicker(false)
        }

        findViewById<TextView>(R.id.btnSave).setOnClickListener {
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

    private fun setupFolderSpinner() {

        val folderNames = mutableListOf<String>()

        folderNames.add("No Folder")

        folderIds.clear()
        folderIds.add("")

        val preferences =
            getSharedPreferences(
                "ExpiryReminder",
                Context.MODE_PRIVATE
            )

        val data =
            preferences.getString("folders", "[]")

        try {

            val foldersArray = JSONArray(data)

            for (i in 0 until foldersArray.length()) {

                val folder =
                    foldersArray.getJSONObject(i)

                val id =
                    folder.optString("id")

                val name =
                    folder.optString("name")

                if (id.isNotBlank() && name.isNotBlank()) {

                    folderIds.add(id)
                    folderNames.add(name)
                }
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

    private fun showDatePicker(isIssueDate: Boolean) {

        val calendar = Calendar.getInstance()

        val selectedDate =
            if (isIssueDate) {
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

                    if (
                        expiryDateMillis != null &&
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
                        displayFormat.format(
                            selectedCalendar.time
                        )

                    tvIssueDate.setTextColor(
                        getColor(R.color.text_primary)
                    )

                } else {

                    if (
                        issueDateMillis != null &&
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
                        displayFormat.format(
                            selectedCalendar.time
                        )

                    tvExpiryDate.setTextColor(
                        getColor(R.color.text_primary)
                    )
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        dialog.show()
    }

    private fun saveDocument() {

        val name =
            etDocumentName.text.toString().trim()

        val documentNumber =
            etDocumentNumber.text.toString().trim()

        val category =
            spinnerCategory.selectedItem.toString()

        val notes =
            etNotes.text.toString().trim()

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

        // Validate document name
        if (name.isEmpty()) {

            etDocumentName.error =
                "Enter document name"

            etDocumentName.requestFocus()

            return
        }

        // Validate category
        if (spinnerCategory.selectedItemPosition == 0) {

            Toast.makeText(
                this,
                "Please select a category",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // Validate issue date
        if (issueDateMillis == null) {

            Toast.makeText(
                this,
                "Please select issue date",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // Validate expiry date
        if (expiryDateMillis == null) {

            Toast.makeText(
                this,
                "Please select expiry date",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val document = Document(
            id = UUID.randomUUID().toString(),
            name = name,
            type = category,
            documentNumber = documentNumber,
            issueDate = issueDateMillis!!.toString(),
            expiryDate = expiryDateMillis!!.toString(),
            folderId = folderId,
            notes = notes
        )

        saveDocumentToPreferences(document)

        Toast.makeText(
            this,
            "Document saved successfully",
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }

    private fun saveDocumentToPreferences(
        document: Document
    ) {

        val preferences =
            getSharedPreferences(
                "DocumentStorage",
                Context.MODE_PRIVATE
            )

        val oldData =
            preferences.getString(
                "documents",
                "[]"
            )

        val documentsArray =
            try {
                JSONArray(oldData)
            } catch (e: Exception) {
                JSONArray()
            }

        val documentObject =
            JSONObject()

        documentObject.put(
            "id",
            document.id
        )

        documentObject.put(
            "name",
            document.name
        )

        // Save both names for compatibility
        documentObject.put(
            "type",
            document.type
        )

        documentObject.put(
            "category",
            document.type
        )

        documentObject.put(
            "documentNumber",
            document.documentNumber
        )

        documentObject.put(
            "issueDate",
            document.issueDate
        )

        documentObject.put(
            "expiryDate",
            document.expiryDate
        )

        documentObject.put(
            "folderId",
            document.folderId
        )

        documentObject.put(
            "notes",
            document.notes
        )

        documentsArray.put(documentObject)

        preferences.edit()
            .putString(
                "documents",
                documentsArray.toString()
            )
            .apply()
    }

    override fun onResume() {
        super.onResume()

        // Reload folders if a folder was created
        // before returning to this screen.
        if (::spinnerFolder.isInitialized) {
            setupFolderSpinner()
        }
    }
}