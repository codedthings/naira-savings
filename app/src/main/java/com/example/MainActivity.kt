package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.repository.SavingsRepository
import com.example.ui.dialogs.*
import com.example.ui.screens.*
import com.example.ui.theme.EmeraldGreenPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.SavingsViewModel
import com.example.ui.viewmodel.SavingsViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = SavingsRepository(
            userDao = database.userDao(),
            goalDao = database.goalDao(),
            transactionDao = database.transactionDao(),
            systemConfigDao = database.systemConfigDao()
        )
        val viewModelFactory = SavingsViewModelFactory(repository)

        setContent {
            MyApplicationTheme {
                val viewModel: SavingsViewModel = viewModel(factory = viewModelFactory)
                NairaSaveApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NairaSaveApp(viewModel: SavingsViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val isBalanceHidden by viewModel.isBalanceHidden.collectAsStateWithLifecycle()
    val userGoals by viewModel.userGoals.collectAsStateWithLifecycle()
    val userTransactions by viewModel.userTransactions.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val allPlatformGoals by viewModel.allPlatformGoals.collectAsStateWithLifecycle()
    val allPlatformTransactions by viewModel.allPlatformTransactions.collectAsStateWithLifecycle()
    val adminMetrics by viewModel.adminMetrics.collectAsStateWithLifecycle()

    val selectedGoalForDetail by viewModel.selectedGoalForDetail.collectAsStateWithLifecycle()
    val goalForDeposit by viewModel.goalForDeposit.collectAsStateWithLifecycle()
    val goalForWithdraw by viewModel.goalForWithdraw.collectAsStateWithLifecycle()
    val selectedTrxForReceipt by viewModel.selectedTransactionForReceipt.collectAsStateWithLifecycle()
    val showCreateGoalDialog by viewModel.showCreateGoalDialog.collectAsStateWithLifecycle()
    val showAuthProfileDialog by viewModel.showAuthProfileDialog.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackBarMessage.collectAsStateWithLifecycle()
    val selectedCategoryFilter by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackBar()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (selectedGoalForDetail == null) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets.navigationBars,
                    modifier = Modifier.testTag("main_navigation_bar")
                ) {
                    AppTab.entries.forEach { tab ->
                        val isSelected = currentTab == tab
                        val icon: ImageVector = when (tab) {
                            AppTab.DASHBOARD -> Icons.Default.Dashboard
                            AppTab.GOALS -> Icons.Default.Savings
                            AppTab.ANALYTICS -> Icons.Default.BarChart
                            AppTab.HISTORY -> Icons.Default.ReceiptLong
                            AppTab.ADMIN -> Icons.Default.AdminPanelSettings
                        }

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.setTab(tab) },
                            icon = { Icon(imageVector = icon, contentDescription = tab.title) },
                            label = { Text(tab.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = EmeraldGreenPrimary,
                                selectedTextColor = EmeraldGreenPrimary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("nav_tab_${tab.name}")
                        )
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedGoalForDetail != null) {
                GoalDetailScreen(
                    goal = selectedGoalForDetail!!,
                    transactions = userTransactions,
                    onBackClick = { viewModel.selectGoalForDetail(null) },
                    onDepositClick = { viewModel.openDepositSheet(selectedGoalForDetail!!) },
                    onWithdrawClick = { viewModel.openWithdrawSheet(selectedGoalForDetail!!) },
                    onClaimInterestClick = { viewModel.claimInterest(selectedGoalForDetail!!.id) },
                    onDeleteGoalClick = { viewModel.deleteGoal(selectedGoalForDetail!!.id) },
                    onTransactionClick = { viewModel.openReceiptSheet(it) }
                )
            } else {
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tab_transition"
                ) { tab ->
                    when (tab) {
                        AppTab.DASHBOARD -> {
                            DashboardScreen(
                                user = currentUser,
                                goals = userGoals,
                                transactions = userTransactions,
                                isBalanceHidden = isBalanceHidden,
                                onToggleBalanceHidden = { viewModel.toggleBalanceHidden() },
                                onGoalClick = { viewModel.selectGoalForDetail(it) },
                                onQuickDepositClick = { viewModel.openDepositSheet(it) },
                                onCreateGoalClick = { viewModel.openCreateGoalDialog() },
                                onViewAllGoalsClick = { viewModel.setTab(AppTab.GOALS) },
                                onViewAllTransactionsClick = { viewModel.setTab(AppTab.HISTORY) },
                                onTransactionClick = { viewModel.openReceiptSheet(it) },
                                onProfileClick = { viewModel.openAuthProfileDialog() },
                                onNavigateTab = { viewModel.setTab(it) }
                            )
                        }

                        AppTab.GOALS -> {
                            GoalsListScreen(
                                goals = userGoals,
                                selectedCategory = selectedCategoryFilter,
                                onSelectCategory = { viewModel.setCategoryFilter(it) },
                                onGoalClick = { viewModel.selectGoalForDetail(it) },
                                onQuickDepositClick = { viewModel.openDepositSheet(it) },
                                onCreateGoalClick = { viewModel.openCreateGoalDialog() }
                            )
                        }

                        AppTab.ANALYTICS -> {
                            AnalyticsScreen(goals = userGoals)
                        }

                        AppTab.HISTORY -> {
                            TransactionsScreen(
                                transactions = userTransactions,
                                onTransactionClick = { viewModel.openReceiptSheet(it) }
                            )
                        }

                        AppTab.ADMIN -> {
                            AdminDashboardScreen(
                                adminMetrics = adminMetrics,
                                allUsers = allUsers,
                                allGoals = allPlatformGoals,
                                allTransactions = allPlatformTransactions,
                                onUpdateRates = { base, lock -> viewModel.adminUpdateRates(base, lock) },
                                onAdminCreditBonus = { gId, amt, note -> viewModel.adminCreditBonus(gId, amt, note) },
                                onSelectTransaction = { viewModel.openReceiptSheet(it) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Bottom Sheets and Dialogs
    if (showCreateGoalDialog) {
        CreateGoalDialog(
            onDismiss = { viewModel.closeCreateGoalDialog() },
            onGoalCreated = { title, cat, target, date, freq, locked, notes ->
                viewModel.createSavingsGoal(title, cat, target, date, freq, locked, notes)
            }
        )
    }

    if (goalForDeposit != null) {
        DepositBottomSheet(
            goal = goalForDeposit!!,
            onDismiss = { viewModel.closeDepositSheet() },
            onDepositConfirm = { amount, method, notes ->
                viewModel.makeDeposit(goalForDeposit!!.id, amount, method, notes)
            }
        )
    }

    if (goalForWithdraw != null) {
        WithdrawBottomSheet(
            goal = goalForWithdraw!!,
            onDismiss = { viewModel.closeWithdrawSheet() },
            onWithdrawConfirm = { amount, bank, acc ->
                viewModel.makeWithdrawal(goalForWithdraw!!.id, amount, bank, acc)
            }
        )
    }

    if (selectedTrxForReceipt != null) {
        ReceiptBottomSheet(
            transaction = selectedTrxForReceipt!!,
            onDismiss = { viewModel.closeReceiptSheet() }
        )
    }

    if (showAuthProfileDialog) {
        AuthProfileDialog(
            currentUser = currentUser,
            allUsers = allUsers,
            onDismiss = { viewModel.closeAuthProfileDialog() },
            onUserSelect = { viewModel.switchUser(it) },
            onRegisterNewUser = { name, email, phone, pin, isAdmin ->
                viewModel.registerNewAccount(name, email, phone, pin, isAdmin)
            }
        )
    }
}
