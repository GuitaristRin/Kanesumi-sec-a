package io.github.takahashirinta.kanesumi.sample

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import io.github.takahashirinta.kanesumi.core.theme.MetroColors
import io.github.takahashirinta.kanesumi.core.insets.metroStatusBarsPadding
import io.github.takahashirinta.kanesumi.structure.sidebar.MetroSidebar
import io.github.takahashirinta.kanesumi.structure.sidebar.MetroSidebarItem
import io.github.takahashirinta.kanesumi.controls.MetroBottomSheet
import io.github.takahashirinta.kanesumi.controls.MetroButton
import io.github.takahashirinta.kanesumi.controls.MetroDialog
import io.github.takahashirinta.kanesumi.controls.MetroDivider
import io.github.takahashirinta.kanesumi.controls.MetroDropdownMenu
import io.github.takahashirinta.kanesumi.controls.MetroDropdownMenuItem
import io.github.takahashirinta.kanesumi.controls.MetroIconButton
import io.github.takahashirinta.kanesumi.controls.MetroListRow
import io.github.takahashirinta.kanesumi.controls.MetroLyricLine
import io.github.takahashirinta.kanesumi.controls.MetroLyricsPanel
import io.github.takahashirinta.kanesumi.controls.MetroProgressIndicator
import io.github.takahashirinta.kanesumi.controls.MetroResponsiveContent
import io.github.takahashirinta.kanesumi.controls.MetroSurface
import io.github.takahashirinta.kanesumi.controls.MetroSwitch
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
import io.github.takahashirinta.kanesumi.structure.MetroDetailScaffold
import io.github.takahashirinta.kanesumi.structure.MetroShell
import io.github.takahashirinta.kanesumi.structure.bottomnav.MetroBottomNav
import io.github.takahashirinta.kanesumi.structure.bottomnav.MetroBottomNavItem
import io.github.takahashirinta.kanesumi.structure.bottomnav.MetroBottomNavLabelMode
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var dark by rememberSaveable { mutableStateOf(false) }
            MetroTheme(colors = if (dark) MetroColors.dark() else MetroColors.light()) {
                SampleRoot(dark = dark, onToggleTheme = { dark = !dark })
            }
        }
    }
}

@Composable
private fun galleryNavigation() = listOf(
    MetroBottomNavItem(Icons.Filled.Home, stringResource(R.string.nav_overview)),
    MetroBottomNavItem(Icons.Filled.Settings, stringResource(R.string.nav_controls)),
    MetroBottomNavItem(Icons.Filled.Star, stringResource(R.string.nav_layout)),
    MetroBottomNavItem(Icons.Filled.Search, stringResource(R.string.nav_tools)),
)

private data class DemoDetail(val title: String, val subtitle: String, val artwork: Int)

@Composable
private fun galleryDetails() = List(3) { index ->
    DemoDetail(artTitle(index), stringResource(R.string.collection_subtitle), index)
}

