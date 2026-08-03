package io.github.takahashirinta.kanesumi.structure.bottomnav

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.github.takahashirinta.kanesumi.anim.sokuou.MetroCubic
import io.github.takahashirinta.kanesumi.core.insets.rememberBottomStackReservation
import io.github.takahashirinta.kanesumi.core.theme.LocalMetroColors
import io.github.takahashirinta.kanesumi.core.theme.LocalMetroTypography
import io.github.takahashirinta.kanesumi.core.theme.MetroIcon
import io.github.takahashirinta.kanesumi.core.theme.MetroText

@Immutable
data class MetroBottomNavItem(
    val icon: ImageVector,
    val label: String,
)

/**
 * Metro 风底部导航。
 *
 * 三个动效全部通过 graphicsLayer 读取 Animatable,零重组:
 * - 顶部指示条从旧位滑到新位 (MetroCubic 200ms),不闪切
 * - 选中/未选中颜色以叠层 alpha 插值 (MetroCubic 180ms)
 * - 按压反馈由 LocalIndication (MetroTheme 默认注入 MetroIndication) 提供,
 *   直角矩形闪切,无 ripple 无圆角
 *
 * 默认颜色从 LocalMetroColors 取。默认自动登记进 MetroBottomStack。
 * 系统导航栏 padding 不代管 —— 调用方外层套 .metroNavigationBarsPadding()。
 */
@Composable
fun MetroBottomNav(
    items: List<MetroBottomNavItem>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = LocalMetroColors.current.onSurface,
    inactiveColor: Color = LocalMetroColors.current.onSurfaceVariant,
    indicatorColor: Color = LocalMetroColors.current.primary,
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
                .background(indicatorColor),
        )
    }
}

@Composable
private fun RowScope.MetroBottomNavTab(
    item: MetroBottomNavItem,
    isSelected: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    onClick: () -> Unit,
) {
    val selectionProgress = remember { Animatable(if (isSelected) 1f else 0f) }
    LaunchedEffect(isSelected) {
        selectionProgress.animateTo(
            targetValue = if (isSelected) 1f else 0f,
            animationSpec = tween(durationMillis = 180, easing = MetroCubic),
        )
    }

    val labelStyle = LocalMetroTypography.current.label

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(onClick = onClick)  // uses LocalIndication → MetroIndication
            .semantics {
                contentDescription = item.label
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(contentAlignment = Alignment.Center) {
                MetroIcon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = inactiveColor,
                    sizeDp = 22.dp,
                )
                MetroIcon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = activeColor,
                    sizeDp = 22.dp,
                    modifier = Modifier.graphicsLayer { alpha = selectionProgress.value },
                )
            }
            Spacer(Modifier.height(2.dp))
            Box {
                MetroText(
                    text = item.label,
                    color = inactiveColor,
                    style = labelStyle,
                )
                MetroText(
                    text = item.label,
                    color = activeColor,
                    style = labelStyle,
                    modifier = Modifier.graphicsLayer { alpha = selectionProgress.value },
                )
            }
        }
    }
}
