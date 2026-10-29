package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.GoalEntity
import com.example.ui.components.CategoryDistributionProgress
import com.example.ui.components.MonthlySavingsBar
import com.example.ui.components.MonthlySavingsVelocityChart
import com.example.ui.theme.*
import com.example.util.NairaFormatter

@Composable
fun AnalyticsScreen(
    goals: List<GoalEntity>,
    modifier: Modifier = Modifier
) {
    val totalSaved = goals.sumOf { it.currentAmountNaira }
    val totalTarget = goals.sumOf { it.targetAmountNaira }
    val overallPercent = if (totalTarget > 0) ((totalSaved / totalTarget) * 100).toInt() else 0
    val totalInterest = goals.sumOf { it.totalInterestEarned }

    val monthlyData = listOf(
        MonthlySavingsBar("May", 850000.0, 1000000.0),
        MonthlySavingsBar("Jun", 1400000.0, 1200000.0),
        MonthlySavingsBar("Jul", 1950000.0, 1500000.0),
        MonthlySavingsBar("Aug", 2600000.0, 2000000.0)
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Savings Intelligence",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Real-time analytics and predictive velocity in Nigerian Naira",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }

        // Aggregate Metrics 3-Card Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnalyticsStatCard(
                    title = "Total Saved",
                    value = NairaFormatter.formatNairaCompact(totalSaved),
                    subtitle = "$overallPercent% of target",
                    color = EmeraldGreenPrimary,
                    container = EmeraldGreenLight,
                    modifier = Modifier.weight(1f)
                )

                AnalyticsStatCard(
                    title = "Target Pool",
                    value = NairaFormatter.formatNairaCompact(totalTarget),
                    subtitle = "${goals.size} Active Goals",
                    color = Color(0xFF0284C7),
                    container = Color(0xFFE0F2FE),
                    modifier = Modifier.weight(1f)
                )

                AnalyticsStatCard(
                    title = "Total Yield",
                    value = "+" + NairaFormatter.formatNairaCompact(totalInterest),
                    subtitle = "14.5% avg APY",
                    color = NairaGoldDark,
                    container = NairaGoldLight,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Monthly Savings Velocity Chart
        item {
            MonthlySavingsVelocityChart(data = monthlyData)
        }

        // Category Portfolio Breakdown
        if (goals.isNotEmpty()) {
            item {
                CategoryDistributionProgress(goals = goals)
            }
        }

        // Financial Health & SafeLock Insight
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(EmeraldGreenLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = "Growth",
                                tint = EmeraldGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Naira Inflation Shield",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "High-yield compounding outpaces inflation",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }

                    HorizontalDivider()

                    Text(
                        text = "By locking ₦${NairaFormatter.formatNaira(goals.filter { it.isLocked }.sumOf { it.currentAmountNaira })} in SafeLock targets, you are earning an estimated ₦${NairaFormatter.formatNaira((goals.filter { it.isLocked }.sumOf { it.currentAmountNaira } * 0.145) / 12)} every month automatically.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 20.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun AnalyticsStatCard(
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
            modifier = Modifier.padding(12.dp),
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
                ),
                maxLines = 1
            )
        }
    }
}
