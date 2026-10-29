package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.GoalEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserEntity
import com.example.ui.components.TransactionItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.AdminPlatformMetrics
import com.example.util.DateUtils
import com.example.util.NairaFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    adminMetrics: AdminPlatformMetrics,
    allUsers: List<UserEntity>,
    allGoals: List<GoalEntity>,
    allTransactions: List<TransactionEntity>,
    onUpdateRates: (baseApy: Double, lockBonusApy: Double) -> Unit,
    onAdminCreditBonus: (goalId: Long, amount: Double, note: String) -> Unit,
    onSelectTransaction: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var baseRateText by remember(adminMetrics.baseInterestApy) { mutableStateOf(adminMetrics.baseInterestApy.toString()) }
    var lockBonusText by remember(adminMetrics.safelockBonusApy) { mutableStateOf(adminMetrics.safelockBonusApy.toString()) }

    var selectedGoalForBonus by remember { mutableStateOf<GoalEntity?>(null) }
    var bonusAmountText by remember { mutableStateOf("100000") }
    var bonusNote by remember { mutableStateOf("Admin Promotional Savings Match") }
    var showBonusDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Admin Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurfaceDark),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(NairaGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Central Vault Overseer",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Platform liquidity, rate governance & user audit trail",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.7f))
                        )
                    }
                }
            }
        }

        // Platform KPI Overview Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Platform Liquidity & Totals",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminMetricCard(
                        title = "Total Vault Pool",
                        value = NairaFormatter.formatNaira(adminMetrics.totalPlatformSavings),
                        subtitle = "Cumulative user balance",
                        color = EmeraldGreenPrimary,
                        container = EmeraldGreenLight,
                        modifier = Modifier.weight(1f)
                    )

                    AdminMetricCard(
                        title = "Active Users",
                        value = "${adminMetrics.totalUsersCount} Users",
                        subtitle = "100% BVN Verified",
                        color = Color(0xFF0284C7),
                        container = Color(0xFFE0F2FE),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminMetricCard(
                        title = "Total Savings Goals",
                        value = "${adminMetrics.totalGoalsCount} Goals",
                        subtitle = "Across all categories",
                        color = Color(0xFF7C3AED),
                        container = Color(0xFFEDE9FE),
                        modifier = Modifier.weight(1f)
                    )

                    AdminMetricCard(
                        title = "Ledger Records",
                        value = "${adminMetrics.totalTransactionsCount} TRX",
                        subtitle = "Audited & Cleared",
                        color = NairaGoldDark,
                        container = NairaGoldLight,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Interest Rate Governance Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Vault Yield Governance",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Configure interest rates for all savers",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Tune",
                            tint = EmeraldGreenPrimary
                        )
                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = baseRateText,
                            onValueChange = { baseRateText = it },
                            label = { Text("Base APY (%)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = lockBonusText,
                            onValueChange = { lockBonusText = it },
                            label = { Text("Lock Bonus (%)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Button(
                        onClick = {
                            val base = baseRateText.toDoubleOrNull() ?: 12.5
                            val lock = lockBonusText.toDoubleOrNull() ?: 3.5
                            onUpdateRates(base, lock)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("update_rates_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary)
                    ) {
                        Text("Apply Yield Updates Across Platform")
                    }
                }
            }
        }

        // Platform Users & Goal Credit Tools
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Savers Directory & Goal Incentives (${allUsers.size} Users)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                allUsers.forEach { user ->
                    val userGoals = allGoals.filter { it.userId == user.id }
                    val userTotalSaved = userGoals.sumOf { it.currentAmountNaira }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = user.fullName,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "${user.email} • Wema: ${user.virtualAccountNumber}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.sp
                                        )
                                    )
                                }

                                Text(
                                    text = NairaFormatter.formatNaira(userTotalSaved),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldGreenPrimary
                                    )
                                )
                            }

                            if (userGoals.isNotEmpty()) {
                                HorizontalDivider()
                                Text(
                                    text = "Active Goals (${userGoals.size}):",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )

                                userGoals.forEach { g ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${g.title} (${NairaFormatter.formatNairaCompact(g.currentAmountNaira)} / ${NairaFormatter.formatNairaCompact(g.targetAmountNaira)})",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                                        )

                                        OutlinedButton(
                                            onClick = {
                                                selectedGoalForBonus = g
                                                showBonusDialog = true
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text(
                                                text = "+ Credit Bonus",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Real-time Platform Audit Stream
        item {
            Text(
                text = "Central Audit Stream (${allTransactions.size} Records)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(allTransactions.take(8), key = { it.id }) { trx ->
            TransactionItem(
                transaction = trx,
                onClick = { onSelectTransaction(trx) }
            )
        }
    }

    // Admin Bonus Dialog
    if (showBonusDialog && selectedGoalForBonus != null) {
        AlertDialog(
            onDismissRequest = { showBonusDialog = false },
            title = { Text("Credit Admin Matching Bonus") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Target: ${selectedGoalForBonus?.title}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    OutlinedTextField(
                        value = bonusAmountText,
                        onValueChange = { bonusAmountText = it },
                        label = { Text("Bonus Amount (₦)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = bonusNote,
                        onValueChange = { bonusNote = it },
                        label = { Text("Bonus Note") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = bonusAmountText.toDoubleOrNull() ?: 0.0
                        if (amount > 0 && selectedGoalForBonus != null) {
                            onAdminCreditBonus(selectedGoalForBonus!!.id, amount, bonusNote)
                            showBonusDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary)
                ) {
                    Text("Credit ₦$bonusAmountText")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBonusDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun AdminMetricCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    container: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = container)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = color
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.sp,
                    color = color.copy(alpha = 0.8f)
                )
            )
        }
    }
}
