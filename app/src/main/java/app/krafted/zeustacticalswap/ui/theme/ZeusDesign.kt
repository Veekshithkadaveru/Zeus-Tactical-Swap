package app.krafted.zeustacticalswap.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Shared design system for Zeus Tactical Swap — a single source of truth that mirrors the
 * `src/styles.css` token set from the HTML/JSX prototype. All screens and components should
 * pull colors, fonts, and primitives from here so the look stays consistent.
 */
object Zeus {

    // ── Palette (mirrors :root CSS variables) ──────────────────────────────
    val Ink = Color(0xFFF7E9C4)
    val InkDim = Color(0xFFC9B78B)
    val InkMute = Color(0xFF8A7B5A)

    val Gold = Color(0xFFE7B549)
    val GoldDeep = Color(0xFFA8761F)
    val GoldHi = Color(0xFFFFE49A)
    val GoldEdge = Color(0xFF6E4811)

    val Crimson = Color(0xFFD23C3C)
    val CrimsonDeep = Color(0xFF761A1A)
    val CrimsonHi = Color(0xFFFF8C8C)

    val Electric = Color(0xFF5DC9FF)
    val ElectricDeep = Color(0xFF1C5A8F)
    val ElectricHi = Color(0xFFA8D8FF)

    val Emerald = Color(0xFF4FD49A)
    val Poison = Color(0xFF9BE23A)
    val Stone = Color(0xFFB6B6B6)
    val Purple = Color(0xFFC97CF2)

    val BgDeep = Color(0xFF06081A)
    val Night = Color(0xFF04050F)
    val ButtonInk = Color(0xFF1A1303)

    // Panel fills (top → bottom gradient stops)
    val PanelTop = Color(0xFF141732)
    val PanelBottom = Color(0xFF080B1C)
    val PanelBorder = Gold.copy(alpha = 0.55f)
    val PanelBorderInner = Gold.copy(alpha = 0.25f)

    // ── Typography ─────────────────────────────────────────────────────────
    // No bundled font files, so we approximate the prototype's Cinzel/serif
    // display face with the platform serif, and its JetBrains Mono with monospace.
    val Display: FontFamily = FontFamily.Serif
    val Mono: FontFamily = FontFamily.Monospace

    // ── Brushes ──────────────────────────────────────────────────────────────
    val goldTextBrush = Brush.verticalGradient(listOf(GoldHi, Gold, GoldDeep))
    val goldFillBrush = Brush.verticalGradient(listOf(GoldHi, Gold, GoldDeep))
    val panelBrush = Brush.verticalGradient(listOf(PanelTop, PanelBottom))

    // HP-bar fills by kind
    val hpBossBrush = Brush.verticalGradient(listOf(CrimsonHi, Crimson, CrimsonDeep))
    val hpPlayerBrush = Brush.verticalGradient(listOf(GoldHi, Gold, GoldDeep))
    val hpShieldBrush = Brush.verticalGradient(listOf(ElectricHi, Electric, ElectricDeep))
    val hpTrackBrush = Brush.verticalGradient(listOf(Color(0xFF0A0A17), Color(0xFF1A1A2E)))

    // ── Text styles ──────────────────────────────────────────────────────────
    /** Cinzel-style display heading with the gold gradient + soft glow. */
    fun goldHeading(size: Int, weight: FontWeight = FontWeight.Black, tracking: Double = 0.04) =
        TextStyle(
            brush = goldTextBrush,
            fontFamily = Display,
            fontWeight = weight,
            fontSize = size.sp,
            letterSpacing = (size * tracking).sp,
            shadow = Shadow(color = Gold.copy(alpha = 0.35f), blurRadius = 22f)
        )

    /** Tracked uppercase mono label (the ".tracked .mono" eyebrow text). */
    fun monoLabel(size: Int = 10, color: Color = InkMute, tracking: Double = 0.22) =
        TextStyle(
            fontFamily = Mono,
            fontWeight = FontWeight.Medium,
            fontSize = size.sp,
            letterSpacing = (size * tracking).sp,
            color = color
        )
}

enum class HpKind { BOSS, PLAYER, SHIELD }
enum class ChipTone { NEUTRAL, LIVE, POISON, RAGE, CRIT, PETRIFY, COMPLETE }

