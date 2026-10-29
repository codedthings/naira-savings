package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.GoalCategory
import com.example.data.model.SavingsFrequency

@Entity(tableName = "savings_goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val title: String,
    val category: String = GoalCategory.CUSTOM.name,
    val targetAmountNaira: Double,
    val currentAmountNaira: Double = 0.0,
    val targetDateMillis: Long,
    val frequency: String = SavingsFrequency.FLEXIBLE.name,
    val isLocked: Boolean = false, // SafeLock feature with locked maturity
    val interestRatePercent: Double = 12.5, // Annual interest yield
    val totalInterestEarned: Double = 0.0,
    val colorHex: Long = 0xFF00875A,
    val iconName: String = "Savings",
    val notes: String = "",
    val isCompleted: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis()
)
