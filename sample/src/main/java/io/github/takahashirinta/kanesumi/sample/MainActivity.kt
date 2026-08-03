package io.github.takahashirinta.kanesumi.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import io.github.takahashirinta.kanesumi.controls.MetroButton
import io.github.takahashirinta.kanesumi.controls.MetroListRow
import io.github.takahashirinta.kanesumi.controls.MetroSurface
import io.github.takahashirinta.kanesumi.core.insets.LocalMetroBottomStack
import io.github.takahashirinta.kanesumi.core.insets.MetroInsets
import io.github.takahashirinta.kanesumi.core.insets.bottomOverlayPadding
import io.github.takahashirinta.kanesumi.core.insets.metroNavigationBarsPadding
import io.github.takahashirinta.kanesumi.core.insets.rememberBottomStackReservation
import io.github.takahashirinta.kanesumi.core.insets.rememberMetroInsets
import io.github.takahashirinta.kanesumi.core.theme.LocalMetroColors
import io.github.takahashirinta.kanesumi.core.theme.LocalMetroTypography
import io.github.takahashirinta.kanesumi.core.theme.MetroIcon
import io.github.takahashirinta.kanesumi.core.theme.MetroText
import io.github.takahashirinta.kanesumi.core.theme.MetroTheme
import io.github.takahashirinta.kanesumi.structure.MetroAppBar
import io.github.takahashirinta.kanesumi.structure.MetroShell
import io.github.takahashirinta.kanesumi.structure.bottomnav.MetroBottomNav
import io.github.takahashirinta.kanesumi.structure.bottomnav.MetroBottomNavItem

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MetroTheme {
                SampleRoot()
            }
        }
    }
}

private val DemoMiniBar = Color(0xFF2E67B5)

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

    MetroShell(
        bottomBar = {
            Column {
                if (miniBarVisible) OverlayBar(label = "mini player (56dp)", tint = DemoMiniBar)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LocalMetroColors.current.surface)
                        .metroNavigationBarsPadding(),
                ) {
                    MetroBottomNav(
                        items = NavItems,
                        selectedIndex = selectedTab,
                        onSelected = { selectedTab = it },
                    )
                }
            }
        },
    ) {
        // MetroBottomStackScope is provided by MetroShell — this reservation goes into it.
        if (miniBarVisible) {
            rememberBottomStackReservation(key = "sample.miniPlayer", heightDp = 56.dp)
        }

        val insets = rememberMetroInsets()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = bottomOverlayPadding(),
        ) {
            item {
                MetroAppBar(
                    title = "Kanesumi · shell demo",
                    actions = {
                        MetroIcon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "settings",
                            tint = LocalMetroColors.current.onSurfaceMuted,
                            sizeDp = 22.dp,
                        )
                    },
                )
            }
            item { DebugPanel(insets = insets) }
            item { Spacer(Modifier.height(8.dp)) }
            item {
                MetroButton(
                    text = if (miniBarVisible) "Hide mini player" else "Show mini player",
                    onClick = { miniBarVisible = !miniBarVisible },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    leadingIcon = Icons.Filled.Star,
                )
            }
            item { Spacer(Modifier.height(8.dp)) }
            items(80) { idx ->
                SampleRow(index = idx)
            }
        }
    }
}

@Composable
private fun DebugPanel(insets: MetroInsets) {
    val stack = LocalMetroBottomStack.current
    val colors = LocalMetroColors.current
    val typography = LocalMetroTypography.current
    val mono = typography.caption.copy(fontFamily = FontFamily.Monospace)

    MetroSurface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        color = colors.surfaceVariant,
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            MetroText(text = "System insets", style = typography.caption)
            MetroText("  statusBar.top    = ${fmt(insets.statusBar.dp)}  (${"%.1f".format(insets.statusBar.px)} px)", style = mono)
            MetroText("  navigationBar    = ${fmt(insets.navigationBar.dp)}  (${"%.1f".format(insets.navigationBar.px)} px)", style = mono)
            MetroText("  displayCutout.T  = ${fmt(insets.displayCutout.top.dp)}", style = mono)
            MetroText("  ime.bottom       = ${fmt(insets.ime.dp)}", style = mono)
            Spacer(Modifier.height(6.dp))
            MetroText(text = "Bottom stack (total ${fmt(stack.totalHeightDp)})", style = typography.caption)
            stack.reservationsByKey.forEach { (k, v) ->
                MetroText("  $k  →  ${fmt(v)}", style = mono, color = colors.onSurfaceMuted)
            }
        }
    }
}

@Composable
private fun SampleRow(index: Int) {
    val colors = LocalMetroColors.current
    MetroListRow(
        title = "Row $index",
        subtitle = "scroll to bottom; last row must clear both bars",
        leading = {
            Box(
                Modifier
                    .size(40.dp)
                    .background(colors.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                MetroText(
                    text = "%02d".format(index),
                    color = colors.onSurfaceMuted,
                    style = LocalMetroTypography.current.label,
                )
            }
        },
        trailing = {
            MetroIcon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = colors.onSurfaceMuted,
                sizeDp = 20.dp,
            )
        },
        onClick = {},
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
    )
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
