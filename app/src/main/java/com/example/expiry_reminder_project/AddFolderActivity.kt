package com.example.expiry_reminder_project

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.expiry_reminder_project.model.Folder
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class AddFolderActivity : AppCompatActivity() {

    private lateinit var etFolderName: EditText
    private lateinit var etDescription: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_folder)

        etFolderName = findViewById(R.id.etFolderName)
        etDescription = findViewById(R.id.etDescription)

        // Back button
        findViewById<TextView>(R.id.btnBack).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Create Folder
        findViewById<TextView>(R.id.btnSaveFolder).setOnClickListener {
            saveFolder()
        }
    }

    private fun saveFolder() {

        val name = etFolderName.text.toString().trim()
        val description = etDescription.text.toString().trim()

        if (name.isEmpty()) {

            etFolderName.error = "Enter folder name"
            etFolderName.requestFocus()

            return
        }

        val folder = Folder(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description
        )

        saveFolderToPreferences(folder)

        Toast.makeText(
            this,
            "Folder created successfully",
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }

    private fun saveFolderToPreferences(folder: Folder) {

        val preferences = getSharedPreferences(
            "ExpiryReminder",
            Context.MODE_PRIVATE
        )

        val oldData = preferences.getString(
            "folders",
            "[]"
        )

        val foldersArray = JSONArray(oldData)

        val folderObject = JSONObject()

        folderObject.put("id", folder.id)
        folderObject.put("name", folder.name)
        folderObject.put("description", folder.description)

        foldersArray.put(folderObject)

        preferences.edit()
            .putString(
                "folders",
                foldersArray.toString()
            )
            .apply()
    }
}