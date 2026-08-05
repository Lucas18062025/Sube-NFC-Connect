package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.data.TransitCard
import com.example.nfc.NfcMode
import com.example.ui.theme.SubeBluePrimary
import com.example.ui.theme.SubeCyanAccent
import com.example.ui.theme.SubeMintSuccess
import com.example.ui.theme.ThemeMode

@Composable
fun HeaderStatusBar(
    cards: List<TransitCard>,
    selectedCard: TransitCard?,
    onSelectCard: (String) -> Unit,
    nfcMode: NfcMode,
    isLegacyModeEnabled: Boolean,
    onToggleLegacyMode: (Boolean) -> Unit,
    themeMode: ThemeMode = ThemeMode.OLED_BLACK,
    onSelectThemeMode: (ThemeMode) -> Unit = {},
    onOpenDiagnostic: () -> Unit,
    modifier: Modifier = Modifier
) {
    var cardDropdownExpanded by remember { mutableStateOf(false) }
    var themeDropdownExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Top Row: Title & App Icon + Diagnostic Tool Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = SubeBluePrimary,
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Nfc,
                            contentDescription = "App Icon Logo",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "SUBE NFC Connect",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Adaptador Teléfonos Antiguos",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Card Selector Button
                    Box {
                        Surface(
                            modifier = Modifier
                                .clickable { cardDropdownExpanded = true }
                                .testTag("card_selector_dropdown"),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CreditCard,
                                    contentDescription = "Cambiar Tarjeta",
                                    tint = SubeBluePrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = selectedCard?.name?.take(10) ?: "Tarjetas",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = cardDropdownExpanded,
                            onDismissRequest = { cardDropdownExpanded = false }
                        ) {
                            cards.forEach { card ->
                                DropdownMenuItem(
                                    text = {
                                        Text("${card.name} (${card.cardId.takeLast(4)})")
                                    },
                                    onClick = {
                                        onSelectCard(card.cardId)
                                        cardDropdownExpanded = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.CreditCard,
                                            contentDescription = null
                                        )
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Theme Selector Dropdown
                    Box {
                        IconButton(
                            onClick = { themeDropdownExpanded = true },
                            modifier = Modifier.testTag("btn_theme_selector")
                        ) {
                            Icon(
                                imageVector = when (themeMode) {
                                    ThemeMode.OLED_BLACK -> Icons.Default.BatterySaver
                                    ThemeMode.DARK -> Icons.Default.DarkMode
                                    ThemeMode.LIGHT -> Icons.Default.LightMode
                                    ThemeMode.SYSTEM -> Icons.Default.Palette
                                },
                                contentDescription = "Cambiar Tema / Ahorro OLED",
                                tint = if (themeMode == ThemeMode.OLED_BLACK) SubeMintSuccess else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        DropdownMenu(
                            expanded = themeDropdownExpanded,
                            onDismissRequest = { themeDropdownExpanded = false }
                        ) {
                            ThemeMode.values().forEach { mode ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(mode.displayName, fontWeight = FontWeight.Bold)
                                            Text(
                                                mode.badgeText,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (mode == ThemeMode.OLED_BLACK) SubeMintSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    onClick = {
                                        onSelectThemeMode(mode)
                                        themeDropdownExpanded = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = when (mode) {
                                                ThemeMode.OLED_BLACK -> Icons.Default.BatterySaver
                                                ThemeMode.DARK -> Icons.Default.DarkMode
                                                ThemeMode.LIGHT -> Icons.Default.LightMode
                                                ThemeMode.SYSTEM -> Icons.Default.Palette
                                            },
                                            contentDescription = null,
                                            tint = if (mode == themeMode) SubeMintSuccess else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    // Hardware Audit Diagnostic Button
                    IconButton(
                        onClick = onOpenDiagnostic,
                        modifier = Modifier.testTag("btn_diagnostic")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "Diagnóstico Hardware Antiguo",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Status Pill: NFC Protocol Status & Biometric Protection & J7 Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Hardware Status Chip
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = when (nfcMode) {
                            NfcMode.HARDWARE_NFC -> SubeMintSuccess.copy(alpha = 0.15f)
                            NfcMode.LEGACY_J7_BRIDGE -> SubeCyanAccent.copy(alpha = 0.15f)
                            NfcMode.SIMULATED_TESTER -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (nfcMode) {
                                    NfcMode.HARDWARE_NFC -> Icons.Default.FlashOn
                                    NfcMode.LEGACY_J7_BRIDGE -> Icons.Default.PhoneAndroid
                                    NfcMode.SIMULATED_TESTER -> Icons.Default.Speed
                                },
                                contentDescription = "Status Icon",
                                tint = when (nfcMode) {
                                    NfcMode.HARDWARE_NFC -> SubeMintSuccess
                                    NfcMode.LEGACY_J7_BRIDGE -> SubeBluePrimary
                                    NfcMode.SIMULATED_TESTER -> MaterialTheme.colorScheme.primary
                                },
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (nfcMode) {
                                    NfcMode.HARDWARE_NFC -> "NFC Nativo"
                                    NfcMode.LEGACY_J7_BRIDGE -> "Puente J7"
                                    NfcMode.SIMULATED_TESTER -> "Simulador"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Biometric Security Active Badge
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SubeMintSuccess.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Biometría Activa",
                                tint = SubeMintSuccess,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Biometría OK",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = SubeMintSuccess
                            )
                        }
                    }
                }

                // Modo Ligero J7 Switcher
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Modo Ligero J7",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = isLegacyModeEnabled,
                        onCheckedChange = onToggleLegacyMode,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SubeBluePrimary,
                            checkedTrackColor = SubeBluePrimary.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.testTag("switch_legacy_mode")
                    )
                }
            }
        }
    }
}
