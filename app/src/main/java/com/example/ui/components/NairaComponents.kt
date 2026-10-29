package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.GoalCategory
import com.example.ui.theme.*
import com.example.util.NairaFormatter

@Composable
fun PresetNairaChips(
    selectedAmount: Double,
    onAmountSelected: (Double) -> Unit,
    modifier: Modifier = Modifier,
    amounts: List<Double> = listOf(5000.0, 10000.0, 20000.0, 50000.0, 100000.0, 250000.0, 500000.0, 1000000.0)
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
    ) {
        items(amounts) { amount ->
            val isSelected = selectedAmount == amount
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onAmountSelected(amount) }
                    .testTag("chip_${amount.toLong()}"),
                color = if (isSelected) EmeraldGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(20.dp),
                border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)) else null
            ) {
                Text(
                    text = "+" + NairaFormatter.formatNairaCompact(amount),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryIcon(
    iconName: String,
    tint: Color = EmeraldGreenPrimary,
    modifier: Modifier = Modifier
) {
    val vector: ImageVector = when (iconName) {
        "Home" -> Icons.Default.Home
        "Security" -> Icons.Default.Shield
        "FlightTakeoff" -> Icons.Default.FlightTakeoff
        "Devices" -> Icons.Default.Devices
        "Storefront" -> Icons.Default.Storefront
        "Celebration" -> Icons.Default.Celebration
        "Favorite" -> Icons.Default.Favorite
        "DirectionsCar" -> Icons.Default.DirectionsCar
        "TrendingUp" -> Icons.Default.TrendingUp
        "School" -> Icons.Default.School
        else -> Icons.Default.Savings
    }

    Icon(
        imageVector = vector,
        contentDescription = iconName,
        tint = tint,
        modifier = modifier
    )
}

@Composable
fun SafeLockBadge(
    isLocked: Boolean,
    modifier: Modifier = Modifier
) {
    if (isLocked) {
        Row(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .background(StatusLockedGoldBg)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                tint = StatusLockedGold,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = "SafeLocked (High APY)",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = StatusLockedGold,
                    fontSize = 11.sp
                )
            )
        }
    } else {
        Row(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .background(StatusActiveGreenBg)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LockOpen,
                contentDescription = "Flexible",
                tint = StatusActiveGreen,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = "Flex Savings",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = StatusActiveGreen,
                    fontSize = 11.sp
                )
            )
        }
    }
}
