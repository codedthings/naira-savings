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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.GoalEntity
import com.example.data.model.PaymentMethod
import com.example.ui.components.PresetNairaChips
import com.example.ui.theme.*
import com.example.util.NairaFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepositBottomSheet(
    goal: GoalEntity,
    onDismiss: () -> Unit,
    onDepositConfirm: (amount: Double, method: PaymentMethod, notes: String) -> Unit
) {
    var amountText by remember { mutableStateOf("50000") }
    var selectedMethod by remember { mutableStateOf(PaymentMethod.BANK_TRANSFER) }
    var notes by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }

    val amount = NairaFormatter.parseNairaInput(amountText)
    val remaining = (goal.targetAmountNaira - goal.currentAmountNaira).coerceAtLeast(0.0)

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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Add Deposit",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "To: ${goal.title}",
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
                        text = "Target: ${NairaFormatter.formatNairaCompact(goal.targetAmountNaira)}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreenDark
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            // Amount Input Field
            Column {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Deposit Amount (₦)") },
                    prefix = {
                        Text("₦ ", fontWeight = FontWeight.Bold, color = EmeraldGreenPrimary, fontSize = 18.sp)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("deposit_amount_field"),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(6.dp))

                PresetNairaChips(
                    selectedAmount = amount,
                    onAmountSelected = { amountText = it.toLong().toString() }
                )
            }

            // Payment Methods
            Text(
                text = "Select Payment Channel",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    Triple(PaymentMethod.BANK_TRANSFER, Icons.Default.AccountBalance, "Instant Dedicated Wema Bank Transfer (Zero Fee)"),
                    Triple(PaymentMethod.DEBIT_CARD, Icons.Default.CreditCard, "Nigerian Debit Card (Mastercard / Visa / Verve)"),
                    Triple(PaymentMethod.USSD, Icons.Default.PhoneIphone, "Bank USSD (*737#, *894#, *966#)"),
                    Triple(PaymentMethod.AUTO_DEBIT, Icons.Default.Sync, "Smart Automated Daily / Weekly Direct Debit")
                ).forEach { (method, icon, desc) ->
                    val isSelected = selectedMethod == method
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { selectedMethod = method }
                            .testTag("payment_method_${method.name}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) EmeraldGreenLight else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, EmeraldGreenPrimary) else null,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) EmeraldGreenPrimary else MaterialTheme.colorScheme.surface),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = method.displayName,
                                    tint = if (isSelected) Color.White else EmeraldGreenPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = method.displayName,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = desc,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                )
                            }

                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedMethod = method },
                                colors = RadioButtonDefaults.colors(selectedColor = EmeraldGreenPrimary)
                            )
                        }
                    }
                }
            }

            // Virtual Account info if Bank Transfer selected
            if (selectedMethod == PaymentMethod.BANK_TRANSFER) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateSurfaceCardLight),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🏦 Virtual Account Transfer",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Active",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = StatusActiveGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Account: 9901847291 • Wema Bank",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Beneficiary: NairaSave / Chinedu Okafor",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }

            // Optional Note
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Deposit Note (Optional)") },
                placeholder = { Text("e.g. November salary allocation") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Submit Button
            Button(
                onClick = {
                    if (amount > 0) {
                        isProcessing = true
                        onDepositConfirm(amount, selectedMethod, notes)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("confirm_deposit_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary),
                enabled = amount > 0 && !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Text(
                        text = "Pay ${NairaFormatter.formatNaira(amount)}",
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
