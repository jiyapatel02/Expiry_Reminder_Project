package com.example.expiry_reminder_project

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class AddFolderActivity : AppCompatActivity() {

    private lateinit var etFolderName: EditText
    private lateinit var etFolderDescription: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_folder)

        etFolderName =
            findViewById(R.id.etFolderName)

        etFolderDescription =
            findViewById(R.id.etFolderDescription)

        findViewById<ImageButton>(
            R.id.btnBack
        ).setOnClickListener {

            onBackPressedDispatcher.onBackPressed()
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
            etFolderDescription.text
                .toString()
                .trim()

        if (name.isEmpty()) {

            etFolderName.error =
                "Folder name is required"

            etFolderName.requestFocus()

            return
        }

        val preferences =
            getSharedPreferences(
                "ExpiryReminder",
                Context.MODE_PRIVATE
            )

        val oldData =
            preferences.getString(
                "folders",
                "[]"
            ) ?: "[]"

        val folders =
            try {
                JSONArray(oldData)
            } catch (e: Exception) {
                JSONArray()
            }

        val folder =
            JSONObject()

        folder.put(
            "id",
            UUID.randomUUID().toString()
        )

        folder.put(
            "name",
            name
        )

        folder.put(
            "folderName",
            name
        )

        folder.put(
            "description",
            description
        )

        folder.put(
            "createdAt",
            System.currentTimeMillis()
        )

        folders.put(folder)

        preferences.edit()
            .putString(
                "folders",
                folders.toString()
            )
            .apply()

        Toast.makeText(
            this,
            "Folder created successfully",
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }
}