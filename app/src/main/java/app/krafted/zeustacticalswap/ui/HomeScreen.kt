package app.krafted.zeustacticalswap.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.zeustacticalswap.game.BossId

private val Gold = Color(0xFFE7B549)
private val GoldBright = Color(0xFFFFE49A)
private val GoldDeep = Color(0xFFA8761F)
private val GoldEdge = Color(0xFF6E4811)
private val Crimson = Color(0xFFD24747)
private val Emerald = Color(0xFF4FD49A)
private val Ink = Color(0xFFEDE7D6)
private val InkDim = Color(0xFFB8AE97)
private val InkMute = Color(0xFF7A745F)
private val Night = Color(0xFF04050F)
private val PanelBg = Color(0xCC0B0D18)
private val PanelBorder = Color(0x40E7B549)

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

    fun lockState(index: Int): LockState = when {
        defeatedBosses.contains(index) -> LockState.COMPLETE
        index == 0 || defeatedBosses.contains(index - 1) -> LockState.OPEN
        else -> LockState.LOCKED
    }

    Box(Modifier
        .fillMaxSize()
        .background(Night)) {
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
                        listOf(Color.Transparent, Night.copy(alpha = 0.85f))
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 18.dp, end = 18.dp, top = 54.dp, bottom = 24.dp)
        ) {
            Header(onLeaderboardClick)
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
                color = InkMute,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                letterSpacing = 3.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLeaderboardClick() },
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun Header(onLeaderboardClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Text(
                "OLYMPIAN TRIALS",
                color = InkMute,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                letterSpacing = 3.2.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Choose Your Trial",
                color = Gold,
                fontWeight = FontWeight.Black,
                fontSize = 28.sp,
                letterSpacing = 1.sp,
                lineHeight = 30.sp
            )
        }
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .border(1.dp, Gold.copy(alpha = 0.45f), CircleShape)
                .clickable { onLeaderboardClick() },
            contentAlignment = Alignment.Center
        ) {
            Text("⚙", color = InkDim, fontSize = 16.sp)
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
            .clip(RoundedCornerShape(12.dp))
            .background(PanelBg)
            .border(1.dp, PanelBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "CURRENT RUN",
                    color = InkMute,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    letterSpacing = 1.8.sp
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "$clearedCount/3 ",
                        color = Ink,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        "trials cleared",
                        color = InkMute,
                        fontSize = 14.sp
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "HERO HP",
                    color = InkMute,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    letterSpacing = 1.8.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "$currentHp/$maxHp",
                    color = Gold,
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
                Text(
                    "BEST CLEAR",
                    color = InkMute,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    letterSpacing = 1.6.sp
                )
                Text(
                    bestClearTime,
                    color = Ink,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
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
    if (!locked) cardModifier = cardModifier.clickable(onClick = onClick)

    Box(modifier = cardModifier.alpha(if (locked) 0.45f else 1f)) {
        Image(
            painter = painterResource(boss.backgroundRes),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .alpha(if (state == LockState.COMPLETE) 0.45f else 0.85f),
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
                    color = Gold,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    lineHeight = 18.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "${boss.maxHp} HP · ${boss.minAttack}-${boss.maxAttack} DMG",
                    color = InkMute,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    letterSpacing = 1.8.sp
                )
                Spacer(Modifier.height(6.dp))
                Row {
                    Text(
                        "${lore.ability}. ",
                        color = Ink,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                    Text(
                        lore.hint,
                        color = InkDim,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
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
            .background(Brush.verticalGradient(listOf(GoldBright, GoldDeep)))
            .border(2.dp, GoldEdge, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            numeral,
            color = Color(0xFF1A1303),
            fontWeight = FontWeight.Black,
            fontSize = 18.sp
        )
    }
}

@Composable
private fun StatusChip(state: LockState) {
    val (label, color) = when (state) {
        LockState.COMPLETE -> "✓ DEFEATED" to Emerald
        LockState.OPEN -> "⚔ READY" to Gold
        LockState.LOCKED -> "🔒 LOCKED" to InkMute
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .border(BorderStroke(1.dp, color.copy(alpha = 0.5f)), RoundedCornerShape(100.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            label,
            color = color,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Bold
        )
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
