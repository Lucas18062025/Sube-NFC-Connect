package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsSubway
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.Transaction
import com.example.data.TransactionType
import com.example.data.TransitCard
import com.example.ui.components.SubeCardVisual
import com.example.ui.theme.SubeBluePrimary
import com.example.ui.theme.SubeCyanAccent
import com.example.ui.theme.SubeMintSuccess
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    cards: List<TransitCard>,
    selectedCard: TransitCard?,
    recentTransactions: List<Transaction>,
    onSelectCard: (String) -> Unit,
    onNavigateToScan: () -> Unit,
    onNavigateToTopUp: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToP2p: () -> Unit,
    onSimulateTrip: (TransactionType, String, Double) -> Unit,
    onAddCardClick: () -> Unit,
    onUpdateBalanceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "AR")).apply {
        maximumFractionDigits = 2
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Hero Card Banner / Visual Card
        item {
            if (selectedCard != null) {
                Column {
                    SubeCardVisual(
                        card = selectedCard,
                        onClick = onNavigateToScan
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onUpdateBalanceClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_sync_real_balance"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ajustar / Consultar Saldo Real (Sincronización Manual)",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No tenés tarjetas registradas")
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = onAddCardClick) {
                            Icon(Icons.Default.AddCard, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Agregar Tarjeta SUBE")
                        }
                    }
                }
            }
        }

        // Pending Accreditation Prompt Banner
        if (selectedCard != null && selectedCard.pendingAccreditationAmount > 0) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SubeMintSuccess.copy(alpha = 0.15f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SubeMintSuccess),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToScan() }
                        .testTag("banner_accredit_pending")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(SubeMintSuccess, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Nfc,
                                    contentDescription = "Accredit",
                                    tint = Color.Black,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "¡Tenés ${currencyFormat.format(selectedCard.pendingAccreditationAmount)} listos!",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Tocá acá y apoyá la tarjeta detrás del celular.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = onNavigateToScan,
                            colors = ButtonDefaults.buttonColors(containerColor = SubeMintSuccess),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Acreditar", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Quick Action Grid (4 core buttons: Cargar Saldo, Lectura NFC, Transferir P2P, Historial)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Default.Payment,
                    label = "Cargar Saldo",
                    color = SubeBluePrimary,
                    onClick = onNavigateToTopUp,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_action_topup")
                )
                QuickActionButton(
                    icon = Icons.Default.Nfc,
                    label = "Escanear / Acreditar",
                    color = SubeCyanAccent,
                    onClick = onNavigateToScan,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_action_scan")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Default.SwapHoriz,
                    label = "Transferir P2P",
                    color = MaterialTheme.colorScheme.secondary,
                    onClick = onNavigateToP2p,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_action_p2p")
                )
                QuickActionButton(
                    icon = Icons.Default.History,
                    label = "Historial",
                    color = MaterialTheme.colorScheme.tertiary,
                    onClick = onNavigateToHistory,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_action_history")
                )
            }
        }

        // Contactless Transit Pass Simulator (Colectivo, Subte, Tren)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PASE RÁPIDO SIN CONTACTO (TAP & PAY)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Simulá apoyo de tarjeta en molinete o colectivo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TransitShortcutChip(
                            icon = Icons.Default.DirectionsBus,
                            title = "Colectivo",
                            subtitle = "$370.00",
                            onClick = {
                                onSimulateTrip(TransactionType.TRIP_BUS, "Colectivo Línea 60", 370.00)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chip_trip_bus")
                        )
                        TransitShortcutChip(
                            icon = Icons.Default.DirectionsSubway,
                            title = "Subte D",
                            subtitle = "$650.00",
                            onClick = {
                                onSimulateTrip(TransactionType.TRIP_SUBTE, "Subte Línea D", 650.00)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chip_trip_subte")
                        )
                        TransitShortcutChip(
                            icon = Icons.Default.Train,
                            title = "Tren Mitre",
                            subtitle = "$260.00",
                            onClick = {
                                onSimulateTrip(TransactionType.TRIP_TRAIN, "Tren Mitre", 260.00)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chip_trip_train")
                        )
                    }
                }
            }
        }

        // Recent Transactions Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Movimientos Recientes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Ver todo",
                    style = MaterialTheme.typography.labelMedium,
                    color = SubeBluePrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToHistory() }
                )
            }
        }

        // Recent Transactions List Items
        if (recentTransactions.isEmpty()) {
            item {
                Text(
                    text = "Aún no realizaste viajes o cargas.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        } else {
            items(recentTransactions.take(4)) { transaction ->
                TransactionRowItem(transaction = transaction, currencyFormat = currencyFormat)
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun TransitShortcutChip(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = SubeBluePrimary,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TransactionRowItem(
    transaction: Transaction,
    currencyFormat: NumberFormat,
    isMasked: Boolean = false
) {
    val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale("es", "AR"))
    val dateStr = dateFormat.format(Date(transaction.timestamp))

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (transaction.type.isDeduction) Color(0xFFEF4444).copy(alpha = 0.12f) else SubeMintSuccess.copy(alpha = 0.15f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (transaction.type) {
                            TransactionType.TRIP_BUS -> Icons.Default.DirectionsBus
                            TransactionType.TRIP_SUBTE -> Icons.Default.DirectionsSubway
                            TransactionType.TRIP_TRAIN -> Icons.Default.Train
                            TransactionType.TOP_UP_RECHARGE, TransactionType.ACCREDITATION_NFC -> Icons.Default.Nfc
                            TransactionType.P2P_SENT, TransactionType.P2P_RECEIVED -> Icons.Default.SwapHoriz
                            else -> Icons.Default.Payment
                        },
                        contentDescription = null,
                        tint = if (transaction.type.isDeduction) Color(0xFFDC2626) else SubeMintSuccess,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (isMasked) "Movimiento Oculto (Biométrico)" else transaction.serviceName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isMasked) "$dateStr • Ubicación Protegida" else "$dateStr • ${transaction.locationOrBranch}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (isMasked) "${if (transaction.type.isDeduction) "-" else "+"} $ ••••••" else "${if (transaction.type.isDeduction) "-" else "+"}${currencyFormat.format(transaction.amount)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.type.isDeduction) MaterialTheme.colorScheme.onSurface else SubeMintSuccess
                )
                if (transaction.isLegacyBridgeMethod) {
                    Text(
                        text = "Puente J7",
                        style = MaterialTheme.typography.labelSmall,
                        color = SubeBluePrimary,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
