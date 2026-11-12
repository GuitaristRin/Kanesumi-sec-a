package io.github.takahashirinta.kanesumi.sample

import androidx.compose.foundation.clickable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.takahashirinta.kanesumi.controls.*
import io.github.takahashirinta.kanesumi.core.insets.bottomOverlayPadding
import io.github.takahashirinta.kanesumi.core.theme.*

@Composable
internal fun GalleryOverview(header: @Composable () -> Unit, onDetail: (Int) -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = bottomOverlayPadding()) {
        item { header() }
        item { GalleryIntro(R.string.gallery_intro, R.string.gallery_note) }
        item { GallerySection(R.string.section_collection) }
        item {
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                val count = if (maxWidth >= 600.dp) 3 else 2
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(count) { index ->
                        Column(Modifier.weight(1f).clickable { onDetail(index) }) {
                            GalleryArtwork(index, Modifier.fillMaxWidth().aspectRatio(1f))
                            Column(Modifier.padding(start = 16.dp, top = 10.dp, bottom = 8.dp)) {
                                MetroText(artTitle(index), style = LocalMetroTypography.current.bodyLarge)
                                MetroText(stringResource(R.string.art_caption),
                                    style = LocalMetroTypography.current.bodySmall,
                                    color = LocalMetroColors.current.onSurfaceVariant,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }
        }
        item { GallerySection(R.string.section_recent) }
        galleryRows(onDetail)
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
internal fun artTitle(index: Int): String = stringResource(when (index) {
    0 -> R.string.art_morning
    1 -> R.string.art_quiet
    else -> R.string.art_city
})

private fun LazyListScope.galleryRows(onDetail: (Int) -> Unit) {
    items(3) { index ->
        MetroListRow(
            title = if (index == 2) stringResource(R.string.long_title) else artTitle(index),
            subtitle = stringResource(R.string.list_metadata),
            leading = { GalleryArtwork(index, Modifier.size(72.dp)) },
            titleStyle = LocalMetroTypography.current.bodyLarge,
            subtitleStyle = LocalMetroTypography.current.bodySmall,
            onClick = { onDetail(index) },
        )
    }
}

@Composable
private fun GalleryIntro(title: Int, note: Int? = null) {
    Column(Modifier.padding(horizontal = 16.dp, vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        MetroText(stringResource(title), color = LocalMetroColors.current.onSurfaceVariant)
        if (note != null) MetroText(stringResource(note), style = LocalMetroTypography.current.caption,
            color = LocalMetroColors.current.onSurfaceVariant)
    }
}

@Composable
private fun GallerySection(title: Int) {
    MetroText(stringResource(title), style = LocalMetroTypography.current.subtitle,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 28.dp, bottom = 12.dp))
}

@Composable
internal fun GalleryLayouts(header: @Composable () -> Unit, onDetail: (Int) -> Unit, onLyrics: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = bottomOverlayPadding()) {
        item { header() }
        item { GalleryIntro(R.string.layout_intro) }
        item { GallerySection(R.string.section_list) }
        galleryRows(onDetail)
        item { GallerySection(R.string.section_details) }
        item {
            MetroListRow(stringResource(R.string.open_lyrics), subtitle = stringResource(R.string.lyrics_note),
                onClick = onLyrics, contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp))
        }
    }
}

@Composable
internal fun GalleryControls(header: @Composable () -> Unit, onDialog: () -> Unit, onSheet: () -> Unit) {
    var autoPlay by remember { mutableStateOf(false) }
    var translation by remember { mutableStateOf(true) }
    var name by remember { mutableStateOf("") }
    var tab by remember { mutableIntStateOf(0) }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = bottomOverlayPadding()) {
        item { header() }
        item { GalleryIntro(R.string.controls_intro) }
        item { GallerySection(R.string.section_playback) }
        item { GallerySwitchRow(R.string.autoplay, R.string.autoplay_note, autoPlay) { autoPlay = it } }
        item { GallerySwitchRow(R.string.translation, R.string.translation_note, translation) { translation = it } }
        item { GallerySection(R.string.section_actions) }
        item {
            Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetroButton(stringResource(R.string.action_apply), {})
                MetroButton(stringResource(R.string.action_play), onDialog, kind = MetroButtonKind.Accent)
                MetroButton(stringResource(R.string.unavailable), {}, enabled = false)
            }
        }
        item {
            Row(Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetroButton(stringResource(R.string.action_dialog), onDialog)
                MetroButton(stringResource(R.string.action_sheet), onSheet)
            }
        }
        item { GallerySection(R.string.section_input) }
        item {
            Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                MetroText(stringResource(R.string.field_name))
                MetroTextField(name, { name = it }, singleLine = true, placeholder = stringResource(R.string.field_hint))
            }
        }
        item { GallerySection(R.string.section_browse) }
        item {
            MetroTabRow(listOf(MetroTabItem(stringResource(R.string.tab_all)),
                MetroTabItem(stringResource(R.string.tab_albums)), MetroTabItem(stringResource(R.string.tab_songs))),
                tab, { tab = it }, modifier = Modifier.padding(horizontal = 16.dp))
        }
        item {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                MetroProgressIndicator(modifier = Modifier.size(24.dp))
                Spacer(Modifier.weight(1f))
                OverflowMenuButton()
            }
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun GallerySwitchRow(title: Int, note: Int, checked: Boolean, onChange: (Boolean) -> Unit) {
    val state = stringResource(if (checked) R.string.switch_on else R.string.switch_off)
    // 父行统一语义与点击入口，轨道不再作为无名的第二个无障碍目标。
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp).heightIn(min = 76.dp)
        .toggleable(value = checked, role = androidx.compose.ui.semantics.Role.Switch, onValueChange = onChange),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            MetroText(stringResource(title))
            MetroText(stringResource(note), style = LocalMetroTypography.current.caption,
                color = LocalMetroColors.current.onSurfaceVariant)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            MetroSwitch(checked, onChange, modifier = Modifier.clearAndSetSemantics { })
            MetroText(state, style = LocalMetroTypography.current.label, color = LocalMetroColors.current.onSurfaceVariant)
        }
    }
}

@Composable
internal fun GalleryTools(header: @Composable () -> Unit, miniVisible: Boolean,
    onToggleMini: () -> Unit, onFocus: () -> Unit, diagnostics: @Composable () -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = bottomOverlayPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { header() }
        item { GalleryIntro(R.string.tools_intro) }
        item { MetroButton(stringResource(R.string.focus_demo), onFocus, Modifier.padding(horizontal = 16.dp)) }
        item { MetroButton(stringResource(if (miniVisible) R.string.hide_mini else R.string.show_mini),
            onToggleMini, Modifier.padding(horizontal = 16.dp)) }
        item { diagnostics() }
    }
}
