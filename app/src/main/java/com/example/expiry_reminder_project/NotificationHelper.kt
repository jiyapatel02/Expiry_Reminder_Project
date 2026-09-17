package com.example.expiry_reminder_project

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationHelper {

    private const val CHANNEL_ID = "expiry_reminders"
    private const val CHANNEL_NAME = "Expiry Reminders"

    fun createChannel(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            channel.description =
                "Notifications for document expiry"

            val manager =
                context.getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }

    fun showDocumentReminder(
        context: Context,
        notificationId: Int,
        documentId: String,
        documentName: String,
        expiryDate: String,
        message: String
    ) {

        createChannel(context)

        val intent =
            Intent(
                context,
                DocumentDetailsActivity::class.java
            )

        intent.putExtra(
            "document_id",
            documentId
        )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP

        val pendingIntent =
            PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val notification =
            NotificationCompat.Builder(
                context,
                CHANNEL_ID
            )
                .setSmallIcon(
                    android.R.drawable.ic_dialog_info
                )
                .setContentTitle(
                    "Document Expiry Reminder"
                )
                .setContentText(
                    "$documentName expires on $expiryDate"
                )
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(message)
                )
                .setPriority(
                    NotificationCompat.PRIORITY_DEFAULT
                )
                .setAutoCancel(true)
                .setContentIntent(
                    pendingIntent
                )
                .build()

        try {

            NotificationManagerCompat
                .from(context)
                .notify(
                    notificationId,
                    notification
                )

        } catch (e: SecurityException) {

            e.printStackTrace()
        }
    }
}