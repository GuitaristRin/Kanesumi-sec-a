package io.github.takahashirinta.kanesumi.controls

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.takahashirinta.kanesumi.anim.sokuou.MetroCubic
import io.github.takahashirinta.kanesumi.core.theme.LocalMetroColors
import io.github.takahashirinta.kanesumi.core.theme.MetroText

@Immutable
data class MetroTabItem(
    val title: String,
)

/**
 * Metro 风 Tab 行。取代 M3 TabRow + Tab。
 *
 * 单一 progress: Animatable<Float> 驱动顶部 2dp 满宽指示条的 translationX
 * (MetroCubic 200ms),不闪切 -- 这是 UWP Pivot 的签名动作,NcrustTabRow
 * 用"每 tab 一条 + 硬切"顶替,呆板感来源之一。库版改成全局单条滑动。
 *
 * 文字颜色:选中态 primary Medium 14sp,未选中 onSurfaceVariant Regular 14sp,
 * 两层堆叠 + graphicsLayer alpha 插值 (MetroCubic 180ms),零重组。
 *
 * 高度默认 48dp(与 M3 Tab 一致,不破坏现有布局)。无 ripple、无下划线背板。
 */
@Composable
fun MetroTabRow(
    items: List<MetroTabItem>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = LocalMetroColors.current.primary,
    inactiveColor: Color = LocalMetroColors.current.onSurfaceVariant,
    indicatorColor: Color = LocalMetroColors.current.primary,
    heightDp: Dp = 48.dp,
    textStyle: TextStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
    activeTextStyle: TextStyle = textStyle.copy(fontWeight = FontWeight.Medium),
) {
    val indicatorProgress = remember { Animatable(selectedTabIndex.toFloat()) }
    LaunchedEffect(selectedTabIndex) {
        indicatorProgress.animateTo(
            targetValue = selectedTabIndex.toFloat(),
            animationSpec = tween(durationMillis = 200, easing = MetroCubic),
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(heightDp),
    ) {
        val density = LocalDensity.current
        val itemCount = items.size.coerceAtLeast(1)
        val tabWidthPx = with(density) { maxWidth.toPx() } / itemCount

        Row(Modifier.fillMaxSize()) {
            items.forEachIndexed { index, item ->
                MetroTabRowTab(
                    title = item.title,
                    isSelected = index == selectedTabIndex,
                    activeColor = activeColor,
                    inactiveColor = inactiveColor,
                    textStyle = textStyle,
                    activeTextStyle = activeTextStyle,
                    onClick = { onTabSelected(index) },
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth(1f / itemCount)
                .height(2.dp)
                .graphicsLayer {
                    translationX = indicatorProgress.value * tabWidthPx
                }
                .background(indicatorColor),
        )
    }
}

@Composable
private fun RowScope.MetroTabRowTab(
    title: String,
    isSelected: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    textStyle: TextStyle,
    activeTextStyle: TextStyle,
    onClick: () -> Unit,
) {
    val selectionProgress = remember { Animatable(if (isSelected) 1f else 0f) }
    LaunchedEffect(isSelected) {
        selectionProgress.animateTo(
            targetValue = if (isSelected) 1f else 0f,
            animationSpec = tween(durationMillis = 180, easing = MetroCubic),
        )
    }

    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxSize()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        // 2dp 跳过指示条自身厚度 + 12dp 呼吸 —— 否则文字紧贴指示条下沿,视觉挤。
        // 数值对齐 NcrustTabRow / UWP Pivot 手感,不做参数化(没有真实用例驱动)。
        Spacer(Modifier.height(14.dp))
        Box(contentAlignment = Alignment.Center) {
            MetroText(text = title, color = inactiveColor, style = textStyle)
            MetroText(
                text = title,
                color = activeColor,
                style = activeTextStyle,
                modifier = Modifier.graphicsLayer { alpha = selectionProgress.value },
            )
        }
    }
}
