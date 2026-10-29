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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
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
import com.example.data.model.GoalCategory
import com.example.ui.components.CategoryIcon
import com.example.ui.components.GoalCard
import com.example.ui.theme.*
import com.example.util.NairaFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsListScreen(
    goals: List<GoalEntity>,
    selectedCategory: GoalCategory?,
    onSelectCategory: (GoalCategory?) -> Unit,
    onGoalClick: (GoalEntity) -> Unit,
    onQuickDepositClick: (GoalEntity) -> Unit,
    onCreateGoalClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusTab by remember { mutableStateOf(0) } // 0: All, 1: Active, 2: SafeLocked, 3: Completed

    val filteredGoals = goals.filter { goal ->
        val matchesCategory = selectedCategory == null || goal.category == selectedCategory.name
        val matchesSearch = searchQuery.isBlank() || goal.title.contains(searchQuery, ignoreCase = true) || goal.category.contains(searchQuery, ignoreCase = true)
        val matchesStatus = when (selectedStatusTab) {
            1 -> !goal.isCompleted && !goal.isLocked
            2 -> goal.isLocked && !goal.isCompleted
            3 -> goal.isCompleted
            else -> true
        }
        matchesCategory && matchesSearch && matchesStatus
    }

    val totalSaved = filteredGoals.sumOf { it.currentAmountNaira }
    val totalTarget = filteredGoals.sumOf { it.targetAmountNaira }
    val overallPercent = if (totalTarget > 0) ((totalSaved / totalTarget) * 100).toInt() else 0

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateGoalClick,
                containerColor = EmeraldGreenPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .padding(bottom = 70.dp)
                    .testTag("fab_create_goal")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create Goal")
                Spacer(modifier = Modifier.width(6.dp))
                Text("New Target", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            item {
                Column {
                    Text(
                        text = "Savings Targets",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Manage and fund your individual Naira savings buckets",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }

            // Summary Aggregate Box
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Filtered Targets Balance",
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Text(
                                text = NairaFormatter.formatNaira(totalSaved),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldGreenPrimary
                                )
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Target: ${NairaFormatter.formatNairaCompact(totalTarget)}",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Surface(
                                color = EmeraldGreenLight,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "$overallPercent% Reached",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = EmeraldGreenDark,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search targets (e.g. Rent, Tuition, Gadgets)") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = EmeraldGreenPrimary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("goals_search_field"),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
            }

            // Status Tabs (All, Active, SafeLocked, Completed)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("All (${goals.size})", "Flex", "SafeLocked", "Completed").forEachIndexed { index, label ->
                        val isSelected = selectedStatusTab == index
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedStatusTab = index },
                            color = if (isSelected) EmeraldGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Category Filter Chips Row
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    item {
                        val isAllSelected = selectedCategory == null
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onSelectCategory(null) },
                            color = if (isAllSelected) EmeraldGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = "All Categories",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isAllSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }

                    items(GoalCategory.entries) { cat ->
                        val isSelected = selectedCategory == cat
                        val catColor = Color(cat.defaultColor)
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onSelectCategory(if (isSelected) null else cat) }
                                .testTag("filter_cat_${cat.name}"),
                            color = if (isSelected) catColor else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                CategoryIcon(
                                    iconName = cat.iconName,
                                    tint = if (isSelected) Color.White else catColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = cat.title,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Goals List
            if (filteredGoals.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "🎯", fontSize = 36.sp)
                            Text(
                                text = "No matching goals found",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Try clearing your filters or create a new savings target.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                }
            } else {
                items(filteredGoals, key = { it.id }) { goal ->
                    GoalCard(
                        goal = goal,
                        onClick = { onGoalClick(goal) },
                        onQuickDepositClick = { onQuickDepositClick(goal) }
                    )
                }
            }
        }
    }
}
