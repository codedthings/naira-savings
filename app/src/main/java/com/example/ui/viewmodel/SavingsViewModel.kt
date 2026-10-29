package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.GoalEntity
import com.example.data.local.entity.SystemConfigEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.GoalCategory
import com.example.data.model.PaymentMethod
import com.example.data.model.SavingsFrequency
import com.example.data.model.UserRole
import com.example.data.repository.SavingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val title: String, val iconName: String) {
    DASHBOARD("Dashboard", "Dashboard"),
    GOALS("Goals", "Savings"),
    ANALYTICS("Analytics", "Analytics"),
    HISTORY("Transactions", "ReceiptLong"),
    ADMIN("Admin Vault", "AdminPanelSettings")
}

data class AdminPlatformMetrics(
    val totalPlatformSavings: Double = 0.0,
    val totalGoalsCount: Int = 0,
    val totalUsersCount: Int = 0,
    val totalTransactionsCount: Int = 0,
    val baseInterestApy: Double = 12.5,
    val safelockBonusApy: Double = 3.5,
    val systemConfig: SystemConfigEntity? = null
)

class SavingsViewModel(
    private val repository: SavingsRepository
) : ViewModel() {

    // Current User Session
    private val _currentUserId = MutableStateFlow<String>("user_chinedu")
    val currentUserId: StateFlow<String> = _currentUserId.asStateFlow()

    val currentUser: StateFlow<UserEntity?> = _currentUserId
        .flatMapLatest { userId -> repository.getUser(userId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Navigation & UI Tab State
    private val _currentTab = MutableStateFlow(AppTab.DASHBOARD)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    // Privacy Eye: Hide/Show Naira balance
    private val _isBalanceHidden = MutableStateFlow(false)
    val isBalanceHidden: StateFlow<Boolean> = _isBalanceHidden.asStateFlow()

    // User's Goals
    val userGoals: StateFlow<List<GoalEntity>> = _currentUserId
        .flatMapLatest { userId -> repository.getGoalsForUser(userId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User's Transactions
    val userTransactions: StateFlow<List<TransactionEntity>> = _currentUserId
        .flatMapLatest { userId -> repository.getTransactionsForUser(userId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Users (for Admin or Profile Switcher)
    val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Goals & Transactions (for Admin)
    val allPlatformGoals: StateFlow<List<GoalEntity>> = repository.getAllGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPlatformTransactions: StateFlow<List<TransactionEntity>> = repository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val systemConfig: StateFlow<SystemConfigEntity?> = repository.getSystemConfig()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Admin Aggregated Metrics
    val adminMetrics: StateFlow<AdminPlatformMetrics> = combine(
        repository.getTotalPlatformSavings(),
        repository.getTotalGoalsCount(),
        repository.getTotalUsersCount(),
        repository.getTotalTransactionsCount(),
        systemConfig
    ) { savings, goalsCount, usersCount, trxCount, config ->
        AdminPlatformMetrics(
            totalPlatformSavings = savings ?: 0.0,
            totalGoalsCount = goalsCount,
            totalUsersCount = usersCount,
            totalTransactionsCount = trxCount,
            baseInterestApy = config?.baseInterestRateAnnual ?: 12.5,
            safelockBonusApy = config?.safelockBonusRate ?: 3.5,
            systemConfig = config
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminPlatformMetrics())

    // UI Dialog & BottomSheet Controls
    private val _selectedGoalForDetail = MutableStateFlow<GoalEntity?>(null)
    val selectedGoalForDetail: StateFlow<GoalEntity?> = _selectedGoalForDetail.asStateFlow()

    private val _goalForDeposit = MutableStateFlow<GoalEntity?>(null)
    val goalForDeposit: StateFlow<GoalEntity?> = _goalForDeposit.asStateFlow()

    private val _goalForWithdraw = MutableStateFlow<GoalEntity?>(null)
    val goalForWithdraw: StateFlow<GoalEntity?> = _goalForWithdraw.asStateFlow()

    private val _selectedTransactionForReceipt = MutableStateFlow<TransactionEntity?>(null)
    val selectedTransactionForReceipt: StateFlow<TransactionEntity?> = _selectedTransactionForReceipt.asStateFlow()

    private val _showCreateGoalDialog = MutableStateFlow(false)
    val showCreateGoalDialog: StateFlow<Boolean> = _showCreateGoalDialog.asStateFlow()

    private val _showAuthProfileDialog = MutableStateFlow(false)
    val showAuthProfileDialog: StateFlow<Boolean> = _showAuthProfileDialog.asStateFlow()

    private val _snackBarMessage = MutableStateFlow<String?>(null)
    val snackBarMessage: StateFlow<String?> = _snackBarMessage.asStateFlow()

    // Goals Filter Category
    private val _selectedCategoryFilter = MutableStateFlow<GoalCategory?>(null)
    val selectedCategoryFilter: StateFlow<GoalCategory?> = _selectedCategoryFilter.asStateFlow()

    // Actions
    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun toggleBalanceHidden() {
        _isBalanceHidden.value = !_isBalanceHidden.value
    }

    fun setCategoryFilter(category: GoalCategory?) {
        _selectedCategoryFilter.value = category
    }

    fun selectGoalForDetail(goal: GoalEntity?) {
        _selectedGoalForDetail.value = goal
    }

    fun openDepositSheet(goal: GoalEntity) {
        _goalForDeposit.value = goal
    }

    fun closeDepositSheet() {
        _goalForDeposit.value = null
    }

    fun openWithdrawSheet(goal: GoalEntity) {
        _goalForWithdraw.value = goal
    }

    fun closeWithdrawSheet() {
        _goalForWithdraw.value = null
    }

    fun openReceiptSheet(transaction: TransactionEntity) {
        _selectedTransactionForReceipt.value = transaction
    }

    fun closeReceiptSheet() {
        _selectedTransactionForReceipt.value = null
    }

    fun openCreateGoalDialog() {
        _showCreateGoalDialog.value = true
    }

    fun closeCreateGoalDialog() {
        _showCreateGoalDialog.value = false
    }

    fun openAuthProfileDialog() {
        _showAuthProfileDialog.value = true
    }

    fun closeAuthProfileDialog() {
        _showAuthProfileDialog.value = false
    }

    fun clearSnackBar() {
        _snackBarMessage.value = null
    }

    fun switchUser(userId: String) {
        _currentUserId.value = userId
        _snackBarMessage.value = "Switched active profile"
    }

    fun createSavingsGoal(
        title: String,
        category: GoalCategory,
        targetAmount: Double,
        targetDateMillis: Long,
        frequency: SavingsFrequency,
        isLocked: Boolean,
        notes: String
    ) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val apy = if (isLocked) 14.5 else 12.0
            val newGoal = GoalEntity(
                userId = user.id,
                title = title.trim(),
                category = category.name,
                targetAmountNaira = targetAmount,
                currentAmountNaira = 0.0,
                targetDateMillis = targetDateMillis,
                frequency = frequency.name,
                isLocked = isLocked,
                interestRatePercent = apy,
                colorHex = category.defaultColor,
                iconName = category.iconName,
                notes = notes.trim()
            )
            repository.createGoal(newGoal)
            _showCreateGoalDialog.value = false
            _snackBarMessage.value = "Savings Goal '$title' created successfully!"
        }
    }

    fun makeDeposit(
        goalId: Long,
        amount: Double,
        paymentMethod: PaymentMethod,
        notes: String = ""
    ) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val success = repository.addDeposit(
                goalId = goalId,
                userId = user.id,
                amount = amount,
                paymentMethod = paymentMethod,
                notes = notes
            )
            if (success) {
                _goalForDeposit.value = null
                _snackBarMessage.value = "Deposit of ₦${amount.toLong()} added successfully!"
            } else {
                _snackBarMessage.value = "Deposit failed. Please check inputs."
            }
        }
    }

    fun makeWithdrawal(
        goalId: Long,
        amount: Double,
        bankName: String,
        accountNumber: String
    ) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val success = repository.withdrawFromGoal(
                goalId = goalId,
                userId = user.id,
                amount = amount,
                destinationBank = bankName,
                destinationAccount = accountNumber
            )
            if (success) {
                _goalForWithdraw.value = null
                _snackBarMessage.value = "Payout of ₦${amount.toLong()} dispatched to $bankName!"
            } else {
                _snackBarMessage.value = "Insufficient goal balance or withdrawal error."
            }
        }
    }

    fun claimInterest(goalId: Long) {
        val user = currentUser.value ?: return
        val goal = userGoals.value.find { it.id == goalId } ?: return
        // Compound 1 month yield: (currentAmount * (rate / 100) / 12)
        val interest = (goal.currentAmountNaira * (goal.interestRatePercent / 100.0)) / 12.0
        if (interest <= 0.0) {
            _snackBarMessage.value = "Deposit funds first to begin earning monthly vault interest."
            return
        }
        viewModelScope.launch {
            repository.creditInterest(goalId, user.id, interest)
            _snackBarMessage.value = "₦${interest.toLong()} interest earned and credited to ${goal.title}!"
        }
    }

    fun deleteGoal(goalId: Long) {
        viewModelScope.launch {
            repository.deleteGoal(goalId)
            _selectedGoalForDetail.value = null
            _snackBarMessage.value = "Goal deleted."
        }
    }

    fun adminCreditBonus(goalId: Long, amount: Double, note: String) {
        viewModelScope.launch {
            val success = repository.adminCreditBonus(goalId, amount, note)
            if (success) {
                _snackBarMessage.value = "Admin bonus of ₦${amount.toLong()} credited to goal!"
            }
        }
    }

    fun adminUpdateRates(baseApy: Double, lockBonusApy: Double) {
        viewModelScope.launch {
            val current = systemConfig.value ?: SystemConfigEntity()
            repository.updateSystemConfig(
                current.copy(
                    baseInterestRateAnnual = baseApy,
                    safelockBonusRate = lockBonusApy,
                    lastAuditTimestamp = System.currentTimeMillis()
                )
            )
            _snackBarMessage.value = "Platform Vault APY updated to ${baseApy}% (+${lockBonusApy}% lock bonus)!"
        }
    }

    fun registerNewAccount(fullName: String, email: String, phone: String, pin: String, isAdmin: Boolean) {
        viewModelScope.launch {
            val role = if (isAdmin) UserRole.ADMIN else UserRole.USER
            val user = repository.registerUser(fullName, email, phone, pin, role)
            _currentUserId.value = user.id
            _showAuthProfileDialog.value = false
            _snackBarMessage.value = "Welcome to NairaSave, ${user.fullName}!"
        }
    }
}

class SavingsViewModelFactory(
    private val repository: SavingsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SavingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SavingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
