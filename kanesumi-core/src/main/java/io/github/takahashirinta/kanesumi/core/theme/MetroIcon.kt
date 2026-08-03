package io.github.takahashirinta.kanesumi.core.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Theme-aware icon. Tinted from [LocalMetroColors].onSurface by default.
 * a11y: sets Role.Image and contentDescription semantics when [contentDescription] is non-null.
 */
@Composable
fun MetroIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalMetroColors.current.onSurface,
    sizeDp: Dp = 24.dp,
) {
    val painter = rememberVectorPainter(imageVector)
    val semanticsModifier = if (contentDescription != null) {
        Modifier.semantics {
            this.contentDescription = contentDescription
            role = Role.Image
        }
    } else Modifier
    Box(
        modifier = modifier
            .size(sizeDp)
            .paint(painter = painter, colorFilter = ColorFilter.tint(tint))
            .then(semanticsModifier)
    )
}
