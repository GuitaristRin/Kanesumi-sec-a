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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.takahashirinta.kanesumi.core.insets.LocalMetroBottomStack
import io.github.takahashirinta.kanesumi.core.insets.MetroBottomStackScope
import io.github.takahashirinta.kanesumi.core.insets.MetroInsets
import io.github.takahashirinta.kanesumi.core.insets.bottomOverlayPadding
import io.github.takahashirinta.kanesumi.core.insets.metroNavigationBarsPadding
import io.github.takahashirinta.kanesumi.core.insets.metroStatusBarsPadding
import io.github.takahashirinta.kanesumi.core.insets.rememberBottomStackReservation
import io.github.takahashirinta.kanesumi.core.insets.rememberMetroInsets
import io.github.takahashirinta.kanesumi.core.theme.LocalMetroColors
import io.github.takahashirinta.kanesumi.core.theme.LocalMetroTypography
import io.github.takahashirinta.kanesumi.core.theme.MetroText
import io.github.takahashirinta.kanesumi.core.theme.MetroTheme
import io.github.takahashirinta.kanesumi.structure.bottomnav.MetroBottomNav
import io.github.takahashirinta.kanesumi.structure.bottomnav.MetroBottomNavItem

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MetroTheme {
                MetroBottomStackScope {
                    SampleRoot()
                }
            }
        }
    }
}

// Sample-specific accent colours. These are demo highlights (blue mini bar,
// teal toggle) that don't correspond to theme roles.
private val DemoMiniBar = Color(0xFF2E67B5)
private val DemoToggle = Color(0xFF17A2A2)

private val NavItems = listOf(
    MetroBottomNavItem(Icons.Filled.Home, "home"),
    MetroBottomNavItem(Icons.Filled.Search, "search"),
    MetroBottomNavItem(Icons.Filled.Star, "star"),
    MetroBottomNavItem(Icons.Filled.Settings, "settings"),
)

@Composable
private fun SampleRoot() {
    var miniBarVisible by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }

    if (miniBarVisible) {
        rememberBottomStackReservation(key = "sample.miniPlayer", heightDp = 56.dp)
    }

    val colors = LocalMetroColors.current
    Box(Modifier.fillMaxSize().background(colors.background)) {
        Content(
            miniBarVisible = miniBarVisible,
            onToggleMiniBar = { miniBarVisible = !miniBarVisible },
        )
        BottomOverlayStack(
            miniBarVisible = miniBarVisible,
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
        )
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
        MetroText(
            text = "Kanesumi · MetroTheme demo",
            style = LocalMetroTypography.current.title,
        )
    }
}

@Composable
private fun DebugPanel(insets: MetroInsets) {
    val stack = LocalMetroBottomStack.current
    val colors = LocalMetroColors.current
    val typography = LocalMetroTypography.current
    val mono = typography.caption.copy(fontFamily = FontFamily.Monospace)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(colors.surfaceVariant)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        MetroText(text = "System insets", style = typography.caption)
        MetroText(text = "  statusBar.top    = ${fmt(insets.statusBar.dp)}  (${"%.1f".format(insets.statusBar.px)} px)", style = mono, color = colors.onSurface)
        MetroText(text = "  navigationBar    = ${fmt(insets.navigationBar.dp)}  (${"%.1f".format(insets.navigationBar.px)} px)", style = mono, color = colors.onSurface)
        MetroText(text = "  displayCutout.T  = ${fmt(insets.displayCutout.top.dp)}", style = mono, color = colors.onSurface)
        MetroText(text = "  ime.bottom       = ${fmt(insets.ime.dp)}", style = mono, color = colors.onSurface)
        Spacer(Modifier.height(6.dp))
        MetroText(text = "Bottom stack (total ${fmt(stack.totalHeightDp)})", style = typography.caption)
        stack.reservationsByKey.forEach { (k, v) ->
            MetroText(text = "  $k  →  ${fmt(v)}", style = mono, color = colors.onSurfaceMuted)
        }
    }
}

@Composable
private fun ToggleRow(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(DemoToggle)
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 16.dp),
    ) {
        MetroText(
            text = label,
            color = LocalMetroColors.current.onSurface,
            style = LocalMetroTypography.current.body,
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
        MetroText(
            text = "Row $index — scroll to the bottom; last row must clear both bars.",
            color = LocalMetroColors.current.onSurfaceMuted,
            style = LocalMetroTypography.current.body,
        )
    }
}

@Composable
private fun BoxScope.BottomOverlayStack(
    miniBarVisible: Boolean,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
) {
    val colors = LocalMetroColors.current
    Column(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth(),
    ) {
        if (miniBarVisible) {
            OverlayBar(label = "mini player (56dp)", tint = DemoMiniBar)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surface)
                .metroNavigationBarsPadding(),
        ) {
            MetroBottomNav(
                items = NavItems,
                selectedIndex = selectedTab,
                onSelected = onTabSelected,
            )
        }
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
        MetroText(
            text = label,
            color = LocalMetroColors.current.onSurface,
            style = LocalMetroTypography.current.caption,
        )
    }
}

private fun fmt(dp: Dp): String = "${dp.value.toInt()}dp"
