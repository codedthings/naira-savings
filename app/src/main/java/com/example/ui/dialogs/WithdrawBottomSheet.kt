package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Warning
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
import com.example.data.local.entity.GoalEntity
import com.example.ui.theme.*
import com.example.util.NairaFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithdrawBottomSheet(
    goal: GoalEntity,
    onDismiss: () -> Unit,
    onWithdrawConfirm: (amount: Double, bankName: String, accountNumber: String) -> Unit
) {
    var amountText by remember { mutableStateOf(goal.currentAmountNaira.toLong().toString()) }
    var selectedBank by remember { mutableStateOf("GTBank (Guaranty Trust)") }
    var accountNumber by remember { mutableStateOf("0123456789") }
    var isProcessing by remember { mutableStateOf(false) }

    val amount = NairaFormatter.parseNairaInput(amountText)
    val maxAvailable = goal.currentAmountNaira
    val isAmountValid = amount > 0 && amount <= maxAvailable

    val banks = listOf(
        "GTBank (Guaranty Trust)",
        "Access Bank",
        "Zenith Bank",
        "Kuda Microfinance Bank",
        "OPay Digital Services",
        "PalmPay",
        "First Bank of Nigeria",
        "United Bank for Africa (UBA)",
        "Stanbic IBTC"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Withdraw / Payout",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "From: ${goal.title}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = EmeraldGreenPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Surface(
                    color = EmeraldGreenLight,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Balance: ${NairaFormatter.formatNairaCompact(maxAvailable)}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreenDark
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            if (goal.isLocked && !goal.isCompleted) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = StatusLockedGoldBg),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = StatusLockedGold
                        )
                        Text(
                            text = "This is a SafeLocked goal. Target date has not matured, but early withdrawal is permitted in full.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = StatusLockedGold,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            // Amount Input
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Withdrawal Amount (₦)") },
                prefix = {
                    Text("₦ ", fontWeight = FontWeight.Bold, color = EmeraldGreenPrimary, fontSize = 18.sp)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("withdraw_amount_field"),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                isError = amount > maxAvailable,
                supportingText = {
                    if (amount > maxAvailable) {
                        Text("Amount exceeds available balance of ${NairaFormatter.formatNaira(maxAvailable)}", color = StatusAlertRed)
                    } else {
                        Text("Available to withdraw: ${NairaFormatter.formatNaira(maxAvailable)}")
                    }
                }
            )

            // Select Bank
            Text(
                text = "Destination Commercial Bank",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )

            var bankDropdownExpanded by remember { mutableStateOf(false) }

            Box {
                OutlinedTextField(
                    value = selectedBank,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Nigerian Bank") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.AccountBalance, contentDescription = "Bank", tint = EmeraldGreenPrimary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { bankDropdownExpanded = true }
                        .testTag("bank_selector"),
                    shape = RoundedCornerShape(14.dp)
                )

                DropdownMenu(
                    expanded = bankDropdownExpanded,
                    onDismissRequest = { bankDropdownExpanded = false }
                ) {
                    banks.forEach { bank ->
                        DropdownMenuItem(
                            text = { Text(bank) },
                            onClick = {
                                selectedBank = bank
                                bankDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Account Number
            OutlinedTextField(
                value = accountNumber,
                onValueChange = { if (it.length <= 10) accountNumber = it },
                label = { Text("10-Digit NUBAN Account Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("account_number_field"),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            // Account Name resolution indicator
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurfaceCardLight),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = "Verified",
                        tint = StatusActiveGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Verified NUBAN: CHINEDU OKAFOR",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = StatusActiveGreen
                        )
                    )
                }
            }

            // Confirm Withdrawal Button
            Button(
                onClick = {
                    if (isAmountValid) {
                        isProcessing = true
                        onWithdrawConfirm(amount, selectedBank, accountNumber)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("confirm_withdraw_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary),
                enabled = isAmountValid && accountNumber.length == 10 && !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Text(
                        text = "Withdraw ${NairaFormatter.formatNaira(amount)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
