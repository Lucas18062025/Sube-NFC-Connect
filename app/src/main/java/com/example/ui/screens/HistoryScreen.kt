package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.Transaction
import com.example.data.TransactionType
import com.example.ui.components.BiometricAuthDialog
import com.example.ui.components.MonthlySpendingChart
import com.example.ui.theme.SubeBluePrimary
import com.example.ui.theme.SubeMintSuccess
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HistoryScreen(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "AR")).apply {
        maximumFractionDigits = 2
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("TODOS") }
    var isHistoryUnlocked by remember { mutableStateOf(false) }
    var showBiometricAuth by remember { mutableStateOf(false) }

    val filterOptions = listOf("TODOS", "VIAJES", "CARGAS", "TRANSFERENCIAS")

    if (showBiometricAuth) {
        BiometricAuthDialog(
            actionTitle = "Desbloquear Historial Sensible",
            actionDescription = "Escaneá tu huella digital para visualizar los montos y detalles completos del historial de transacciones.",
            onDismiss = { showBiometricAuth = false },
            onAuthenticated = {
                showBiometricAuth = false
                isHistoryUnlocked = true
            }
        )
    }

    val filteredList = transactions.filter { tx ->
        val matchesSearch = tx.serviceName.contains(searchQuery, ignoreCase = true) ||
                tx.locationOrBranch.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "VIAJES" -> tx.type == TransactionType.TRIP_BUS || tx.type == TransactionType.TRIP_SUBTE || tx.type == TransactionType.TRIP_TRAIN
            "CARGAS" -> tx.type == TransactionType.TOP_UP_RECHARGE || tx.type == TransactionType.ACCREDITATION_NFC
            "TRANSFERENCIAS" -> tx.type == TransactionType.P2P_SENT || tx.type == TransactionType.P2P_RECEIVED
            else -> true
        }

        matchesSearch && matchesFilter
    }

    val totalSpent = transactions.filter { it.type.isDeduction }.sumOf { it.amount }
    val totalTopUp = transactions.filter { !it.type.isDeduction }.sumOf { it.amount }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(12.dp)) }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Historial de Transacciones",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Auditoría completa de viajes, cargas y acreditaciones NFC",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isHistoryUnlocked) SubeMintSuccess.copy(alpha = 0.12f) else SubeBluePrimary.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isHistoryUnlocked) SubeMintSuccess else SubeBluePrimary
                ),
                modifier = Modifier.fillMaxWidth()
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
                        Icon(
                            imageVector = if (isHistoryUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (isHistoryUnlocked) SubeMintSuccess else SubeBluePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isHistoryUnlocked) "Historial Desbloqueado" else "Protección Biométrica Activa",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isHistoryUnlocked) "Detalles visibles en pantalla" else "Montos ocultos hasta verificar huella",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (isHistoryUnlocked) {
                                isHistoryUnlocked = false
                            } else {
                                showBiometricAuth = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isHistoryUnlocked) MaterialTheme.colorScheme.surfaceVariant else SubeBluePrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("btn_toggle_biometric_history")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = null,
                            tint = if (isHistoryUnlocked) MaterialTheme.colorScheme.onSurfaceVariant else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHistoryUnlocked) "Bloquear" else "Verificar Huella",
                            color = if (isHistoryUnlocked) MaterialTheme.colorScheme.onSurfaceVariant else Color.White,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }

        // Summary Metric Banner
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("TOTAL VIAJADO MES", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            if (isHistoryUnlocked) currencyFormat.format(totalSpent) else "$ ••••••",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(40.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Savings, contentDescription = null, tint = SubeMintSuccess, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ahorro RED SUBE", style = MaterialTheme.typography.labelSmall, color = SubeMintSuccess, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            if (isHistoryUnlocked) currencyFormat.format(totalSpent * 0.35) else "$ ••••••",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SubeMintSuccess
                        )
                    }
                }
            }
        }

        // Native Line Chart for Monthly Spending Pattern
        item {
            MonthlySpendingChart(
                transactions = transactions,
                isMasked = !isHistoryUnlocked
            )
        }

        // Search Input
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar por línea, estación o comercio") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_history_search"),
                shape = RoundedCornerShape(14.dp)
            )
        }

        // Filter Chips Row
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filterOptions) { filter ->
                    val isSelected = selectedFilter == filter
                    Surface(
                        onClick = { selectedFilter = filter },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) SubeBluePrimary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.testTag("filter_chip_$filter")
                    ) {
                        Text(
                            text = filter,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Transaction Rows
        if (filteredList.isEmpty()) {
            item {
                Text(
                    text = "No se encontraron movimientos para el filtro seleccionado.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 20.dp)
                )
            }
        } else {
            items(filteredList) { tx ->
                TransactionRowItem(
                    transaction = tx,
                    currencyFormat = currencyFormat,
                    isMasked = !isHistoryUnlocked
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
