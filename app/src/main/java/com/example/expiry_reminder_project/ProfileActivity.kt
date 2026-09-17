package com.example.expiry_reminder_project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        setupBackButton()
        setupProfileActions()
        loadProfile()
    }

    private fun setupBackButton() {

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupProfileActions() {

        findViewById<MaterialCardView>(R.id.btnEditProfile)
            .setOnClickListener {

                startActivity(
                    Intent(
                        this,
                        EditProfileActivity::class.java
                    )
                )
            }

        findViewById<MaterialCardView>(R.id.cardChangePassword)
            .setOnClickListener {

                startActivity(
                    Intent(
                        this,
                        ChangePasswordActivity::class.java
                    )
                )
            }

        findViewById<MaterialCardView>(R.id.cardPrivacy)
            .setOnClickListener {
                Toast.makeText(
                    this,
                    "Privacy & Security settings",
                    Toast.LENGTH_SHORT
                ).show()
            }

        findViewById<MaterialCardView>(R.id.cardNotifications)
            .setOnClickListener {

                startActivity(
                    Intent(
                        this,
                        SettingsActivity::class.java
                    )
                )
            }

        findViewById<MaterialCardView>(R.id.cardSettings)
            .setOnClickListener {

                startActivity(
                    Intent(
                        this,
                        SettingsActivity::class.java
                    )
                )
            }

        findViewById<MaterialCardView>(R.id.btnLogout)
            .setOnClickListener {

                showLogoutConfirmation()
            }
    }


    private fun loadProfile() {

        val preferences = getSharedPreferences(
            "ExpiryReminder",
            Context.MODE_PRIVATE
        )

        val name = preferences.getString(
            "profile_name",
            "User"
        ) ?: "User"

        val email = preferences.getString(
            "email",
            "user@example.com"
        ) ?: "user@example.com"

        findViewById<android.widget.TextView>(
            R.id.tvName
        ).text = name

        findViewById<android.widget.TextView>(
            R.id.tvEmail
        ).text = email

        findViewById<android.widget.TextView>(
            R.id.tvAvatar
        ).text =
            name.firstOrNull()
                ?.toString()
                ?.uppercase()
                ?: "U"
    }


    private fun showLogoutConfirmation() {

        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage(
                "Are you sure you want to logout?"
            )
            .setNegativeButton(
                "Cancel",
                null
            )
            .setPositiveButton(
                "Logout"
            ) { _, _ ->

                logout()
            }
            .show()
    }

    private fun logout() {

        val preferences = getSharedPreferences(
            "ExpiryReminder",
            Context.MODE_PRIVATE
        )

        preferences.edit()
            .putBoolean(
                "isLoggedIn",
                false
            )
            .apply()

        val intent = Intent(
            this,
            LoginActivity::class.java
        )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)

        finish()
    }


    override fun onResume() {

        super.onResume()

        if (!isFinishing) {
            loadProfile()
        }
    }
}