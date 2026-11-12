package io.github.takahashirinta.kanesumi.sample

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

/** 本仓原创几何习作，充当内容资产；固定墨/纸色不属于主题令牌或占位加载状态。 */
@Composable
internal fun GalleryArtwork(index: Int, modifier: Modifier = Modifier) {
    val description = artTitle(index)
    Canvas(modifier.semantics { contentDescription = description }) {
        val paper = Color(0xFFDDD6C6)
        val ink = Color(0xFF17242D)
        val blue = Color(0xFF54778B)
        fun panel(color: Color, x: Float, y: Float, w: Float, h: Float) =
            drawRect(color, Offset(size.width * x, size.height * y), Size(size.width * w, size.height * h))
        when (index % 3) {
            0 -> {
                drawRect(paper)
                panel(ink, .56f, 0f, .44f, 1f)
                panel(blue, .44f, .08f, .12f, .76f)
                panel(ink, 0f, .72f, .56f, .035f)
                panel(paper, .72f, .18f, .12f, .39f)
                panel(ink, .1f, .12f, .025f, .48f)
            }
            1 -> {
                drawRect(ink)
                panel(paper, .18f, .12f, .61f, .7f)
                panel(ink, .47f, .12f, .06f, .7f)
                panel(ink, .18f, .42f, .61f, .035f)
                panel(blue, .07f, .74f, .72f, .1f)
            }
            else -> {
                drawRect(blue)
                panel(paper, .1f, .12f, .44f, .88f)
                panel(ink, .45f, .31f, .45f, .69f)
                panel(paper, .61f, .44f, .1f, .28f)
                panel(ink, .21f, 0f, .035f, .74f)
                panel(ink, 0f, .82f, 1f, .04f)
            }
        }
    }
}
