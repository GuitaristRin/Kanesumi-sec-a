package io.github.takahashirinta.kanesumi.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.takahashirinta.kanesumi.core.theme.LocalMetroColors

/**
 * Metro 风对话框外壳。包一层 androidx.compose.ui.window.Dialog (foundation,
 * 非 M3),提供直角矩形 surface 容器 -- 无圆角、无 elevation、无默认 padding,
 * 调用方在 content 里用 MetroDivider / MetroListRow 等拼菜单式内容。
 *
 * usePlatformDefaultWidth = false 让我们用 widthDp 精确控宽,避免平台默认
 * 把 dialog 撑到不合理宽度。默认 280dp,约对应 Ncrust PlayAllDialog 的视觉
 * 宽度;覆盖 widthDp 即可。
 *
 * 取代 M3 AlertDialog (圆角 + 按钮 row 布局 + tonal elevation) -- Metro
 * 对话框更接近"菜单列":一列直角行 + 分隔线,没有显式确认/取消按钮槽。
 */
@Composable
fun MetroDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    widthDp: Dp = 280.dp,
    containerColor: Color = LocalMetroColors.current.surface,
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Column(
            modifier = modifier
                .width(widthDp)
                .background(containerColor),
        ) {
            content()
        }
    }
}
