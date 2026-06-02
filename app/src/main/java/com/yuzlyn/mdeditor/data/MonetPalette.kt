package com.yuzlyn.mdeditor.data

import androidx.compose.ui.graphics.Color
import com.yuzlyn.mdeditor.R

data class MonetEntry(val labelResId: Int, val bgColorLong: Long, val textColor: Color)

object MonetPalette {
    val entries = listOf(
        MonetEntry(R.string.color_default, 0x00000000L, Color.Unspecified),
        MonetEntry(R.string.color_sage_green, 0xFFE6F4EAL, Color(0xFF137333)),
        MonetEntry(R.string.color_soft_blue, 0xFFE8F0FEL, Color(0xFF174EA6)),
        MonetEntry(R.string.color_sunset_pink, 0xFFFCE4ECL, Color(0xFFC62828)),
        MonetEntry(R.string.color_cream_apricot, 0xFFFFF0E0L, Color(0xFFE65100)),
        MonetEntry(R.string.color_lavender, 0xFFF3E8FDL, Color(0xFF6B24C1)),
        MonetEntry(R.string.color_mint_green, 0xFFE0F2F1L, Color(0xFF00695C)),
        MonetEntry(R.string.color_warm_ivory, 0xFFFFF8E1L, Color(0xFF795548)),
        MonetEntry(R.string.color_deep_night_blue, 0xFF1A237EL, Color(0xFFE8EAF6)),
        MonetEntry(R.string.color_charcoal_gray, 0xFF424242L, Color(0xFFFAFAFA))
    )

    fun Long.toComposeColor(): Color = Color(this.toInt())

    fun textColorFor(backgroundColor: Long): Color {
        if (backgroundColor == 0x00000000L) return Color.Unspecified
        return entries.find { it.bgColorLong == backgroundColor }?.textColor ?: Color.Unspecified
    }

    fun bgColorFor(backgroundColor: Long, defaultSurface: Color): Color {
        return if (backgroundColor == 0x00000000L) defaultSurface else backgroundColor.toComposeColor()
    }
}
