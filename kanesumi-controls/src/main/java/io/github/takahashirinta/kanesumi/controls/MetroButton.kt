package io.github.takahashirinta.kanesumi.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.takahashirinta.kanesumi.core.theme.LocalMetroColors
import io.github.takahashirinta.kanesumi.core.theme.LocalMetroTypography
import io.github.takahashirinta.kanesumi.core.theme.MetroIcon
import io.github.takahashirinta.kanesumi.core.theme.MetroText

enum class MetroButtonKind { Standard, Accent, Text }

/** 默认中性命令按钮；参主仓 ButtonKind，强调动作显式选择 Accent。自定义配色始终优先。 */
@Composable
fun MetroButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    leadingIcon: ImageVector? = null,
    iconSize: Dp = 18.dp,
    textStyle: TextStyle = LocalMetroTypography.current.body,
    kind: MetroButtonKind = MetroButtonKind.Standard,
) {
    val colors = LocalMetroColors.current
    val effectiveContainer = if (containerColor != Color.Unspecified) containerColor else when (kind) {
        MetroButtonKind.Standard -> colors.controlFill
        MetroButtonKind.Accent -> colors.primary
        MetroButtonKind.Text -> Color.Transparent
    }
    val foreground = if (contentColor != Color.Unspecified) contentColor else colors.contentColorFor(effectiveContainer)
    val effectiveContent = if (enabled) foreground else foreground.copy(alpha = foreground.alpha * 0.38f)

    Row(
        modifier = modifier
            .heightIn(min = 48.dp)
            .background(effectiveContainer)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(contentPadding),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            MetroIcon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = effectiveContent,
                sizeDp = iconSize,
            )
            Spacer(Modifier.width(8.dp))
        }
        MetroText(
            text = text,
            color = effectiveContent,
            style = textStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