@Composable
private fun SampleRoot(dark: Boolean, onToggleTheme: () -> Unit) {
    var miniBarVisible by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var openedDetail by remember { mutableStateOf<DemoDetail?>(null) }
    var dialogOpen by remember { mutableStateOf(false) }
    var sheetOpen by remember { mutableStateOf(false) }
    var lyricsOpen by remember { mutableStateOf(false) }
    var focusDemoOpen by remember { mutableStateOf(false) }
    var focusLabelMode by remember { mutableStateOf(MetroBottomNavLabelMode.Always) }
    var focusNavHeight by remember { mutableStateOf(56.dp) }
    val items = galleryNavigation()
    val details = galleryDetails()
    val colors = LocalMetroColors.current
    val activity = LocalContext.current as? ComponentActivity
    val imageVisible = openedDetail != null
    SideEffect {
        val transparent = android.graphics.Color.TRANSPARENT
        activity?.enableEdgeToEdge(
            statusBarStyle = if (dark || imageVisible) SystemBarStyle.dark(transparent)
                else SystemBarStyle.light(transparent, transparent),
            navigationBarStyle = if (dark) SystemBarStyle.dark(transparent)
                else SystemBarStyle.light(transparent, transparent),
        )
    }
    BackHandler(enabled = sheetOpen || lyricsOpen || focusDemoOpen || openedDetail != null) {
        when {
            sheetOpen -> sheetOpen = false
            lyricsOpen -> lyricsOpen = false
            focusDemoOpen -> focusDemoOpen = false
            else -> openedDetail = null
        }
    }
    val select: (Int) -> Unit = {
        selectedTab = it; openedDetail = null; lyricsOpen = false; focusDemoOpen = false
    }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val wide = maxWidth >= 600.dp
        MetroShell(bottomBar = {
            Column(Modifier.background(colors.surface).metroNavigationBarsPadding()) {
                if (miniBarVisible) OverlayBar(stringResource(R.string.mini_player), colors.surface)
                if (!wide) MetroBottomNav(items, selectedTab, select,
                    labelMode = if (focusDemoOpen) focusLabelMode else MetroBottomNavLabelMode.Always,
                    heightDp = if (focusDemoOpen) focusNavHeight else 56.dp)
            }
        }) {
            val insets = rememberMetroInsets()
            rememberBottomStackReservation("sample.systemNav", insets.navigationBar.dp)
            if (miniBarVisible) rememberBottomStackReservation("sample.miniPlayer", 56.dp)
            Row(Modifier.fillMaxSize()) {
                if (wide) MetroSidebar(
                    items.map { MetroSidebarItem(it.icon, it.label) }, selectedTab, select,
                    modifier = Modifier.metroStatusBarsPadding(), width = 200.dp,
                    header = { MetroText(stringResource(R.string.app_name),
                        modifier = Modifier.padding(16.dp), style = LocalMetroTypography.current.title) },
                )
                Box(Modifier.weight(1f)) {
                    if (lyricsOpen) LyricsDemo(onBack = { lyricsOpen = false })
                    else if (openedDetail != null) MetroResponsiveContent(maxWidthDp = if (wide) 640.dp else Dp.Unspecified) {
                        DetailDemo(openedDetail!!, onBack = { openedDetail = null })
                    }
                    else if (focusDemoOpen) MetroResponsiveContent(Modifier.metroStatusBarsPadding()) {
                        FocusSmallScreenDemo(items, focusLabelMode, focusNavHeight,
                            { focusLabelMode = it }, { focusNavHeight = it }, { focusDemoOpen = false })
                    }
                    else MetroResponsiveContent(modifier = Modifier.metroStatusBarsPadding(),
                        maxWidthDp = if (wide && (selectedTab == 1 || selectedTab == 3)) 640.dp else Dp.Unspecified) {
                        val header: @Composable () -> Unit = {
                            MetroAppBar(applyStatusBarsPadding = false,
                                title = if (selectedTab == 0) stringResource(R.string.gallery_title)
                                else items[selectedTab].label,
                                actions = { MetroButton(stringResource(if (dark) R.string.theme_light else R.string.theme_dark),
                                    onToggleTheme, kind = io.github.takahashirinta.kanesumi.controls.MetroButtonKind.Text) })
                        }
                        when (selectedTab) {
                            0 -> GalleryOverview(header) { openedDetail = details[it] }
                            1 -> GalleryControls(header, { dialogOpen = true }, { sheetOpen = true })
                            2 -> GalleryLayouts(header, { openedDetail = details[it] }, { lyricsOpen = true })
                            else -> GalleryTools(header, miniBarVisible, { miniBarVisible = !miniBarVisible },
                                { focusDemoOpen = true }, { DebugPanel(insets) })
                        }
                    }
                }
            }
        }
        // 顶栏仍随内容滚动；只遮住系统状态栏区域，避免列表字形与时间/信号重叠。
        if (!imageVisible) {
            Box(Modifier.fillMaxWidth().height(rememberMetroInsets().statusBar.dp).background(colors.surface))
        }
        // 同窗口弹层必须在不透明 Shell 之后绘制；Dialog 自带独立 Window。
        if (sheetOpen) SongMenuSheetStyleDemo(onDismiss = { sheetOpen = false })
        if (dialogOpen) PlayAllStyleDialog(onDismiss = { dialogOpen = false })
    }
}

