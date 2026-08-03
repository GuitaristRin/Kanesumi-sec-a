package io.github.takahashirinta.kanesumi.structure.bottomnav

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.takahashirinta.kanesumi.anim.sokuou.MetroCubic
import io.github.takahashirinta.kanesumi.core.insets.rememberBottomStackReservation

@Immutable
data class MetroBottomNavItem(
    val icon: ImageVector,
    val label: String,
)

/**
 * Metro 风底部导航。
 *
 * 三个动效全部通过 graphicsLayer / drawBehind 读取 Animatable,零重组:
 * - 顶部指示条从旧位滑到新位 (MetroCubic 200ms),不闪切
 * - 选中/未选中颜色以叠层 alpha 插值 (MetroCubic 180ms)
 * - 按下时直角矩形闪切 (100ms 淡入 / 200ms 淡出),无 ripple 无圆角
 *
 * 默认自动登记进 MetroBottomStack,内容侧一句 metroBottomOverlayPadding()
 * 即避开。系统导航栏 padding 不代管 —— 调用方在外层套
 * .metroNavigationBarsPadding() 保持可控。
 */
@Composable
fun MetroBottomNav(
    items: List<MetroBottomNavItem>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = Color(0xFFE6E6E6),
    inactiveColor: Color = Color(0xFF7A7A7A),
    pressTint: Color = Color(0x22FFFFFF),
    indicatorSize: DpSize = DpSize(24.dp, 2.dp),
    heightDp: Dp = 56.dp,
    autoReserveBottomStack: Boolean = true,
    reservationKey: Any = "kanesumi.metroBottomNav",
) {
    if (autoReserveBottomStack) {
        rememberBottomStackReservation(reservationKey, heightDp)
    }

    val indicatorProgress = remember { Animatable(selectedIndex.toFloat()) }
    LaunchedEffect(selectedIndex) {
        indicatorProgress.animateTo(
            targetValue = selectedIndex.toFloat(),
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
        val indicatorWidthPx = with(density) { indicatorSize.width.toPx() }
        val indicatorLeftInTabPx = (tabWidthPx - indicatorWidthPx) / 2f

        Row(Modifier.fillMaxSize()) {
            items.forEachIndexed { index, item ->
                MetroBottomNavTab(
                    item = item,
                    isSelected = index == selectedIndex,
                    activeColor = activeColor,
                    inactiveColor = inactiveColor,
                    pressTint = pressTint,
                    onClick = { onSelected(index) },
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(indicatorSize)
                .graphicsLayer {
                    translationX = indicatorProgress.value * tabWidthPx + indicatorLeftInTabPx
                }
                .background(activeColor),
        )
    }
}

@Composable
private fun RowScope.MetroBottomNavTab(
    item: MetroBottomNavItem,
    isSelected: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    pressTint: Color,
    onClick: () -> Unit,
) {
    val selectionProgress = remember { Animatable(if (isSelected) 1f else 0f) }
    LaunchedEffect(isSelected) {
        selectionProgress.animateTo(
            targetValue = if (isSelected) 1f else 0f,
            animationSpec = tween(durationMillis = 180, easing = MetroCubic),
        )
    }

    val interactionSource = remember { MutableInteractionSource() }
    val pressProgress = remember { Animatable(0f) }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press ->
                    pressProgress.animateTo(1f, tween(100, easing = MetroCubic))
                is PressInteraction.Release, is PressInteraction.Cancel ->
                    pressProgress.animateTo(0f, tween(200, easing = MetroCubic))
            }
        }
    }

    val iconPainter = rememberVectorPainter(item.icon)

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .drawBehind {
                drawRect(color = pressTint, alpha = pressProgress.value)
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            TintedIconLayer(
                painter = iconPainter,
                inactiveColor = inactiveColor,
                activeColor = activeColor,
                selectionAlpha = { selectionProgress.value },
            )
            Spacer(Modifier.height(2.dp))
            TintedLabelLayer(
                text = item.label,
                inactiveColor = inactiveColor,
                activeColor = activeColor,
                selectionAlpha = { selectionProgress.value },
            )
        }
    }
}

/**
 * Two stacked icons — inactive fully opaque, active on top with alpha driven by
 * [selectionAlpha] read inside graphicsLayer. Achieves color interpolation without
 * recomposing the composition tree.
 */
@Composable
private fun TintedIconLayer(
    painter: Painter,
    inactiveColor: Color,
    activeColor: Color,
    selectionAlpha: () -> Float,
) {
    Box(Modifier.size(22.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    with(painter) {
                        draw(size = this@drawBehind.size, colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(inactiveColor))
                    }
                }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = selectionAlpha() }
                .drawBehind {
                    with(painter) {
                        draw(size = this@drawBehind.size, colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(activeColor))
                    }
                }
        )
    }
}

@Composable
private fun TintedLabelLayer(
    text: String,
    inactiveColor: Color,
    activeColor: Color,
    selectionAlpha: () -> Float,
) {
    Box {
        BasicText(
            text = text,
            style = TextStyle(color = inactiveColor, fontSize = 11.sp, lineHeight = 12.sp),
        )
        BasicText(
            text = text,
            style = TextStyle(color = activeColor, fontSize = 11.sp, lineHeight = 12.sp),
            modifier = Modifier.graphicsLayer { alpha = selectionAlpha() },
        )
    }
}
