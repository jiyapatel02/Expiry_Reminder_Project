package com.example.expiry_reminder_project

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class RemindersActivity : AppCompatActivity() {

    private lateinit var remindersContainer: ConstraintLayout
    private lateinit var tvReminderSummary: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_reminders)

        val btnBack = findViewById<Button>(R.id.btnBack)

        remindersContainer =
            findViewById(R.id.remindersContainer)

        tvReminderSummary =
            findViewById(R.id.tvReminderSummary)

        btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadReminders()
    }

    private fun loadReminders() {

        remindersContainer.removeAllViews()

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

        val documents = JSONArray(savedDocuments)

        if (documents.length() == 0) {

            tvReminderSummary.text =
                "No documents to check"

            val emptyText = TextView(this)

            emptyText.id =
                View.generateViewId()

            emptyText.text =
                "🔔\n\nNo reminders available"

            emptyText.textSize = 18f

            emptyText.gravity =
                Gravity.CENTER

            emptyText.setTextColor(
                Color.rgb(102, 113, 111)
            )

            val params =
                ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    250
                )

            emptyText.layoutParams = params

            remindersContainer.addView(emptyText)

            return
        }

        var expiredCount = 0
        var urgentCount = 0
        var upcomingCount = 0
        var validCount = 0

        for (i in 0 until documents.length()) {

            val document =
                documents.getJSONObject(i)

            val name =
                document.getString("name")

            val category =
                document.getString("category")

            val expiryDate =
                document.getString("expiryDate")

            val daysLeft =
                calculateDaysLeft(expiryDate)

            when {

                daysLeft < 0 -> {

                    expiredCount++

                    createReminderCard(
                        name,
                        category,
                        expiryDate,
                        "🔴 EXPIRED",
                        Color.rgb(217, 54, 54)
                    )
                }

                daysLeft <= 7 -> {

                    urgentCount++

                    createReminderCard(
                        name,
                        category,
                        expiryDate,
                        "🟠 EXPIRES IN $daysLeft DAYS",
                        Color.rgb(216, 137, 0)
                    )
                }

                daysLeft <= 30 -> {

                    upcomingCount++

                    createReminderCard(
                        name,
                        category,
                        expiryDate,
                        "🟡 EXPIRES IN $daysLeft DAYS",
                        Color.rgb(216, 137, 0)
                    )
                }

                else -> {

                    validCount++

                    createReminderCard(
                        name,
                        category,
                        expiryDate,
                        "🟢 VALID - $daysLeft DAYS LEFT",
                        Color.rgb(27, 154, 89)
                    )
                }
            }
        }

        tvReminderSummary.text =
            "Expired: $expiredCount  |  " +
                    "Urgent: $urgentCount  |  " +
                    "Upcoming: $upcomingCount  |  " +
                    "Valid: $validCount"
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

            val difference =
                expiry.time - today.time

            TimeUnit.MILLISECONDS.toDays(
                difference
            )

        } catch (e: Exception) {

            0
        }
    }

    private fun createReminderCard(
        name: String,
        category: String,
        expiryDate: String,
        status: String,
        statusColor: Int
    ) {

        val card =
            CardView(this)

        card.radius = 20f
        card.cardElevation = 5f

        card.setContentPadding(
            20,
            20,
            20,
            20
        )

        val cardLayout =
            ConstraintLayout(this)

        cardLayout.layoutParams =
            ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                150
            )

        val title =
            TextView(this)

        title.id =
            View.generateViewId()

        title.text = name
        title.textSize = 19f

        title.setTextColor(
            Color.rgb(23, 32, 31)
        )

        title.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        val categoryText =
            TextView(this)

        categoryText.id =
            View.generateViewId()

        categoryText.text =
            "Category: $category"

        categoryText.textSize = 14f

        categoryText.setTextColor(
            Color.rgb(102, 113, 111)
        )

        val expiryText =
            TextView(this)

        expiryText.id =
            View.generateViewId()

        expiryText.text =
            "Expiry Date: $expiryDate"

        expiryText.textSize = 14f

        expiryText.setTextColor(
            Color.rgb(102, 113, 111)
        )

        val statusText =
            TextView(this)

        statusText.id =
            View.generateViewId()

        statusText.text = status
        statusText.textSize = 15f

        statusText.setTextColor(
            statusColor
        )

        statusText.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        cardLayout.addView(title)
        cardLayout.addView(categoryText)
        cardLayout.addView(expiryText)
        cardLayout.addView(statusText)

        val titleParams =
            title.layoutParams
                    as ConstraintLayout.LayoutParams

        titleParams.startToStart =
            ConstraintLayout.LayoutParams.PARENT_ID

        titleParams.topToTop =
            ConstraintLayout.LayoutParams.PARENT_ID

        title.layoutParams =
            titleParams

        val categoryParams =
            categoryText.layoutParams
                    as ConstraintLayout.LayoutParams

        categoryParams.startToStart =
            ConstraintLayout.LayoutParams.PARENT_ID

        categoryParams.topToBottom =
            title.id

        categoryParams.topMargin = 5

        categoryText.layoutParams =
            categoryParams

        val expiryParams =
            expiryText.layoutParams
                    as ConstraintLayout.LayoutParams

        expiryParams.startToStart =
            ConstraintLayout.LayoutParams.PARENT_ID

        expiryParams.topToBottom =
            categoryText.id

        expiryParams.topMargin = 5

        expiryText.layoutParams =
            expiryParams

        val statusParams =
            statusText.layoutParams
                    as ConstraintLayout.LayoutParams

        statusParams.startToStart =
            ConstraintLayout.LayoutParams.PARENT_ID

        statusParams.topToBottom =
            expiryText.id

        statusParams.topMargin = 8

        statusText.layoutParams =
            statusParams

        card.addView(cardLayout)

        val cardParams =
            ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )

        cardParams.bottomMargin = 15

        card.layoutParams =
            cardParams

        remindersContainer.addView(card)
    }
}