package com.yuzlyn.mdeditor.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.yuzlyn.mdeditor.data.MonetPalette
import com.yuzlyn.mdeditor.data.ThemeConfig

private val DefaultLightColorScheme =
        lightColorScheme(
                primary = md_theme_light_primary,
                onPrimary = md_theme_light_onPrimary,
                primaryContainer = md_theme_light_primaryContainer,
                onPrimaryContainer = md_theme_light_onPrimaryContainer,
                secondary = md_theme_light_secondary,
                onSecondary = md_theme_light_onSecondary,
                secondaryContainer = md_theme_light_secondaryContainer,
                onSecondaryContainer = md_theme_light_onSecondaryContainer,
                tertiary = md_theme_light_tertiary,
                onTertiary = md_theme_light_onTertiary,
                tertiaryContainer = md_theme_light_tertiaryContainer,
                onTertiaryContainer = md_theme_light_onTertiaryContainer,
                error = md_theme_light_error,
                onError = md_theme_light_onError,
                errorContainer = md_theme_light_errorContainer,
                onErrorContainer = md_theme_light_onErrorContainer,
                background = md_theme_light_background,
                onBackground = md_theme_light_onBackground,
                surface = md_theme_light_surface,
                onSurface = md_theme_light_onSurface,
                surfaceVariant = md_theme_light_surfaceVariant,
                onSurfaceVariant = md_theme_light_onSurfaceVariant,
                outline = md_theme_light_outline,
                outlineVariant = md_theme_light_outlineVariant,
                inverseSurface = md_theme_light_inverseSurface,
                inverseOnSurface = md_theme_light_inverseOnSurface,
                inversePrimary = md_theme_light_inversePrimary,
                surfaceTint = md_theme_light_surfaceTint,
                surfaceContainerHighest = md_theme_light_surfaceContainerHighest,
                surfaceContainerHigh = md_theme_light_surfaceContainerHigh,
                surfaceContainer = md_theme_light_surfaceContainer,
                surfaceContainerLow = md_theme_light_surfaceContainerLow,
                surfaceContainerLowest = md_theme_light_surfaceContainerLowest,
        )

private val DefaultDarkColorScheme =
        darkColorScheme(
                primary = md_theme_dark_primary,
                onPrimary = md_theme_dark_onPrimary,
                primaryContainer = md_theme_dark_primaryContainer,
                onPrimaryContainer = md_theme_dark_onPrimaryContainer,
                secondary = md_theme_dark_secondary,
                onSecondary = md_theme_dark_onSecondary,
                secondaryContainer = md_theme_dark_secondaryContainer,
                onSecondaryContainer = md_theme_dark_onSecondaryContainer,
                tertiary = md_theme_dark_tertiary,
                onTertiary = md_theme_dark_onTertiary,
                tertiaryContainer = md_theme_dark_tertiaryContainer,
                onTertiaryContainer = md_theme_dark_onTertiaryContainer,
                error = md_theme_dark_error,
                onError = md_theme_dark_onError,
                errorContainer = md_theme_dark_errorContainer,
                onErrorContainer = md_theme_dark_onErrorContainer,
                background = md_theme_dark_background,
                onBackground = md_theme_dark_onBackground,
                surface = md_theme_dark_surface,
                onSurface = md_theme_dark_onSurface,
                surfaceVariant = md_theme_dark_surfaceVariant,
                onSurfaceVariant = md_theme_dark_onSurfaceVariant,
                outline = md_theme_dark_outline,
                outlineVariant = md_theme_dark_outlineVariant,
                inverseSurface = md_theme_dark_inverseSurface,
                inverseOnSurface = md_theme_dark_inverseOnSurface,
                inversePrimary = md_theme_dark_inversePrimary,
                surfaceTint = md_theme_dark_surfaceTint,
                surfaceContainerHighest = md_theme_dark_surfaceContainerHighest,
                surfaceContainerHigh = md_theme_dark_surfaceContainerHigh,
                surfaceContainer = md_theme_dark_surfaceContainer,
                surfaceContainerLow = md_theme_dark_surfaceContainerLow,
                surfaceContainerLowest = md_theme_dark_surfaceContainerLowest,
        )

