package com.example.expiry_reminder_project

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import org.json.JSONArray

class EditFolderActivity : AppCompatActivity() {

    private lateinit var etFolderName: EditText
    private lateinit var etDescription: EditText

    private var folderId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_edit_folder
        )

        etFolderName =
            findViewById(R.id.etFolderName)

        etDescription =
            findViewById(R.id.etDescription)

        folderId =
            intent.getStringExtra(
                "folder_id"
            ).orEmpty()

        loadFolder()

        findViewById<TextView>(
            R.id.btnBack
        ).setOnClickListener {

            onBackPressedDispatcher
                .onBackPressed()
        }

        findViewById<MaterialCardView>(
            R.id.btnUpdateFolder
        ).setOnClickListener {

            updateFolder()
        }

        findViewById<MaterialCardView>(
            R.id.btnDeleteFolder
        ).setOnClickListener {

            showDeleteDialog()
        }
    }

    private fun loadFolder() {

        val preferences =
            getSharedPreferences(
                "ExpiryReminder",
                Context.MODE_PRIVATE
            )

        val data =
            preferences.getString(
                "folders",
                "[]"
            )

        try {

            val folders =
                JSONArray(data)

            for (i in 0 until folders.length()) {

                val folder =
                    folders.getJSONObject(i)

                if (
                    folder.optString("id")
                    == folderId
                ) {

                    etFolderName.setText(
                        folder.optString("name")
                    )

                    etDescription.setText(
                        folder.optString(
                            "description"
                        )
                    )

                    return
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateFolder() {

        val name =
            etFolderName.text
                .toString()
                .trim()

        val description =
            etDescription.text
                .toString()
                .trim()

        if (name.isEmpty()) {

            etFolderName.error =
                "Enter folder name"

            etFolderName.requestFocus()

            return
        }

        val preferences =
            getSharedPreferences(
                "ExpiryReminder",
                Context.MODE_PRIVATE
            )

        val data =
            preferences.getString(
                "folders",
                "[]"
            )

        try {

            val folders =
                JSONArray(data)

            var updated = false

            for (i in 0 until folders.length()) {

                val folder =
                    folders.getJSONObject(i)

                if (
                    folder.optString("id")
                    == folderId
                ) {

                    folder.put(
                        "name",
                        name
                    )

                    folder.put(
                        "description",
                        description
                    )

                    updated = true

                    break
                }
            }

            if (updated) {

                preferences.edit()
                    .putString(
                        "folders",
                        folders.toString()
                    )
                    .apply()

                Toast.makeText(
                    this,
                    "Folder updated successfully",
                    Toast.LENGTH_SHORT
                ).show()

                finish()

            } else {

                Toast.makeText(
                    this,
                    "Folder not found",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } catch (e: Exception) {

            e.printStackTrace()

            Toast.makeText(
                this,
                "Unable to update folder",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showDeleteDialog() {

        AlertDialog.Builder(this)

            .setTitle("Delete Folder")

            .setMessage(
                "Are you sure you want to delete this folder?"
            )

            .setNegativeButton(
                "Cancel",
                null
            )

            .setPositiveButton(
                "Delete"
            ) { _, _ ->

                deleteFolder()
            }

            .show()
    }

    private fun deleteFolder() {

        val preferences =
            getSharedPreferences(
                "ExpiryReminder",
                Context.MODE_PRIVATE
            )

        val data =
            preferences.getString(
                "folders",
                "[]"
            )

        try {

            val folders =
                JSONArray(data)

            val updatedFolders =
                JSONArray()

            for (i in 0 until folders.length()) {

                val folder =
                    folders.getJSONObject(i)

                if (
                    folder.optString("id")
                    != folderId
                ) {

                    updatedFolders.put(
                        folder
                    )
                }
            }

            preferences.edit()
                .putString(
                    "folders",
                    updatedFolders.toString()
                )
                .apply()

            removeFolderFromDocuments()

            Toast.makeText(
                this,
                "Folder deleted successfully",
                Toast.LENGTH_SHORT
            ).show()

            finish()

        } catch (e: Exception) {

            e.printStackTrace()

            Toast.makeText(
                this,
                "Unable to delete folder",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun removeFolderFromDocuments() {

        val preferences =
            getSharedPreferences(
                "DocumentStorage",
                Context.MODE_PRIVATE
            )

        val data =
            preferences.getString(
                "documents",
                "[]"
            )

        try {

            val documents =
                JSONArray(data)

            for (
            i in 0 until documents.length()
            ) {

                val document =
                    documents.getJSONObject(i)

                if (
                    document.optString(
                        "folderId"
                    ) == folderId
                ) {

                    document.put(
                        "folderId",
                        ""
                    )
                }
            }

            preferences.edit()
                .putString(
                    "documents",
                    documents.toString()
                )
                .apply()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}