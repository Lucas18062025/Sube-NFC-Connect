package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.SubeCyanAccent
import com.example.ui.theme.SubeMintSuccess

@Composable
fun NfcPulseAnimation(
    isScanning: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 140.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "NfcPulse")
    
    val pulseScale1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isScanning) 1.5f else 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Scale1"
    )

    val pulseScale2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isScanning) 1.8f else 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Scale2"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Alpha"
    )

    Box(
        modifier = modifier.size(size * 1.8f),
        contentAlignment = Alignment.Center
    ) {
        // Outer wave 2
        Box(
            modifier = Modifier
                .size(size)
                .scale(pulseScale2)
                .background(
                    color = (if (isScanning) SubeMintSuccess else SubeCyanAccent).copy(alpha = pulseAlpha * 0.4f),
                    shape = CircleShape
                )
        )

        // Outer wave 1
        Box(
            modifier = Modifier
                .size(size)
                .scale(pulseScale1)
                .background(
                    color = (if (isScanning) SubeMintSuccess else SubeCyanAccent).copy(alpha = pulseAlpha * 0.7f),
                    shape = CircleShape
                )
        )

        // Core Center Sensor Button
        Box(
            modifier = Modifier
                .size(size)
                .background(
                    color = if (isScanning) SubeMintSuccess else SubeCyanAccent,
                    shape = CircleShape
                )
                .border(
                    width = 3.dp,
                    color = Color.White,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isScanning) Icons.Default.Nfc else Icons.Default.Contactless,
                contentDescription = "NFC Pulse Core",
                tint = Color.Black,
                modifier = Modifier.size(size * 0.5f)
            )
        }
    }
}
