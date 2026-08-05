package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType(val title: String, val isDeduction: Boolean) {
    TRIP_BUS("Viaje en Colectivo", true),
    TRIP_SUBTE("Viaje en Subte", true),
    TRIP_TRAIN("Viaje en Tren", true),
    POSNET_PAY("Pago Comercio NFC", true),
    TOP_UP_RECHARGE("Carga de Saldo", false),
    ACCREDITATION_NFC("Acreditación NFC", false),
    P2P_SENT("Transferencia Enviada", true),
    P2P_RECEIVED("Transferencia Recibida", false)
}

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cardId: String,
    val type: TransactionType,
    val serviceName: String, // e.g. "Línea 60 - Interno 104", "Subte D - Est. Bulnes", "MercadoPago Topup"
    val amount: Double,
    val balanceAfter: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val locationOrBranch: String = "Buenos Aires, CABA",
    val discountApplied: String = "", // e.g. "Descuento RED SUBE 50%"
    val isLegacyBridgeMethod: Boolean = false // If performed via J7 / Non-NFC soft payload bridge
)
