package com.duoc.visionsyncc1.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AzulPrimarioOscuro,
    onPrimary = Color(0xFF111A66),
    primaryContainer = AzulContenedorOscuro,
    onPrimaryContainer = SobreAzulContenedorOscuro,
    secondary = Color(0xFF9DC0FF),
    onSecondary = Color(0xFF00296B),
    tertiary = VerdeExitoOscuro,
    onTertiary = Color(0xFF00201F),
    background = FondoOscuro,
    onBackground = TextoPrincipalOscuro,
    surface = SuperficieOscura,
    onSurface = TextoPrincipalOscuro,
    surfaceVariant = Color(0xFF2B3A42),
    onSurfaceVariant = TextoSecundarioOscuro,
    surfaceContainerLowest = Color(0xFF0C1317),
    surfaceContainerLow = SuperficieOscura,
    surfaceContainer = Color(0xFF1E2A31),
    surfaceContainerHigh = Color(0xFF243139),
    surfaceContainerHighest = Color(0xFF2B3A42),
    outline = BordeOscuro,
    outlineVariant = Color(0xFF3F4E57),
    error = RojoErrorOscuro,
    onError = Color(0xFF690005)
)

private val LightColorScheme = lightColorScheme(
    primary = AzulPrimario,
    onPrimary = Color.White,
    primaryContainer = AzulPrimarioContenedor,
    onPrimaryContainer = SobreAzulContenedor,
    secondary = AzulSecundario,
    onSecondary = Color.White,
    tertiary = VerdeExito,
    onTertiary = Color.White,
    background = FondoClaro,
    onBackground = TextoPrincipal,
    surface = SuperficieClara,
    onSurface = TextoPrincipal,
    surfaceVariant = Color(0xFFE6E1E0),
    onSurfaceVariant = TextoSecundario,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = FondoClaro,
    surfaceContainer = Color(0xFFF2EEED),
    surfaceContainerHigh = Color(0xFFECE8E7),
    surfaceContainerHighest = Color(0xFFE6E1E0),
    outline = BordeClaro,
    outlineVariant = Color(0xFFC9D0D4),
    error = RojoError,
    onError = Color.White
)

@Composable
fun VisionSyncTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Desactivado a propósito: el color dinámico cambia según el teléfono
    // y no permite garantizar el contraste WCAG AA.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}