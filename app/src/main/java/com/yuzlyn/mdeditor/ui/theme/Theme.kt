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

private fun customColorScheme(seedColor: Color, darkTheme: Boolean) =
        if (darkTheme) {
          val darkPrimary = seedColor.copy(alpha = 0.8f)
          darkColorScheme(
                  primary = darkPrimary,
                  onPrimary = Color(0xFF00325C),
                  primaryContainer = seedColor.copy(alpha = 0.3f),
                  onPrimaryContainer = seedColor.copy(alpha = 0.9f),
                  secondary = seedColor.copy(alpha = 0.65f),
                  onSecondary = Color(0xFF00325C),
                  secondaryContainer = seedColor.copy(alpha = 0.2f),
                  onSecondaryContainer = seedColor.copy(alpha = 0.85f),
                  tertiary = seedColor.copy(red = seedColor.red * 0.7f, alpha = 0.7f),
                  onTertiary = Color(0xFF3E001D),
                  tertiaryContainer = seedColor.copy(red = seedColor.red * 0.7f, alpha = 0.25f),
                  onTertiaryContainer = seedColor.copy(red = seedColor.red * 0.7f, alpha = 0.85f),
                  error = google_blue_dark_error,
                  onError = google_blue_dark_onError,
                  errorContainer = google_blue_dark_errorContainer,
                  onErrorContainer = google_blue_dark_onErrorContainer,
                  background = Color(0xFF1C1B1F),
                  onBackground = Color(0xFFE6E1E5),
                  surface = Color(0xFF1C1B1F),
                  onSurface = Color(0xFFE6E1E5),
                  surfaceVariant = Color(0xFF49454F),
                  onSurfaceVariant = Color(0xFFCAC4D0),
                  outline = Color(0xFF938F99),
                  outlineVariant = Color(0xFF49454F),
                  inverseSurface = Color(0xFFE6E1E5),
                  inverseOnSurface = Color(0xFF313033),
                  inversePrimary = seedColor,
                  surfaceTint = darkPrimary,
                  surfaceContainerHighest = Color(0xFF333236),
                  surfaceContainerHigh = Color(0xFF2D2B30),
                  surfaceContainer = Color(0xFF27262A),
                  surfaceContainerLow = Color(0xFF222125),
                  surfaceContainerLowest = Color(0xFF0F0D13),
          )
        } else {
          lightColorScheme(
                  primary = seedColor,
                  onPrimary = Color.White,
                  primaryContainer = seedColor.copy(alpha = 0.15f),
                  onPrimaryContainer = seedColor.copy(red = seedColor.red * 0.3f, green = seedColor.green * 0.3f, blue = seedColor.blue * 0.5f),
                  secondary = seedColor.copy(alpha = 0.6f),
                  onSecondary = Color.White,
                  secondaryContainer = seedColor.copy(alpha = 0.1f),
                  onSecondaryContainer = seedColor.copy(red = seedColor.red * 0.35f, green = seedColor.green * 0.35f, blue = seedColor.blue * 0.55f),
                  tertiary = seedColor.copy(red = seedColor.red * 0.7f),
                  onTertiary = Color.White,
                  tertiaryContainer = seedColor.copy(red = seedColor.red * 0.7f, alpha = 0.12f),
                  onTertiaryContainer = seedColor.copy(red = seedColor.red * 0.3f, alpha = 0.8f),
                  error = google_blue_light_error,
                  onError = google_blue_light_onError,
                  errorContainer = google_blue_light_errorContainer,
                  onErrorContainer = google_blue_light_onErrorContainer,
                  background = Color(0xFFFFFBFE),
                  onBackground = Color(0xFF1C1B1F),
                  surface = Color(0xFFFFFBFE),
                  onSurface = Color(0xFF1C1B1F),
                  surfaceVariant = Color(0xFFE7E0EC),
                  onSurfaceVariant = Color(0xFF49454F),
                  outline = Color(0xFF79747E),
                  outlineVariant = Color(0xFFCAC4D0),
                  inverseSurface = Color(0xFF313033),
                  inverseOnSurface = Color(0xFFF4EFF4),
                  inversePrimary = seedColor.copy(alpha = 0.7f),
                  surfaceTint = seedColor,
                  surfaceContainerHighest = Color(0xFFE6E0E9),
                  surfaceContainerHigh = Color(0xFFECE6F0),
                  surfaceContainer = Color(0xFFF3EDF7),
                  surfaceContainerLow = Color(0xFFF7F2FA),
                  surfaceContainerLowest = Color.White,
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
