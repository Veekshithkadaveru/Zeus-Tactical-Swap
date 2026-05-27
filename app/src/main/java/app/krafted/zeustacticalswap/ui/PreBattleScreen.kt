package app.krafted.zeustacticalswap.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.zeustacticalswap.game.BossId

private val Gold = Color(0xFFE7B549)
private val GoldBright = Color(0xFFFFE49A)
private val Crimson = Color(0xFFD24747)
private val Ink = Color(0xFFEDE7D6)
private val InkDim = Color(0xFFB8AE97)
private val InkMute = Color(0xFF7A745F)
private val PanelBg = Color(0xCC0B0D18)
private val PanelBorder = Color(0x40E7B549)

private data class BossLore(val ability: String, val hint: String)

private fun BossId.lore(): BossLore = when (this) {
    BossId.KRONOS -> BossLore("Rage", "Damage doubles below 50% HP.")
    BossId.TYPHON -> BossLore("Cyclone", "Every 5 turns, wipes a row.")
    BossId.HADES -> BossLore("Corruption", "Every 4 turns, places 3 skull tiles.")
}

private fun BossId.trialNumber(): Int = when (this) {
    BossId.KRONOS -> 1
    BossId.TYPHON -> 2
    BossId.HADES -> 3
}

@Composable
fun PreBattleScreen(boss: BossId, onBegin: () -> Unit, onBack: () -> Unit) {
    val lore = boss.lore()
    val firstName = boss.displayName.substringBefore(' ')
    val subtitle = boss.displayName.substringAfter(' ').uppercase()

    val pulse = rememberInfiniteTransition(label = "trialPulse")
    val warnAlpha by pulse.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "warnAlpha"
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF04050F))
    ) {
        Image(
            painter = painterResource(boss.backgroundRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.7f),
            contentScale = ContentScale.Crop
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0x66000000), Color(0xE6000000)),
                        center = Offset.Unspecified
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 22.dp, end = 22.dp, top = 54.dp, bottom = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.Start),
                shape = RoundedCornerShape(100.dp),
                border = BorderStroke(1.dp, Gold.copy(alpha = 0.35f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = InkDim),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)
            ) {
                Text(
                    "← BACK",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    letterSpacing = 1.8.sp
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "⚠ TRIAL ${boss.trialNumber()} ⚠",
                    color = Crimson,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 4.6.sp,
                    modifier = Modifier.alpha(warnAlpha)
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    firstName,
                    color = Gold,
                    fontWeight = FontWeight.Black,
                    fontSize = 38.sp,
                    letterSpacing = 1.5.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    subtitle,
                    color = InkDim,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 4.2.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                GoldRule(Modifier.width(180.dp))
                Spacer(Modifier.height(16.dp))

                StatPanel(boss = boss, lore = lore)
            }

            Button(
                onClick = onBegin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = Color(0xFF1A1303)
                )
            ) {
                Text(
                    "Begin the Trial",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}

@Composable
private fun StatPanel(boss: BossId, lore: BossLore) {
    Column(
        modifier = Modifier
            .widthIn(max = 320.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(PanelBg)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatColumn("HP", boss.maxHp.toString(), Crimson)
            StatColumn("STRIKE", "${boss.minAttack}–${boss.maxAttack}", Ink)
            StatColumn("POWER", lore.ability, Gold)
        }
        Spacer(Modifier.height(12.dp))
        GoldRule(Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        Text(
            "\"${lore.hint}\"",
            color = InkDim,
            fontSize = 12.sp,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun StatColumn(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            label,
            color = InkMute,
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            letterSpacing = 1.8.sp
        )
        Spacer(Modifier.height(4.dp))
        Text(value, color = valueColor, fontWeight = FontWeight.Black, fontSize = 22.sp)
    }
}

@Composable
private fun GoldRule(modifier: Modifier = Modifier) {
    Box(
        modifier
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(Color.Transparent, GoldBright.copy(alpha = 0.6f), Color.Transparent)
                )
            )
    )
}

@Preview
@Composable
private fun PreBattleScreenPreview() {
    PreBattleScreen(boss = BossId.TYPHON, onBegin = {}, onBack = {})
}
