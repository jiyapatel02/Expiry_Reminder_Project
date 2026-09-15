package com.example.expiry_reminder_project

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var etCurrentPassword: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_change_password)

        etCurrentPassword = findViewById(R.id.etCurrentPassword)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)

        setupBackButton()
        setupChangePassword()
    }

    private fun setupBackButton() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupChangePassword() {

        findViewById<MaterialCardView>(R.id.btnChangePassword)
            .setOnClickListener {

                val currentPassword =
                    etCurrentPassword.text.toString()

                val newPassword =
                    etNewPassword.text.toString()

                val confirmPassword =
                    etConfirmPassword.text.toString()

                if (currentPassword.isEmpty()) {
                    etCurrentPassword.error = "Enter current password"
                    etCurrentPassword.requestFocus()
                    return@setOnClickListener
                }

                if (newPassword.isEmpty()) {
                    etNewPassword.error = "Enter new password"
                    etNewPassword.requestFocus()
                    return@setOnClickListener
                }

                if (newPassword.length < 6) {
                    etNewPassword.error =
                        "Password must be at least 6 characters"
                    etNewPassword.requestFocus()
                    return@setOnClickListener
                }

                if (confirmPassword.isEmpty()) {
                    etConfirmPassword.error =
                        "Confirm your new password"
                    etConfirmPassword.requestFocus()
                    return@setOnClickListener
                }

                if (newPassword != confirmPassword) {
                    etConfirmPassword.error =
                        "Passwords do not match"
                    etConfirmPassword.requestFocus()
                    return@setOnClickListener
                }

                val preferences =
                    getSharedPreferences(
                        "ExpiryReminder",
                        Context.MODE_PRIVATE
                    )

                val savedPassword =
                    preferences.getString("password", "123456")

                if (currentPassword != savedPassword) {
                    etCurrentPassword.error =
                        "Current password is incorrect"
                    etCurrentPassword.requestFocus()
                    return@setOnClickListener
                }

                preferences.edit()
                    .putString("password", newPassword)
                    .apply()

                Toast.makeText(
                    this,
                    "Password changed successfully",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
    }
}