private val GoogleBlueLightColorScheme =
        lightColorScheme(
                primary = google_blue_light_primary,
                onPrimary = google_blue_light_onPrimary,
                primaryContainer = google_blue_light_primaryContainer,
                onPrimaryContainer = google_blue_light_onPrimaryContainer,
                secondary = google_blue_light_secondary,
                onSecondary = google_blue_light_onSecondary,
                secondaryContainer = google_blue_light_secondaryContainer,
                onSecondaryContainer = google_blue_light_onSecondaryContainer,
                tertiary = google_blue_light_tertiary,
                onTertiary = google_blue_light_onTertiary,
                tertiaryContainer = google_blue_light_tertiaryContainer,
                onTertiaryContainer = google_blue_light_onTertiaryContainer,
                error = google_blue_light_error,
                onError = google_blue_light_onError,
                errorContainer = google_blue_light_errorContainer,
                onErrorContainer = google_blue_light_onErrorContainer,
                background = google_blue_light_background,
                onBackground = google_blue_light_onBackground,
                surface = google_blue_light_surface,
                onSurface = google_blue_light_onSurface,
                surfaceVariant = google_blue_light_surfaceVariant,
                onSurfaceVariant = google_blue_light_onSurfaceVariant,
                outline = google_blue_light_outline,
                outlineVariant = google_blue_light_outlineVariant,
                inverseSurface = google_blue_light_inverseSurface,
                inverseOnSurface = google_blue_light_inverseOnSurface,
                inversePrimary = google_blue_light_inversePrimary,
                surfaceTint = google_blue_light_surfaceTint,
                surfaceContainerHighest = google_blue_light_surfaceContainerHighest,
                surfaceContainerHigh = google_blue_light_surfaceContainerHigh,
                surfaceContainer = google_blue_light_surfaceContainer,
                surfaceContainerLow = google_blue_light_surfaceContainerLow,
                surfaceContainerLowest = google_blue_light_surfaceContainerLowest,
        )

private val GoogleBlueDarkColorScheme =
        darkColorScheme(
                primary = google_blue_dark_primary,
                onPrimary = google_blue_dark_onPrimary,
                primaryContainer = google_blue_dark_primaryContainer,
                onPrimaryContainer = google_blue_dark_onPrimaryContainer,
                secondary = google_blue_dark_secondary,
                onSecondary = google_blue_dark_onSecondary,
                secondaryContainer = google_blue_dark_secondaryContainer,
                onSecondaryContainer = google_blue_dark_onSecondaryContainer,
                tertiary = google_blue_dark_tertiary,
                onTertiary = google_blue_dark_onTertiary,
                tertiaryContainer = google_blue_dark_tertiaryContainer,
                onTertiaryContainer = google_blue_dark_onTertiaryContainer,
                error = google_blue_dark_error,
                onError = google_blue_dark_onError,
                errorContainer = google_blue_dark_errorContainer,
                onErrorContainer = google_blue_dark_onErrorContainer,
                background = google_blue_dark_background,
                onBackground = google_blue_dark_onBackground,
                surface = google_blue_dark_surface,
                onSurface = google_blue_dark_onSurface,
                surfaceVariant = google_blue_dark_surfaceVariant,
                onSurfaceVariant = google_blue_dark_onSurfaceVariant,
                outline = google_blue_dark_outline,
                outlineVariant = google_blue_dark_outlineVariant,
                inverseSurface = google_blue_dark_inverseSurface,
                inverseOnSurface = google_blue_dark_inverseOnSurface,
                inversePrimary = google_blue_dark_inversePrimary,
                surfaceTint = google_blue_dark_surfaceTint,
                surfaceContainerHighest = google_blue_dark_surfaceContainerHighest,
                surfaceContainerHigh = google_blue_dark_surfaceContainerHigh,
                surfaceContainer = google_blue_dark_surfaceContainer,
                surfaceContainerLow = google_blue_dark_surfaceContainerLow,
                surfaceContainerLowest = google_blue_dark_surfaceContainerLowest,
        )

