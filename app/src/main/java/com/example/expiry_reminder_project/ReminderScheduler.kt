package com.example.expiry_reminder_project

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object ReminderScheduler {

    private const val REQUEST_CODE = 1001

    fun schedule(context: Context) {

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        val intent =
            Intent(
                context,
                ReminderReceiver::class.java
            )

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        // First notification check at 9:00 AM
        val calendar = Calendar.getInstance()

        calendar.set(
            Calendar.HOUR_OF_DAY,
            9
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

        // If 9 AM has already passed,
        // run tomorrow
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(
                Calendar.DAY_OF_YEAR,
                1
            )
        }

        val interval =
            AlarmManager.INTERVAL_DAY

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            interval,
            pendingIntent
        )
    }

    fun cancel(context: Context) {

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        val intent =
            Intent(
                context,
                ReminderReceiver::class.java
            )

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        alarmManager.cancel(pendingIntent)
    }
}