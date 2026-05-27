package app.krafted.zeustacticalswap.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import app.krafted.zeustacticalswap.game.BossId
import app.krafted.zeustacticalswap.game.BossState
import app.krafted.zeustacticalswap.ui.theme.ChipTone
import app.krafted.zeustacticalswap.ui.theme.HpBar
import app.krafted.zeustacticalswap.ui.theme.HpKind
import app.krafted.zeustacticalswap.ui.theme.Zeus
import app.krafted.zeustacticalswap.ui.theme.ZeusChip
import app.krafted.zeustacticalswap.ui.theme.zeusPanel
import kotlin.math.roundToInt

@Composable
fun BossPanel(boss: BossState, trialNumber: Int = 1, modifier: Modifier = Modifier) {
    val animatedFraction by animateFloatAsState(
        targetValue = boss.hpFraction.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 600),
        label = "bossHp"
    )

    val shakeOffset = remember { Animatable(0f) }
    val previousHp = remember { intArrayOf(boss.currentHp) }
    LaunchedEffect(boss.currentHp) {
        if (boss.currentHp < previousHp[0]) {
            shakeOffset.snapTo(12f)
            shakeOffset.animateTo(0f, spring(dampingRatio = 0.12f, stiffness = 700f))
        }
        previousHp[0] = boss.currentHp
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
            .zeusPanel()
            .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp)
    ) {
        // Row 1: Trial label + HP
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "BOSS · TRIAL $trialNumber",
                style = Zeus.monoLabel(size = 9, color = Zeus.InkMute, tracking = 0.3)
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(Zeus.monoLabel(size = 11, color = Zeus.Ink, tracking = 0.0).toSpanStyle()) {
                        append("${boss.currentHp}")
                    }
                    withStyle(Zeus.monoLabel(size = 11, color = Zeus.InkDim.copy(alpha = 0.6f), tracking = 0.0).toSpanStyle()) {
                        append(" / ${boss.maxHp}")
                    }
                }
            )
        }
        // Row 2: Boss name
        Text(
            text = boss.name,
            style = Zeus.goldHeading(size = 18)
        )
        // Row 3: Status chips (only if any active)
        val hasChips = boss.isEnraged || boss.petrifyTurnsLeft > 0 || boss.poisonTurnsLeft > 0
        if (hasChips) {
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (boss.isEnraged) ZeusChip("⚔ RAGE", ChipTone.RAGE)
                if (boss.petrifyTurnsLeft > 0) ZeusChip("◈ PETRIFIED", ChipTone.PETRIFY)
                if (boss.poisonTurnsLeft > 0) ZeusChip("☠ POISON ${boss.poisonTurnsLeft}", ChipTone.POISON)
            }
        }
        Spacer(Modifier.height(6.dp))
        HpBar(fraction = animatedFraction, kind = HpKind.BOSS)
    }
}

