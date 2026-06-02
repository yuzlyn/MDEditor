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

private fun customColorScheme(seedColor: Color, darkTheme: Boolean) =
        if (darkTheme) {
          val primary =
                  Color(
                          red = (seedColor.red * 0.7f + 0.3f).coerceIn(0f, 1f),
                          green = (seedColor.green * 0.7f + 0.3f).coerceIn(0f, 1f),
                          blue = (seedColor.blue * 0.7f + 0.3f).coerceIn(0f, 1f)
                  )
          val baseSurface = Color(0xFF0F0D13)
          val tintedSurface = lerpColor(baseSurface, seedColor, 0.06f)
          val tintedSurfaceVariant = lerpColor(baseSurface, seedColor, 0.12f)
          val tintedOutline = lerpColor(tintedSurfaceVariant, primary, 0.3f)
          darkColorScheme(
                  primary = primary,
                  onPrimary = Color(0xFF0F0D13),
                  primaryContainer = lerpColor(baseSurface, seedColor, 0.25f),
                  onPrimaryContainer = lerpColor(primary, Color.White, 0.3f),
                  secondary = lerpColor(tintedSurfaceVariant, seedColor, 0.4f),
                  onSecondary = Color(0xFF0F0D13),
                  secondaryContainer = lerpColor(baseSurface, seedColor, 0.15f),
                  onSecondaryContainer =
                          lerpColor(tintedSurfaceVariant, seedColor, 0.7f).copy(alpha = 1f),
                  tertiary =
                          Color(
                                  red =
                                          (seedColor.green * 0.6f + seedColor.blue * 0.4f).coerceIn(
                                                  0f,
                                                  1f
                                          ),
                                  green =
                                          (seedColor.blue * 0.6f + seedColor.red * 0.4f).coerceIn(
                                                  0f,
                                                  1f
                                          ),
                                  blue =
                                          (seedColor.red * 0.6f + seedColor.green * 0.4f).coerceIn(
                                                  0f,
                                                  1f
                                          )
                          ),
                  onTertiary = Color(0xFF0F0D13),
                  tertiaryContainer = lerpColor(baseSurface, seedColor, 0.18f),
                  onTertiaryContainer = lerpColor(tintedSurfaceVariant, seedColor, 0.6f),
                  error = google_blue_dark_error,
                  onError = google_blue_dark_onError,
                  errorContainer = google_blue_dark_errorContainer,
                  onErrorContainer = google_blue_dark_onErrorContainer,
                  background = tintedSurface,
                  onBackground = lerpColor(tintedSurface, Color.White, 0.85f),
                  surface = tintedSurface,
                  onSurface = lerpColor(tintedSurface, Color.White, 0.85f),
                  surfaceVariant = tintedSurfaceVariant,
                  onSurfaceVariant = lerpColor(tintedSurfaceVariant, Color.White, 0.7f),
                  outline = tintedOutline,
                  outlineVariant =
                          tintedSurfaceVariant.copy(
                                  red = (tintedSurfaceVariant.red * 1.15f).coerceIn(0f, 1f),
                                  green = (tintedSurfaceVariant.green * 1.15f).coerceIn(0f, 1f),
                                  blue = (tintedSurfaceVariant.blue * 1.15f).coerceIn(0f, 1f)
                          ),
                  inverseSurface = lerpColor(tintedSurface, Color.White, 0.85f),
                  inverseOnSurface = lerpColor(tintedSurface, Color.Black, 0.2f),
                  inversePrimary = seedColor,
                  surfaceTint = primary,
                  surfaceContainerHighest = lerpColor(baseSurface, seedColor, 0.16f),
                  surfaceContainerHigh = lerpColor(baseSurface, seedColor, 0.12f),
                  surfaceContainer = lerpColor(baseSurface, seedColor, 0.09f),
                  surfaceContainerLow = lerpColor(baseSurface, seedColor, 0.06f),
                  surfaceContainerLowest = lerpColor(baseSurface, seedColor, 0.03f),
          )
        } else {
          val baseSurface = Color.White
          val tintedBackground = lerpColor(baseSurface, seedColor, 0.015f)
          val tintedSurfaceLowest = lerpColor(baseSurface, seedColor, 0.03f)
          val tintedSurfaceLow = lerpColor(baseSurface, seedColor, 0.06f)
          val tintedSurface = lerpColor(baseSurface, seedColor, 0.09f)
          val tintedSurfaceHigh = lerpColor(baseSurface, seedColor, 0.12f)
          val tintedSurfaceHighest = lerpColor(baseSurface, seedColor, 0.16f)
          val tintedSurfaceVariant = lerpColor(baseSurface, seedColor, 0.10f)
          val onSurfaceBase = Color(0xFF1C1B1F)
          lightColorScheme(
                  primary = seedColor,
                  onPrimary = Color.White,
                  primaryContainer = lerpColor(baseSurface, seedColor, 0.12f),
                  onPrimaryContainer = lerpColor(seedColor, Color.Black, 0.55f),
                  secondary = lerpColor(tintedSurfaceVariant, seedColor, 0.35f),
                  onSecondary = Color.White,
                  secondaryContainer = lerpColor(baseSurface, seedColor, 0.07f),
                  onSecondaryContainer = lerpColor(seedColor, Color.Black, 0.6f),
                  tertiary =
                          Color(
                                  red =
                                          (seedColor.green * 0.5f + seedColor.blue * 0.5f).coerceIn(
                                                  0f,
                                                  1f
                                          ),
                                  green =
                                          (seedColor.blue * 0.5f + seedColor.red * 0.5f).coerceIn(
                                                  0f,
                                                  1f
                                          ),
                                  blue =
                                          (seedColor.red * 0.5f + seedColor.green * 0.5f).coerceIn(
                                                  0f,
                                                  1f
                                          )
                          ),
                  onTertiary = Color.White,
                  tertiaryContainer = lerpColor(baseSurface, seedColor, 0.09f),
                  onTertiaryContainer = lerpColor(seedColor, Color.Black, 0.55f),
                  error = google_blue_light_error,
                  onError = google_blue_light_onError,
                  errorContainer = google_blue_light_errorContainer,
                  onErrorContainer = google_blue_light_onErrorContainer,
                  background = tintedBackground,
                  onBackground = onSurfaceBase,
                  surface = tintedBackground,
                  onSurface = onSurfaceBase,
                  surfaceVariant = lerpColor(baseSurface, seedColor, 0.10f),
                  onSurfaceVariant = lerpColor(tintedSurfaceVariant, Color.Black, 0.6f),
                  outline = lerpColor(tintedSurfaceVariant, Color.Black, 0.5f),
                  outlineVariant = lerpColor(baseSurface, seedColor, 0.08f),
                  inverseSurface = Color(0xFF313033),
                  inverseOnSurface = lerpColor(baseSurface, seedColor, 0.06f),
                  inversePrimary = lerpColor(seedColor, Color.White, 0.3f),
                  surfaceTint = seedColor,
                  surfaceContainerHighest = tintedSurfaceHighest,
                  surfaceContainerHigh = tintedSurfaceHigh,
                  surfaceContainer = tintedSurface,
                  surfaceContainerLow = tintedSurfaceLow,
                  surfaceContainerLowest = tintedSurfaceLowest,
          )
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
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
              } else {
                if (darkTheme) GoogleBlueDarkColorScheme else GoogleBlueLightColorScheme
              }
            }
            is ThemeConfig.SystemDefault -> {
              if (darkTheme) GoogleBlueDarkColorScheme else GoogleBlueLightColorScheme
            }
            is ThemeConfig.CustomColor -> {
              val seed = Color(config.colorLong.toInt())
              customColorScheme(seed, darkTheme)
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
