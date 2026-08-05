package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CardType(val label: String, val provider: String) {
    SUBE("Tarjeta SUBE Argentina", "NFC ISO/IEC 14443-A"),
    BIP("Tarjeta BIP! Santiago", "Mifare Classic / Desfire"),
    METRO_CDMX("Metro CDMX Movilidad", "ISO-DEP APDU"),
    BUS_URBANO("Red Bus / Tarjeta Urbana", "Contactless Transport"),
    CUSTOM_NFC("Pase Virtual Personalizado", "HCE Bridge Token")
}

@Entity(tableName = "transit_cards")
data class TransitCard(
    @PrimaryKey val cardId: String, // e.g. "6061 2849 9012 3411"
    val name: String,
    val cardType: CardType = CardType.SUBE,
    val balance: Double = 2850.00,
    val emergencyBalanceLimit: Double = -1200.00, // Negative threshold
    val pendingAccreditationAmount: Double = 0.0, // Top-up bought waiting to be tapped to card
    val isPrimary: Boolean = false,
    val lastReadTimestamp: Long = System.currentTimeMillis(),
    val isVirtualHceEnabled: Boolean = true,
    val legacyBridgeSynced: Boolean = true
)
