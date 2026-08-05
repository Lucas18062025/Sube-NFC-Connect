package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TopUpStatus(val label: String) {
    PENDING_ACCREDITATION("Pendiente Acreditar NFC"),
    ACCREDITED("Acreditado en Tarjeta"),
    CANCELLED("Cancelado")
}

enum class PaymentChannel(val displayName: String) {
    MERCADO_PAGO("Mercado Pago"),
    DEBIT_CARD("Tarjeta de Débito / Crédito"),
    BANK_TRANSFER("Transferencia CBU / CVU"),
    PIN_VOUCHER("Cupón de Pago / Punto Efectivo")
}

@Entity(tableName = "top_up_orders")
data class TopUpOrder(
    @PrimaryKey val referenceCode: String, // e.g. "SUBE-98421"
    val cardId: String,
    val amount: Double,
    val paymentChannel: PaymentChannel,
    val status: TopUpStatus = TopUpStatus.PENDING_ACCREDITATION,
    val timestamp: Long = System.currentTimeMillis()
)
