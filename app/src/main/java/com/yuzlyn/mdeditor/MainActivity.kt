package com.yuzlyn.mdeditor

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yuzlyn.mdeditor.data.ThemeConfig
import com.yuzlyn.mdeditor.ui.screen.EditorScreen
import com.yuzlyn.mdeditor.ui.screen.MainScreen
import com.yuzlyn.mdeditor.ui.screen.SettingsScreen
import com.yuzlyn.mdeditor.ui.theme.MDEditorTheme
import com.yuzlyn.mdeditor.ui.viewmodel.FileViewModel
import java.util.Locale

class MainActivity : ComponentActivity() {

  companion object {
    const val KEY_LOCALE = "app_locale"

    fun applyLocale(context: Context, tag: String) {
      val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
      prefs.edit().putString(KEY_LOCALE, tag).apply()
      val locale =
              when (tag) {
                "zh-CN" -> Locale.SIMPLIFIED_CHINESE
                "zh-TW" -> Locale.TRADITIONAL_CHINESE
                "fr" -> Locale.FRENCH
                else -> Locale.ENGLISH
              }
      Locale.setDefault(locale)
      val config = context.resources.configuration
      config.setLocale(locale)
      context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    fun getSavedLocale(context: Context): String {
      return context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
              .getString(KEY_LOCALE, "en")
              ?: "en"
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    applyLocale(this, getSavedLocale(this))
    ThemeConfig.load(this)
    super.onCreate(savedInstanceState)
    applySplashBackground()
    enableEdgeToEdge()
    setContent {
      MDEditorTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
          val navController = rememberNavController()
          val fileViewModel: FileViewModel = viewModel()
          val context = LocalContext.current
          fileViewModel.tryLoadSavedFolder(context)

          NavHost(
                  navController = navController,
                  startDestination = "main",
                  modifier = Modifier.fillMaxSize()
          ) {
            composable("main") {
              MainScreen(navController = navController, viewModel = fileViewModel)
            }
            composable("editor") {
              EditorScreen(navController = navController, viewModel = fileViewModel)
            }
            composable("settings") { SettingsScreen(onBack = { navController.popBackStack() }) }
          }
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    applyLocale(this, getSavedLocale(this))
  }

  private fun applySplashBackground() {
    val primaryColor =
            when (val cfg = ThemeConfig.configFlow.value) {
              is ThemeConfig.CustomColor -> cfg.colorLong.toInt()
              else -> android.graphics.Color.parseColor("#ffffaa")
            }
    window.decorView.setBackgroundColor(primaryColor)
  }
}
