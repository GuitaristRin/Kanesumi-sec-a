package io.github.takahashirinta.kanesumi.core.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

val LocalMetroColors = staticCompositionLocalOf { MetroColors() }

val LocalMetroTypography = staticCompositionLocalOf { MetroTypography() }

@Composable
fun MetroTheme(
    colors: MetroColors = MetroColors(),
    typography: MetroTypography = MetroTypography(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalMetroColors provides colors,
        LocalMetroTypography provides typography,
    ) {
        content()
    }
}
