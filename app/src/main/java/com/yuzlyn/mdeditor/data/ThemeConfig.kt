package com.yuzlyn.mdeditor.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class ThemeConfig(val modeKey: String) {
  data object SystemDefault : ThemeConfig("system_default")
  data object DynamicMonet : ThemeConfig("dynamic_monet")
  data class CustomColor(val colorLong: Long) : ThemeConfig("custom_color")

  companion object {
    private const val TAG = "MDEditor_Debug"
    private const val PREFS_KEY_THEME_MODE = "theme_mode"
    private const val PREFS_KEY_CUSTOM_COLOR = "theme_custom_color"

    private val _configFlow = MutableStateFlow<ThemeConfig>(SystemDefault)
    val configFlow: StateFlow<ThemeConfig> = _configFlow.asStateFlow()

    fun load(context: Context) {
      val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
      val mode = prefs.getString(PREFS_KEY_THEME_MODE, "system_default") ?: "system_default"
      val config =
              when (mode) {
                "dynamic_monet" -> DynamicMonet
                "custom_color" -> {
                  val colorLong = prefs.getLong(PREFS_KEY_CUSTOM_COLOR, 0xFF6750A4L)
                  CustomColor(colorLong)
                }
                else -> SystemDefault
              }
      _configFlow.value = config
      Log.d(
              TAG,
              "ThemeConfig 载入: mode=$mode, customColor=${if (config is CustomColor) config.colorLong.toString(16) else "N/A"}"
      )
    }

    fun set(config: ThemeConfig, context: Context) {
      val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
      prefs.edit().apply {
        putString(PREFS_KEY_THEME_MODE, config.modeKey)
        if (config is CustomColor) {
          putLong(PREFS_KEY_CUSTOM_COLOR, config.colorLong)
        }
        apply()
      }
      _configFlow.value = config
      Log.d(TAG, "全域主题变更为: ${config.modeKey}, 是否启用动态取色: ${config is DynamicMonet}")
    }
  }
}
