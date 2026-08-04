package io.github.takahashirinta.kanesumi.controls

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitHorizontalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.dp
import io.github.takahashirinta.kanesumi.anim.sokuou.SokuouPresets
import io.github.takahashirinta.kanesumi.core.theme.LocalMetroColors
import kotlinx.coroutines.launch

/**
 * Metro 风开关。直角矩形 track + 直角矩形 thumb -- 取代 M3 Switch 的胶囊
 * 圆角 + ripple。52×28dp,与 Ncrust UserScreen 现有手滚版同尺寸。
 *
 * 交互对齐 UWP ToggleSwitch:tap 翻转 + 水平拖拽跟手,释放时按位置 > 0.5
 * 就近吸附。拖拽阈值走系统 touchSlop —— slop 之内的释放当 tap 处理,
 * 消除误触。拖拽期间用 isDragging 抑制 LaunchedEffect(checked) 的外部
 * 动画,不让它跟拖拽 delta 打架。
 *
 * 单一 progress: Animatable<Float> (0=off,1=on) 驱动 track 颜色、border
 * 颜色、thumb 位移、thumb 颜色,全部在 drawBehind 内读取 -- 严格零重组,
 * 每次翻转/拖拽只 invalidateDraw。动画走 SokuouPresets.ToggleFlip (220ms
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
    var isDragging by remember { mutableStateOf(false) }
    val animScope = rememberCoroutineScope()

    LaunchedEffect(checked) {
        if (!isDragging) {
            progress.animateTo(if (checked) 1f else 0f, SokuouPresets.ToggleFlip)
        }
    }

    Box(
        modifier = modifier
            .width(52.dp)
            .height(28.dp)
            .pointerInput(Unit) {
                // travelPx 用 pointerInput 的 Density 现算,拿到的是 drawBehind 里
                // 同一套常量 (52 - 3*2 - 22 = 24dp) 的像素值。手指 delta -> progress
                // 就用它做比例映射。
                val travelPx = (52.dp - 3.dp * 2 - 22.dp).toPx()
                awaitEachGesture {
                    val down = awaitFirstDown()
                    val drag = awaitHorizontalTouchSlopOrCancellation(down.id) { change, _ ->
                        change.consume()
                    }
                    if (drag == null) {
                        // 没越过 touchSlop 就抬起 = tap。动画由 LaunchedEffect(checked) 兜。
                        onCheckedChange(!checked)
                    } else {
                        isDragging = true
                        // 越过 slop 那一下的 delta 也要吃进去,不然起手有丢帧感。
                        var localProgress = (progress.value + drag.positionChange().x / travelPx)
                            .coerceIn(0f, 1f)
                        animScope.launch { progress.snapTo(localProgress) }
                        horizontalDrag(drag.id) { change ->
                            localProgress = (localProgress + change.positionChange().x / travelPx)
                                .coerceIn(0f, 1f)
                            animScope.launch { progress.snapTo(localProgress) }
                            change.consume()
                        }
                        val target = if (localProgress >= 0.5f) 1f else 0f
                        val targetChecked = target == 1f
                        isDragging = false
                        // 无论 checked 是否翻转,都本地 animateTo 一次 -- 因为释放位置几乎
                        // 不会正好在 0/1 上,得让 thumb 滑到端点。
                        animScope.launch { progress.animateTo(target, SokuouPresets.ToggleFlip) }
                        if (targetChecked != checked) {
                            onCheckedChange(targetChecked)
                        }
                    }
                }
            }
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