private fun lerpColor(a: Color, b: Color, fraction: Float): Color {
  val aRed = a.red * 255f
  val aGreen = a.green * 255f
  val aBlue = a.blue * 255f
  val bRed = b.red * 255f
  val bGreen = b.green * 255f
  val bBlue = b.blue * 255f
  return Color(
          red = (aRed + (bRed - aRed) * fraction).coerceIn(0f, 255f) / 255f,
          green = (aGreen + (bGreen - aGreen) * fraction).coerceIn(0f, 255f) / 255f,
          blue = (aBlue + (bBlue - aBlue) * fraction).coerceIn(0f, 255f) / 255f,
          alpha = 1f
  )
}

private fun customColorScheme(
        colorKey: Long,
        darkTheme: Boolean
): androidx.compose.material3.ColorScheme {
  val entry = MonetPalette.entryForBgColor(colorKey)
  val container =
          if (entry != null) {
            if (darkTheme) entry.darkContainer else entry.lightContainer
          } else if (darkTheme) Color(0xFF0F0D13) else Color(0xFFFFFBFE)
  val onContainer =
          if (entry != null) {
            if (darkTheme) entry.darkOnContainer else entry.lightOnContainer
          } else if (darkTheme) Color(0xFFE6E1E5) else Color(0xFF1C1B1F)

  if (darkTheme) {
    val base = Color(0xFF0F0D13)
    return darkColorScheme(
            primary = onContainer,
            onPrimary = Color(0xFF0F0D13),
            primaryContainer = container.copy(alpha = 0.3f),
            onPrimaryContainer = Color(0xFFE0E0E0),
            secondary = Color(0xFFCCC2DC),
            onSecondary = Color(0xFF332D41),
            secondaryContainer = lerpColor(base, container, 0.5f),
            onSecondaryContainer = Color(0xFFE0E0E0),
            tertiary = Color(0xFFE8B9CC),
            onTertiary = Color(0xFF442C36),
            tertiaryContainer = lerpColor(base, container, 0.4f),
            onTertiaryContainer = Color(0xFFE0E0E0),
            error = google_blue_dark_error,
            onError = google_blue_dark_onError,
            errorContainer = google_blue_dark_errorContainer,
            onErrorContainer = google_blue_dark_onErrorContainer,
            background = container,
            onBackground = Color(0xFFE6E1E5),
            surface = container,
            onSurface = Color(0xFFE6E1E5),
            surfaceVariant = lerpColor(base, container, 0.6f),
            onSurfaceVariant = Color(0xFFCAC4D0),
            outline = onContainer.copy(alpha = 0.5f),
            outlineVariant = onContainer.copy(alpha = 0.2f),
            inverseSurface = Color(0xFFE6E1E5),
            inverseOnSurface = Color(0xFF313033),
            inversePrimary = onContainer,
            surfaceTint = onContainer,
            surfaceContainerHighest = lerpColor(base, container, 1.0f),
            surfaceContainerHigh = lerpColor(base, container, 0.85f),
            surfaceContainer = lerpColor(base, container, 0.7f),
            surfaceContainerLow = lerpColor(base, container, 0.5f),
            surfaceContainerLowest = lerpColor(base, container, 0.3f),
    )
  } else {
    val base = Color.White
    return lightColorScheme(
            primary = onContainer,
            onPrimary = Color.White,
            primaryContainer = lerpColor(base, container, 0.12f),
            onPrimaryContainer = Color(0xFF1C1B1F),
            secondary = Color(0xFF625B71),
            onSecondary = Color.White,
            secondaryContainer = lerpColor(base, container, 0.15f),
            onSecondaryContainer = Color(0xFF1C1B1F),
            tertiary = Color(0xFF7D5260),
            onTertiary = Color.White,
            tertiaryContainer = lerpColor(base, container, 0.12f),
            onTertiaryContainer = Color(0xFF1C1B1F),
            error = google_blue_light_error,
            onError = google_blue_light_onError,
            errorContainer = google_blue_light_errorContainer,
            onErrorContainer = google_blue_light_onErrorContainer,
            background = container,
            onBackground = Color(0xFF1C1B1F),
            surface = container,
            onSurface = Color(0xFF1C1B1F),
            surfaceVariant = lerpColor(base, container, 0.25f),
            onSurfaceVariant = Color(0xFF49454F),
            outline = onContainer.copy(alpha = 0.55f),
            outlineVariant = onContainer.copy(alpha = 0.15f),
            inverseSurface = Color(0xFF313033),
            inverseOnSurface = Color(0xFFF4EFF4),
            inversePrimary = lerpColor(onContainer, Color.White, 0.3f),
            surfaceTint = onContainer,
            surfaceContainerHighest = lerpColor(base, container, 0.35f),
            surfaceContainerHigh = lerpColor(base, container, 0.25f),
            surfaceContainer = lerpColor(base, container, 0.18f),
            surfaceContainerLow = lerpColor(base, container, 0.10f),
            surfaceContainerLowest = lerpColor(base, container, 0.05f),
    )
  }
}

