package com.fhws.transport.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// FHWS Transport brand — Pantone Process Purple (websafe #993399).
val BrandPurple = Color(0xFF993399)
val BrandPurpleDark = Color(0xFF802B80)
val BrandPurpleLight = Color(0xFFA44CA4)
val BrandTint = Color(0xFFF3E0F3)
val AppBackground = Color(0xFFF7F7F9)

private val LightColors = lightColorScheme(
    primary = BrandPurple,
    onPrimary = Color.White,
    primaryContainer = BrandTint,
    onPrimaryContainer = BrandPurpleDark,
    secondary = BrandPurpleLight,
    onSecondary = Color.White,
    background = AppBackground,
    surface = Color.White,
    surfaceVariant = Color(0xFFF1EDF1),
    onSurface = Color(0xFF1D1D1F),
    onSurfaceVariant = Color(0xFF6E6E73),
)

@Composable
fun FhwsTransportTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightColors, content = content)
}