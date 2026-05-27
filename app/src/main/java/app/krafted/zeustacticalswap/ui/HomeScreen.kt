package app.krafted.zeustacticalswap.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.zeustacticalswap.game.BossId
import app.krafted.zeustacticalswap.ui.components.RulesDialog
import app.krafted.zeustacticalswap.ui.theme.ChipTone
import app.krafted.zeustacticalswap.ui.theme.GoldRule
import app.krafted.zeustacticalswap.ui.theme.Zeus
import app.krafted.zeustacticalswap.ui.theme.ZeusChip
import app.krafted.zeustacticalswap.ui.theme.zeusPanel

private enum class LockState { COMPLETE, OPEN, LOCKED }

private data class HomeBossLore(val ability: String, val hint: String)

private fun BossId.homeLore(): HomeBossLore = when (this) {
    BossId.KRONOS -> HomeBossLore("Rage", "Damage doubles below 50% HP.")
    BossId.TYPHON -> HomeBossLore("Cyclone", "Every 5 turns, wipes a row.")
    BossId.HADES -> HomeBossLore("Corruption", "Every 4 turns, places 3 skull tiles.")
}

private val RomanNumerals = listOf("I", "II", "III")

@Composable
fun HomeScreen(
    defeatedBosses: List<Int>,
    currentHp: Int = 120,
    maxHp: Int = 120,
    bestClearTime: String? = null,
    onSelectBoss: (Int) -> Unit,
    onLeaderboardClick: () -> Unit
) {
    val bosses = BossId.values()
    var showRules by remember { mutableStateOf(false) }

    if (showRules) {
        RulesDialog(onDismiss = { showRules = false })
    }

    fun lockState(index: Int): LockState = when {
        defeatedBosses.contains(index) -> LockState.COMPLETE
        index == 0 || defeatedBosses.contains(index - 1) -> LockState.OPEN
        else -> LockState.LOCKED
    }

    Box(Modifier
        .fillMaxSize()
        .background(Zeus.Night)) {
        Image(
            painter = painterResource(app.krafted.zeustacticalswap.R.drawable.zeus_back_2),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.55f),
            contentScale = ContentScale.Crop
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Zeus.Night.copy(alpha = 0.85f))
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 18.dp, end = 18.dp, top = 54.dp, bottom = 24.dp)
        ) {
            Header(
                onLeaderboardClick = onLeaderboardClick,
                onRulesClick = { showRules = true }
            )
            Spacer(Modifier.height(18.dp))
            RunSummary(
                clearedCount = defeatedBosses.size,
                currentHp = currentHp,
                maxHp = maxHp,
                bestClearTime = bestClearTime
            )
            Spacer(Modifier.height(16.dp))
            bosses.forEachIndexed { index, boss ->
                if (index > 0) Spacer(Modifier.height(14.dp))
                ArenaCard(
                    boss = boss,
                    romanNumeral = RomanNumerals[index],
                    state = lockState(index),
                    onClick = { onSelectBoss(index) }
                )
            }
            Spacer(Modifier.height(18.dp))
            Text(
                "HOME · LEADERBOARD · ABOUT",
                style = Zeus.monoLabel(9, Zeus.InkMute, tracking = 0.3),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLeaderboardClick() },
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun Header(
    onLeaderboardClick: () -> Unit,
    onRulesClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Text(
                "OLYMPIAN TRIALS",
                style = Zeus.monoLabel(9, Zeus.InkMute, tracking = 0.35)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Choose Your Trial",
                style = Zeus.goldHeading(28),
                lineHeight = 30.sp
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .border(1.dp, Zeus.Gold.copy(alpha = 0.45f), CircleShape)
                    .clickable { onRulesClick() },
                contentAlignment = Alignment.Center
            ) {
                Text("?", color = Zeus.InkDim, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .border(1.dp, Zeus.Gold.copy(alpha = 0.45f), CircleShape)
                    .clickable { onLeaderboardClick() },
                contentAlignment = Alignment.Center
            ) {
                Text("🏆", color = Zeus.InkDim, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun RunSummary(
    clearedCount: Int,
    currentHp: Int,
    maxHp: Int,
    bestClearTime: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .zeusPanel(corner = 12)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("CURRENT RUN", style = Zeus.monoLabel(9, Zeus.InkMute, tracking = 0.2))
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "$clearedCount/3 ",
                        color = Zeus.Ink,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text("trials cleared", color = Zeus.InkMute, fontSize = 14.sp)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("HERO HP", style = Zeus.monoLabel(9, Zeus.InkMute, tracking = 0.2))
                Spacer(Modifier.height(4.dp))
                Text(
                    "$currentHp/$maxHp",
                    color = Zeus.Gold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
        if (bestClearTime != null) {
            Spacer(Modifier.height(12.dp))
            GoldRule(Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("BEST CLEAR", style = Zeus.monoLabel(10, Zeus.InkMute, tracking = 0.18))
                Text(bestClearTime, style = Zeus.monoLabel(11, Zeus.Ink, tracking = 0.0))
            }
        }
    }
}

@Composable
private fun ArenaCard(
    boss: BossId,
    romanNumeral: String,
    state: LockState,
    onClick: () -> Unit
) {
    val lore = boss.homeLore()
    val locked = state == LockState.LOCKED

    var cardModifier = Modifier
        .fillMaxWidth()
        .heightIn(min = 124.dp)
        .clip(RoundedCornerShape(14.dp))
        .border(1.dp, Zeus.Gold.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
    if (!locked) cardModifier = cardModifier.clickable(onClick = onClick)

    Box(modifier = cardModifier.alpha(if (locked) 0.55f else 1f)) {
        Image(
            painter = painterResource(boss.backgroundRes),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .alpha(if (state == LockState.COMPLETE) 0.5f else 0.85f),
            contentScale = ContentScale.Crop
        )
        Box(
            Modifier
                .matchParentSize()
                .background(
                    Brush.horizontalGradient(
                        0f to Color(0xD905060F),
                        0.7f to Color(0x8C05060F),
                        1f to Color(0x4D05060F)
                    )
                )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Medallion(romanNumeral)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    boss.displayName,
                    style = Zeus.goldHeading(18),
                    lineHeight = 18.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "${boss.maxHp} HP · ${boss.minAttack}-${boss.maxAttack} DMG",
                    style = Zeus.monoLabel(9, Zeus.InkMute, tracking = 0.18)
                )
                Spacer(Modifier.height(6.dp))
                Row {
                    Text(
                        "${lore.ability}. ",
                        color = Zeus.Ink,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                    Text(lore.hint, color = Zeus.InkDim, fontSize = 11.sp, lineHeight = 15.sp)
                }
                Spacer(Modifier.height(8.dp))
                StatusChip(state)
            }
        }
    }
}

@Composable
private fun Medallion(numeral: String) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(Brush.verticalGradient(listOf(Zeus.GoldHi, Zeus.GoldDeep)))
            .border(2.dp, Zeus.GoldEdge, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            numeral,
            color = Zeus.ButtonInk,
            fontFamily = Zeus.Display,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp
        )
    }
}

@Composable
private fun StatusChip(state: LockState) {
    val (label, tone) = when (state) {
        LockState.COMPLETE -> "✓ DEFEATED" to ChipTone.COMPLETE
        LockState.OPEN -> "⚔ READY" to ChipTone.LIVE
        LockState.LOCKED -> "🔒 LOCKED" to ChipTone.NEUTRAL
    }
    ZeusChip(label = label, tone = tone)
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        defeatedBosses = listOf(0),
        currentHp = 95,
        maxHp = 120,
        bestClearTime = "04:12",
        onSelectBoss = {},
        onLeaderboardClick = {}
    )
}