/** The double-gold-border panel from `.panel` — gradient fill, outer + inset gold rule. */
fun Modifier.zeusPanel(corner: Int = 10): Modifier = this
    .clip(RoundedCornerShape(corner.dp))
    .background(Zeus.panelBrush, RoundedCornerShape(corner.dp))
    .border(1.dp, Zeus.PanelBorder, RoundedCornerShape(corner.dp))
    .drawBehind {
        val inset = 3.dp.toPx()
        drawRoundRectStroke(inset, Zeus.PanelBorderInner, corner)
    }

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawRoundRectStroke(
    inset: Float,
    color: Color,
    corner: Int
) {
    drawRoundRect(
        color = color,
        topLeft = Offset(inset, inset),
        size = androidx.compose.ui.geometry.Size(size.width - inset * 2, size.height - inset * 2),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius((corner - 1).dp.toPx()),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
    )
}

/** Thin gold gradient divider (`.gold-rule`). */
@Composable
fun GoldRule(modifier: Modifier = Modifier) {
    Box(
        modifier
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(Color.Transparent, Zeus.Gold.copy(alpha = 0.6f), Color.Transparent)
                )
            )
    )
}

/** Primary gold gradient button (`.btn`). */
@Composable
fun GoldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "pressScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        label = "pressAlpha"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .clip(RoundedCornerShape(4.dp))
            .background(Zeus.goldFillBrush, RoundedCornerShape(4.dp))
            .border(1.dp, Zeus.GoldEdge, RoundedCornerShape(4.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.foundation.LocalIndication.current,
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = 24.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = text.uppercase(),
            color = Zeus.ButtonInk,
            fontFamily = Zeus.Display,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            letterSpacing = 2.5.sp
        )
    }
}

/** Bordered ghost button (`.btn-ghost`). */
@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "pressScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        label = "pressAlpha"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .clip(RoundedCornerShape(4.dp))
            .border(1.dp, Zeus.Gold.copy(alpha = 0.45f), RoundedCornerShape(4.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.foundation.LocalIndication.current,
                onClick = onClick
            )
            .padding(horizontal = 24.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = text.uppercase(),
            color = Zeus.InkDim,
            fontFamily = Zeus.Display,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            letterSpacing = 2.5.sp
        )
    }
}

/** Pill chip (`.chip`) with status tones. */
@Composable
fun ZeusChip(
    label: String,
    tone: ChipTone = ChipTone.NEUTRAL,
    modifier: Modifier = Modifier
) {
    val color = when (tone) {
        ChipTone.NEUTRAL -> Zeus.InkDim
        ChipTone.LIVE -> Zeus.Electric
        ChipTone.POISON -> Zeus.Poison
        ChipTone.RAGE -> Zeus.Crimson
        ChipTone.CRIT -> Zeus.GoldHi
        ChipTone.PETRIFY -> Zeus.Stone
        ChipTone.COMPLETE -> Zeus.Emerald
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(100.dp))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(100.dp))
            .padding(horizontal = 9.dp, vertical = 3.dp)
    ) {
        androidx.compose.material3.Text(
            text = label,
            color = color,
            fontFamily = Zeus.Mono,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            letterSpacing = 1.2.sp
        )
    }
}

/**
 * HP / shield bar (`.bar`) — 14dp tall, rounded, dark track, gradient fill by [kind],
 * with the faint vertical tick overlay. [fraction] should already be animated by the caller.
 */
@Composable
fun HpBar(
    fraction: Float,
    kind: HpKind,
    modifier: Modifier = Modifier
) {
    val fill = when (kind) {
        HpKind.BOSS -> Zeus.hpBossBrush
        HpKind.PLAYER -> Zeus.hpPlayerBrush
        HpKind.SHIELD -> Zeus.hpShieldBrush
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(14.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(Zeus.hpTrackBrush, RoundedCornerShape(7.dp))
            .border(1.dp, Color.Black.copy(alpha = 0.6f), RoundedCornerShape(7.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction.coerceIn(0f, 1f))
                .height(14.dp)
                .padding(1.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(fill, RoundedCornerShape(6.dp))
        )
        // tick overlay every 10%
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    val step = size.width / 10f
                    for (i in 1 until 10) {
                        drawLine(
                            color = Color.Black.copy(alpha = 0.4f),
                            start = Offset(step * i, 0f),
                            end = Offset(step * i, size.height),
                            strokeWidth = 1f
                        )
                    }
                }
        )
    }
}

/** Three glowing charge pips (`.pips`/`.pip`). */
@Composable
fun ChargePips(
    charges: Int,
    glow: Float = 1f,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(3) { i ->
            val on = i < charges
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .then(
                        if (on) Modifier
                            .background(
                                Brush.radialGradient(listOf(Zeus.GoldHi, Zeus.Gold, Zeus.GoldEdge)),
                                CircleShape
                            )
                            .border(1.dp, Zeus.GoldHi.copy(alpha = glow), CircleShape)
                        else Modifier
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .border(1.dp, Zeus.Gold.copy(alpha = 0.4f), CircleShape)
                    )
            )
        }
    }
}
