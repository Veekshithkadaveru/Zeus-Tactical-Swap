package app.krafted.zeustacticalswap.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.zeustacticalswap.R

private val Night = Color(0xFF04050F)
private val Gold = Color(0xFFE7B549)
private val GoldBright = Color(0xFFFFE49A)
private val Ink = Color(0xFFEDE7D6)
private val InkMute = Color(0xFF7A745F)

@Composable
fun ArenaCompleteScreen(
    onHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Night),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.zeus_back_5),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.5f),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0x33E7B549), Night)
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
                text = "⚡ ARENA CONQUERED ⚡",
                color = Gold,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 5.sp
            )
            Spacer(Modifier.height(14.dp))
            Text(
                text = "OLYMPUS IS YOURS",
                color = GoldBright,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color.Transparent, Gold.copy(alpha = 0.6f), Color.Transparent)
                        )
                    )
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = "YOU HAVE PROVED YOUR WILL TO THE GODS.\nKRONOS, TYPHON, AND HADES HAVE BEEN SUBDUED.\nYOUR NAME IS ETCHED IN SACRED STONE.",
                color = Ink,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Spacer(Modifier.height(48.dp))
            Button(
                onClick = onHome,
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
                    text = "Return to Temple",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
