package io.github.takahashirinta.kanesumi.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Immutable
data class MetroTypography(
    val pageHeading: TextStyle = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.Normal),
    val title: TextStyle = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Normal),
    val body: TextStyle = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
    val caption: TextStyle = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal),
    val label: TextStyle = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Normal),
)
