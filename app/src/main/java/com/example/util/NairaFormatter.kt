package com.example.util

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.ceil
import kotlin.math.max

object NairaFormatter {
    private val nairaFormat = (NumberFormat.getCurrencyInstance(Locale("en", "NG")) as DecimalFormat).apply {
        positivePrefix = "₦"
        negativePrefix = "-₦"
        maximumFractionDigits = 2
        minimumFractionDigits = 0
    }

    private val nairaFormatWithDecimals = (NumberFormat.getCurrencyInstance(Locale("en", "NG")) as DecimalFormat).apply {
        positivePrefix = "₦"
        negativePrefix = "-₦"
        maximumFractionDigits = 2
        minimumFractionDigits = 2
    }

    fun formatNaira(amount: Double, includeDecimals: Boolean = false): String {
        return if (includeDecimals || amount % 1.0 != 0.0) {
            nairaFormatWithDecimals.format(amount)
        } else {
            nairaFormat.format(amount)
        }
    }

    fun formatNairaCompact(amount: Double): String {
        return when {
            amount >= 1_000_000_000 -> String.format(Locale.US, "₦%.2fB", amount / 1_000_000_000.0)
            amount >= 1_000_000 -> String.format(Locale.US, "₦%.2fM", amount / 1_000_000.0)
            amount >= 1_000 -> String.format(Locale.US, "₦%.1fK", amount / 1_000.0)
            else -> formatNaira(amount)
        }
    }

    fun parseNairaInput(text: String): Double {
        val clean = text.replace("₦", "").replace(",", "").trim()
        return clean.toDoubleOrNull() ?: 0.0
    }

    /**
     * Calculates the savings needed per period to hit the target amount by the target date.
     */
    fun calculateSavingsPlan(
        targetAmount: Double,
        currentAmount: Double,
        targetDateMillis: Long,
        createdDateMillis: Long = System.currentTimeMillis()
    ): SavingsPlanCalculation {
        val remainingAmount = max(0.0, targetAmount - currentAmount)
        val now = System.currentTimeMillis()
        val diffMillis = max(0L, targetDateMillis - now)
        val daysRemaining = max(1L, TimeUnit.MILLISECONDS.toDays(diffMillis))
        val weeksRemaining = max(1L, ceil(daysRemaining / 7.0).toLong())
        val monthsRemaining = max(1L, ceil(daysRemaining / 30.0).toLong())

        val dailyRequirement = if (daysRemaining > 0) remainingAmount / daysRemaining else remainingAmount
        val weeklyRequirement = if (weeksRemaining > 0) remainingAmount / weeksRemaining else remainingAmount
        val monthlyRequirement = if (monthsRemaining > 0) remainingAmount / monthsRemaining else remainingAmount

        val progressPercent = if (targetAmount > 0) ((currentAmount / targetAmount) * 100).coerceIn(0.0, 100.0) else 0.0

        return SavingsPlanCalculation(
            remainingAmount = remainingAmount,
            daysRemaining = daysRemaining,
            weeksRemaining = weeksRemaining,
            monthsRemaining = monthsRemaining,
            dailyAmount = dailyRequirement,
            weeklyAmount = weeklyRequirement,
            monthlyAmount = monthlyRequirement,
            progressPercent = progressPercent.toFloat()
        )
    }
}

data class SavingsPlanCalculation(
    val remainingAmount: Double,
    val daysRemaining: Long,
    val weeksRemaining: Long,
    val monthsRemaining: Long,
    val dailyAmount: Double,
    val weeklyAmount: Double,
    val monthlyAmount: Double,
    val progressPercent: Float
)
