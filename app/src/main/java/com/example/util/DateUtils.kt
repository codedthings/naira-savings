package com.example.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateUtils {
    private val standardDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
    private val dateTimeFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH)
    private val shortDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
    private val monthYearFormat = SimpleDateFormat("MMM yyyy", Locale.ENGLISH)
    private val monthNameFormat = SimpleDateFormat("MMM", Locale.ENGLISH)

    fun formatDate(millis: Long): String {
        return standardDateFormat.format(Date(millis))
    }

    fun formatDateTime(millis: Long): String {
        return dateTimeFormat.format(Date(millis))
    }

    fun formatShortDate(millis: Long): String {
        return shortDateFormat.format(Date(millis))
    }

    fun formatMonthYear(millis: Long): String {
        return monthYearFormat.format(Date(millis))
    }

    fun formatMonthName(millis: Long): String {
        return monthNameFormat.format(Date(millis))
    }

    fun getDaysLeft(targetDateMillis: Long): Long {
        val now = System.currentTimeMillis()
        val diff = targetDateMillis - now
        return if (diff <= 0) 0 else TimeUnit.MILLISECONDS.toDays(diff)
    }

    fun getDaysLeftText(targetDateMillis: Long): String {
        val days = getDaysLeft(targetDateMillis)
        return when {
            days <= 0 -> "Target Date Reached"
            days == 1L -> "1 day left"
            days < 30 -> "$days days left"
            days < 365 -> "${days / 30} months left (${days}d)"
            else -> "${days / 365} years left"
        }
    }
}
