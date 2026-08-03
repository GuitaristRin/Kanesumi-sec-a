package io.github.takahashirinta.kanesumi.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.takahashirinta.kanesumi.core.insets.LocalMetroBottomStack
import io.github.takahashirinta.kanesumi.core.insets.MetroBottomStackScope
import io.github.takahashirinta.kanesumi.core.insets.MetroInsets
import io.github.takahashirinta.kanesumi.core.insets.bottomOverlayPadding
import io.github.takahashirinta.kanesumi.core.insets.metroStatusBarsPadding
import io.github.takahashirinta.kanesumi.core.insets.rememberBottomStackReservation
import io.github.takahashirinta.kanesumi.core.insets.rememberMetroInsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MetroBottomStackScope {
                SampleRoot()
            }
        }
    }
}

private val BgBlack = Color(0xFF000000)
private val PanelInk = Color(0xFF0E1116)
private val AccentBlue = Color(0xFF2E67B5)
private val AccentTeal = Color(0xFF17A2A2)
private val TextPrimary = Color(0xFFF0F0F0)
private val TextMuted = Color(0xFF9AA0A6)

@Composable
private fun SampleRoot() {
    var miniBarVisible by remember { mutableStateOf(false) }

    // Structural elements register their heights into the bottom stack.
    // Bottom nav is always present; mini bar is toggleable.
    rememberBottomStackReservation(key = "sample.bottomNav", heightDp = 56.dp)
    if (miniBarVisible) {
        rememberBottomStackReservation(key = "sample.miniPlayer", heightDp = 56.dp)
    }

    Box(Modifier.fillMaxSize().background(BgBlack)) {
        Content(
            miniBarVisible = miniBarVisible,
            onToggleMiniBar = { miniBarVisible = !miniBarVisible },
        )
        BottomOverlayStack(miniBarVisible = miniBarVisible)
    }
}

@Composable
private fun Content(
    miniBarVisible: Boolean,
    onToggleMiniBar: () -> Unit,
) {
    val insets = rememberMetroInsets()
    LazyColumn(
        modifier = Modifier.fillMaxSize().metroStatusBarsPadding(),
        contentPadding = bottomOverlayPadding(),
    ) {
        item { HeaderTitle() }
        item { DebugPanel(insets = insets) }
        item {
            ToggleRow(
                label = if (miniBarVisible) "Mini bar: ON  (tap to hide)"
                        else "Mini bar: OFF  (tap to show)",
                onClick = onToggleMiniBar,
            )
        }
        item { Spacer(Modifier.height(8.dp)) }
        items(80) { idx ->
            ListItem(index = idx)
        }
    }
}

@Composable
private fun HeaderTitle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        BasicText(
            text = "Kanesumi · MetroInsets demo",
            style = TextStyle(
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Normal,
            ),
        )
    }
}

@Composable
private fun DebugPanel(insets: MetroInsets) {
    val stack = LocalMetroBottomStack.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(PanelInk)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Line("System insets", bold = true)
        Line("  statusBar.top    = ${fmt(insets.statusBar.dp)}  (${"%.1f".format(insets.statusBar.px)} px)")
        Line("  navigationBar    = ${fmt(insets.navigationBar.dp)}  (${"%.1f".format(insets.navigationBar.px)} px)")
        Line("  displayCutout.T  = ${fmt(insets.displayCutout.top.dp)}")
        Line("  ime.bottom       = ${fmt(insets.ime.dp)}")
        Spacer(Modifier.height(6.dp))
        Line("Bottom stack (total ${fmt(stack.totalHeightDp)})", bold = true)
        stack.reservationsByKey.forEach { (k, v) ->
            Line("  $k  →  ${fmt(v)}", muted = true)
        }
    }
}

@Composable
private fun ToggleRow(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(AccentTeal)
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 16.dp),
    ) {
        BasicText(
            text = label,
            style = TextStyle(
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
            ),
        )
    }
}

@Composable
private fun ListItem(index: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        BasicText(
            text = "Row $index — scroll to the bottom; last row must not be covered.",
            style = TextStyle(color = TextMuted, fontSize = 14.sp),
        )
    }
}

@Composable
private fun BoxScope.BottomOverlayStack(miniBarVisible: Boolean) {
    Column(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth(),
    ) {
        if (miniBarVisible) {
            OverlayBar(label = "mini player (56dp)", tint = AccentBlue)
        }
        OverlayBar(label = "bottom nav (56dp)", tint = Color(0xFF16324F))
    }
}

@Composable
private fun OverlayBar(label: String, tint: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(tint),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = label,
            style = TextStyle(color = TextPrimary, fontSize = 13.sp),
        )
    }
}

@Composable
private fun Line(text: String, bold: Boolean = false, muted: Boolean = false) {
    BasicText(
        text = text,
        style = TextStyle(
            color = if (muted) TextMuted else TextPrimary,
            fontSize = 13.sp,
            fontWeight = if (bold) FontWeight.SemiBold else FontWeight.Normal,
            fontFamily = FontFamily.Monospace,
        ),
    )
}

private fun fmt(dp: Dp): String = "${dp.value.toInt()}dp"
