package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class GoalCategory(
    val title: String,
    val description: String,
    val iconName: String,
    val defaultColor: Long
) {
    RENT(
        title = "House Rent",
        description = "Lagos / Abuja accommodation & service charge",
        iconName = "Home",
        defaultColor = 0xFF00875A
    ),
    EMERGENCY(
        title = "Emergency Fund",
        description = "3-6 months rainy day reserve in high-yield vault",
        iconName = "Security",
        defaultColor = 0xFF0284C7
    ),
    JAPA(
        title = "Japa Relocation",
        description = "Proof of funds, visa fees, flights & tuition",
        iconName = "FlightTakeoff",
        defaultColor = 0xFF7C3AED
    ),
    TECH(
        title = "Tech & Gadgets",
        description = "MacBook Pro, Starlink, iPhone, Solar Inverter",
        iconName = "Devices",
        defaultColor = 0xFF0D9488
    ),
    BUSINESS(
        title = "Business Capital",
        description = "SME startup capital, inventory & workspace",
        iconName = "Storefront",
        defaultColor = 0xFFD97706
    ),
    DETTY_DECEMBER(
        title = "Detty December",
        description = "Festive enjoyment, concerts, parties & travel",
        iconName = "Celebration",
        defaultColor = 0xFFEC4899
    ),
    WEDDING(
        title = "Wedding & Owambe",
        description = "Traditional, white wedding & aso ebi fund",
        iconName = "Favorite",
        defaultColor = 0xFFE11D48
    ),
    CAR(
        title = "Car / Mobility",
        description = "Tokunbo vehicle purchase, maintenance & fuel",
        iconName = "DirectionsCar",
        defaultColor = 0xFF4F46E5
    ),
    INVESTMENT(
        title = "Wealth & Land",
        description = "Real estate parcel, FGN bonds & equity",
        iconName = "TrendingUp",
        defaultColor = 0xFF059669
    ),
    EDUCATION(
        title = "School Fees / Certs",
        description = "Professional exams, university tuition & master's",
        iconName = "School",
        defaultColor = 0xFF2563EB
    ),
    CUSTOM(
        title = "Custom Goal",
        description = "Personalized savings target",
        iconName = "Savings",
        defaultColor = 0xFF00875A
    );

    companion object {
        fun fromString(name: String): GoalCategory {
            return entries.find { it.name.equals(name, ignoreCase = true) } ?: CUSTOM
        }
    }
}

enum class SavingsFrequency(val label: String, val intervalDays: Int) {
    DAILY("Daily Auto-Save", 1),
    WEEKLY("Weekly Auto-Save", 7),
    MONTHLY("Monthly Auto-Save", 30),
    FLEXIBLE("Manual / Flexible", 0)
}

enum class TransactionType(val label: String, val isCredit: Boolean) {
    DEPOSIT("Goal Deposit", true),
    WITHDRAWAL("Goal Payout / Withdrawal", false),
    INTEREST("Vault Interest Earned", true),
    BONUS("Admin Welcome Bonus", true)
}

enum class PaymentMethod(val displayName: String, val badge: String) {
    BANK_TRANSFER("Dedicated Wema Bank Account", "Virtual NGN"),
    DEBIT_CARD("Nigerian Debit Card (Mastercard/Visa/Verve)", "Instant"),
    USSD("Bank USSD (*737#, *894#)", "USSD"),
    AUTO_DEBIT("Smart Automated Saver", "Direct Debit"),
    ADMIN_CREDIT("System Administrator Credit", "System")
}

enum class UserRole {
    USER,
    ADMIN
}
