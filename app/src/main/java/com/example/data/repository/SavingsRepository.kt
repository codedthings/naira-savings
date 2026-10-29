package com.example.data.repository

import com.example.data.local.dao.GoalDao
import com.example.data.local.dao.SystemConfigDao
import com.example.data.local.dao.TransactionDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.GoalEntity
import com.example.data.local.entity.SystemConfigEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.PaymentMethod
import com.example.data.model.TransactionType
import com.example.data.model.UserRole
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class SavingsRepository(
    private val userDao: UserDao,
    private val goalDao: GoalDao,
    private val transactionDao: TransactionDao,
    private val systemConfigDao: SystemConfigDao
) {
    // User / Auth
    fun getUser(userId: String): Flow<UserEntity?> = userDao.getUserById(userId)
    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()
    fun getTotalUsersCount(): Flow<Int> = userDao.getTotalUsersCount()

    suspend fun authenticate(email: String, pin: String): UserEntity? {
        val user = userDao.getUserByEmail(email)
        return if (user != null && user.pinHash == pin) user else null
    }

    suspend fun registerUser(
        fullName: String,
        email: String,
        phoneNumber: String,
        pin: String,
        role: UserRole = UserRole.USER
    ): UserEntity {
        val id = "user_" + UUID.randomUUID().toString().take(8)
        val randAcc = (9900000000L + (100000000L..999999999L).random()).toString().take(10)
        val newUser = UserEntity(
            id = id,
            fullName = fullName,
            email = email,
            phoneNumber = phoneNumber,
            pinHash = pin,
            role = role.name,
            virtualAccountNumber = randAcc,
            virtualBankName = "Wema Bank / NairaSave",
            bvnVerified = true
        )
        userDao.insertUser(newUser)
        return newUser
    }

    // Goals
    fun getGoalsForUser(userId: String): Flow<List<GoalEntity>> = goalDao.getGoalsByUser(userId)
    fun getGoalById(goalId: Long): Flow<GoalEntity?> = goalDao.getGoalById(goalId)
    fun getAllGoals(): Flow<List<GoalEntity>> = goalDao.getAllGoals()
    fun getTotalSavedForUser(userId: String): Flow<Double?> = goalDao.getTotalSavedByUser(userId)
    fun getTotalPlatformSavings(): Flow<Double?> = goalDao.getTotalPlatformSavings()
    fun getTotalGoalsCount(): Flow<Int> = goalDao.getTotalGoalsCount()

    suspend fun createGoal(goal: GoalEntity): Long = goalDao.insertGoal(goal)
    suspend fun updateGoal(goal: GoalEntity) = goalDao.updateGoal(goal)
    suspend fun deleteGoal(goalId: Long) = goalDao.deleteGoalById(goalId)

    suspend fun addDeposit(
        goalId: Long,
        userId: String,
        amount: Double,
        paymentMethod: PaymentMethod,
        notes: String = ""
    ): Boolean {
        val goal = goalDao.getGoalByIdSync(goalId) ?: return false
        val newAmount = goal.currentAmountNaira + amount
        val isCompleted = newAmount >= goal.targetAmountNaira

        // Generate reference
        val refCode = "TRX-NG-${System.currentTimeMillis().toString().takeLast(6)}-${(100..999).random()}"

        goalDao.updateGoal(
            goal.copy(
                currentAmountNaira = newAmount,
                isCompleted = isCompleted
            )
        )

        transactionDao.insertTransaction(
            TransactionEntity(
                goalId = goal.id,
                goalTitle = goal.title,
                userId = userId,
                amountNaira = amount,
                transactionType = TransactionType.DEPOSIT.name,
                paymentMethod = paymentMethod.name,
                referenceCode = refCode,
                timestampMillis = System.currentTimeMillis(),
                notes = if (notes.isNotBlank()) notes else "Deposit to ${goal.title}",
                status = "SUCCESSFUL"
            )
        )
        return true
    }

    suspend fun withdrawFromGoal(
        goalId: Long,
        userId: String,
        amount: Double,
        destinationBank: String,
        destinationAccount: String
    ): Boolean {
        val goal = goalDao.getGoalByIdSync(goalId) ?: return false
        if (goal.currentAmountNaira < amount) return false

        val newAmount = goal.currentAmountNaira - amount
        val refCode = "WD-NG-${System.currentTimeMillis().toString().takeLast(6)}-${(100..999).random()}"

        goalDao.updateGoal(
            goal.copy(
                currentAmountNaira = newAmount,
                isCompleted = newAmount >= goal.targetAmountNaira
            )
        )

        transactionDao.insertTransaction(
            TransactionEntity(
                goalId = goal.id,
                goalTitle = goal.title,
                userId = userId,
                amountNaira = amount,
                transactionType = TransactionType.WITHDRAWAL.name,
                paymentMethod = PaymentMethod.BANK_TRANSFER.name,
                referenceCode = refCode,
                timestampMillis = System.currentTimeMillis(),
                notes = "Payout to $destinationBank ($destinationAccount)",
                status = "SUCCESSFUL"
            )
        )
        return true
    }

    suspend fun creditInterest(goalId: Long, userId: String, interestAmount: Double): Boolean {
        val goal = goalDao.getGoalByIdSync(goalId) ?: return false
        val newAmount = goal.currentAmountNaira + interestAmount
        val newInterest = goal.totalInterestEarned + interestAmount
        val refCode = "INT-SAFE-${System.currentTimeMillis().toString().takeLast(6)}"

        goalDao.updateGoal(
            goal.copy(
                currentAmountNaira = newAmount,
                totalInterestEarned = newInterest
            )
        )

        transactionDao.insertTransaction(
            TransactionEntity(
                goalId = goal.id,
                goalTitle = goal.title,
                userId = userId,
                amountNaira = interestAmount,
                transactionType = TransactionType.INTEREST.name,
                paymentMethod = PaymentMethod.ADMIN_CREDIT.name,
                referenceCode = refCode,
                timestampMillis = System.currentTimeMillis(),
                notes = "Monthly Compound Vault Yield @ ${goal.interestRatePercent}% p.a.",
                status = "SUCCESSFUL"
            )
        )
        return true
    }

    // Transactions
    fun getTransactionsForUser(userId: String): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsByUser(userId)

    fun getTransactionsForGoal(goalId: Long): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsByGoal(goalId)

    fun getAllTransactions(): Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    fun getRecentTransactions(limit: Int = 10): Flow<List<TransactionEntity>> =
        transactionDao.getRecentTransactions(limit)
    fun getTotalTransactionsCount(): Flow<Int> = transactionDao.getTotalTransactionsCount()

    // System Config / Admin
    fun getSystemConfig(): Flow<SystemConfigEntity?> = systemConfigDao.getSystemConfig()
    suspend fun updateSystemConfig(config: SystemConfigEntity) = systemConfigDao.update(config)

    suspend fun adminCreditBonus(goalId: Long, bonusAmount: Double, adminNotes: String): Boolean {
        val goal = goalDao.getGoalByIdSync(goalId) ?: return false
        val newAmount = goal.currentAmountNaira + bonusAmount
        val refCode = "ADM-BONUS-${System.currentTimeMillis().toString().takeLast(6)}"

        goalDao.updateGoal(
            goal.copy(currentAmountNaira = newAmount)
        )

        transactionDao.insertTransaction(
            TransactionEntity(
                goalId = goal.id,
                goalTitle = goal.title,
                userId = goal.userId,
                amountNaira = bonusAmount,
                transactionType = TransactionType.BONUS.name,
                paymentMethod = PaymentMethod.ADMIN_CREDIT.name,
                referenceCode = refCode,
                timestampMillis = System.currentTimeMillis(),
                notes = adminNotes.ifBlank { "Admin Promotional Savings Match Boost" },
                status = "SUCCESSFUL"
            )
        )
        return true
    }
}
