package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.GoalEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.UserRole
import com.example.ui.components.CategoryDistributionProgress
import com.example.ui.components.GoalCard
import com.example.ui.components.MonthlySavingsBar
import com.example.ui.components.MonthlySavingsVelocityChart
import com.example.ui.components.TransactionItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppTab
import com.example.util.DateUtils
import com.example.util.NairaFormatter

@Composable
fun DashboardScreen(
    user: UserEntity?,
    goals: List<GoalEntity>,
    transactions: List<TransactionEntity>,
    isBalanceHidden: Boolean,
    onToggleBalanceHidden: () -> Unit,
    onGoalClick: (GoalEntity) -> Unit,
    onQuickDepositClick: (GoalEntity) -> Unit,
    onCreateGoalClick: () -> Unit,
    onViewAllGoalsClick: () -> Unit,
    onViewAllTransactionsClick: () -> Unit,
    onTransactionClick: (TransactionEntity) -> Unit,
    onProfileClick: () -> Unit,
    onNavigateTab: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalSaved = goals.sumOf { it.currentAmountNaira }
    val totalTarget = goals.sumOf { it.targetAmountNaira }
    val safelockedAmount = goals.filter { it.isLocked }.sumOf { it.currentAmountNaira }
    val flexAmount = goals.filter { !it.isLocked }.sumOf { it.currentAmountNaira }
    val totalInterest = goals.sumOf { it.totalInterestEarned }

    val recentTransactions = transactions.take(4)

    // Velocity data for last 4 months
    val monthlyData = remember(transactions) {
        listOf(
            MonthlySavingsBar("May", 850000.0, 1000000.0),
            MonthlySavingsBar("Jun", 1400000.0, 1200000.0),
            MonthlySavingsBar("Jul", 1950000.0, 1500000.0),
            MonthlySavingsBar("Aug", 2600000.0, 2000000.0)
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Top Bar: Profile Greeting + BVN Verified Badge + Profile Switcher
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(
                                    if (user?.role == UserRole.ADMIN.name) NairaGold else EmeraldGreenPrimary
                                )
                                .clickable { onProfileClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (user?.role == UserRole.ADMIN.name) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = user?.fullName ?: "Chinedu Okafor",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                if (user?.bvnVerified == true) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "BVN Verified",
                                        tint = StatusActiveGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Wema Acc: ${user?.virtualAccountNumber ?: "9901847291"}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onProfileClick() }
                            .testTag("switch_account_btn"),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Switch",
                                tint = EmeraldGreenPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Profiles",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldGreenPrimary
                                )
                            )
                        }
                    }
                }
            }
        }

        // 2. Net Worth & Total Savings Hero Card in Emerald Green & Gold Glow
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(EmeraldGreenDark, EmeraldGreenPrimary, Color(0xFF004D40))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Total Vault Balance",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                                IconButton(
                                    onClick = onToggleBalanceHidden,
                                    modifier = Modifier.size(24.dp).testTag("toggle_balance_eye")
                                ) {
                                    Icon(
                                        imageVector = if (isBalanceHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle Balance",
                                        tint = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Surface(
                                color = NairaGold.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoGraph,
                                        contentDescription = "APY",
                                        tint = NairaGoldLight,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Up to 15% APY",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = NairaGoldLight,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }

                        // Main Naira Amount
                        Text(
                            text = if (isBalanceHidden) "₦ ••••••••••" else NairaFormatter.formatNaira(totalSaved, includeDecimals = true),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 30.sp
                            )
                        )

                        HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

                        // SafeLock vs Flex Savings Pill Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "SafeLock (Locked)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                )
                                Text(
                                    text = if (isBalanceHidden) "••••••" else NairaFormatter.formatNairaCompact(safelockedAmount),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = NairaGoldLight,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Column {
                                Text(
                                    text = "Flex Savings",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                )
                                Text(
                                    text = if (isBalanceHidden) "••••••" else NairaFormatter.formatNairaCompact(flexAmount),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = MintGreenAccent,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Total Yield Earned",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                )
                                Text(
                                    text = if (isBalanceHidden) "••••••" else "+" + NairaFormatter.formatNairaCompact(totalInterest),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Quick Action Buttons
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Default.AddCircle,
                    label = "New Target",
                    tint = EmeraldGreenPrimary,
                    container = EmeraldGreenLight,
                    onClick = onCreateGoalClick,
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    icon = Icons.Default.Savings,
                    label = "All Goals",
                    tint = Color(0xFF0284C7),
                    container = Color(0xFFE0F2FE),
                    onClick = onViewAllGoalsClick,
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    icon = Icons.Default.BarChart,
                    label = "Analytics",
                    tint = Color(0xFF7C3AED),
                    container = Color(0xFFEDE9FE),
                    onClick = { onNavigateTab(AppTab.ANALYTICS) },
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    icon = Icons.Default.ReceiptLong,
                    label = "History",
                    tint = NairaGoldDark,
                    container = NairaGoldLight,
                    onClick = onViewAllTransactionsClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 4. Savings Streak & Motivation Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NairaGoldLight)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(NairaGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🔥", fontSize = 20.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "14-Day Savings Streak! 🇳🇬",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = NairaGoldDark
                            )
                        )
                        Text(
                            text = "You're consistently saving towards your Lekki Rent & Japa plans.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF78350F),
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }

        // 5. Active Goals Header + Goal Cards
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Active Savings Targets",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${goals.size} targets running in Naira",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }

                    TextButton(onClick = onViewAllGoalsClick) {
                        Text(
                            text = "View All",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = EmeraldGreenPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                if (goals.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "🌱", fontSize = 32.sp)
                            Text(
                                text = "No savings goals yet",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Create your first goal for rent, japa, or emergency fund.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(
                                onClick = onCreateGoalClick,
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary)
                            ) {
                                Text("+ Create Target")
                            }
                        }
                    }
                } else {
                    goals.forEach { goal ->
                        GoalCard(
                            goal = goal,
                            onClick = { onGoalClick(goal) },
                            onQuickDepositClick = { onQuickDepositClick(goal) }
                        )
                    }
                }
            }
        }

        // 6. Monthly Savings Chart
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                MonthlySavingsVelocityChart(data = monthlyData)
            }
        }

        // 7. Portfolio Distribution
        if (goals.isNotEmpty()) {
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    CategoryDistributionProgress(goals = goals)
                }
            }
        }

        // 8. Recent Transactions Preview
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Transactions",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    TextButton(onClick = onViewAllTransactionsClick) {
                        Text(
                            text = "See All",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = EmeraldGreenPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                recentTransactions.forEach { trx ->
                    TransactionItem(
                        transaction = trx,
                        onClick = { onTransactionClick(trx) }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    tint: Color,
    container: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = container,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 11.sp
                ),
                maxLines = 1
            )
        }
    }
}
