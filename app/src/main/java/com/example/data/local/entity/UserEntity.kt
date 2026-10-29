package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val pinHash: String, // 4-digit PIN
    val role: String = UserRole.USER.name,
    val virtualAccountNumber: String = "9901847291",
    val virtualBankName: String = "Wema Bank / NairaSave",
    val bvnVerified: Boolean = true,
    val createdAtMillis: Long = System.currentTimeMillis()
)
