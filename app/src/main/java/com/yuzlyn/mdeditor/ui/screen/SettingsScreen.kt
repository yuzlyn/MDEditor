package com.yuzlyn.mdeditor.ui.screen

import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yuzlyn.mdeditor.MainActivity
import com.yuzlyn.mdeditor.R
import com.yuzlyn.mdeditor.data.MonetPalette
import com.yuzlyn.mdeditor.data.ThemeConfig
import java.util.Locale

private data class LanguageOption(val tag: String, val labelResId: Int)

private val languageOptions =
        listOf(
                LanguageOption("en", R.string.lang_en),
                LanguageOption("zh-CN", R.string.lang_zh_cn),
                LanguageOption("zh-TW", R.string.lang_zh_tw),
                LanguageOption("fr", R.string.lang_fr)
        )

private fun getCurrentLocaleTag(): String {
  val tag = Locale.getDefault().toLanguageTag()
  return languageOptions.find { it.tag == tag }?.tag ?: "en"
}

private data class MonetThemeColor(val labelResId: Int, val colorLong: Long)

private val themeColorOptions =
        MonetPalette.entries.drop(1).map { MonetThemeColor(it.labelResId, it.bgColorLong) }

@Composable
fun SettingsScreen(onBack: () -> Unit) {
  var subScreen by remember { mutableStateOf<String?>(null) }

  if (subScreen == null) {
    SettingsMainScreen(
            onBack = onBack,
            onLanguageClick = { subScreen = "language" },
            onThemeClick = { subScreen = "theme" }
    )
  } else if (subScreen == "language") {
    LanguageSelectionScreen(onBack = { subScreen = null })
  } else {
    ThemeSelectionScreen(onBack = { subScreen = null })
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsMainScreen(
        onBack: () -> Unit,
        onLanguageClick: () -> Unit,
        onThemeClick: () -> Unit
) {
  val currentTag = getCurrentLocaleTag()
  val currentLangLabel =
          languageOptions.find { it.tag == currentTag }?.labelResId ?: R.string.lang_en
  val context = LocalContext.current
  val themeConfig by ThemeConfig.configFlow.collectAsState()
  var cardVisible by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) { cardVisible = true }

  val themeStatusLabel =
          when (themeConfig) {
            is ThemeConfig.SystemDefault -> stringResource(R.string.theme_mode_system)
            is ThemeConfig.DynamicMonet -> stringResource(R.string.theme_mode_dynamic)
            is ThemeConfig.CustomColor -> {
              val entry =
                      MonetPalette.entries.find {
                        it.bgColorLong == (themeConfig as ThemeConfig.CustomColor).colorLong
                      }
              val name = entry?.labelResId?.let { stringResource(it) } ?: ""
              stringResource(R.string.theme_mode_custom, name)
            }
          }

  Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
    Row(
            modifier =
                    Modifier.fillMaxWidth()
                            .statusBarsPadding()
                            .height(56.dp)
                            .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = onBack) {
        Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
      Text(
              stringResource(R.string.drawer_settings),
              style = MaterialTheme.typography.titleLarge,
              color = MaterialTheme.colorScheme.onSurface
      )
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
      Text(
              stringResource(R.string.settings_language),
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(vertical = 12.dp)
      )
      Text(
              stringResource(R.string.settings_language_desc),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(bottom = 8.dp)
      )
      HorizontalDivider()

      ProfileFlyInCard(visible = cardVisible, delayMs = 80L) {
        Row(
                modifier =
                        Modifier.fillMaxWidth()
                                .clickable { onLanguageClick() }
                                .padding(horizontal = 4.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
                  Icons.Default.Language,
                  contentDescription = null,
                  modifier = Modifier.size(24.dp),
                  tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(Modifier.width(16.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
                    stringResource(R.string.settings_language),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                    stringResource(currentLangLabel),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          Icon(
                  Icons.AutoMirrored.Filled.KeyboardArrowRight,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(20.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
              stringResource(R.string.settings_theme),
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(vertical = 12.dp)
      )
      Text(
              stringResource(R.string.settings_theme_desc),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(bottom = 8.dp)
      )
      HorizontalDivider()

      ProfileFlyInCard(visible = cardVisible, delayMs = 120L) {
        Row(
                modifier =
                        Modifier.fillMaxWidth()
                                .clickable { onThemeClick() }
                                .padding(horizontal = 4.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
                  Icons.Default.Palette,
                  contentDescription = null,
                  modifier = Modifier.size(24.dp),
                  tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(Modifier.width(16.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
                    stringResource(R.string.settings_theme),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                    themeStatusLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          Icon(
                  Icons.AutoMirrored.Filled.KeyboardArrowRight,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeSelectionScreen(onBack: () -> Unit) {
  val context = LocalContext.current
  val themeConfig by ThemeConfig.configFlow.collectAsState()
  var cardVisible by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) { cardVisible = true }

  val selectedCustomColor =
          if (themeConfig is ThemeConfig.CustomColor)
                  (themeConfig as ThemeConfig.CustomColor).colorLong
          else 0L

  Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
    Row(
            modifier =
                    Modifier.fillMaxWidth()
                            .statusBarsPadding()
                            .height(56.dp)
                            .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = onBack) {
        Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
      Text(
              stringResource(R.string.settings_theme),
              style = MaterialTheme.typography.titleLarge,
              color = MaterialTheme.colorScheme.onSurface
      )
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
      ProfileFlyInCard(visible = cardVisible, delayMs = 50L) {
        Text(
                stringResource(R.string.theme_system_default),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 12.dp)
        )
      }

      ProfileFlyInCard(visible = cardVisible, delayMs = 80L) {
        ThemeOptionCard(
                label = stringResource(R.string.theme_system_default),
                desc = stringResource(R.string.theme_system_default_desc),
                isSelected = themeConfig is ThemeConfig.SystemDefault,
                onClick = { ThemeConfig.set(ThemeConfig.SystemDefault, context) }
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      ProfileFlyInCard(visible = cardVisible, delayMs = 110L) {
        val monetAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        ThemeOptionCard(
                label = stringResource(R.string.theme_dynamic_monet),
                desc =
                        if (monetAvailable) stringResource(R.string.theme_dynamic_monet_desc)
                        else stringResource(R.string.theme_dynamic_disabled),
                isSelected = themeConfig is ThemeConfig.DynamicMonet,
                enabled = monetAvailable,
                onClick = { if (monetAvailable) ThemeConfig.set(ThemeConfig.DynamicMonet, context) }
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      ProfileFlyInCard(visible = cardVisible, delayMs = 140L) {
        Text(
                stringResource(R.string.theme_monet_palette),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 12.dp)
        )
      }

      ProfileFlyInCard(visible = cardVisible, delayMs = 170L) {
        Row(
                Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
          themeColorOptions.forEachIndexed { index, option ->
            val isSelected =
                    themeConfig is ThemeConfig.CustomColor &&
                            selectedCustomColor == option.colorLong
            val bgColor = Color(option.colorLong.toInt())
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                        modifier =
                                Modifier.size(44.dp)
                                        .clip(CircleShape)
                                        .background(bgColor)
                                        .then(
                                                if (isSelected)
                                                        Modifier.border(
                                                                3.dp,
                                                                MaterialTheme.colorScheme.primary,
                                                                CircleShape
                                                        )
                                                else
                                                        Modifier.border(
                                                                0.5.dp,
                                                                MaterialTheme.colorScheme
                                                                        .outlineVariant,
                                                                CircleShape
                                                        )
                                        )
                                        .clickable {
                                          ThemeConfig.set(
                                                  ThemeConfig.CustomColor(option.colorLong),
                                                  context
                                          )
                                        },
                        contentAlignment = Alignment.Center
                ) {
                  if (isSelected) {
                    Box(
                            Modifier.size(14.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                    )
                  }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                        stringResource(option.labelResId),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(32.dp))
    }
  }
}

@Composable
private fun ThemeOptionCard(
        label: String,
        desc: String,
        isSelected: Boolean,
        enabled: Boolean = true,
        onClick: () -> Unit
) {
  val alpha = if (enabled) 1f else 0.38f
  Surface(
          onClick = onClick,
          enabled = enabled,
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceContainerHigh,
          modifier = Modifier.fillMaxWidth()
  ) {
    Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
                label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
                desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha)
        )
      }
      RadioButton(selected = isSelected, onClick = onClick, enabled = enabled)
    }
  }
}

@Composable
private fun LanguageSelectionScreen(onBack: () -> Unit) {
  val activity = LocalActivity.current
  val context = LocalContext.current
  var selectedTag by remember { mutableStateOf(getCurrentLocaleTag()) }
  var cardVisible by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) { cardVisible = true }

  Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
    Row(
            modifier =
                    Modifier.fillMaxWidth()
                            .statusBarsPadding()
                            .height(56.dp)
                            .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = onBack) {
        Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
      Text(
              stringResource(R.string.settings_language),
              style = MaterialTheme.typography.titleLarge,
              color = MaterialTheme.colorScheme.onSurface
      )
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
      Spacer(modifier = Modifier.height(8.dp))
      languageOptions.forEachIndexed { index, option ->
        ProfileFlyInCard(visible = cardVisible, delayMs = index * 50L + 50L) {
          Row(
                  modifier =
                          Modifier.fillMaxWidth()
                                  .clickable {
                                    selectedTag = option.tag
                                    MainActivity.applyLocale(context, option.tag)
                                    activity?.recreate()
                                  }
                                  .padding(vertical = 12.dp, horizontal = 4.dp),
                  verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
                    Icons.Default.Language,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(16.dp))
            Text(
                    text = stringResource(option.labelResId),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
            )
            RadioButton(
                    selected = selectedTag == option.tag,
                    onClick = {
                      selectedTag = option.tag
                      MainActivity.applyLocale(context, option.tag)
                      activity?.recreate()
                    }
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ProfileFlyInCard(
        visible: Boolean,
        delayMs: Long = 0L,
        content: @Composable () -> Unit
) {
  val animProgress = remember { Animatable(0f) }

  LaunchedEffect(visible) {
    if (visible) {
      kotlinx.coroutines.delay(delayMs)
      animProgress.animateTo(
              targetValue = 1f,
              animationSpec =
                      spring(
                              dampingRatio = Spring.DampingRatioMediumBouncy,
                              stiffness = Spring.StiffnessLow
                      )
      )
    }
  }

  Box(
          modifier =
                  Modifier.graphicsLayer {
                    alpha = animProgress.value
                    translationY = 40.dp.toPx() * (1f - animProgress.value)
                  }
  ) { content() }
}
