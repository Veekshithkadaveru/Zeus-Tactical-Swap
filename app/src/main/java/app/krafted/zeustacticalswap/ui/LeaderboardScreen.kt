package app.krafted.zeustacticalswap.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.zeustacticalswap.R
import app.krafted.zeustacticalswap.game.BossId

private val Night = Color(0xFF04050F)
private val PanelBg = Color(0xCC0B0D18)
private val PanelBorder = Color(0x40E7B549)
private val Gold = Color(0xFFE7B549)
private val GoldBright = Color(0xFFFFE49A)
private val Ink = Color(0xFFEDE7D6)
private val InkDim = Color(0xFFB8AE97)
private val InkMute = Color(0xFF7A745F)

@Composable
fun LeaderboardScreen(
    bestTimes: Map<BossId, String>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Night),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.zeus_back_4),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.25f),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.5f), Night.copy(alpha = 0.95f))
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 22.dp, end = 22.dp, top = 54.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.Start)
                    .height(32.dp),
                shape = RoundedCornerShape(100.dp),
                border = BorderStroke(1.dp, Gold.copy(alpha = 0.35f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = InkDim),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "← BACK",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    letterSpacing = 1.8.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = "SACRED SCROLLS",
                color = InkMute,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 4.sp,
                fontFamily = FontFamily.Monospace
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Best Clear Times",
                color = Gold,
                fontWeight = FontWeight.Black,
                fontSize = 28.sp,
                letterSpacing = 1.sp
            )

            Spacer(Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BossId.values().forEachIndexed { index, bossId ->
                    val clearTime = bestTimes[bossId] ?: "--:--"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PanelBg, RoundedCornerShape(12.dp))
                            .border(1.dp, PanelBorder, RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TRIAL ${index + 1}",
                                color = Gold.copy(alpha = 0.7f),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = bossId.displayName,
                                color = Ink,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = clearTime,
                            color = GoldBright,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Text(
                text = "SWAP TACTICALLY · SHATTER THE RECORD",
                color = InkMute,
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
    }
}
