package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.GoalDao
import com.example.data.local.dao.SystemConfigDao
import com.example.data.local.dao.TransactionDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.GoalEntity
import com.example.data.local.entity.SystemConfigEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        GoalEntity::class,
        TransactionEntity::class,
        SystemConfigEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun goalDao(): GoalDao
    abstract fun transactionDao(): TransactionDao
    abstract fun systemConfigDao(): SystemConfigDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "naira_save_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            val userDao = database.userDao()
            val goalDao = database.goalDao()
            val transactionDao = database.transactionDao()
            val systemConfigDao = database.systemConfigDao()

            // 1. Initial System Configuration
            systemConfigDao.insertOrUpdate(
                SystemConfigEntity(
                    id = 1,
                    baseInterestRateAnnual = 12.5,
                    safelockBonusRate = 3.5,
                    totalBonusPoolDistributed = 2500000.0,
                    platformFeePercent = 0.0,
                    isSystemAuditingActive = true
                )
            )

            // 2. Demo Nigerian Users
            val chinedu = UserEntity(
                id = "user_chinedu",
                fullName = "Chinedu Okafor",
                email = "chinedu.okafor@gmail.com",
                phoneNumber = "+234 803 456 7890",
                pinHash = "1234",
                role = "USER",
                virtualAccountNumber = "9901847291",
                virtualBankName = "Wema Bank / NairaSave",
                bvnVerified = true
            )

            val amina = UserEntity(
                id = "user_amina",
                fullName = "Amina Bello",
                email = "amina.bello@abuja-ventures.ng",
                phoneNumber = "+234 812 345 6789",
                pinHash = "1234",
                role = "USER",
                virtualAccountNumber = "9902348512",
                virtualBankName = "Providus Bank / NairaSave",
                bvnVerified = true
            )

            val admin = UserEntity(
                id = "admin_super",
                fullName = "Solomon Uzor (Chief Vault Overseer)",
                email = "uzorsolomon@gmail.com",
                phoneNumber = "+234 809 112 3344",
                pinHash = "9999",
                role = "ADMIN",
                virtualAccountNumber = "9900000001",
                virtualBankName = "Central Reserve / NairaSave",
                bvnVerified = true
            )

            userDao.insertUser(chinedu)
            userDao.insertUser(amina)
            userDao.insertUser(admin)

            val now = System.currentTimeMillis()
            val dayMillis = 86400000L

            // 3. Goals for Chinedu Okafor (Lekki Rent, Japa UK, Starlink & Tech, Detty December)
            val rentGoalId = goalDao.insertGoal(
                GoalEntity(
                    userId = "user_chinedu",
                    title = "Lekki Phase 1 Rent (2026)",
                    category = "RENT",
                    targetAmountNaira = 3500000.0,
                    currentAmountNaira = 2250000.0,
                    targetDateMillis = now + (90 * dayMillis),
                    frequency = "MONTHLY",
                    isLocked = true,
                    interestRatePercent = 14.0,
                    totalInterestEarned = 78750.0,
                    colorHex = 0xFF00875A,
                    iconName = "Home",
                    notes = "Landlord renewal due by November. 2 Bed apartment."
                )
            )

            val japaGoalId = goalDao.insertGoal(
                GoalEntity(
                    userId = "user_chinedu",
                    title = "Japa Relocation & Tuition Fund",
                    category = "JAPA",
                    targetAmountNaira = 12000000.0,
                    currentAmountNaira = 5400000.0,
                    targetDateMillis = now + (180 * dayMillis),
                    frequency = "WEEKLY",
                    isLocked = true,
                    interestRatePercent = 15.0,
                    totalInterestEarned = 202500.0,
                    colorHex = 0xFF7C3AED,
                    iconName = "FlightTakeoff",
                    notes = "Proof of funds & visa processing reserve."
                )
            )

            val techGoalId = goalDao.insertGoal(
                GoalEntity(
                    userId = "user_chinedu",
                    title = "M3 Max MacBook & Starlink Setup",
                    category = "TECH",
                    targetAmountNaira = 2800000.0,
                    currentAmountNaira = 2800000.0,
                    targetDateMillis = now + (10 * dayMillis),
                    frequency = "MONTHLY",
                    isLocked = false,
                    interestRatePercent = 12.0,
                    totalInterestEarned = 35000.0,
                    colorHex = 0xFF0D9488,
                    iconName = "Devices",
                    notes = "Target completed! Ready for dispatch.",
                    isCompleted = true
                )
            )

            val dettyDecId = goalDao.insertGoal(
                GoalEntity(
                    userId = "user_chinedu",
                    title = "Detty December & Owambe Trips",
                    category = "DETTY_DECEMBER",
                    targetAmountNaira = 1500000.0,
                    currentAmountNaira = 650000.0,
                    targetDateMillis = now + (120 * dayMillis),
                    frequency = "WEEKLY",
                    isLocked = false,
                    interestRatePercent = 11.5,
                    totalInterestEarned = 18600.0,
                    colorHex = 0xFFEC4899,
                    iconName = "Celebration",
                    notes = "Flytime Fest, Afro Nation, and Calabar trip."
                )
            )

            // Goals for Amina Bello
            goalDao.insertGoal(
                GoalEntity(
                    userId = "user_amina",
                    title = "Abuja Tech Hub Seed Expansion",
                    category = "BUSINESS",
                    targetAmountNaira = 8000000.0,
                    currentAmountNaira = 4500000.0,
                    targetDateMillis = now + (150 * dayMillis),
                    frequency = "MONTHLY",
                    isLocked = true,
                    interestRatePercent = 14.5,
                    totalInterestEarned = 163125.0,
                    colorHex = 0xFFD97706,
                    iconName = "Storefront"
                )
            )

            // 4. Initial Transaction Records for Chinedu
            transactionDao.insertTransaction(
                TransactionEntity(
                    goalId = rentGoalId,
                    goalTitle = "Lekki Phase 1 Rent (2026)",
                    userId = "user_chinedu",
                    amountNaira = 1000000.0,
                    transactionType = "DEPOSIT",
                    paymentMethod = "BANK_TRANSFER",
                    referenceCode = "TRX-NG-2026-88192",
                    timestampMillis = now - (25 * dayMillis),
                    notes = "Direct Wema Bank transfer deposit",
                    status = "SUCCESSFUL"
                )
            )

            transactionDao.insertTransaction(
                TransactionEntity(
                    goalId = rentGoalId,
                    goalTitle = "Lekki Phase 1 Rent (2026)",
                    userId = "user_chinedu",
                    amountNaira = 1250000.0,
                    transactionType = "DEPOSIT",
                    paymentMethod = "DEBIT_CARD",
                    referenceCode = "TRX-NG-2026-90412",
                    timestampMillis = now - (7 * dayMillis),
                    notes = "Mastercard quick topup",
                    status = "SUCCESSFUL"
                )
            )

            transactionDao.insertTransaction(
                TransactionEntity(
                    goalId = rentGoalId,
                    goalTitle = "Lekki Phase 1 Rent (2026)",
                    userId = "user_chinedu",
                    amountNaira = 78750.0,
                    transactionType = "INTEREST",
                    paymentMethod = "ADMIN_CREDIT",
                    referenceCode = "INT-SAFE-2026-110",
                    timestampMillis = now - (2 * dayMillis),
                    notes = "Monthly SafeLock 14.0% APY Interest Credit",
                    status = "SUCCESSFUL"
                )
            )

            transactionDao.insertTransaction(
                TransactionEntity(
                    goalId = japaGoalId,
                    goalTitle = "Japa Relocation & Tuition Fund",
                    userId = "user_chinedu",
                    amountNaira = 3000000.0,
                    transactionType = "DEPOSIT",
                    paymentMethod = "BANK_TRANSFER",
                    referenceCode = "TRX-NG-2026-77312",
                    timestampMillis = now - (40 * dayMillis),
                    notes = "Savings allocation from freelance payout",
                    status = "SUCCESSFUL"
                )
            )

            transactionDao.insertTransaction(
                TransactionEntity(
                    goalId = japaGoalId,
                    goalTitle = "Japa Relocation & Tuition Fund",
                    userId = "user_chinedu",
                    amountNaira = 2400000.0,
                    transactionType = "DEPOSIT",
                    paymentMethod = "AUTO_DEBIT",
                    referenceCode = "TRX-NG-2026-81993",
                    timestampMillis = now - (12 * dayMillis),
                    notes = "Smart Weekly Auto-Debit",
                    status = "SUCCESSFUL"
                )
            )

            transactionDao.insertTransaction(
                TransactionEntity(
                    goalId = techGoalId,
                    goalTitle = "M3 Max MacBook & Starlink Setup",
                    userId = "user_chinedu",
                    amountNaira = 2800000.0,
                    transactionType = "DEPOSIT",
                    paymentMethod = "BANK_TRANSFER",
                    referenceCode = "TRX-NG-2026-64101",
                    timestampMillis = now - (15 * dayMillis),
                    notes = "Target reached in full",
                    status = "SUCCESSFUL"
                )
            )

            transactionDao.insertTransaction(
                TransactionEntity(
                    goalId = dettyDecId,
                    goalTitle = "Detty December & Owambe Trips",
                    userId = "user_chinedu",
                    amountNaira = 650000.0,
                    transactionType = "DEPOSIT",
                    paymentMethod = "USSD",
                    referenceCode = "TRX-NG-2026-95021",
                    timestampMillis = now - (3 * dayMillis),
                    notes = "GTBank *737* Quick Deposit",
                    status = "SUCCESSFUL"
                )
            )
        }
    }
}
