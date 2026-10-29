package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY timestampMillis DESC")
    fun getTransactionsByUser(userId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE goalId = :goalId ORDER BY timestampMillis DESC")
    fun getTransactionsByGoal(goalId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY timestampMillis DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY timestampMillis DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int = 10): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Query("SELECT SUM(amountNaira) FROM transactions WHERE userId = :userId AND transactionType = 'DEPOSIT'")
    fun getTotalDepositsByUser(userId: String): Flow<Double?>

    @Query("SELECT SUM(amountNaira) FROM transactions WHERE transactionType = 'DEPOSIT'")
    fun getTotalPlatformDeposits(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM transactions")
    fun getTotalTransactionsCount(): Flow<Int>
}
