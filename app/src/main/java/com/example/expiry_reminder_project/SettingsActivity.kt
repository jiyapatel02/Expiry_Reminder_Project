package com.example.expiry_reminder_project

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var switchNotifications: Switch
    private lateinit var spReminderPeriod: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnBack = findViewById<Button>(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }

        switchNotifications = findViewById(R.id.switchNotifications)
        spReminderPeriod = findViewById(R.id.spReminderPeriod)

        setupReminderPeriod()

        loadSettings()

        switchNotifications.setOnCheckedChangeListener { _, isChecked ->

            getSharedPreferences(
                "ExpiryReminder",
                MODE_PRIVATE
            ).edit()
                .putBoolean("notifications", isChecked)
                .apply()
        }

        spReminderPeriod.setOnItemSelectedListener(
            object : android.widget.AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {

                    getSharedPreferences(
                        "ExpiryReminder",
                        MODE_PRIVATE
                    ).edit()
                        .putInt("reminderPeriod", position)
                        .apply()
                }

                override fun onNothingSelected(
                    parent: android.widget.AdapterView<*>?
                ) {
                }
            }
        )
    }

    private fun setupReminderPeriod() {

        val periods = arrayOf(
            "7 days before expiry",
            "15 days before expiry",
            "30 days before expiry"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            periods
        )

        spReminderPeriod.adapter = adapter
    }

    private fun loadSettings() {

        val preferences = getSharedPreferences(
            "ExpiryReminder",
            MODE_PRIVATE
        )

        val notifications =
            preferences.getBoolean("notifications", true)

        val reminderPeriod =
            preferences.getInt("reminderPeriod", 0)

        switchNotifications.isChecked = notifications
        spReminderPeriod.setSelection(reminderPeriod)
    }
}