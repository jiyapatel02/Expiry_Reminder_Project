package com.example.expiry_reminder_project

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.expiry_reminder_project.utils.DateUtils
import org.json.JSONArray

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent?
    ) {

        val preferences = context.getSharedPreferences(
            "ExpiryReminder",
            Context.MODE_PRIVATE
        )

        // Check whether notifications are enabled
        val notificationsEnabled =
            preferences.getBoolean(
                "notifications",
                true
            )

        if (!notificationsEnabled) {
            return
        }

        val reminderPeriod =
            preferences.getInt(
                "reminder_period",
                7
            )

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

        val documents = try {
            JSONArray(data)
        } catch (e: Exception) {
            JSONArray()
        }

        for (i in 0 until documents.length()) {

            val document =
                documents.getJSONObject(i)

            val id =
                document.optString("id")

            val name =
                document.optString(
                    "name",
                    "Document"
                )

            val expiryDate =
                document.optString(
                    "expiryDate",
                    ""
                )

            if (expiryDate.isBlank()) {
                continue
            }

            val status =
                DateUtils.getStatus(expiryDate)

            val days =
                DateUtils.getDaysRemaining(expiryDate)

            when (status) {

                "EXPIRED" -> {

                    sendOncePerDay(
                        context,
                        id,
                        "Document Expired",
                        "$name has expired."
                    )
                }

                "EXPIRING SOON" -> {

                    if (days <= reminderPeriod) {

                        val message =
                            if (days == 0L) {
                                "$name expires today."
                            } else {
                                "$name expires in $days days."
                            }

                        sendOncePerDay(
                            context,
                            id,
                            "Document Expiry Reminder",
                            message
                        )
                    }
                }
            }
        }
    }

    private fun sendOncePerDay(
        context: Context,
        documentId: String,
        title: String,
        message: String
    ) {

        val preferences =
            context.getSharedPreferences(
                "ReminderNotifications",
                Context.MODE_PRIVATE
            )

        val today =
            java.text.SimpleDateFormat(
                "yyyy-MM-dd",
                java.util.Locale.getDefault()
            ).format(java.util.Date())

        val key =
            "last_notification_$documentId"

        val lastNotification =
            preferences.getString(
                key,
                ""
            )

        // Prevent duplicate notifications on the same day
        if (lastNotification == today) {
            return
        }

        NotificationHelper.showNotification(
            context,
            documentId.hashCode(),
            title,
            message
        )

        preferences.edit()
            .putString(key, today)
            .apply()
    }
}