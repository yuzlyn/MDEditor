package com.yuzlyn.mdeditor.ui.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yuzlyn.mdeditor.R
import com.yuzlyn.mdeditor.data.profile.AuthorProfile

private const val TAG = "MDEditor_Debug"

private data class SocialItem(
        val titleResId: Int,
        val subtitleResId: Int? = null,
        val icon: @Composable () -> Unit,
        val action: (Context) -> Unit
)

@Composable
fun AboutScreen(onBack: () -> Unit) {
  val context = LocalContext.current
  var cardVisible by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    cardVisible = true
    Log.d(TAG, "关于页面成功加载，读取 profile 模组数据")
  }

  val socialItems = remember {
    listOf(
            SocialItem(
                    titleResId = R.string.about_github,
                    subtitleResId = R.string.about_github_desc,
                    icon = {
                      Icon(
                              Icons.Default.Code,
                              null,
                              tint = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
            ) { ctx -> openUrl(ctx, AuthorProfile.GITHUB_URL, "GitHub") },
            SocialItem(
                    titleResId = R.string.about_qq_group,
                    subtitleResId = R.string.about_qq_group_desc,
                    icon = {
                      Icon(
                              Icons.AutoMirrored.Filled.Chat,
                              null,
                              tint = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
            ) { ctx -> openUrl(ctx, AuthorProfile.QQ_GROUP_URL, "QQ") },
            SocialItem(
                    titleResId = R.string.about_tg_group,
                    subtitleResId = R.string.about_tg_group_desc,
                    icon = {
                      Icon(
                              Icons.AutoMirrored.Filled.Send,
                              null,
                              tint = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
            ) { ctx -> openUrl(ctx, AuthorProfile.TG_GROUP_URL, "Telegram") },
            SocialItem(
                    titleResId = R.string.about_donate,
                    subtitleResId = R.string.about_donate_desc,
                    icon = {
                      Icon(
                              Icons.Default.CardGiftcard,
                              null,
                              tint = MaterialTheme.colorScheme.primary
                      )
                    }
            ) { ctx -> openUrl(ctx, AuthorProfile.DONATE_URL, "Donate") }
    )
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
              stringResource(R.string.drawer_about),
              style = MaterialTheme.typography.titleLarge,
              color = MaterialTheme.colorScheme.onSurface
      )
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
      ProfileFlyInCard(visible = cardVisible) {
        Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                tonalElevation = 3.dp,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
        ) {
          Column(
                  modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 32.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    tonalElevation = 4.dp,
                    modifier = Modifier.size(96.dp)
            ) {
              Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                val avatarRes = avatarResourceOrNull()
                if (avatarRes != null) {
                  androidx.compose.foundation.Image(
                          painter = painterResource(id = avatarRes),
                          contentDescription = "Author avatar",
                          contentScale = ContentScale.Crop,
                          modifier = Modifier.fillMaxSize().clip(CircleShape)
                  )
                } else {
                  Text(
                          text = AuthorProfile.NICKNAME.take(1).uppercase(),
                          style = MaterialTheme.typography.headlineLarge,
                          color = MaterialTheme.colorScheme.onPrimaryContainer,
                          textAlign = TextAlign.Center
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                    text = AuthorProfile.NICKNAME,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                    text = AuthorProfile.SUBTITLE,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      Surface(
              shape = RoundedCornerShape(16.dp),
              color = MaterialTheme.colorScheme.surfaceContainerLow,
              tonalElevation = 1.dp,
              modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.fillMaxWidth()) {
          socialItems.forEachIndexed { index, item ->
            ProfileFlyInCard(visible = cardVisible, delayMs = index * 60L + 180L) {
              Surface(
                      onClick = { item.action(context) },
                      color = MaterialTheme.colorScheme.surfaceContainerLow
              ) {
                ListItem(
                        headlineContent = {
                          Text(
                                  stringResource(item.titleResId),
                                  style = MaterialTheme.typography.bodyLarge,
                                  fontWeight = FontWeight.Medium
                          )
                        },
                        supportingContent =
                                item.subtitleResId?.let { resId ->
                                  {
                                    Text(
                                            stringResource(resId),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                  }
                                },
                        leadingContent = item.icon,
                        trailingContent = {
                          Icon(
                                  Icons.AutoMirrored.Filled.OpenInNew,
                                  contentDescription = null,
                                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                  modifier = Modifier.size(20.dp)
                          )
                        },
                        colors =
                                ListItemDefaults.colors(
                                        containerColor =
                                                MaterialTheme.colorScheme.surfaceContainerLow
                                ),
                        modifier = Modifier.fillMaxWidth()
                )
              }
            }
            if (index < socialItems.lastIndex) {
              HorizontalDivider(
                      modifier = Modifier.padding(horizontal = 16.dp),
                      color = MaterialTheme.colorScheme.outlineVariant
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(32.dp))

      Text(
              text = stringResource(R.string.app_name),
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
              modifier = Modifier.fillMaxWidth(),
              textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

private fun avatarResourceOrNull(): Int? {
  return try {
    val id = AuthorProfile.AVATAR_RES_ID
    if (id != 0) id else null
  } catch (_: Exception) {
    null
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

private fun openUrl(context: Context, url: String, label: String) {
  Log.d(TAG, "用户点击跳转 $label")
  try {
    val intent =
            Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
              addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
    context.startActivity(intent)
  } catch (e: Exception) {
    Log.w(TAG, "无法打开 $label: ${e.message}")
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText(label, url))
    Toast.makeText(context, context.getString(R.string.about_open_failed, label), Toast.LENGTH_LONG)
            .show()
  }
}