@Composable
private fun DetailDemo(detail: DemoDetail, onBack: () -> Unit) {
    MetroDetailScaffold(
        onBack = onBack,
        backIcon = Icons.AutoMirrored.Filled.ArrowBack,
        header = { DetailHeader(detail) },
        content = { detailBody() },
    )
}

@Composable
private fun PlayAllStyleDialog(onDismiss: () -> Unit) {
    val colors = LocalMetroColors.current
    val typography = LocalMetroTypography.current
    MetroDialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
        ) {
            MetroText(
                text = stringResource(R.string.dialog_title),
                color = colors.onSurfaceVariant,
                style = typography.caption,
            )
        }
        MetroDivider()
        DialogRow(
            icon = Icons.Filled.PlayArrow,
            iconTint = colors.primary,
            title = stringResource(R.string.play_now),
            subtitle = stringResource(R.string.replace_queue),
            onClick = onDismiss,
        )
        MetroDivider()
        DialogRow(
            icon = Icons.Filled.Star,
            iconTint = colors.onSurface,
            title = stringResource(R.string.play_next),
            subtitle = stringResource(R.string.insert_next),
            onClick = onDismiss,
        )
        MetroDivider()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onDismiss)
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            MetroText(
                text = stringResource(R.string.action_cancel),
                color = colors.onSurfaceVariant,
                style = typography.body,
            )
        }
    }
}

@Composable
private fun SongMenuSheetStyleDemo(onDismiss: () -> Unit) {
    val colors = LocalMetroColors.current
    val typography = LocalMetroTypography.current
    val song = galleryDetails().first()
    MetroBottomSheet(
        onDismiss = onDismiss,
        applyNavigationBarsPadding = true,
        dragHandle = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                GalleryArtwork(song.artwork, Modifier.size(112.dp))
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    MetroText(
                        text = song.title,
                        color = colors.onSurface,
                        style = typography.title,
                    )
                    Spacer(Modifier.height(4.dp))
                    MetroText(
                        text = song.subtitle,
                        color = colors.primary,
                        style = typography.caption,
                    )
                }
            }
        },
    ) {
        MetroDivider()
        SheetAction(Icons.Filled.PlayArrow, stringResource(R.string.play_next), colors.primary, onDismiss)
        MetroDivider()
        SheetAction(Icons.Filled.Star, stringResource(R.string.add_library), colors.onSurface, onDismiss)
        MetroDivider()
        SheetAction(Icons.Filled.Settings, stringResource(R.string.share), colors.onSurface, onDismiss)
    }
}

@Composable
private fun SheetAction(
    icon: ImageVector,
    label: String,
    tint: Color,
    onDismiss: () -> Unit,
) {
    val typography = LocalMetroTypography.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onDismiss)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MetroIcon(imageVector = icon, contentDescription = null, tint = tint, sizeDp = 24.dp)
        Spacer(Modifier.width(16.dp))
        MetroText(text = label, color = LocalMetroColors.current.onSurface, style = typography.body)
    }
}

@Composable
internal fun OverflowMenuButton() {
    var expanded by remember { mutableStateOf(false) }
    val colors = LocalMetroColors.current
    Box {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(colors.surfaceVariant)
                .clickable { expanded = true },
            contentAlignment = Alignment.Center,
        ) {
            MetroIcon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = stringResource(R.string.more_actions),
                tint = colors.onSurface,
                sizeDp = 22.dp,
            )
        }
        MetroDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            MetroDropdownMenuItem(
                text = stringResource(R.string.play_next),
                onClick = { expanded = false },
                leading = { MetroIcon(Icons.Filled.PlayArrow, null, tint = colors.primary, sizeDp = 20.dp) },
            )
            MetroDivider()
            MetroDropdownMenuItem(
                text = stringResource(R.string.add_library),
                onClick = { expanded = false },
                leading = { MetroIcon(Icons.Filled.Star, null, tint = colors.onSurface, sizeDp = 20.dp) },
            )
            MetroDivider()
            MetroDropdownMenuItem(
                text = stringResource(R.string.share),
                enabled = false,
                onClick = { expanded = false },
            )
        }
    }
}

