package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.TransactionType
import com.example.ui.theme.*
import com.example.util.DateUtils
import com.example.util.NairaFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptBottomSheet(
    transaction: TransactionEntity,
    onDismiss: () -> Unit
) {
    val isCredit = transaction.transactionType != TransactionType.WITHDRAWAL.name

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Success Check Badge
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(EmeraldGreenLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = EmeraldGreenPrimary,
                    modifier = Modifier.size(36.dp)
                )
            }

            Text(
                text = "Transaction Receipt",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            Text(
                text = "NairaSave Official Vault Ledger",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )

            // Amount Display
            Text(
                text = (if (isCredit) "+" else "-") + NairaFormatter.formatNaira(transaction.amountNaira),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isCredit) EmeraldGreenPrimary else StatusAlertRed
                )
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // Receipt Metadata Box
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurfaceCardLight),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ReceiptRow(label = "Reference Code", value = transaction.referenceCode, isHighlight = true)
                    ReceiptRow(label = "Goal Target", value = transaction.goalTitle)
                    ReceiptRow(label = "Transaction Type", value = transaction.transactionType)
                    ReceiptRow(label = "Payment Channel", value = transaction.paymentMethod.replace("_", " "))
                    ReceiptRow(label = "Date & Time", value = DateUtils.formatDateTime(transaction.timestampMillis))
                    ReceiptRow(label = "Status", value = transaction.status, isStatus = true)
                    if (transaction.notes.isNotBlank()) {
                        ReceiptRow(label = "Description / Note", value = transaction.notes)
                    }
                }
            }

            // Central Bank / NDIC security note
            Text(
                text = "🛡️ Secured by 256-bit encryption & Bank-grade NDIC Insured Vault protocols.",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            // Dismiss Button
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("close_receipt_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary)
            ) {
                Text(
                    text = "Close Receipt",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ReceiptRow(
    label: String,
    value: String,
    isHighlight: Boolean = false,
    isStatus: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
        if (isStatus) {
            Surface(
                color = StatusActiveGreenBg,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = StatusActiveGreen,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (isHighlight) EmeraldGreenPrimary else MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 1
            )
        }
    }
}
