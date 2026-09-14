package com.example.expiry_reminder_project.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    private const val DATE_FORMAT = "dd/MM/yyyy"

    fun getStatus(expiryDate: String): String {

        return try {
            val formatter = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            formatter.isLenient = false

            val expiry = formatter.parse(expiryDate)
                ?: return "INVALID"

            val today = Calendar.getInstance()

            val expiryCalendar = Calendar.getInstance()
            expiryCalendar.time = expiry

            // Remove time part for accurate date comparison
            today.set(Calendar.HOUR_OF_DAY, 0)
            today.set(Calendar.MINUTE, 0)
            today.set(Calendar.SECOND, 0)
            today.set(Calendar.MILLISECOND, 0)

            expiryCalendar.set(Calendar.HOUR_OF_DAY, 0)
            expiryCalendar.set(Calendar.MINUTE, 0)
            expiryCalendar.set(Calendar.SECOND, 0)
            expiryCalendar.set(Calendar.MILLISECOND, 0)

            val difference =
                expiryCalendar.timeInMillis - today.timeInMillis

            val daysRemaining =
                difference / (1000L * 60 * 60 * 24)

            when {
                daysRemaining < 0 -> "EXPIRED"
                daysRemaining <= 30 -> "EXPIRING SOON"
                else -> "VALID"
            }

        } catch (e: Exception) {
            "INVALID"
        }
    }

    fun getDaysRemaining(expiryDate: String): Long {

        return try {
            val formatter = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            formatter.isLenient = false

            val expiry = formatter.parse(expiryDate)
                ?: return 0

            val today = Calendar.getInstance()

            val expiryCalendar = Calendar.getInstance()
            expiryCalendar.time = expiry

            today.set(Calendar.HOUR_OF_DAY, 0)
            today.set(Calendar.MINUTE, 0)
            today.set(Calendar.SECOND, 0)
            today.set(Calendar.MILLISECOND, 0)

            expiryCalendar.set(Calendar.HOUR_OF_DAY, 0)
            expiryCalendar.set(Calendar.MINUTE, 0)
            expiryCalendar.set(Calendar.SECOND, 0)
            expiryCalendar.set(Calendar.MILLISECOND, 0)

            val difference =
                expiryCalendar.timeInMillis - today.timeInMillis

            difference / (1000L * 60 * 60 * 24)

        } catch (e: Exception) {
            0
        }
    }

    fun isValidDate(date: String): Boolean {

        return try {
            val formatter =
                SimpleDateFormat(DATE_FORMAT, Locale.getDefault())

            formatter.isLenient = false
            formatter.parse(date)

            true

        } catch (e: Exception) {
            false
        }
    }

    fun getTodayDate(): String {

        val formatter =
            SimpleDateFormat(DATE_FORMAT, Locale.getDefault())

        return formatter.format(Date())
    }
}