package com.yuzlyn.mdeditor.ui.screen

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yuzlyn.mdeditor.MainActivity
import com.yuzlyn.mdeditor.R
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

@Composable
fun SettingsScreen(onBack: () -> Unit) {
  var subScreen by remember { mutableStateOf<String?>(null) }

  if (subScreen == null) {
    SettingsMainScreen(onBack = onBack, onLanguageClick = { subScreen = "language" })
  } else {
    LanguageSelectionScreen(onBack = { subScreen = null })
  }
}

@Composable
private fun SettingsMainScreen(onBack: () -> Unit, onLanguageClick: () -> Unit) {
  val currentTag = getCurrentLocaleTag()
  val currentLabel = languageOptions.find { it.tag == currentTag }?.labelResId ?: R.string.lang_en
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
                    stringResource(currentLabel),
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
