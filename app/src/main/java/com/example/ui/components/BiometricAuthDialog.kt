package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.example.ui.theme.SubeBluePrimary
import com.example.ui.theme.SubeMintSuccess
import com.example.util.BiometricHelper

@Composable
fun BiometricAuthDialog(
    actionTitle: String,
    actionDescription: String,
    onDismiss: () -> Unit,
    onAuthenticated: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var pinText by remember { mutableStateOf("") }
    var showPinInput by remember { mutableStateOf(false) }

    val isAvailable = remember { BiometricHelper.isBiometricAvailable(context) }

    fun triggerSystemBiometrics() {
        if (activity != null && isAvailable) {
            BiometricHelper.launchBiometricPrompt(
                activity = activity,
                title = actionTitle,
                subtitle = actionDescription,
                onSuccess = {
                    onAuthenticated()
                },
                onError = { err ->
                    errorMessage = err
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        if (isAvailable && activity != null) {
            triggerSystemBiometrics()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .scale(pulseScale)
                    .background(SubeBluePrimary.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = "Sensor de Huella Digital",
                    tint = SubeBluePrimary,
                    modifier = Modifier.size(36.dp)
                )
            }
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = SubeMintSuccess,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Verificación Biométrica",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Text(
                    text = actionTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SubeBluePrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = actionDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Sensor visual card / Touch simulation area
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isAvailable && activity != null) {
                                triggerSystemBiometrics()
                            } else {
                                onAuthenticated()
                            }
                        }
                        .testTag("touch_sensor_area"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SubeBluePrimary.copy(alpha = 0.08f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = null,
                            tint = SubeBluePrimary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isAvailable) "Toca para abrir Sensor de Huella" else "Simulación Biométrica: Toca aquí para autenticar",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = SubeBluePrimary
                        )
                        Text(
                            text = "Seguridad SUBE de alta resolución",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                AnimatedVisibility(visible = showPinInput) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = pinText,
                            onValueChange = { pinText = it },
                            label = { Text("PIN de Seguridad (ej. 1234)") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_biometric_pin"),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                if (pinText.length >= 4) {
                                    onAuthenticated()
                                } else {
                                    errorMessage = "Ingresá un PIN válido de al menos 4 dígitos"
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = SubeBluePrimary)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Confirmar con PIN")
                        }
                    }
                }

                if (!showPinInput) {
                    OutlinedButton(
                        onClick = { showPinInput = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Usar PIN de respaldo", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isAvailable && activity != null) {
                        triggerSystemBiometrics()
                    } else {
                        onAuthenticated()
                    }
                },
                modifier = Modifier.testTag("btn_biometric_scan_now"),
                colors = ButtonDefaults.buttonColors(containerColor = SubeBluePrimary)
            ) {
                Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Escanear Huella")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
