package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CardType
import com.example.data.TransitCard
import com.example.ui.theme.SubeBluePrimary
import com.example.ui.theme.SubeCyanAccent
import com.example.ui.theme.SubeMintSuccess
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SubeCardVisual(
    card: TransitCard,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "AR")).apply {
        maximumFractionDigits = 2
    }

    val isNegativeBalance = card.balance < 0
    val cardGradient = if (isNegativeBalance) {
        Brush.horizontalGradient(
            colors = listOf(Color(0xFF7F1D1D), Color(0xFF991B1B))
        )
    } else {
        when (card.cardType) {
            CardType.SUBE -> Brush.horizontalGradient(
                colors = listOf(Color(0xFF0284C7), Color(0xFF0369A1), Color(0xFF0F172A))
            )
            CardType.BIP -> Brush.horizontalGradient(
                colors = listOf(Color(0xFF059669), Color(0xFF047857), Color(0xFF064E3B))
            )
            CardType.METRO_CDMX -> Brush.horizontalGradient(
                colors = listOf(Color(0xFFD97706), Color(0xFFB45309), Color(0xFF78350F))
            )
            else -> Brush.horizontalGradient(
                colors = listOf(Color(0xFF4F46E5), Color(0xFF4338CA), Color(0xFF312E81))
            )
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("sube_card_visual_${card.cardId}")
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardGradient)
                .padding(20.dp)
        ) {
            Column {
                // Header Row: Card Name & NFC Wave Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = card.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            if (card.isPrimary) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = SubeCyanAccent.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "Principal",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SubeCyanAccent,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Text(
                            text = card.cardType.label,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Contactless,
                        contentDescription = "NFC Contactless Wave",
                        tint = SubeCyanAccent,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Middle Row: Chip graphic & Card Number
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Smart Chip Graphic
                    Box(
                        modifier = Modifier
                            .size(36.dp, 28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFACC15))
                            .border(1.dp, Color(0xFFCA8A04), RoundedCornerShape(6.dp))
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = formatCardNumber(card.cardId),
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White.copy(alpha = 0.9f),
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Bottom Row: Saldo Actual & Pending Accreditation Chip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "SALDO DISPONIBLE",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = currencyFormat.format(card.balance),
                            style = MaterialTheme.typography.headlineMedium,
                            color = if (isNegativeBalance) Color(0xFFFECACA) else Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    if (card.pendingAccreditationAmount > 0) {
                        Surface(
                            color = SubeMintSuccess,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Nfc,
                                    contentDescription = "Pendiente Acreditar",
                                    tint = Color.Black,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "+${currencyFormat.format(card.pendingAccreditationAmount)} Acreditar",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else if (isNegativeBalance) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Saldo Negativo",
                                tint = Color(0xFFFCA5A5),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Límite: ${currencyFormat.format(card.emergencyBalanceLimit)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFFCA5A5)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatCardNumber(cardId: String): String {
    val digits = cardId.replace(" ", "")
    return digits.chunked(4).joinToString(" ")
}
