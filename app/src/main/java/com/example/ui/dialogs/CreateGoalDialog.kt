package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.GoalCategory
import com.example.data.model.SavingsFrequency
import com.example.ui.components.CategoryIcon
import com.example.ui.components.PresetNairaChips
import com.example.ui.theme.*
import com.example.util.DateUtils
import com.example.util.NairaFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGoalDialog(
    onDismiss: () -> Unit,
    onGoalCreated: (
        title: String,
        category: GoalCategory,
        targetAmount: Double,
        targetDateMillis: Long,
        frequency: SavingsFrequency,
        isLocked: Boolean,
        notes: String
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(GoalCategory.RENT) }
    var targetAmountText by remember { mutableStateOf("1000000") }
    var selectedFrequency by remember { mutableStateOf(SavingsFrequency.MONTHLY) }
    var isSafeLocked by remember { mutableStateOf(true) }
    var targetDaysAhead by remember { mutableStateOf(90) } // Default 3 months
    var notes by remember { mutableStateOf("") }

    val targetAmount = NairaFormatter.parseNairaInput(targetAmountText)
    val targetDateMillis = System.currentTimeMillis() + (targetDaysAhead * 86400000L)

    val plan = remember(targetAmount, targetDateMillis) {
        NairaFormatter.calculateSavingsPlan(
            targetAmount = targetAmount,
            currentAmount = 0.0,
            targetDateMillis = targetDateMillis
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Create Savings Target",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Target in Nigerian Naira (₦)",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_create_goal")) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Category Selection
                    Text(
                        text = "Select Category",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(GoalCategory.entries) { cat ->
                            val isSelected = selectedCategory == cat
                            val catColor = Color(cat.defaultColor)
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable {
                                        selectedCategory = cat
                                        if (title.isBlank() || GoalCategory.entries.any { it.title == title }) {
                                            title = cat.title
                                        }
                                    }
                                    .testTag("cat_chip_${cat.name}"),
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
                                        modifier = Modifier.size(16.dp)
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

                    // Goal Title Field
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Goal Title") },
                        placeholder = { Text("e.g. Lekki Rent 2026, UK Visa Fund") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("goal_title_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    // Target Amount in Naira
                    Column {
                        OutlinedTextField(
                            value = targetAmountText,
                            onValueChange = { targetAmountText = it },
                            label = { Text("Target Amount (₦)") },
                            prefix = { Text("₦ ", fontWeight = FontWeight.Bold, color = EmeraldGreenPrimary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("target_amount_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        PresetNairaChips(
                            selectedAmount = targetAmount,
                            onAmountSelected = { targetAmountText = it.toLong().toString() }
                        )
                    }

                    // Target Duration Presets
                    Column {
                        Text(
                            text = "Target Timeline",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                30 to "1 Month",
                                90 to "3 Months",
                                180 to "6 Months",
                                365 to "1 Year"
                            ).forEach { (days, label) ->
                                val isSelected = targetDaysAhead == days
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { targetDaysAhead = days },
                                    color = if (isSelected) EmeraldGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        ),
                                        modifier = Modifier.padding(vertical = 10.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Target Date: ${DateUtils.formatDate(targetDateMillis)} (${DateUtils.getDaysLeftText(targetDateMillis)})",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }

                    // Savings Plan Calculation Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = EmeraldGreenLight),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "💡 Smart Target Breakdown",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldGreenDark
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Daily Save",
                                        style = MaterialTheme.typography.labelSmall.copy(color = EmeraldGreenDark)
                                    )
                                    Text(
                                        text = NairaFormatter.formatNaira(plan.dailyAmount),
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Weekly Save",
                                        style = MaterialTheme.typography.labelSmall.copy(color = EmeraldGreenDark)
                                    )
                                    Text(
                                        text = NairaFormatter.formatNaira(plan.weeklyAmount),
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Monthly Save",
                                        style = MaterialTheme.typography.labelSmall.copy(color = EmeraldGreenDark)
                                    )
                                    Text(
                                        text = NairaFormatter.formatNaira(plan.monthlyAmount),
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }

                    // SafeLock Switch
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSafeLocked) NairaGoldLight else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Lock",
                                    tint = if (isSafeLocked) NairaGoldDark else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Column {
                                    Text(
                                        text = "SafeLock & Boost APY",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = if (isSafeLocked) "Earns 14.5% p.a. (Locks till target date)" else "Flexible savings @ 12.0% p.a.",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                                    )
                                }
                            }

                            Switch(
                                checked = isSafeLocked,
                                onCheckedChange = { isSafeLocked = it },
                                modifier = Modifier.testTag("safelock_switch")
                            )
                        }
                    }

                    // Frequency
                    Column {
                        Text(
                            text = "Auto-Save Frequency",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            SavingsFrequency.entries.forEach { freq ->
                                val isSelected = selectedFrequency == freq
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { selectedFrequency = freq },
                                    color = if (isSelected) EmeraldGreenPrimary else MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = freq.name.take(4),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        ),
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    // Optional Notes
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Personal Notes (Optional)") },
                        placeholder = { Text("e.g. Due before landlord vacation notice") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Create Goal Button
                Button(
                    onClick = {
                        val finalTitle = if (title.isNotBlank()) title else selectedCategory.title
                        if (targetAmount > 0) {
                            onGoalCreated(
                                finalTitle,
                                selectedCategory,
                                targetAmount,
                                targetDateMillis,
                                selectedFrequency,
                                isSafeLocked,
                                notes
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_create_goal_btn"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary)
                ) {
                    Text(
                        text = "Create Savings Target (₦${NairaFormatter.formatNaira(targetAmount)})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}
