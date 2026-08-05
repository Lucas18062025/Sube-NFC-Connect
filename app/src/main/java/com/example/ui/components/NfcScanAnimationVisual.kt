package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SubeBluePrimary
import com.example.ui.theme.SubeCyanAccent
import com.example.ui.theme.SubeMintSuccess
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/**
 * High-tech visual component for NFC card reading and accreditation scan animation.
 * Features:
 * - Animated RF signal wave emission using Compose Canvas
 * - Card approach / tap animation against phone NFC reader
 * - Rotating radar sweep arc
 * - Dynamic status messages (ISO 14443 APDU handshake, Sector auth, Balance read)
 */
@Composable
fun NfcScanAnimationVisual(
    isScanning: Boolean,
    modifier: Modifier = Modifier,
    statusTextOverride: String? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "nfc_scan_waves")

    // Continuous rotation for radar scan ring
    val radarRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_rotation"
    )

    // Wave propagation 1
    val wave1Radius by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave1"
    )

    // Wave propagation 2 (staggered)
    val wave2Radius by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, delayMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave2"
    )

    // Vertical Y-offset for transit card sliding into phone antenna area
    val cardYOffsetAnim by infiniteTransition.animateFloat(
        initialValue = if (isScanning) 35f else 0f,
        targetValue = if (isScanning) -10f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "card_slide"
    )

    // Simulated phase step text cycling during active scanning
    var scanPhaseText by remember { mutableStateOf("Listo para aproximar tarjeta SUBE") }

    LaunchedEffect(isScanning) {
        if (isScanning) {
            val phases = listOf(
                "Detectando campo magnético HF (13.56 MHz)...",
                "Conectando a chip SUBE ISO 14443-A...",
                "Autenticando clave de acceso a sector...",
                "Leyendo bloques de memoria APDU...",
                "Operación NFC completada con éxito"
            )
            for (phase in phases) {
                scanPhaseText = phase
                delay(400)
            }
        } else {
            scanPhaseText = "Listo para aproximar tarjeta SUBE"
        }
    }

    val primaryColor by animateColorAsState(
        targetValue = if (isScanning) SubeMintSuccess else SubeBluePrimary,
        animationSpec = tween(500),
        label = "primary_color"
    )

    val secondaryColor by animateColorAsState(
        targetValue = if (isScanning) SubeCyanAccent else SubeBluePrimary.copy(alpha = 0.6f),
        animationSpec = tween(500),
        label = "secondary_color"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("nfc_scan_animation_visual"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Badge Header
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = primaryColor.copy(alpha = 0.12f),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.RssFeed,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isScanning) "LECTURA NFC EN PROCESO..." else "SENSOR NFC LISTO",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                }
            }

            // Main Animation Canvas Container
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background RF Field Canvas Drawing
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val maxRadius = size.width / 2f

                    // Draw concentric reference rings
                    drawCircle(
                        color = secondaryColor.copy(alpha = 0.15f),
                        radius = maxRadius * 0.9f,
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )
                    drawCircle(
                        color = secondaryColor.copy(alpha = 0.25f),
                        radius = maxRadius * 0.6f,
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Draw animated expanding wave 1
                    val r1 = maxRadius * wave1Radius
                    val alpha1 = (1.0f - wave1Radius).coerceIn(0f, 1f)
                    drawCircle(
                        color = primaryColor.copy(alpha = if (isScanning) alpha1 * 0.7f else alpha1 * 0.3f),
                        radius = r1,
                        center = center,
                        style = Stroke(width = if (isScanning) 4.dp.toPx() else 2.dp.toPx())
                    )

                    // Draw animated expanding wave 2
                    val r2 = maxRadius * wave2Radius
                    val alpha2 = (1.0f - wave2Radius).coerceIn(0f, 1f)
                    drawCircle(
                        color = secondaryColor.copy(alpha = if (isScanning) alpha2 * 0.6f else alpha2 * 0.2f),
                        radius = r2,
                        center = center,
                        style = Stroke(width = 3.dp.toPx())
                    )

                    // Draw rotating radar arc when scanning
                    if (isScanning) {
                        drawArc(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    primaryColor.copy(alpha = 0.3f),
                                    primaryColor
                                ),
                                center = center
                            ),
                            startAngle = radarRotation,
                            sweepAngle = 100f,
                            useCenter = false,
                            topLeft = Offset(center.x - maxRadius * 0.85f, center.y - maxRadius * 0.85f),
                            size = Size(maxRadius * 1.7f, maxRadius * 1.7f),
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }

                // Central Smartphone Representation
                Box(
                    modifier = Modifier
                        .size(100.dp, 150.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(
                            width = 2.dp,
                            color = primaryColor.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // NFC Symbol on Phone Screen
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = primaryColor.copy(alpha = 0.15f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Contactless,
                                contentDescription = "NFC Sensor Target",
                                tint = primaryColor,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "NFC Zone",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Simulated SUBE Card Tapping / Sliding Graphic
                Box(
                    modifier = Modifier
                        .offset { IntOffset(0, cardYOffsetAnim.roundToInt()) }
                        .size(130.dp, 80.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    SubeBluePrimary.copy(alpha = 0.95f),
                                    SubeCyanAccent.copy(alpha = 0.9f)
                                )
                            )
                        )
                        .border(
                            width = 1.5.dp,
                            color = Color.White.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SUBE",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                            Icon(
                                imageVector = Icons.Default.Contactless,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        Text(
                            text = "•••• 6012",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dynamic Terminal / Status Output Line
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    primaryColor.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isScanning) Icons.Default.Nfc else Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = statusTextOverride ?: scanPhaseText,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
