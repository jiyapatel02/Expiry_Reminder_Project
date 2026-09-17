package com.example.expiry_reminder_project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.card.MaterialCardView

class SettingsActivity : AppCompatActivity() {

    private lateinit var switchNotifications: MaterialSwitch
    private lateinit var spReminderPeriod: Spinner

    private val preferencesName = "ExpiryReminder"
    private val notificationsKey = "notifications"
    private val reminderPeriodKey = "reminder_period"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        switchNotifications = findViewById(R.id.switchNotifications)
        spReminderPeriod = findViewById(R.id.spReminderPeriod)

        setupBackButton()
        loadSettings()
        setupNotificationListener()
        setupReminderListener()
        setupTheme()
        setupHelp()
        setupAbout()
        setupBottomNavigation()
    }

    private fun setupBackButton() {
        findViewById<android.widget.ImageButton>(R.id.btnBack).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun loadSettings() {

        val preferences = getSharedPreferences(
            preferencesName,
            Context.MODE_PRIVATE
        )

        val notificationsEnabled =
            preferences.getBoolean(notificationsKey, true)

        val reminderPeriod =
            preferences.getInt(reminderPeriodKey, 7)

        switchNotifications.isChecked = notificationsEnabled

        val spinnerPosition = when (reminderPeriod) {
            14 -> 1
            30 -> 2
            else -> 0
        }

        spReminderPeriod.setSelection(spinnerPosition)
    }

    private fun setupNotificationListener() {

        switchNotifications.setOnCheckedChangeListener { _, isChecked ->

            getSharedPreferences(
                preferencesName,
                Context.MODE_PRIVATE
            )
                .edit()
                .putBoolean(notificationsKey, isChecked)
                .apply()

            if (isChecked) {
                NotificationHelper.createChannel(this)

                Toast.makeText(
                    this,
                    "Expiry reminders enabled",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                Toast.makeText(
                    this,
                    "Expiry reminders disabled",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupReminderListener() {

        spReminderPeriod.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {

                    val days = when (position) {
                        1 -> 14
                        2 -> 30
                        else -> 7
                    }

                    getSharedPreferences(
                        preferencesName,
                        Context.MODE_PRIVATE
                    )
                        .edit()
                        .putInt(reminderPeriodKey, days)
                        .apply()
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                }
            }
    }

    private fun setupTheme() {

        findViewById<MaterialCardView>(R.id.cardTheme)
            .setOnClickListener {

                Toast.makeText(
                    this,
                    "Light theme is currently selected",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun setupHelp() {

        findViewById<MaterialCardView>(R.id.cardHelp)
            .setOnClickListener {

                startActivity(
                    Intent(
                        this,
                        HelpSupportActivity::class.java
                    )
                )
            }
    }

    private fun setupAbout() {

        findViewById<MaterialCardView>(R.id.cardAbout)
            .setOnClickListener {

                startActivity(
                    Intent(
                        this,
                        AboutActivity::class.java
                    )
                )
            }
    }

    private fun setupBottomNavigation() {

        findViewById<android.widget.TextView>(R.id.navHome)
            .setOnClickListener {

                startActivity(
                    Intent(this, MainActivity::class.java)
                )

                finish()
            }

        findViewById<android.widget.TextView>(R.id.navDocuments)
            .setOnClickListener {

                startActivity(
                    Intent(this, DocumentsActivity::class.java)
                )

                finish()
            }

        findViewById<android.widget.TextView>(R.id.navRenewal)
            .setOnClickListener {

                startActivity(
                    Intent(this, RenewalActivity::class.java)
                )

                finish()
            }

        findViewById<android.widget.TextView>(R.id.navSettings)
            .setOnClickListener {

            }
    }
}