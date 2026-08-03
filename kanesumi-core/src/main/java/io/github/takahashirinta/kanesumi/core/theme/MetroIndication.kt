package io.github.takahashirinta.kanesumi.core.theme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import kotlinx.coroutines.launch

/**
 * Kanesumi 的默认交互反馈 —— 直角矩形闪切,取代 M3 ripple。
 *
 * 按下:tint 从 0 → 1 alpha 淡入 (enterDurationMs, EaseOutCubic)。
 * 松手 / 取消:1 → 0 淡出 (exitDurationMs)。
 * 无圆角、无 ripple 扩散、无 elevation 联动 —— Metro 到底。
 *
 * MetroTheme 通过 LocalIndication 自动挂上本 indication,任何 .clickable {}
 * 默认走此反馈,不需要显式设置 indication 参数。
 *
 * EaseOutCubic (1-(1-t)³) 与 Sokuou 里的 MetroCubic 数学上等价,故 core 不
 * 需要依赖 anim 就能保持"感觉一致"。
 *
 * 实现走 Compose 1.7+ 的 IndicationNodeFactory + Modifier.Node,alpha 读取
 * 在 draw phase 内完成,零重组。
 */
class MetroIndication(
    private val tint: Color,
    private val enterDurationMs: Int = 100,
    private val exitDurationMs: Int = 200,
) : IndicationNodeFactory {

    override fun create(interactionSource: InteractionSource): DelegatableNode =
        MetroIndicationNode(tint, interactionSource, enterDurationMs, exitDurationMs)

    override fun equals(other: Any?): Boolean =
        other is MetroIndication &&
        tint == other.tint &&
        enterDurationMs == other.enterDurationMs &&
        exitDurationMs == other.exitDurationMs

    override fun hashCode(): Int {
        var result = tint.hashCode()
        result = 31 * result + enterDurationMs
        result = 31 * result + exitDurationMs
        return result
    }
}

private class MetroIndicationNode(
    private val tint: Color,
    private val interactionSource: InteractionSource,
    private val enterDurationMs: Int,
    private val exitDurationMs: Int,
) : Modifier.Node(), DrawModifierNode {

    private val alpha = Animatable(0f)

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> alpha.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(enterDurationMs, easing = EaseOutCubic),
                    )
                    is PressInteraction.Release, is PressInteraction.Cancel -> alpha.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(exitDurationMs, easing = EaseOutCubic),
                    )
                }
            }
        }
    }

    override fun ContentDrawScope.draw() {
        drawContent()
        drawRect(color = tint, alpha = alpha.value)
    }
}
