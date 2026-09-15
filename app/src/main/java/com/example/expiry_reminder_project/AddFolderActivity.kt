package com.example.expiry_reminder_project

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class AddFolderActivity : AppCompatActivity() {

    private lateinit var etFolderName: EditText
    private lateinit var etDescription: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_add_folder
        )

        etFolderName =
            findViewById(R.id.etFolderName)

        etDescription =
            findViewById(R.id.etDescription)

        findViewById<TextView>(
            R.id.btnBack
        ).setOnClickListener {

            onBackPressedDispatcher
                .onBackPressed()
        }

        findViewById<MaterialCardView>(
            R.id.btnSaveFolder
        ).setOnClickListener {

            saveFolder()
        }
    }

    private fun saveFolder() {

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

        val folderId =
            UUID.randomUUID().toString()

        val preferences =
            getSharedPreferences(
                "ExpiryReminder",
                Context.MODE_PRIVATE
            )

        val oldData =
            preferences.getString(
                "folders",
                "[]"
            )

        try {

            val foldersArray =
                JSONArray(oldData)

            val folderObject =
                JSONObject()

            folderObject.put(
                "id",
                folderId
            )

            folderObject.put(
                "name",
                name
            )

            folderObject.put(
                "description",
                description
            )

            foldersArray.put(
                folderObject
            )

            preferences.edit()
                .putString(
                    "folders",
                    foldersArray.toString()
                )
                .apply()

            Toast.makeText(
                this,
                "Folder created successfully",
                Toast.LENGTH_SHORT
            ).show()

            finish()

        } catch (e: Exception) {

            e.printStackTrace()

            Toast.makeText(
                this,
                "Unable to create folder",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}