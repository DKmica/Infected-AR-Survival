package com.infected.ar.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val InfectedColors = darkColorScheme(
    primary = NeonRed,
    secondary = ToxicGreen,
    background = AbyssBlack,
    surface = HorrorSurface
)

@Composable
fun InfectedTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = InfectedColors,
        typography = Typography,
        content = content
    )
}
