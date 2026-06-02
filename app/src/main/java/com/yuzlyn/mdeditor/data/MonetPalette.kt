package com.yuzlyn.mdeditor.data

import androidx.compose.ui.graphics.Color
import com.yuzlyn.mdeditor.R

data class MonetEntry(
        val labelResId: Int,
        val bgColorLong: Long,
        val textColor: Color,
        val lightContainer: Color,
        val darkContainer: Color,
        val lightOnContainer: Color,
        val darkOnContainer: Color
)

object MonetPalette {
  val entries =
          listOf(
                  MonetEntry(
                          labelResId = R.string.color_default,
                          bgColorLong = 0x00000000L,
                          textColor = Color.Unspecified,
                          lightContainer = Color.Unspecified,
                          darkContainer = Color.Unspecified,
                          lightOnContainer = Color.Unspecified,
                          darkOnContainer = Color.Unspecified
                  ),
                  MonetEntry(
                          labelResId = R.string.color_sage_green,
                          bgColorLong = 0xFFE6F4EAL,
                          textColor = Color(0xFF137333),
                          lightContainer = Color(0xFFE8F5E9),
                          darkContainer = Color(0xFF1A3A1E),
                          lightOnContainer = Color(0xFF1B5E20),
                          darkOnContainer = Color(0xFFA5D6A7)
                  ),
                  MonetEntry(
                          labelResId = R.string.color_soft_blue,
                          bgColorLong = 0xFFE8F0FEL,
                          textColor = Color(0xFF174EA6),
                          lightContainer = Color(0xFFE3F2FD),
                          darkContainer = Color(0xFF1A3048),
                          lightOnContainer = Color(0xFF0D47A1),
                          darkOnContainer = Color(0xFF90CAF9)
                  ),
                  MonetEntry(
                          labelResId = R.string.color_sunset_pink,
                          bgColorLong = 0xFFFCE4ECL,
                          textColor = Color(0xFFC62828),
                          lightContainer = Color(0xFFFCE4EC),
                          darkContainer = Color(0xFF3D1A28),
                          lightOnContainer = Color(0xFF880E4F),
                          darkOnContainer = Color(0xFFF48FB1)
                  ),
                  MonetEntry(
                          labelResId = R.string.color_cream_apricot,
                          bgColorLong = 0xFFFFF0E0L,
                          textColor = Color(0xFFE65100),
                          lightContainer = Color(0xFFFFFDE7),
                          darkContainer = Color(0xFF3D3520),
                          lightOnContainer = Color(0xFFF57F17),
                          darkOnContainer = Color(0xFFFFF176)
                  ),
                  MonetEntry(
                          labelResId = R.string.color_lavender,
                          bgColorLong = 0xFFF3E8FDL,
                          textColor = Color(0xFF6B24C1),
                          lightContainer = Color(0xFFF3E8FD),
                          darkContainer = Color(0xFF2A1E3D),
                          lightOnContainer = Color(0xFF6B24C1),
                          darkOnContainer = Color(0xFFCE93D8)
                  ),
                  MonetEntry(
                          labelResId = R.string.color_mint_green,
                          bgColorLong = 0xFFE0F2F1L,
                          textColor = Color(0xFF00695C),
                          lightContainer = Color(0xFFE0F2F1),
                          darkContainer = Color(0xFF1A3532),
                          lightOnContainer = Color(0xFF00695C),
                          darkOnContainer = Color(0xFF80CBC4)
                  ),
                  MonetEntry(
                          labelResId = R.string.color_warm_ivory,
                          bgColorLong = 0xFFFFF8E1L,
                          textColor = Color(0xFF795548),
                          lightContainer = Color(0xFFFFF8E1),
                          darkContainer = Color(0xFF372B23),
                          lightOnContainer = Color(0xFF795548),
                          darkOnContainer = Color(0xFFBCAAA4)
                  ),
                  MonetEntry(
                          labelResId = R.string.color_deep_night_blue,
                          bgColorLong = 0xFF1A237EL,
                          textColor = Color(0xFFE8EAF6),
                          lightContainer = Color(0xFFE8EAF6),
                          darkContainer = Color(0xFF2A3058),
                          lightOnContainer = Color(0xFF1A237E),
                          darkOnContainer = Color(0xFF9FA8DA)
                  ),
                  MonetEntry(
                          labelResId = R.string.color_charcoal_gray,
                          bgColorLong = 0xFF424242L,
                          textColor = Color(0xFFFAFAFA),
                          lightContainer = Color(0xFFEEEEEE),
                          darkContainer = Color(0xFF333333),
                          lightOnContainer = Color(0xFF424242),
                          darkOnContainer = Color(0xFFBDBDBD)
                  ),
          )

  fun Long.toComposeColor(): Color = Color(this.toInt())

  fun entryForBgColor(backgroundColor: Long): MonetEntry? =
          entries.find { it.bgColorLong == backgroundColor }

  fun textColorFor(backgroundColor: Long, darkTheme: Boolean = false): Color {
    if (backgroundColor == 0x00000000L)
            return if (darkTheme) Color(0xFFE6E1E5) else Color(0xFF1C1B1F)
    val entry =
            entries.find { it.bgColorLong == backgroundColor }
                    ?: return if (darkTheme) Color(0xFFE6E1E5) else Color(0xFF1C1B1F)
    return if (darkTheme) entry.darkOnContainer else entry.lightOnContainer
  }

  fun bgColorFor(backgroundColor: Long, defaultSurface: Color, darkTheme: Boolean = false): Color {
    if (backgroundColor == 0x00000000L) return defaultSurface
    val entry = entries.find { it.bgColorLong == backgroundColor } ?: return defaultSurface
    return if (darkTheme) entry.darkContainer else entry.lightContainer
  }

  fun containerColorFor(backgroundColor: Long, darkTheme: Boolean): Color {
    if (backgroundColor == 0x00000000L) return Color.Unspecified
    val entry = entries.find { it.bgColorLong == backgroundColor } ?: return Color.Unspecified
    return if (darkTheme) entry.darkContainer else entry.lightContainer
  }
}
