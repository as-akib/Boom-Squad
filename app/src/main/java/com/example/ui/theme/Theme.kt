package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColorfulArcadeColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = Color(0xFF003640),
    primaryContainer = Color(0xFF004E5D),
    onPrimaryContainer = Color(0xFFBCE9FF),
    secondary = BrightYellow,
    onSecondary = Color(0xFF403000),
    secondaryContainer = Color(0xFF5B4500),
    onSecondaryContainer = Color(0xFFFFE082),
    tertiary = HotPink,
    onTertiary = Color(0xFF4D0026),
    tertiaryContainer = Color(0xFF700039),
    onTertiaryContainer = Color(0xFFFFD8E7),
    background = ArcadeBg,
    onBackground = ImmersiveText,
    surface = ArcadeCard,
    onSurface = ImmersiveText,
    surfaceVariant = Color(0xFF2E2756),
    onSurfaceVariant = Color(0xFFE2E8F0),
    outline = ArcadeCardBorder,
    error = DangerRed,
    onError = Color(0xFF601410)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ColorfulArcadeColorScheme,
        typography = Typography,
        content = content
    )
}
