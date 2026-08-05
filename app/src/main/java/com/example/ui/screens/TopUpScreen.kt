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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.PaymentChannel
import com.example.data.TopUpOrder
import com.example.data.TopUpStatus
import com.example.data.TransitCard
import com.example.ui.components.BiometricAuthDialog
import com.example.ui.theme.SubeBluePrimary
import com.example.ui.theme.SubeMintSuccess
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TopUpScreen(
    selectedCard: TransitCard?,
    pendingOrders: List<TopUpOrder>,
    onGenerateTopUp: (Double, PaymentChannel) -> Unit,
    onNavigateToScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "AR")).apply {
        maximumFractionDigits = 2
    }

    var selectedAmount by remember { mutableDoubleStateOf(2000.0) }
    var customAmountText by remember { mutableStateOf("") }
    var isCustom by remember { mutableStateOf(false) }
    var selectedChannel by remember { mutableStateOf(PaymentChannel.MERCADO_PAGO) }

    var showBiometricAuth by remember { mutableStateOf(false) }
    var pendingTopUpAmount by remember { mutableDoubleStateOf(0.0) }

    val presetAmounts = listOf(1000.0, 2000.0, 3000.0, 5000.0)

    if (showBiometricAuth) {
        BiometricAuthDialog(
            actionTitle = "Autorizar Carga de Saldo",
            actionDescription = "Confirmá con tu huella digital para procesar el pago de ${currencyFormat.format(pendingTopUpAmount)}.",
            onDismiss = { showBiometricAuth = false },
            onAuthenticated = {
                showBiometricAuth = false
                onGenerateTopUp(pendingTopUpAmount, selectedChannel)
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(12.dp)) }

        item {
            Text(
                text = "Cargar Saldo SUBE",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Seleccioná el monto y medio de pago. Luego acreditá apoyando la tarjeta.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Selected Card Summary Bar
        if (selectedCard != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Tarjeta a Cargar:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = selectedCard.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Saldo: ${currencyFormat.format(selectedCard.balance)}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = SubeBluePrimary
                        )
                    }
                }
            }
        }

        // Amount Selection Grid
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "1. SELECCIONÁ EL MONTO",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        presetAmounts.take(2).forEach { amt ->
                            AmountChip(
                                amount = amt,
                                isSelected = !isCustom && selectedAmount == amt,
                                onClick = {
                                    isCustom = false
                                    selectedAmount = amt
                                },
                                currencyFormat = currencyFormat,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("chip_amount_${amt.toInt()}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        presetAmounts.drop(2).forEach { amt ->
                            AmountChip(
                                amount = amt,
                                isSelected = !isCustom && selectedAmount == amt,
                                onClick = {
                                    isCustom = false
                                    selectedAmount = amt
                                },
                                currencyFormat = currencyFormat,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("chip_amount_${amt.toInt()}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = customAmountText,
                        onValueChange = { input ->
                            customAmountText = input
                            val parsed = input.toDoubleOrNull()
                            if (parsed != null && parsed > 0) {
                                isCustom = true
                                selectedAmount = parsed
                            }
                        },
                        label = { Text("Otro Monto Personalizado ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_custom_topup_amount"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Payment Channel Selector
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "2. MEDIO DE PAGO",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    PaymentChannelOptionRow(
                        channel = PaymentChannel.MERCADO_PAGO,
                        icon = Icons.Default.Payment,
                        isSelected = selectedChannel == PaymentChannel.MERCADO_PAGO,
                        onSelect = { selectedChannel = PaymentChannel.MERCADO_PAGO },
                        modifier = Modifier.testTag("channel_mercado_pago")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    PaymentChannelOptionRow(
                        channel = PaymentChannel.DEBIT_CARD,
                        icon = Icons.Default.CreditCard,
                        isSelected = selectedChannel == PaymentChannel.DEBIT_CARD,
                        onSelect = { selectedChannel = PaymentChannel.DEBIT_CARD },
                        modifier = Modifier.testTag("channel_debit")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    PaymentChannelOptionRow(
                        channel = PaymentChannel.BANK_TRANSFER,
                        icon = Icons.Default.AccountBalance,
                        isSelected = selectedChannel == PaymentChannel.BANK_TRANSFER,
                        onSelect = { selectedChannel = PaymentChannel.BANK_TRANSFER },
                        modifier = Modifier.testTag("channel_transfer")
                    )
                }
            }
        }

        // Confirm & Generate Order Button
        item {
            Button(
                onClick = {
                    val finalAmt = if (isCustom && customAmountText.toDoubleOrNull() != null)
                        customAmountText.toDouble()
                    else
                        selectedAmount

                    pendingTopUpAmount = finalAmt
                    showBiometricAuth = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("btn_confirm_topup"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SubeBluePrimary)
            ) {
                Icon(Icons.Default.Payment, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "CARGAR ${currencyFormat.format(if (isCustom && customAmountText.toDoubleOrNull() != null) customAmountText.toDouble() else selectedAmount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Pending Accreditation Queue Items
        if (pendingOrders.isNotEmpty()) {
            item {
                Text(
                    text = "Cargas Pendientes de Acreditar por NFC",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(pendingOrders) { order ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SubeMintSuccess.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SubeMintSuccess.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Código: ${order.referenceCode}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Vía ${order.paymentChannel.displayName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = onNavigateToScan,
                            colors = ButtonDefaults.buttonColors(containerColor = SubeMintSuccess),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Nfc, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(currencyFormat.format(order.amount), color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun AmountChip(
    amount: Double,
    isSelected: Boolean,
    onClick: () -> Unit,
    currencyFormat: NumberFormat,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) SubeBluePrimary else MaterialTheme.colorScheme.surfaceVariant,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, SubeBluePrimary) else null,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = currencyFormat.format(amount),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun PaymentChannelOptionRow(
    channel: PaymentChannel,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onSelect,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) SubeBluePrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, SubeBluePrimary) else null,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = channel.displayName,
                tint = if (isSelected) SubeBluePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = channel.displayName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
