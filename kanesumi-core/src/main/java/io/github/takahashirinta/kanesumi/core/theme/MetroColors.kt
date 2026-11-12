package io.github.takahashirinta.kanesumi.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

val DefaultMetroAccent = Color(0xFF2D6FE0)

@Immutable
data class MetroColors(
    val background: Color = Color(0xFF000000),
    val surface: Color = Color(0xFF171717),
    val surfaceVariant: Color = Color(0xFF333333),
    val divider: Color = Color(0xFF333333),
    val primary: Color = Color(0xFF3982FF),
    val onPrimary: Color = metroContentColorFor(primary),
    val onBackground: Color = Color.White,
    val onSurface: Color = Color.White,
    val onSurfaceVariant: Color = Color(0xFF9AA0A6),
    val pressTint: Color = Color(0x22FFFFFF),
    // 追加而不插入参数；Ncrust 的显式配色仍以其 surfaceVariant 作为控件底。
    val controlFill: Color = surfaceVariant,
) {
    val isLight: Boolean get() = background.luminance() > 0.5f

    fun contentColorFor(container: Color): Color = when (container) {
        primary -> onPrimary
        controlFill, surface, surfaceVariant -> onSurface
        background -> onBackground
        Color.Transparent -> onSurface
        else -> metroContentColorFor(container)
    }

    companion object {
        /** 参主仓 K-79：纯黑内容、#171717 chrome、#333333 控件，强调色暗色取 Light1。 */
        fun dark(accent: Color = DefaultMetroAccent): MetroColors = MetroColors(primary = lightAccent(accent))

        /** 参主仓 colors.rs；尚未验收的浅色候选值不提前移植。 */
        fun light(accent: Color = DefaultMetroAccent): MetroColors = MetroColors(
            background = Color(0xFFFAFAFA),
            surface = Color.White,
            surfaceVariant = Color(0xFFF0F0F0),
            divider = Color(0xFFD6D6D6),
            primary = accent,
            onBackground = Color(0xFF1A1A1A),
            onSurface = Color(0xFF1A1A1A),
            onSurfaceVariant = Color(0xFF5A5F66),
            pressTint = Color(0x22000000),
            controlFill = Color.Black.copy(alpha = 0.2f),
        )
    }
}

/** 比较实际黑白对比度，避免绿色/琥珀等强调底仍被强制配白字。 */
fun metroContentColorFor(container: Color): Color {
    val luminance = container.luminance()
    return if ((luminance + 0.05f) / 0.05f >= 1.05f / (luminance + 0.05f)) Color.Black else Color.White
}

// 主仓 accent.rs 的 Light1 模型：保留色相，V 越界后降低饱和度；并非 Windows 闭源色相算法。
private fun lightAccent(base: Color): Color {
    val high = maxOf(base.red, base.green, base.blue)
    val low = minOf(base.red, base.green, base.blue)
    val delta = high - low
    val hue = when {
        delta == 0f -> 0f
        high == base.red -> ((base.green - base.blue) / delta).mod(6f)
        high == base.green -> (base.blue - base.red) / delta + 2f
        else -> (base.red - base.green) / delta + 4f
    } * 60f
    val scaled = high * 1.17f
    val saturation = if (high == 0f) 0f else delta / high
    return Color.hsv(hue, (saturation * if (scaled > 1f) 2f - scaled else 1f).coerceIn(0f, 1f),
        scaled.coerceIn(0f, 1f), base.alpha)
}
