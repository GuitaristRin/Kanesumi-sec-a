package io.github.takahashirinta.kanesumi.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import io.github.takahashirinta.kanesumi.core.theme.LocalMetroColors
import io.github.takahashirinta.kanesumi.core.theme.LocalMetroTypography
import io.github.takahashirinta.kanesumi.core.theme.MetroText

/**
 * Metro 风下拉菜单。包一层 androidx.compose.ui.window.Popup (foundation,
 * 非 M3),直角矩形 surfaceVariant 容器,无圆角无 elevation。
 *
 * 定位:Popup 以 BottomStart 对齐 + 8dp 垂直偏移 -- 即出现在调用方所在
 * Box 的左下角下方。调用约定与 M3 DropdownMenu 一致:把 MetroDropdownMenu
 * 与锚点图标放在同一个 Box 里,菜单自动出现在图标下方。
 *
 * focusable = true 让 Popup 抢焦点,触屏在菜单外点击 / 返回键都会触发
 * onDismissRequest。这是 M3 DropdownMenu 默认行为的核心。
 *
 * 不做 M3 那套"边界检测 + 翻转 / 偏移到锚点右侧"的复杂定位 -- 没有真实
 * 用例驱动,避免预抽象。需要时调用方自己包 Box 调位置。
 *
 * 取代 M3 DropdownMenu (默认圆角 + tonal elevation + ripple menu item)。
 */
@Composable
fun MetroDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = LocalMetroColors.current.surfaceVariant,
    maxWidthDp: Dp = 220.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (!expanded) return
    val density = LocalDensity.current
    Popup(
        alignment = Alignment.BottomStart,
        offset = IntOffset(0, with(density) { 8.dp.roundToPx() }),
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = true),
    ) {
        Column(
            modifier = modifier
                .widthIn(max = maxWidthDp)
                .background(containerColor),
        ) {
            content()
        }
    }
}

/**
 * Metro 风菜单项。直角、无 ripple 槽位(点击走 LocalIndication ->
 * MetroIndication 直角闪切),默认水平 16dp / 垂直 12dp padding。
 *
 * leading / trailing 是 slot,与 MetroListRow 同构 -- 想要图标 + 文字
 * 菜单项就传 leading,想要快捷键标签就传 trailing。
 */
@Composable
fun MetroDropdownMenuItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textColor: Color = LocalMetroColors.current.onSurface,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
) {
    val base = modifier.fillMaxWidth()
    val withClick = if (enabled) base.clickable(onClick = onClick) else base
    Row(
        modifier = withClick.padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leading != null) {
            leading()
            Spacer(Modifier.padding(end = 12.dp))
        }
        Column(Modifier.weight(1f)) {
            MetroText(
                text = text,
                color = if (enabled) textColor else textColor.copy(alpha = 0.4f),
                style = LocalMetroTypography.current.body,
            )
        }
        if (trailing != null) {
            Spacer(Modifier.padding(start = 12.dp))
            trailing()
        }
    }
}
