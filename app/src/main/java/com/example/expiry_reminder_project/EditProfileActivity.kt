package com.example.expiry_reminder_project

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView

class EditProfileActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_profile)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)

        loadProfile()
        setupBackButton()
        setupSaveButton()
    }

    private fun loadProfile() {
        val preferences =
            getSharedPreferences("ExpiryReminder", Context.MODE_PRIVATE)

        etName.setText(
            preferences.getString("profile_name", "")
        )

        etEmail.setText(
            preferences.getString("email", "")
        )
    }

    private fun setupBackButton() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupSaveButton() {

        findViewById<MaterialCardView>(R.id.btnSave).setOnClickListener {

            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()

            if (name.isEmpty()) {
                etName.error = "Enter your name"
                etName.requestFocus()
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                etEmail.error = "Enter your email"
                etEmail.requestFocus()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Enter a valid email"
                etEmail.requestFocus()
                return@setOnClickListener
            }

            val preferences =
                getSharedPreferences("ExpiryReminder", Context.MODE_PRIVATE)

            preferences.edit()
                .putString("profile_name", name)
                .putString("email", email)
                .apply()

            Toast.makeText(
                this,
                "Profile updated successfully",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }
    }
}