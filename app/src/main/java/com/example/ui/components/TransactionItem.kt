package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.TransactionType
import com.example.ui.theme.*
import com.example.util.DateUtils
import com.example.util.NairaFormatter

@Composable
fun TransactionItem(
    transaction: TransactionEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCredit = transaction.transactionType != TransactionType.WITHDRAWAL.name
    val iconVector: ImageVector = when (transaction.transactionType) {
        TransactionType.DEPOSIT.name -> Icons.Default.ArrowDownward
        TransactionType.WITHDRAWAL.name -> Icons.Default.ArrowUpward
        TransactionType.INTEREST.name -> Icons.Default.AutoGraph
        TransactionType.BONUS.name -> Icons.Default.CardGiftcard
        else -> Icons.Default.Receipt
    }

    val iconColor = when (transaction.transactionType) {
        TransactionType.DEPOSIT.name -> EmeraldGreenPrimary
        TransactionType.WITHDRAWAL.name -> StatusAlertRed
        TransactionType.INTEREST.name -> NairaGold
        TransactionType.BONUS.name -> Color(0xFF7C3AED)
        else -> MaterialTheme.colorScheme.primary
    }

    val iconBg = when (transaction.transactionType) {
        TransactionType.DEPOSIT.name -> EmeraldGreenLight
        TransactionType.WITHDRAWAL.name -> StatusAlertRedBg
        TransactionType.INTEREST.name -> NairaGoldLight
        TransactionType.BONUS.name -> Color(0xFFEDE9FE)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("trx_item_${transaction.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = transaction.transactionType,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = transaction.goalTitle,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )
                    Text(
                        text = "${transaction.paymentMethod.replace("_", " ")} • ${DateUtils.formatDate(transaction.timestampMillis)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        ),
                        maxLines = 1
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = (if (isCredit) "+" else "-") + NairaFormatter.formatNaira(transaction.amountNaira),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isCredit) EmeraldGreenPrimary else StatusAlertRed
                    )
                )

                Surface(
                    color = StatusActiveGreenBg,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = transaction.status,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = StatusActiveGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
