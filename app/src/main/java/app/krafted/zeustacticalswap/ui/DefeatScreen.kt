package app.krafted.zeustacticalswap.ui

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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

private val Crimson = Color(0xFFD24747)
private val CrimsonBright = Color(0xFFFF6E6E)
private val InkMute = Color(0xFF7A745F)
private val InkDim = Color(0xFFB8AE97)

@Composable
fun DefeatScreen(boss: BossId, onRetry: () -> Unit, onHome: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF04050F)),
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
            Text(
                "DEFEAT",
                color = Crimson,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 5.5.sp
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "FALLEN",
                fontWeight = FontWeight.Black,
                fontSize = 40.sp,
                letterSpacing = 1.5.sp,
                style = androidx.compose.ui.text.TextStyle(
                    brush = Brush.verticalGradient(listOf(CrimsonBright, Color(0xFFAD1A1A)))
                )
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "${boss.displayName.uppercase()} HAS BROKEN YOU.\nTHE OLYMPIANS WATCH IN SILENCE.",
                color = InkMute,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                letterSpacing = 1.8.sp,
                lineHeight = 17.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 280.dp)
            )

            Spacer(Modifier.height(32.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onHome,
                    modifier = Modifier.height(52.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Crimson.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = InkDim),
                    contentPadding = PaddingValues(horizontal = 28.dp)
                ) {
                    Text("Home", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Button(
                    onClick = onRetry,
                    modifier = Modifier.height(52.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Crimson,
                        contentColor = Color(0xFFFFF1F1)
                    ),
                    contentPadding = PaddingValues(horizontal = 28.dp)
                ) {
                    Text("Try Again", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}

@Preview
@Composable
private fun DefeatScreenPreview() {
    DefeatScreen(boss = BossId.HADES, onRetry = {}, onHome = {})
}
