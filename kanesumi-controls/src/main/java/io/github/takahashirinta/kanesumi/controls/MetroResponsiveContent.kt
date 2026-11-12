package io.github.takahashirinta.kanesumi.controls

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** 参 Ncrust ResponsiveContent：手机限宽、宽屏铺开；显式 maxWidthDp 仍在所有宽度生效。 */
@Composable
fun MetroResponsiveContent(
    modifier: Modifier = Modifier,
    maxWidthDp: Dp = Dp.Unspecified,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        val widthLimit = if (maxWidthDp != Dp.Unspecified) maxWidthDp else if (maxWidth < 600.dp) 360.dp else Dp.Unspecified
        val body = if (widthLimit == Dp.Unspecified) Modifier.fillMaxSize().padding(horizontal = 24.dp)
            else Modifier.widthIn(max = widthLimit).fillMaxSize()
        Box(modifier = body) {
            content()
        }
    }
}
