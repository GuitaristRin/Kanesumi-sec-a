package io.github.takahashirinta.kanesumi.controls

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import io.github.takahashirinta.kanesumi.anim.sokuou.SokuouPresets
import io.github.takahashirinta.kanesumi.core.theme.LocalMetroColors

/**
 * Metro 风开关。直角矩形 track + 直角矩形 thumb -- 取代 M3 Switch 的胶囊
 * 圆角 + ripple。52×28dp,与 Ncrust UserScreen 现有手滚版同尺寸。
 *
 * 单一 progress: Animatable<Float> (0=off,1=on) 驱动 track 颜色、border
 * 颜色、thumb 位移、thumb 颜色,全部在 drawBehind 内读取 -- 严格零重组,
 * 每次翻转只 invalidateDraw。动画走 SokuouPresets.ToggleFlip (220ms
 * MetroCubic),与 UWP toggle 时长对齐。
 *
 * thumb 在 progress > 0.5 时翻转为黑色(on 态,在 primary track 上可读),
 * 否则白色(off 态,在深灰 track 上可读)。直角不圆。
 */
@Composable
fun MetroSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = LocalMetroColors.current.primary,
    trackOffColor: Color = Color(0xFF333333),
    borderOffColor: Color = Color.Gray.copy(alpha = 0.35f),
) {
    val progress = remember { Animatable(if (checked) 1f else 0f) }
    LaunchedEffect(checked) {
        progress.animateTo(if (checked) 1f else 0f, SokuouPresets.ToggleFlip)
    }
    Box(
        modifier = modifier
            .width(52.dp)
            .height(28.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onCheckedChange(!checked) }
            .drawBehind {
                val p = progress.value
                val track = lerp(trackOffColor, accentColor, p)
                val border = lerp(borderOffColor, accentColor, p)
                drawRect(track)
                val strokeWidth = 1.dp.toPx()
                drawRect(color = border, style = Stroke(width = strokeWidth))
                val padPx = 3.dp.toPx()
                val thumbW = 22.dp.toPx()
                val travel = size.width - padPx * 2f - thumbW
                val thumbX = padPx + travel * p
                val thumbColor = if (p > 0.5f) Color.Black else Color.White
                drawRect(
                    color = thumbColor,
                    topLeft = Offset(thumbX, padPx),
                    size = Size(thumbW, size.height - padPx * 2f),
                )
            },
    )
}
