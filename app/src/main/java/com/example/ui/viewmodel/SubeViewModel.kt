package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CardType
import com.example.data.PaymentChannel
import com.example.data.SubeRepository
import com.example.data.TopUpOrder
import com.example.data.Transaction
import com.example.data.TransactionType
import com.example.data.TransitCard
import com.example.nfc.NfcBridgeManager
import com.example.nfc.NfcScanResult
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SubeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = SubeRepository(db.subeDao())
    val nfcBridgeManager = NfcBridgeManager(application)

    val cards: StateFlow<List<TransitCard>> = repository.allCards
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedCardId = MutableStateFlow<String?>(null)
    val selectedCardId: StateFlow<String?> = _selectedCardId.asStateFlow()

    val selectedCard: StateFlow<TransitCard?> = combine(cards, _selectedCardId) { cardList, currentId ->
        if (cardList.isEmpty()) null
        else cardList.find { it.cardId == currentId } ?: cardList.firstOrNull { it.isPrimary } ?: cardList.first()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val allTransactions: StateFlow<List<Transaction>> = repository.allTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val pendingTopUps: StateFlow<List<TopUpOrder>> = repository.pendingTopUps
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    private val _scanResult = MutableStateFlow<NfcScanResult?>(null)
    val scanResult: StateFlow<NfcScanResult?> = _scanResult.asStateFlow()

    private val _themeMode = MutableStateFlow(ThemeMode.OLED_BLACK)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        _uiMessage.value = "Tema actualizado: ${mode.displayName}"
    }

    fun cycleThemeMode() {
        val nextMode = when (_themeMode.value) {
            ThemeMode.OLED_BLACK -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.LIGHT
            ThemeMode.LIGHT -> ThemeMode.SYSTEM
            ThemeMode.SYSTEM -> ThemeMode.OLED_BLACK
        }
        setThemeMode(nextMode)
    }

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    fun selectCard(cardId: String) {
        _selectedCardId.value = cardId
    }

    fun clearUiMessage() {
        _uiMessage.value = null
    }

    fun clearScanResult() {
        _scanResult.value = null
    }

    fun toggleLegacyMode(enabled: Boolean) {
        nfcBridgeManager.toggleLegacyOptimizedMode(enabled)
        _uiMessage.value = if (enabled) "Modo Ligero J7 Activado (Bajo Consumo de Memoria)" else "Modo Estándar Activado"
    }

    fun createTopUpOrder(amount: Double, paymentChannel: PaymentChannel) {
        val card = selectedCard.value ?: return
        viewModelScope.launch {
            val refCode = "SUBE-${(10000..99999).random()}"
            val order = TopUpOrder(
                referenceCode = refCode,
                cardId = card.cardId,
                amount = amount,
                paymentChannel = paymentChannel
            )
            repository.createTopUpOrder(order)
            _uiMessage.value = "Carga de $$amount generada con éxito. Listo para acreditar por NFC."
        }
    }

    fun accreditCardBalanceNfcSimulated() {
        val card = selectedCard.value ?: return
        viewModelScope.launch {
            val pendingAmount = card.pendingAccreditationAmount
            val result = nfcBridgeManager.simulateContactlessTap(
                cardId = card.cardId,
                currentBalance = card.balance,
                pendingAmountToAccredit = pendingAmount
            )
            if (pendingAmount > 0) {
                repository.accreditPendingTopUps(card.cardId)
            }
            _scanResult.value = result
            _uiMessage.value = result.message
        }
    }

    fun simulateTrip(type: TransactionType, lineName: String, amount: Double) {
        val card = selectedCard.value ?: return
        viewModelScope.launch {
            val success = repository.executePaymentOrTrip(
                cardId = card.cardId,
                type = type,
                serviceName = lineName,
                amount = amount,
                isLegacyBridge = nfcBridgeManager.legacyModeEnabled.value
            )
            if (success) {
                nfcBridgeManager.triggerHapticFeedback()
                _uiMessage.value = "¡Pase registrado! Desc. $$amount (${lineName})"
            } else {
                _uiMessage.value = "Saldo insuficiente. Límite de saldo negativo alcanzado."
            }
        }
    }

    fun executeP2pTransfer(targetCardId: String, amount: Double) {
        val sourceCard = selectedCard.value ?: return
        if (sourceCard.cardId == targetCardId) {
            _uiMessage.value = "No podés transferir a la misma tarjeta."
            return
        }

        viewModelScope.launch {
            val success = repository.executePaymentOrTrip(
                cardId = sourceCard.cardId,
                type = TransactionType.P2P_SENT,
                serviceName = "Transferencia NFC Enviada -> $targetCardId",
                amount = amount,
                location = "Puente NFC P2P",
                isLegacyBridge = true
            )

            if (success) {
                val targetCard = repository.getCardById(targetCardId)
                if (targetCard != null) {
                    val newTargetBalance = targetCard.balance + amount
                    repository.saveCard(
                        targetCard.copy(balance = newTargetBalance)
                    )
                    repository.addTransaction(
                        Transaction(
                            cardId = targetCardId,
                            type = TransactionType.P2P_RECEIVED,
                            serviceName = "Transferencia NFC Recibida <- ${sourceCard.name}",
                            amount = amount,
                            balanceAfter = newTargetBalance,
                            locationOrBranch = "Puente NFC P2P",
                            isLegacyBridgeMethod = true
                        )
                    )
                }
                _uiMessage.value = "¡Transferencia de $$amount enviada correctamente!"
            } else {
                _uiMessage.value = "Saldo insuficiente para transferir."
            }
        }
    }

    fun updateCardBalanceManually(newBalance: Double) {
        val card = selectedCard.value ?: return
        viewModelScope.launch {
            val updatedCard = card.copy(
                balance = newBalance,
                lastReadTimestamp = System.currentTimeMillis()
            )
            repository.saveCard(updatedCard)
            repository.addTransaction(
                Transaction(
                    cardId = card.cardId,
                    type = TransactionType.TOP_UP_RECHARGE,
                    serviceName = "Ajuste / Consulta Manual de Saldo",
                    amount = newBalance,
                    balanceAfter = newBalance,
                    locationOrBranch = "Ajuste de Usuario",
                    discountApplied = "Saldo Real Sincronizado"
                )
            )
            _uiMessage.value = "Saldo actualizado a $$newBalance correctamente."
        }
    }

    fun addCustomCard(number: String, name: String, cardType: CardType) {
        val cleanNumber = number.trim()
        if (cleanNumber.length < 8) {
            _uiMessage.value = "Ingresá un número de tarjeta válido."
            return
        }

        viewModelScope.launch {
            val newCard = TransitCard(
                cardId = cleanNumber,
                name = name.ifEmpty { "Tarjeta ${cardType.name}" },
                cardType = cardType,
                balance = 1500.00,
                emergencyBalanceLimit = if (cardType == CardType.SUBE) -1200.00 else 0.0,
                isPrimary = cards.value.isEmpty()
            )
            repository.saveCard(newCard)
            _selectedCardId.value = newCard.cardId
            _uiMessage.value = "Tarjeta ${newCard.name} agregada con éxito."
        }
    }

    fun deleteCard(cardId: String) {
        viewModelScope.launch {
            repository.deleteCard(cardId)
            _uiMessage.value = "Tarjeta eliminada."
        }
    }
}
