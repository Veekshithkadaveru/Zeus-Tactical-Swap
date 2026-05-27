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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.zeustacticalswap.game.BossId
import app.krafted.zeustacticalswap.ui.theme.GhostButton
import app.krafted.zeustacticalswap.ui.theme.Zeus

@Composable
fun DefeatScreen(boss: BossId, onRetry: () -> Unit, onHome: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Zeus.Night),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(boss.backgroundRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.35f),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0x99500000), Color(0xF7020000)),
                        center = Offset.Unspecified
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("DEFEAT", style = Zeus.monoLabel(11, Zeus.Crimson, tracking = 0.5))
            Spacer(Modifier.height(10.dp))
            Text(
                "FALLEN",
                style = TextStyle(
                    brush = Brush.verticalGradient(listOf(Zeus.CrimsonHi, Zeus.CrimsonDeep)),
                    fontFamily = Zeus.Display,
                    fontWeight = FontWeight.Black,
                    fontSize = 40.sp,
                    letterSpacing = 1.6.sp,
                    shadow = Shadow(color = Zeus.Crimson.copy(alpha = 0.4f), blurRadius = 30f)
                )
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "${boss.displayName.uppercase()} HAS BROKEN YOU.\nTHE OLYMPIANS WATCH IN SILENCE.",
                style = Zeus.monoLabel(11, Zeus.InkMute, tracking = 0.18),
                lineHeight = 17.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 280.dp)
            )

            Spacer(Modifier.height(32.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GhostButton(text = "Home", onClick = onHome)
                CrimsonButton(text = "Try Again", onClick = onRetry)
            }
        }
    }
}

@Composable
private fun CrimsonButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(
                Brush.verticalGradient(listOf(Zeus.CrimsonHi, Zeus.Crimson, Zeus.CrimsonDeep)),
                RoundedCornerShape(4.dp)
            )
            .border(1.dp, Zeus.CrimsonDeep, RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text.uppercase(),
            color = Color(0xFFFFF1F1),
            fontFamily = Zeus.Display,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            letterSpacing = 2.5.sp
        )
    }
}

@Preview
@Composable
private fun DefeatScreenPreview() {
    DefeatScreen(boss = BossId.HADES, onRetry = {}, onHome = {})
}
