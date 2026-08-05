package com.example.ui.screens

import android.os.Build
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nfc.NfcMode
import com.example.ui.theme.SubeBluePrimary
import com.example.ui.theme.SubeMintSuccess

@Composable
fun DeviceDiagnosticScreen(
    nfcMode: NfcMode,
    isHardwareNfcAvailable: Boolean,
    isHardwareNfcEnabled: Boolean,
    isLegacyModeEnabled: Boolean,
    onToggleLegacyMode: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(12.dp)) }

        item {
            Text(
                text = "Diagnóstico y Adaptador Legado",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Compatibilidad de hardware y rendimiento para teléfonos de gama de entrada (ej. Samsung J7 Prime).",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Hardware Overview Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "ESPECIFICACIONES DEL DISPOSITIVO",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    DiagnosticMetricRow(
                        label = "Modelo de Dispositivo",
                        value = "${Build.MANUFACTURER.uppercase()} ${Build.MODEL}",
                        icon = Icons.Default.PhoneAndroid
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    DiagnosticMetricRow(
                        label = "Versión de Android API",
                        value = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
                        icon = Icons.Default.Memory
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    DiagnosticMetricRow(
                        label = "Antena NFC Física Hardware",
                        value = if (isHardwareNfcAvailable) "Presente" else "No Detectada (Usa Puente Soft-NFC)",
                        icon = if (isHardwareNfcAvailable) Icons.Default.CheckCircle else Icons.Default.Warning,
                        valueColor = if (isHardwareNfcAvailable) SubeMintSuccess else Color(0xFFF59E0B)
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    DiagnosticMetricRow(
                        label = "Protocolo Activo",
                        value = when (nfcMode) {
                            NfcMode.HARDWARE_NFC -> "ISO/IEC 14443-A Nativo"
                            NfcMode.LEGACY_J7_BRIDGE -> "Puente Adaptador APDU J7"
                            NfcMode.SIMULATED_TESTER -> "Simulador ISO-7816"
                        },
                        icon = Icons.Default.FlashOn,
                        valueColor = SubeBluePrimary
                    )
                }
            }
        }

        // Low Memory / Legacy Mode Performance Settings
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = SubeBluePrimary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Modo Rendimiento Ligero J7", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text("Reduce animaciones y optimiza uso de RAM", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Switch(
                            checked = isLegacyModeEnabled,
                            onCheckedChange = onToggleLegacyMode,
                            modifier = Modifier.testTag("diag_switch_legacy")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.BatterySaver, contentDescription = null, tint = SubeMintSuccess)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Ahorro de Batería en Lectura Continua: Activado",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = SubeMintSuccess
                        )
                    }
                }
            }
        }

        // Antenna Placement Tips
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "CONSEJOS DE APOYO DE TARJETA",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "1. Quitar fundas gruesas o metálicas que puedan interferir en la lectura del sensor.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "2. Apoyar la tarjeta SUBE en la parte trasera del teléfono cerca de la cámara principal y mantenerla firme 2 segundos.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "3. En teléfonos sin NFC nativo (ej. J7 Prime), utilizar el botón 'Escanear / Acreditar' para procesar el pase o carga vía puente digital APDU.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun DiagnosticMetricRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = valueColor)
    }
}
