package com.example.expiry_reminder_project

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent?
    ) {

        checkAllDocuments(context)
    }


    private fun checkAllDocuments(
        context: Context
    ) {

        val settings =
            context.getSharedPreferences(
                "ExpiryReminder",
                Context.MODE_PRIVATE
            )

        // Notification ON/OFF
        val notificationsEnabled =
            settings.getBoolean(
                "notifications",
                true
            )

        if (!notificationsEnabled) {
            return
        }


        val documentPreferences =
            context.getSharedPreferences(
                "DocumentStorage",
                Context.MODE_PRIVATE
            )

        val data =
            documentPreferences.getString(
                "documents",
                "[]"
            ) ?: "[]"


        val documents =
            try {
                JSONArray(data)
            } catch (e: Exception) {
                JSONArray()
            }


        // Check every document
        for (i in 0 until documents.length()) {

            try {

                val document =
                    documents.getJSONObject(i)

                val documentId =
                    document.optString("id")

                val documentName =
                    document.optString(
                        "name",
                        "Document"
                    )

                val expiryDate =
                    document.optString(
                        "expiryDate",
                        ""
                    )

                // Each document has its own reminder
                val reminderPeriod =
                    document.optString(
                        "reminderPeriod",
                        "7 days before"
                    )

                if (
                    documentId.isEmpty() ||
                    expiryDate.isEmpty()
                ) {
                    continue
                }


                checkDocument(
                    context,
                    documentId,
                    documentName,
                    expiryDate,
                    reminderPeriod
                )

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }


    private fun checkDocument(
        context: Context,
        documentId: String,
        documentName: String,
        expiryDate: String,
        reminderPeriod: String
    ) {

        val dateFormat =
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
            )


        val expiry =
            try {
                dateFormat.parse(expiryDate)
            } catch (e: Exception) {
                null
            }


        if (expiry == null) {
            return
        }


        val today =
            Calendar.getInstance()

        today.set(
            Calendar.HOUR_OF_DAY,
            0
        )

        today.set(
            Calendar.MINUTE,
            0
        )

        today.set(
            Calendar.SECOND,
            0
        )

        today.set(
            Calendar.MILLISECOND,
            0
        )


        val expiryCalendar =
            Calendar.getInstance()

        expiryCalendar.time =
            expiry

        expiryCalendar.set(
            Calendar.HOUR_OF_DAY,
            0
        )

        expiryCalendar.set(
            Calendar.MINUTE,
            0
        )

        expiryCalendar.set(
            Calendar.SECOND,
            0
        )

        expiryCalendar.set(
            Calendar.MILLISECOND,
            0
        )

        if (
            expiryCalendar.before(today)
        ) {

            sendNotification(
                context,
                documentId,
                documentName,
                expiryDate,
                "$documentName has expired. " +
                        "Please renew it as soon as possible."
            )

            return
        }


        val reminderDate =
            Calendar.getInstance()

        reminderDate.time =
            today.time


        when (reminderPeriod) {

            "7 days before" -> {

                reminderDate.add(
                    Calendar.DAY_OF_YEAR,
                    7
                )
            }

            "14 days before" -> {

                reminderDate.add(
                    Calendar.DAY_OF_YEAR,
                    14
                )
            }

            "30 days before" -> {

                reminderDate.add(
                    Calendar.DAY_OF_YEAR,
                    30
                )
            }

            "3 months before" -> {

                reminderDate.add(
                    Calendar.MONTH,
                    3
                )
            }

            "6 months before" -> {

                reminderDate.add(
                    Calendar.MONTH,
                    6
                )
            }

            else -> {

                reminderDate.add(
                    Calendar.DAY_OF_YEAR,
                    7
                )
            }
        }


        if (
            !expiryCalendar.after(reminderDate)
        ) {

            val difference =
                expiryCalendar.timeInMillis -
                        today.timeInMillis

            val daysRemaining =
                difference /
                        (
                                1000L *
                                        60L *
                                        60L *
                                        24L
                                )


            val message =
                if (daysRemaining == 0L) {

                    "$documentName expires today. " +
                            "Please renew it today."

                } else {

                    "$documentName expires in " +
                            "$daysRemaining days " +
                            "on $expiryDate. " +
                            "Please renew it before expiry."
                }


            sendNotification(
                context,
                documentId,
                documentName,
                expiryDate,
                message
            )
        }
    }


    private fun sendNotification(
        context: Context,
        documentId: String,
        documentName: String,
        expiryDate: String,
        message: String
    ) {

        val notificationId =
            documentId.hashCode()
                .and(0x7FFFFFFF)


        NotificationHelper.showDocumentReminder(
            context,
            notificationId,
            documentId,
            documentName,
            expiryDate,
            message
        )
    }
}