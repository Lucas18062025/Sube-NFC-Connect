package com.example

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.CardType
import com.example.ui.components.AddCardDialog
import com.example.ui.components.HeaderStatusBar
import androidx.fragment.app.FragmentActivity
import com.example.ui.components.BiometricAuthDialog
import com.example.ui.components.UpdateBalanceDialog
import com.example.ui.screens.DeviceDiagnosticScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NfcScanScreen
import com.example.ui.screens.P2pTransferScreen
import com.example.ui.screens.TopUpScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SubeBluePrimary
import com.example.ui.viewmodel.SubeViewModel
import kotlinx.coroutines.launch

sealed class NavDestination(val routeIndex: Int, val title: String, val icon: ImageVector) {
    object Home : NavDestination(0, "Inicio", Icons.Default.Home)
    object Scan : NavDestination(1, "Escanear", Icons.Default.Nfc)
    object TopUp : NavDestination(2, "Cargar", Icons.Default.Payment)
    object P2p : NavDestination(3, "Transferir", Icons.Default.SwapHoriz)
    object History : NavDestination(4, "Historial", Icons.Default.Receipt)
    object Diagnostic : NavDestination(5, "Diag. J7", Icons.Default.Build)
}

class MainActivity : FragmentActivity() {

    private val viewModel: SubeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle initial NFC Intent if launched via tag tap
        handleNfcIntent(intent)

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            MyApplicationTheme(themeMode = themeMode) {
                MainAppContent(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNfcIntent(intent)
    }

    private fun handleNfcIntent(intent: Intent?) {
        if (intent == null) return
        val action = intent.action
        if (NfcAdapter.ACTION_TECH_DISCOVERED == action ||
            NfcAdapter.ACTION_TAG_DISCOVERED == action ||
            NfcAdapter.ACTION_NDEF_DISCOVERED == action
        ) {
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            if (tag != null) {
                Toast.makeText(this, "¡Tarjeta NFC Física Detectada!", Toast.LENGTH_SHORT).show()
                viewModel.accreditCardBalanceNfcSimulated()
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: SubeViewModel) {
    val cards by viewModel.cards.collectAsStateWithLifecycle()
    val selectedCard by viewModel.selectedCard.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val pendingTopUps by viewModel.pendingTopUps.collectAsStateWithLifecycle()

    val uiMessage by viewModel.uiMessage.collectAsStateWithLifecycle()
    val scanResult by viewModel.scanResult.collectAsStateWithLifecycle()

    val nfcMode by viewModel.nfcBridgeManager.nfcMode.collectAsStateWithLifecycle()
    val isScanning by viewModel.nfcBridgeManager.isScanning.collectAsStateWithLifecycle()
    val legacyModeEnabled by viewModel.nfcBridgeManager.legacyModeEnabled.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    var currentRouteIndex by remember { mutableIntStateOf(0) }
    var showAddCardDialog by remember { mutableStateOf(false) }
    var showUpdateBalanceDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiMessage) {
        val msg = uiMessage
        if (msg != null) {
            scope.launch {
                snackbarHostState.showSnackbar(msg)
                viewModel.clearUiMessage()
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HeaderStatusBar(
                cards = cards,
                selectedCard = selectedCard,
                onSelectCard = { viewModel.selectCard(it) },
                nfcMode = nfcMode,
                isLegacyModeEnabled = legacyModeEnabled,
                onToggleLegacyMode = { viewModel.toggleLegacyMode(it) },
                themeMode = themeMode,
                onSelectThemeMode = { viewModel.setThemeMode(it) },
                onOpenDiagnostic = { currentRouteIndex = 5 }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val destinations = listOf(
                    NavDestination.Home,
                    NavDestination.Scan,
                    NavDestination.TopUp,
                    NavDestination.P2p,
                    NavDestination.History,
                    NavDestination.Diagnostic
                )

                destinations.forEach { dest ->
                    NavigationBarItem(
                        selected = currentRouteIndex == dest.routeIndex,
                        onClick = { currentRouteIndex = dest.routeIndex },
                        icon = {
                            Icon(imageVector = dest.icon, contentDescription = dest.title)
                        },
                        label = {
                            Text(
                                text = dest.title,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SubeBluePrimary,
                            selectedTextColor = SubeBluePrimary,
                            indicatorColor = SubeBluePrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_item_${dest.routeIndex}")
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentRouteIndex) {
                0 -> HomeScreen(
                    cards = cards,
                    selectedCard = selectedCard,
                    recentTransactions = allTransactions,
                    onSelectCard = { viewModel.selectCard(it) },
                    onNavigateToScan = { currentRouteIndex = 1 },
                    onNavigateToTopUp = { currentRouteIndex = 2 },
                    onNavigateToHistory = { currentRouteIndex = 4 },
                    onNavigateToP2p = { currentRouteIndex = 3 },
                    onSimulateTrip = { type, line, amt -> viewModel.simulateTrip(type, line, amt) },
                    onAddCardClick = { showAddCardDialog = true },
                    onUpdateBalanceClick = { showUpdateBalanceDialog = true }
                )
                1 -> NfcScanScreen(
                    selectedCard = selectedCard,
                    nfcMode = nfcMode,
                    isScanning = isScanning,
                    lastResult = scanResult,
                    onTriggerScan = { viewModel.accreditCardBalanceNfcSimulated() },
                    onAccreditPending = { viewModel.accreditCardBalanceNfcSimulated() }
                )
                2 -> TopUpScreen(
                    selectedCard = selectedCard,
                    pendingOrders = pendingTopUps,
                    onGenerateTopUp = { amt, channel -> viewModel.createTopUpOrder(amt, channel) },
                    onNavigateToScan = { currentRouteIndex = 1 }
                )
                3 -> P2pTransferScreen(
                    cards = cards,
                    selectedCard = selectedCard,
                    onExecuteTransfer = { targetId, amt -> viewModel.executeP2pTransfer(targetId, amt) }
                )
                4 -> HistoryScreen(
                    transactions = allTransactions
                )
                5 -> DeviceDiagnosticScreen(
                    nfcMode = nfcMode,
                    isHardwareNfcAvailable = viewModel.nfcBridgeManager.isHardwareNfcAvailable(),
                    isHardwareNfcEnabled = viewModel.nfcBridgeManager.isHardwareNfcEnabled(),
                    isLegacyModeEnabled = legacyModeEnabled,
                    onToggleLegacyMode = { viewModel.toggleLegacyMode(it) },
                    themeMode = themeMode,
                    onSelectThemeMode = { viewModel.setThemeMode(it) }
                )
            }
        }
    }

    if (showAddCardDialog) {
        AddCardDialog(
            onDismiss = { showAddCardDialog = false },
            onConfirm = { number, name, type ->
                viewModel.addCustomCard(number, name, type)
                showAddCardDialog = false
            }
        )
    }

    val activeCard = selectedCard
    if (showUpdateBalanceDialog && activeCard != null) {
        UpdateBalanceDialog(
            currentBalance = activeCard.balance,
            onDismiss = { showUpdateBalanceDialog = false },
            onConfirm = { newBalance ->
                viewModel.updateCardBalanceManually(newBalance)
                showUpdateBalanceDialog = false
            }
        )
    }
}
