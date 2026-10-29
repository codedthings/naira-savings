package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "system_config")
data class SystemConfigEntity(
    @PrimaryKey val id: Int = 1,
    val baseInterestRateAnnual: Double = 12.5,
    val safelockBonusRate: Double = 3.0,
    val totalBonusPoolDistributed: Double = 1500000.0,
    val platformFeePercent: Double = 0.0,
    val isSystemAuditingActive: Boolean = true,
    val lastAuditTimestamp: Long = System.currentTimeMillis()
)
