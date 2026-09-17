package com.example.expiry_reminder_project

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
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
    private lateinit var btnSave: MaterialCardView
    private lateinit var tvSelectedFile: TextView

    // Selected file information
    private var selectedFileUri: Uri? = null
    private var selectedFileName: String = ""
    private var selectedFileType: String = ""

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

    private val dateFormat =
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())


    // =========================================================
    // FILE PICKER
    // =========================================================

    private val filePickerLauncher =
        registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->

            if (uri == null) {
                return@registerForActivityResult
            }

            selectedFileUri = uri

            selectedFileName = getFileName(uri)

            selectedFileType =
                contentResolver.getType(uri)
                    ?: "application/octet-stream"

            // Keep permission if the selected provider supports it
            try {

                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

            } catch (e: Exception) {

                e.printStackTrace()
            }

            tvSelectedFile.text =
                "Selected: $selectedFileName"

            Toast.makeText(
                this,
                "File selected successfully",
                Toast.LENGTH_SHORT
            ).show()
        }


    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_document)

        initializeViews()
        setupBackButton()
        setupCategorySpinner()
        setupFolderSpinner()
        setupDatePickers()
        setupFilePicker()
        setupSaveButton()
    }


    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

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

        btnSave =
            findViewById(R.id.btnSave)

        tvSelectedFile =
            findViewById(R.id.tvSelectedFile)
    }


    // =========================================================
    // BACK BUTTON
    // =========================================================

    private fun setupBackButton() {

        findViewById<ImageButton>(R.id.btnBack)
            .setOnClickListener {

                onBackPressedDispatcher.onBackPressed()
            }
    }


    // =========================================================
    // CATEGORY SPINNER
    // =========================================================

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


    // =========================================================
    // FOLDER SPINNER
    // =========================================================

    private fun setupFolderSpinner() {

        val folderNames = ArrayList<String>()
        val folderIds = ArrayList<String>()

        folderNames.add("No Folder")
        folderIds.add("")

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

                val id =
                    folder.optString("id")

                val name =
                    folder.optString(
                        "name",
                        "Unnamed Folder"
                    )

                folderIds.add(id)
                folderNames.add(name)
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

        // Keep folder IDs separately from displayed names
        spinnerFolder.tag = folderIds
    }


    // =========================================================
    // DATE PICKERS
    // =========================================================

    private fun setupDatePickers() {

        val calendar =
            Calendar.getInstance()


        // Issue Date
        tvIssueDate.setOnClickListener {

            val picker =
                DatePickerDialog(
                    this,
                    { _, year, month, dayOfMonth ->

                        calendar.set(
                            year,
                            month,
                            dayOfMonth
                        )

                        tvIssueDate.text =
                            dateFormat.format(
                                calendar.time
                            )
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )

            picker.show()
        }


        // Expiry Date
        tvExpiryDate.setOnClickListener {

            val expiryCalendar =
                Calendar.getInstance()

            val picker =
                DatePickerDialog(
                    this,
                    { _, year, month, dayOfMonth ->

                        expiryCalendar.set(
                            year,
                            month,
                            dayOfMonth
                        )

                        tvExpiryDate.text =
                            dateFormat.format(
                                expiryCalendar.time
                            )
                    },
                    expiryCalendar.get(Calendar.YEAR),
                    expiryCalendar.get(Calendar.MONTH),
                    expiryCalendar.get(Calendar.DAY_OF_MONTH)
                )

            picker.show()
        }
    }


    // =========================================================
    // FILE PICKER
    // =========================================================

    private fun setupFilePicker() {

        findViewById<MaterialCardView>(
            R.id.btnBrowserFile
        ).setOnClickListener {

            filePickerLauncher.launch(
                arrayOf(
                    "image/*",
                    "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                )
            )
        }


        // Sample scan
        findViewById<TextView>(
            R.id.btnUseSample
        ).setOnClickListener {

            selectedFileUri = null
            selectedFileName = ""
            selectedFileType = ""

            tvSelectedFile.text =
                "Sample scan selected"

            Toast.makeText(
                this,
                "Sample scan selected",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    // =========================================================
    // SAVE BUTTON
    // =========================================================

    private fun setupSaveButton() {

        btnSave.setOnClickListener {

            saveDocument()
        }
    }


    // =========================================================
    // SAVE DOCUMENT
    // =========================================================

    private fun saveDocument() {

        val name =
            etDocumentName.text
                .toString()
                .trim()

        val documentNumber =
            etDocumentNumber.text
                .toString()
                .trim()

        val category =
            spinnerCategory.selectedItem
                ?.toString()
                ?.trim()
                ?: ""

        val issueDate =
            tvIssueDate.text
                .toString()
                .trim()

        val expiryDate =
            tvExpiryDate.text
                .toString()
                .trim()

        val notes =
            etNotes.text
                .toString()
                .trim()


        // =====================================================
        // VALIDATION
        // =====================================================

        if (name.isEmpty()) {

            etDocumentName.error =
                "Enter document name"

            etDocumentName.requestFocus()

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
            expiryDate.isEmpty() ||
            expiryDate == "Select Expiry Date"
        ) {

            Toast.makeText(
                this,
                "Please select expiry date",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        // =====================================================
        // DATE VALIDATION
        // =====================================================

        if (
            issueDate.isNotEmpty() &&
            issueDate != "Select Issue Date"
        ) {

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
                        "Expiry date cannot be before issue date",
                        Toast.LENGTH_LONG
                    ).show()

                    return
                }

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }


        // =====================================================
        // GET FOLDER ID
        // =====================================================

        val folderIds =
            spinnerFolder.tag as? ArrayList<String>

        val selectedPosition =
            spinnerFolder.selectedItemPosition

        val folderId =
            if (
                folderIds != null &&
                selectedPosition >= 0 &&
                selectedPosition < folderIds.size
            ) {

                folderIds[selectedPosition]

            } else {

                ""
            }


        // =====================================================
        // COPY ACTUAL FILE INTO APP STORAGE
        // =====================================================

        var localFilePath = ""

        if (selectedFileUri != null) {

            localFilePath =
                copyFileToAppStorage(
                    selectedFileUri!!
                )

            if (localFilePath.isEmpty()) {

                Toast.makeText(
                    this,
                    "Unable to save the selected file",
                    Toast.LENGTH_LONG
                ).show()

                return
            }
        }


        // =====================================================
        // READ EXISTING DOCUMENTS
        // =====================================================

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

        val documents =
            try {

                JSONArray(data)

            } catch (e: Exception) {

                JSONArray()
            }


        // =====================================================
        // CREATE DOCUMENT JSON
        // =====================================================

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

        // Compatibility with older code
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


        // =====================================================
        // FILE INFORMATION
        // =====================================================

        document.put(
            "fileName",
            selectedFileName
        )

        document.put(
            "fileMimeType",
            selectedFileType
        )

        document.put(
            "filePath",
            localFilePath
        )


        // Original URI kept for compatibility
        document.put(
            "fileUri",
            selectedFileUri?.toString() ?: ""
        )


        // =====================================================
        // ADD DOCUMENT
        // =====================================================

        documents.put(document)


        // =====================================================
        // SAVE DOCUMENT JSON
        // =====================================================

        preferences.edit()
            .putString(
                "documents",
                documents.toString()
            )
            .apply()


        // =====================================================
        // SUCCESS
        // =====================================================

        Toast.makeText(
            this,
            "Document saved successfully",
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }


    // =========================================================
    // COPY SELECTED FILE TO APP INTERNAL STORAGE
    // =========================================================

    private fun copyFileToAppStorage(
        uri: Uri
    ): String {

        return try {

            val originalName =
                getFileName(uri)

            val safeName =
                originalName
                    .replace(
                        Regex("[^a-zA-Z0-9._-]"),
                        "_"
                    )

            val uniqueName =
                "${System.currentTimeMillis()}_$safeName"


            // App internal folder
            val documentsFolder =
                File(
                    filesDir,
                    "documents"
                )

            if (!documentsFolder.exists()) {

                documentsFolder.mkdirs()
            }


            val destinationFile =
                File(
                    documentsFolder,
                    uniqueName
                )


            val inputStream =
                contentResolver
                    .openInputStream(uri)

            if (inputStream == null) {

                return ""
            }


            inputStream.use { input ->

                FileOutputStream(
                    destinationFile
                ).use { output ->

                    val buffer =
                        ByteArray(8192)

                    var bytesRead: Int

                    while (
                        input.read(buffer)
                            .also {
                                bytesRead = it
                            } != -1
                    ) {

                        output.write(
                            buffer,
                            0,
                            bytesRead
                        )
                    }

                    output.flush()
                }
            }


            destinationFile.absolutePath

        } catch (e: Exception) {

            e.printStackTrace()

            ""
        }
    }


    // =========================================================
    // GET FILE NAME
    // =========================================================

    private fun getFileName(
        uri: Uri
    ): String {

        var fileName =
            "document"

        try {

            contentResolver.query(
                uri,
                arrayOf(
                    OpenableColumns.DISPLAY_NAME
                ),
                null,
                null,
                null
            )?.use { cursor ->

                val nameIndex =
                    cursor.getColumnIndex(
                        OpenableColumns.DISPLAY_NAME
                    )

                if (
                    nameIndex >= 0 &&
                    cursor.moveToFirst()
                ) {

                    fileName =
                        cursor.getString(
                            nameIndex
                        )
                }
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }

        return fileName
    }
}