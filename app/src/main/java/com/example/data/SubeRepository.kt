package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class SubeRepository(private val dao: SubeDao) {

    val allCards: Flow<List<TransitCard>> = dao.getAllCards()
    val allTransactions: Flow<List<Transaction>> = dao.getAllTransactions()
    val pendingTopUps: Flow<List<TopUpOrder>> = dao.getPendingTopUpOrders()

    fun getTransactionsForCard(cardId: String): Flow<List<Transaction>> =
        dao.getTransactionsForCard(cardId)

    fun getTopUpOrdersForCard(cardId: String): Flow<List<TopUpOrder>> =
        dao.getTopUpOrdersForCard(cardId)

    suspend fun getCardById(cardId: String): TransitCard? = dao.getCardById(cardId)

    suspend fun saveCard(card: TransitCard) = withContext(Dispatchers.IO) {
        dao.insertOrUpdateCard(card)
    }

    suspend fun updateCard(card: TransitCard) = withContext(Dispatchers.IO) {
        dao.updateCard(card)
    }

    suspend fun deleteCard(cardId: String) = withContext(Dispatchers.IO) {
        dao.deleteCard(cardId)
    }

    suspend fun addTransaction(transaction: Transaction) = withContext(Dispatchers.IO) {
        dao.insertTransaction(transaction)
    }

    suspend fun createTopUpOrder(order: TopUpOrder) = withContext(Dispatchers.IO) {
        dao.insertTopUpOrder(order)
        // Also update pending accreditation amount on card
        val card = dao.getCardById(order.cardId)
        if (card != null) {
            val updated = card.copy(
                pendingAccreditationAmount = card.pendingAccreditationAmount + order.amount
            )
            dao.updateCard(updated)
        }
    }

    suspend fun accreditPendingTopUps(cardId: String): Double = withContext(Dispatchers.IO) {
        val card = dao.getCardById(cardId) ?: return@withContext 0.0
        val pendingAmount = card.pendingAccreditationAmount
        if (pendingAmount <= 0) return@withContext 0.0

        val newBalance = card.balance + pendingAmount
        dao.updateCard(
            card.copy(
                balance = newBalance,
                pendingAccreditationAmount = 0.0,
                lastReadTimestamp = System.currentTimeMillis()
            )
        )

        // Record accreditation transaction
        dao.insertTransaction(
            Transaction(
                cardId = cardId,
                type = TransactionType.ACCREDITATION_NFC,
                serviceName = "Acreditación NFC / Lectura Físico-Virtual",
                amount = pendingAmount,
                balanceAfter = newBalance,
                locationOrBranch = "Terminal NFC / App Movil Bridge",
                discountApplied = "Carga Acreditada Exitosamente"
            )
        )

        // Update pending orders status
        val orders = dao.getTopUpOrdersForCard(cardId).first()
        for (order in orders) {
            if (order.status == TopUpStatus.PENDING_ACCREDITATION) {
                dao.updateTopUpOrder(order.copy(status = TopUpStatus.ACCREDITED))
            }
        }

        return@withContext pendingAmount
    }

    suspend fun executePaymentOrTrip(
        cardId: String,
        type: TransactionType,
        serviceName: String,
        amount: Double,
        location: String = "Colectivo CABA / AMBA",
        isLegacyBridge: Boolean = false
    ): Boolean = withContext(Dispatchers.IO) {
        val card = dao.getCardById(cardId) ?: return@withContext false
        val newBalance = card.balance - amount

        // Check if balance doesn't exceed negative threshold limit
        if (newBalance < card.emergencyBalanceLimit) {
            return@withContext false
        }

        val updatedCard = card.copy(
            balance = newBalance,
            lastReadTimestamp = System.currentTimeMillis()
        )
        dao.updateCard(updatedCard)

        dao.insertTransaction(
            Transaction(
                cardId = cardId,
                type = type,
                serviceName = serviceName,
                amount = amount,
                balanceAfter = newBalance,
                locationOrBranch = location,
                discountApplied = if (type == TransactionType.TRIP_BUS) "RED SUBE 50% Aplicado" else "",
                isLegacyBridgeMethod = isLegacyBridge
            )
        )

        return@withContext true
    }

    suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        val cards = dao.getAllCards().first()
        if (cards.isEmpty()) {
            val defaultCardId = "6061 2849 9012 3411"
            val defaultCard = TransitCard(
                cardId = defaultCardId,
                name = "Mi SUBE Principal (J7 Compatible)",
                cardType = CardType.SUBE,
                balance = 2850.00,
                emergencyBalanceLimit = -1200.00,
                pendingAccreditationAmount = 1500.00,
                isPrimary = true,
                lastReadTimestamp = System.currentTimeMillis()
            )
            val secondaryCard = TransitCard(
                cardId = "6061 8820 4410 9901",
                name = "SUBE Auxiliar Trabajo",
                cardType = CardType.SUBE,
                balance = 1200.00,
                emergencyBalanceLimit = -1200.00,
                pendingAccreditationAmount = 0.0,
                isPrimary = false,
                lastReadTimestamp = System.currentTimeMillis() - 86400000 * 2
            )
            val customCard = TransitCard(
                cardId = "8890 1204 5512 8834",
                name = "Pase Virtual BIP! Santiago",
                cardType = CardType.BIP,
                balance = 5400.00,
                emergencyBalanceLimit = 0.0,
                pendingAccreditationAmount = 0.0,
                isPrimary = false,
                lastReadTimestamp = System.currentTimeMillis() - 86400000 * 5
            )

            dao.insertOrUpdateCard(defaultCard)
            dao.insertOrUpdateCard(secondaryCard)
            dao.insertOrUpdateCard(customCard)

            // Seed sample transactions
            val now = System.currentTimeMillis()
            val sampleTxs = listOf(
                Transaction(
                    cardId = defaultCardId,
                    type = TransactionType.TRIP_BUS,
                    serviceName = "Línea 60 - Interno 204",
                    amount = 370.00,
                    balanceAfter = 2850.00,
                    timestamp = now - 3600000 * 2,
                    locationOrBranch = "Av. Santa Fe y Pueyrredón",
                    discountApplied = "RED SUBE 50%"
                ),
                Transaction(
                    cardId = defaultCardId,
                    type = TransactionType.TRIP_SUBTE,
                    serviceName = "Subte D - Est. Bulnes",
                    amount = 650.00,
                    balanceAfter = 3220.00,
                    timestamp = now - 3600000 * 5,
                    locationOrBranch = "Palermo, CABA",
                    discountApplied = "RED SUBE Tarifa Normal"
                ),
                Transaction(
                    cardId = defaultCardId,
                    type = TransactionType.TOP_UP_RECHARGE,
                    serviceName = "Carga Mercado Pago",
                    amount = 2000.00,
                    balanceAfter = 3870.00,
                    timestamp = now - 86400000,
                    locationOrBranch = "MercadoPago App",
                    discountApplied = "Pendiente Acreditar por NFC"
                ),
                Transaction(
                    cardId = defaultCardId,
                    type = TransactionType.TRIP_TRAIN,
                    serviceName = "Línea Mitre - Est. Retiro",
                    amount = 260.00,
                    balanceAfter = 1870.00,
                    timestamp = now - 86400000 * 2,
                    locationOrBranch = "Retiro, CABA",
                    discountApplied = "Tarifa Social SUBE 55%"
                ),
                Transaction(
                    cardId = defaultCardId,
                    type = TransactionType.P2P_RECEIVED,
                    serviceName = "Transferencia NFC P2P",
                    amount = 500.00,
                    balanceAfter = 2130.00,
                    timestamp = now - 86400000 * 3,
                    locationOrBranch = "Puente P2P J7 Prime",
                    isLegacyBridgeMethod = true
                )
            )

            for (tx in sampleTxs) {
                dao.insertTransaction(tx)
            }

            // Seed initial topup order
            dao.insertTopUpOrder(
                TopUpOrder(
                    referenceCode = "SUBE-MP-98421",
                    cardId = defaultCardId,
                    amount = 1500.00,
                    paymentChannel = PaymentChannel.MERCADO_PAGO,
                    status = TopUpStatus.PENDING_ACCREDITATION,
                    timestamp = now - 1800000
                )
            )
        }
    }
}