private fun ensureContrast(
        scheme: androidx.compose.material3.ColorScheme,
        darkTheme: Boolean
): androidx.compose.material3.ColorScheme {
  if (darkTheme) {
    return scheme.copy(
            onSurface = Color(0xFFE6E1E5),
            onBackground = Color(0xFFE6E1E5),
            onSurfaceVariant = Color(0xFFCAC4D0),
            onPrimaryContainer = Color(0xFFE0E0E0),
            onSecondaryContainer = Color(0xFFE0E0E0),
            onTertiaryContainer = Color(0xFFE0E0E0),
    )
  } else {
    return scheme.copy(
            onSurface = Color(0xFF1C1B1F),
            onBackground = Color(0xFF1C1B1F),
            onSurfaceVariant = Color(0xFF49454F),
            onPrimaryContainer = Color(0xFF1C1B1F),
            onSecondaryContainer = Color(0xFF1C1B1F),
            onTertiaryContainer = Color(0xFF1C1B1F),
    )
  }
}

private val AppShapes =
        Shapes(
                small = RoundedCornerShape(8.dp),
                medium = RoundedCornerShape(12.dp),
                large = RoundedCornerShape(16.dp),
                extraLarge = RoundedCornerShape(24.dp),
        )

@Composable
fun MDEditorTheme(
        darkTheme: Boolean = isSystemInDarkTheme(),
        dynamicColor: Boolean = false,
        content: @Composable () -> Unit
) {
  val context = LocalContext.current
  val themeConfig by ThemeConfig.configFlow.collectAsState()

  val colorScheme =
          when (val config = themeConfig) {
            is ThemeConfig.DynamicMonet -> {
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val raw =
                        if (darkTheme) dynamicDarkColorScheme(context)
                        else dynamicLightColorScheme(context)
                ensureContrast(raw, darkTheme)
              } else {
                if (darkTheme) GoogleBlueDarkColorScheme else GoogleBlueLightColorScheme
              }
            }
            is ThemeConfig.SystemDefault -> {
              if (darkTheme) GoogleBlueDarkColorScheme else GoogleBlueLightColorScheme
            }
            is ThemeConfig.CustomColor -> {
              customColorScheme(config.colorLong, darkTheme)
            }
          }

  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }
  }

  MaterialTheme(
          colorScheme = colorScheme,
          shapes = AppShapes,
          typography = AppTypography,
          content = content
  )
}
