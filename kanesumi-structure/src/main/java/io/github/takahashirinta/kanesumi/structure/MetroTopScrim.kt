package io.github.takahashirinta.kanesumi.structure

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.takahashirinta.kanesumi.core.insets.metroStatusBarsPadding
import io.github.takahashirinta.kanesumi.core.theme.MetroIcon

/**
 * 顶部黑色渐隐 scrim + 悬浮裸图标。所有覆盖满屏图片的二级页面(专辑详情 /
 * 艺人详情 / WebView 登录 / About)共用此组件,保证"任何底图上白色图标可读"
 * 的一致性 —— scrim 顶部 alpha 0.55 向下渐隐到透明,图标本身无背板。
 *
 * 48dp 触控区符合无障碍最低触控尺寸;点击自动走 LocalIndication →
 * MetroIndication,得到直角闪切反馈。
 */
@Composable
fun MetroTopScrim(
    icon: ImageVector,
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.TopStart,
    scrimHeightDp: Dp = 120.dp,
    scrimAlphaTop: Float = 0.55f,
    iconColor: Color = Color.White,
    iconSizeDp: Dp = 24.dp,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        // Gradient scrim
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(scrimHeightDp)
                .background(
                    Brush.verticalGradient(
                        0f to Color.Black.copy(alpha = scrimAlphaTop),
                        1f to Color.Transparent,
                    )
                )
        )
        // Floating icon
        Box(
            modifier = Modifier
                .align(alignment)
                .metroStatusBarsPadding()
                .padding(horizontal = 4.dp, vertical = 4.dp)
                .size(48.dp)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            MetroIcon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconColor,
                sizeDp = iconSizeDp,
            )
        }
    }
}
