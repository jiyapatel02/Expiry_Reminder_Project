package com.example.expiry_reminder_project

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var switchNotifications: Switch
    private lateinit var spReminderPeriod: Spinner

    private val preferencesName = "ExpiryReminder"

    private var isLoadingSettings = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        val btnBack =
            findViewById<Button>(R.id.btnBack)

        switchNotifications =
            findViewById<Switch>(R.id.switchNotifications)

        spReminderPeriod =
            findViewById<Spinner>(R.id.spReminderPeriod)

        btnBack.setOnClickListener {
            finish()
        }


        // Setup spinner
        setupReminderPeriod()


        // Load previously saved settings
        loadSettings()


        // Notification switch
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->

            if (!isLoadingSettings) {

                getSharedPreferences(
                    preferencesName,
                    MODE_PRIVATE
                )
                    .edit()
                    .putBoolean(
                        "notifications",
                        isChecked
                    )
                    .apply()

                val message =
                    if (isChecked) {
                        "Notifications enabled"
                    } else {
                        "Notifications disabled"
                    }

                Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        // Reminder period
        spReminderPeriod.setOnItemSelectedListener(
            object :
                AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    if (!isLoadingSettings) {

                        getSharedPreferences(
                            preferencesName,
                            MODE_PRIVATE
                        )
                            .edit()
                            .putInt(
                                "reminderPeriod",
                                position
                            )
                            .apply()

                        val selectedPeriod =
                            parent?.getItemAtPosition(
                                position
                            ).toString()

                        Toast.makeText(
                            this@SettingsActivity,
                            "Reminder: $selectedPeriod",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                }
            }
        )

        // Loading finished
        isLoadingSettings = false
    }


    private fun setupReminderPeriod() {

        val periods = arrayOf(

            "7 days before expiry",

            "15 days before expiry",

            "30 days before expiry"
        )


        val adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                periods
            )


        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )


        spReminderPeriod.adapter =
            adapter
    }


    private fun loadSettings() {

        val preferences =
            getSharedPreferences(
                preferencesName,
                MODE_PRIVATE
            )


        // Default notification = ON
        val notifications =
            preferences.getBoolean(
                "notifications",
                true
            )


        // Default reminder = 7 days
        val reminderPeriod =
            preferences.getInt(
                "reminderPeriod",
                0
            )


        // Apply saved values
        switchNotifications.isChecked =
            notifications


        if (reminderPeriod in 0..2) {

            spReminderPeriod.setSelection(
                reminderPeriod,
                false
            )
        }
    }
}