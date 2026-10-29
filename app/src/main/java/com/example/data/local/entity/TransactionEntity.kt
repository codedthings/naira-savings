package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.PaymentMethod
import com.example.data.model.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val goalId: Long,
    val goalTitle: String,
    val userId: String,
    val amountNaira: Double,
    val transactionType: String = TransactionType.DEPOSIT.name,
    val paymentMethod: String = PaymentMethod.BANK_TRANSFER.name,
    val referenceCode: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val notes: String = "",
    val status: String = "SUCCESSFUL"
)
