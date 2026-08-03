package io.github.takahashirinta.kanesumi.core.theme

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

/**
 * Theme-aware text primitive. Reads [LocalMetroColors] for default color and
 * [LocalMetroTypography] for default style. Explicit [color] wins if [style] leaves
 * `color = Color.Unspecified` (which is the default in Kanesumi's typography presets).
 */
@Composable
fun MetroText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = LocalMetroColors.current.onSurface,
    style: TextStyle = LocalMetroTypography.current.body,
) {
    val merged = if (style.color == Color.Unspecified) style.copy(color = color) else style
    BasicText(text = text, modifier = modifier, style = merged)
}
