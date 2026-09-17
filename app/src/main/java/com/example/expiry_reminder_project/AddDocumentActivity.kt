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
import androidx.appcompat.app.AlertDialog
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

    private lateinit var tvSelectedFile: TextView
    private lateinit var tvReminderButton: TextView

    private var selectedFileUri: Uri? = null

    private var selectedFileName = ""

    private var selectedFileType = ""

    private var selectedFilePath = ""

    private var selectedReminderPeriod = ""

    private var selectedFolderId = ""


    private val dateFormat =
        SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        )

    private val categories =
        arrayOf(
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

    private val filePickerLauncher =
        registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->

            if (uri == null) {
                return@registerForActivityResult
            }
            selectedFileUri = uri

            selectedFileName =
                getFileName(uri)

            selectedFileType =
                contentResolver.getType(uri)
                    ?: "application/octet-stream"

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

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        setContentView(
            R.layout.activity_add_document
        )


        initializeViews()

        setupBackButton()

        setupCategorySpinner()

        loadFolders()

        setupDatePickers()

        setupFilePicker()

        setupReminderButton()

        setupSaveButton()

        selectedFolderId =
            intent.getStringExtra(
                "folder_id"
            ) ?: ""


        if (
            selectedFolderId.isNotEmpty()
        ) {

            spinnerFolder.post {

                selectFolderById(
                    selectedFolderId
                )
            }
        }
    }

    private fun initializeViews() {

        etDocumentName =
            findViewById(
                R.id.etDocumentName
            )


        etDocumentNumber =
            findViewById(
                R.id.etDocumentNumber
            )


        spinnerCategory =
            findViewById(
                R.id.spinnerCategory
            )


        spinnerFolder =
            findViewById(
                R.id.spinnerFolder
            )


        tvIssueDate =
            findViewById(
                R.id.tvIssueDate
            )


        tvExpiryDate =
            findViewById(
                R.id.tvExpiryDate
            )


        etNotes =
            findViewById(
                R.id.etNotes
            )


        tvSelectedFile =
            findViewById(
                R.id.tvSelectedFile
            )


        tvReminderButton =
            findViewById(
                R.id.tvReminderButton
            )
    }

    private fun setupBackButton() {

        findViewById<ImageButton>(
            R.id.btnBack
        ).setOnClickListener {

            onBackPressedDispatcher
                .onBackPressed()
        }
    }

    private fun setupCategorySpinner() {

        val adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                categories
            )


        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )


        spinnerCategory.adapter =
            adapter
    }

    private fun loadFolders() {

        val folderNames =
            mutableListOf<String>()

        val folderIds =
            mutableListOf<String>()


        // Default
        folderNames.add(
            "No Folder"
        )

        folderIds.add(
            ""
        )


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


            for (
            i in 0 until folders.length()
            ) {

                val folder =
                    folders.getJSONObject(i)


                val id =
                    folder.optString(
                        "id"
                    )


                val name =
                    folder.optString(
                        "name",
                        "Folder"
                    )


                folderIds.add(
                    id
                )


                folderNames.add(
                    name
                )
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }


        val adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                folderNames
            )


        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )


        spinnerFolder.adapter =
            adapter


        // Store IDs inside Spinner tag
        spinnerFolder.tag =
            folderIds
    }

    private fun selectFolderById(
        folderId: String
    ) {

        val folderIds =
            spinnerFolder.tag
                    as? List<*>
                ?: return


        val position =
            folderIds.indexOf(
                folderId
            )


        if (position >= 0) {

            spinnerFolder.setSelection(
                position
            )
        }
    }

    private fun setupDatePickers() {

        tvIssueDate.setOnClickListener {

            showDatePicker(
                tvIssueDate
            )
        }


        tvExpiryDate.setOnClickListener {

            showDatePicker(
                tvExpiryDate
            )
        }
    }
    private fun showDatePicker(
        target: TextView
    ) {

        val calendar =
            Calendar.getInstance()


        val dialog =
            DatePickerDialog(
                this,

                { _, year, month, day ->

                    calendar.set(
                        year,
                        month,
                        day
                    )


                    target.text =
                        dateFormat.format(
                            calendar.time
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
        findViewById<TextView>(
            R.id.btnUseSample
        ).setOnClickListener {

            selectedFileUri = null

            selectedFileName = ""

            selectedFileType = ""

            selectedFilePath = ""


            tvSelectedFile.text =
                "Sample scan selected"

            Toast.makeText(
                this,
                "Sample scan selected",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupReminderButton() {

        findViewById<MaterialCardView>(
            R.id.cardReminder
        ).setOnClickListener {

            showReminderDialog()
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

        var selectedIndex =
            options.indexOf(
                selectedReminderPeriod
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

                selectedReminderPeriod =
                    options[which]


                tvReminderButton.text =
                    "🔔  Reminder: $selectedReminderPeriod"


                Toast.makeText(
                    this,
                    "Reminder set: $selectedReminderPeriod",
                    Toast.LENGTH_SHORT
                ).show()


                dialog.dismiss()
            }

            .setNegativeButton(
                "Cancel",
                null
            )

            .show()
    }

    private fun setupSaveButton() {

        findViewById<MaterialCardView>(
            R.id.btnSave
        ).setOnClickListener {

            saveDocument()
        }
    }

    private fun saveDocument() {

        val name =
            etDocumentName.text
                .toString()
                .trim()


        val number =
            etDocumentNumber.text
                .toString()
                .trim()


        val category =
            spinnerCategory.selectedItem
                ?.toString()
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

        if (name.isEmpty()) {

            etDocumentName.error =
                "Enter document name"

            etDocumentName.requestFocus()

            return
        }

        if (
            category ==
            "Select Category"
        ) {

            Toast.makeText(
                this,
                "Please select a category",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (expiryDate.isEmpty()) {

            Toast.makeText(
                this,
                "Please select expiry date",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        var folderId = ""

        val folderIds =
            spinnerFolder.tag
                    as? List<*>


        if (
            folderIds != null &&
            spinnerFolder.selectedItemPosition <
            folderIds.size
        ) {

            folderId =
                folderIds[
                    spinnerFolder.selectedItemPosition
                ]?.toString()
                    ?: ""
        }

        val documentId =
            UUID.randomUUID().toString()

        if (
            selectedFileUri != null
        ) {

            selectedFilePath =
                copyFileToAppStorage(
                    selectedFileUri!!,
                    documentId
                )
        }

        val document =
            JSONObject()

        document.put(
            "id",
            documentId
        )

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
            number
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
            selectedFilePath
        )


        document.put(
            "fileUri",
            selectedFileUri
                ?.toString()
                ?: ""
        )

        document.put(
            "reminderPeriod",
            selectedReminderPeriod
        )


        document.put(
            "reminderEnabled",
            selectedReminderPeriod.isNotEmpty()
        )
        val preferences =
            getSharedPreferences(
                "DocumentStorage",
                Context.MODE_PRIVATE
            )


        val oldData =
            preferences.getString(
                "documents",
                "[]"
            ) ?: "[]"


        val documents =
            try {

                JSONArray(
                    oldData
                )

            } catch (e: Exception) {

                JSONArray()
            }


        documents.put(
            document
        )


        preferences.edit()
            .putString(
                "documents",
                documents.toString()
            )
            .apply()

        if (
            selectedReminderPeriod.isNotEmpty()
        ) {

            ReminderScheduler
                .scheduleDailyReminder(
                    this
                )
        }

        val message =
            if (
                selectedReminderPeriod
                    .isNotEmpty()
            ) {

                "Document saved with $selectedReminderPeriod reminder"

            } else {

                "Document saved successfully"
            }

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()


        finish()
    }

    private fun copyFileToAppStorage(
        uri: Uri,
        documentId: String
    ): String {

        return try {

            val documentsDirectory =
                File(
                    filesDir,
                    "documents"
                )


            if (
                !documentsDirectory.exists()
            ) {

                documentsDirectory.mkdirs()
            }


            val originalName =
                getFileName(
                    uri
                )


            val safeName =
                originalName.replace(
                    Regex(
                        "[^a-zA-Z0-9._-]"
                    ),
                    "_"
                )


            val file =
                File(
                    documentsDirectory,
                    "${documentId}_$safeName"
                )


            val inputStream =
                contentResolver
                    .openInputStream(uri)


            if (
                inputStream == null
            ) {

                return ""
            }


            val outputStream =
                FileOutputStream(
                    file
                )


            inputStream.use { input ->

                outputStream.use { output ->

                    input.copyTo(
                        output
                    )
                }
            }


            file.absolutePath

        } catch (e: Exception) {

            e.printStackTrace()


            Toast.makeText(
                this,
                "Could not save selected file",
                Toast.LENGTH_SHORT
            ).show()


            ""
        }
    }

    private fun getFileName(
        uri: Uri
    ): String {

        var result: String? = null


        if (
            uri.scheme ==
            "content"
        ) {

            val cursor =
                contentResolver.query(
                    uri,
                    arrayOf(
                        OpenableColumns.DISPLAY_NAME
                    ),
                    null,
                    null,
                    null
                )


            cursor?.use {

                if (
                    it.moveToFirst()
                ) {

                    val index =
                        it.getColumnIndex(
                            OpenableColumns.DISPLAY_NAME
                        )


                    if (
                        index >= 0
                    ) {

                        result =
                            it.getString(
                                index
                            )
                    }
                }
            }
        }

        if (
            result == null
        ) {

            result =
                uri.path
                    ?.substringAfterLast(
                        '/'
                    )
        }

        return result
            ?: "document"
    }
}