@Composable
private fun DialogRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    val typography = LocalMetroTypography.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MetroIcon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            sizeDp = 26.dp,
        )
        Spacer(Modifier.width(16.dp))
        Column {
            MetroText(text = title, color = LocalMetroColors.current.onSurface, style = typography.body)
            MetroText(text = subtitle, color = LocalMetroColors.current.onSurfaceVariant, style = typography.caption)
        }
    }
}
@Composable
private fun DetailHeader(detail: DemoDetail) {
    val colors = LocalMetroColors.current
    val typography = LocalMetroTypography.current
    Column(modifier = Modifier.fillMaxWidth()) {
        GalleryArtwork(detail.artwork, Modifier.fillMaxWidth().aspectRatio(1f))
        Spacer(Modifier.height(20.dp))
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            MetroText(text = detail.title, color = colors.onBackground, style = typography.pageHeading.copy(fontSize = 26.sp))
            Spacer(Modifier.height(6.dp))
            MetroText(text = detail.subtitle, color = colors.primary, style = typography.caption)
        }
        Spacer(Modifier.height(16.dp))
    }
}

private fun LazyListScope.detailBody() {
    items(20) { i ->
        MetroListRow(
            title = stringResource(R.string.track_title, i + 1),
            subtitle = "3:%02d".format((15 + i * 7) % 60),
            leading = {
                MetroText(
                    text = "%02d".format(i + 1),
                    modifier = Modifier.padding(start = 16.dp),
                )
            },
            onClick = {},
        )
    }
}

@Composable
private fun LyricsDemo(onBack: () -> Unit) {
    val colors = LocalMetroColors.current
    val typography = LocalMetroTypography.current
    val lyrics = remember {
        listOf(
            MetroLyricLine(0, "Somewhere only we know"),
            MetroLyricLine(3000, "I walked across an empty land"),
            MetroLyricLine(6000, "I knew the pathway like the back of my hand"),
            MetroLyricLine(9500, "I felt the earth beneath my feet"),
            MetroLyricLine(12500, "Sat by the river and it made me complete"),
            MetroLyricLine(15500, "Oh simple thing, where have you gone?"),
            MetroLyricLine(18500, "I'm getting old and I need something to rely on"),
            MetroLyricLine(22500, "So tell me when you're gonna let me in"),
            MetroLyricLine(25500, "I'm getting tired and I need somewhere to begin"),
        )
    }
    var playing by remember { mutableStateOf(false) }
    var positionMs by remember { mutableLongStateOf(0L) }

    LaunchedEffect(playing) {
        while (playing) {
            delay(16)
            positionMs += 16L
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .metroStatusBarsPadding()
            .padding(bottomOverlayPadding()),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MetroIconButton(onClick = onBack) {
                MetroIcon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = colors.onSurface,
                    sizeDp = 22.dp,
                )
            }
            MetroText(
                text = stringResource(R.string.lyrics_title),
                color = colors.onSurface,
                style = typography.title,
                modifier = Modifier.padding(start = 4.dp),
            )
        }
        BoxWithConstraints(Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp)) {
            // 参 Ncrust 的高度适配：短视口先收紧字级，保留实际可操作的控制区。
            val compact = maxHeight < 400.dp
            MetroLyricsPanel(
                lines = lyrics,
                currentPositionMillis = { positionMs },
                fontSize = if (compact) 24.sp else 32.sp,
                lineHeight = if (compact) 32.sp else 42.sp,
                translationFontSize = if (compact) 16.sp else 20.sp,
                translationLineHeight = if (compact) 21.sp else 26.sp,
                onLineClick = { positionMs = it },
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        ) {
            MetroButton(
                text = stringResource(if (playing) R.string.action_pause else R.string.action_play),
                onClick = { playing = !playing },
            )
            MetroButton(
                text = stringResource(R.string.action_restart),
                onClick = {
                    positionMs = 0L
                    playing = false
                },
                containerColor = colors.surfaceVariant,
                contentColor = colors.onSurface,
            )
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
                MetroText("  $k  →  ${fmt(v)}", style = mono, color = colors.onSurfaceVariant)
            }
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
