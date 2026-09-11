package com.example.expiry_reminder_project

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var tvTotalDocuments: TextView
    private lateinit var tvSummaryMessage: TextView
    private lateinit var tvReminderCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        tvTotalDocuments =
            findViewById(R.id.tvTotalDocuments)

        tvSummaryMessage =
            findViewById(R.id.tvSummaryMessage)

        tvReminderCount =
            findViewById(R.id.tvReminderCount)

        val cardAddDocument =
            findViewById<MaterialCardView>(R.id.cardAddDocument)

        val cardDocuments =
            findViewById<MaterialCardView>(R.id.cardDocuments)

        val cardReminder =
            findViewById<MaterialCardView>(R.id.cardReminder)

        val cardProfile =
            findViewById<View>(R.id.cardProfile)

        val navHome =
            findViewById<View>(R.id.navHome)

        val navDocuments =
            findViewById<View>(R.id.navDocuments)

        val navReminders =
            findViewById<View>(R.id.navReminders)

        val navSettings =
            findViewById<View>(R.id.navSettings)


        cardAddDocument.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AddDocumentActivity::class.java
                )
            )
        }

        cardDocuments.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    DocumentsActivity::class.java
                )
            )
        }

        cardReminder.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    RemindersActivity::class.java
                )
            )
        }

        cardProfile.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ProfileActivity::class.java
                )
            )
        }

        navHome.setOnClickListener {
        }

        navDocuments.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    DocumentsActivity::class.java
                )
            )
        }
        navReminders.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    RemindersActivity::class.java
                )
            )
        }
        navSettings.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SettingsActivity::class.java
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()

        updateDashboard()
    }

    private fun updateDashboard() {

        val preferences =
            getSharedPreferences(
                "DocumentStorage",
                MODE_PRIVATE
            )

        val savedDocuments =
            preferences.getString(
                "documents",
                "[]"
            )

        val documents =
            JSONArray(savedDocuments)

        val total =
            documents.length()

        tvTotalDocuments.text =
            total.toString()

        tvSummaryMessage.text =
            when {

                total == 0 ->
                    "Keep your important documents up to date."

                total == 1 ->
                    "You currently have 1 saved document."

                else ->
                    "You currently have $total saved documents."
            }

        var reminderCount = 0

        for (i in 0 until documents.length()) {

            val document =
                documents.getJSONObject(i)

            val expiryDate =
                document.getString(
                    "expiryDate"
                )

            val daysLeft =
                calculateDaysLeft(expiryDate)

            if (daysLeft <= 30) {
                reminderCount++
            }
        }


        tvReminderCount.text =
            reminderCount.toString()
    }


    private fun calculateDaysLeft(
        expiryDate: String
    ): Long {

        val format =
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
            )

        format.isLenient = false

        return try {

            val expiry =
                format.parse(expiryDate)
                    ?: return 0

            val calendar =
                Calendar.getInstance()

            calendar.set(
                Calendar.HOUR_OF_DAY,
                0
            )

            calendar.set(
                Calendar.MINUTE,
                0
            )

            calendar.set(
                Calendar.SECOND,
                0
            )

            calendar.set(
                Calendar.MILLISECOND,
                0
            )

            val today =
                calendar.time

            TimeUnit.MILLISECONDS.toDays(
                expiry.time - today.time
            )

        } catch (e: Exception) {

            0
        }
    }
}