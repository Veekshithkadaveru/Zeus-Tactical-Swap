package app.krafted.zeustacticalswap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

private val Gold = Color(0xFFE7B549)
private val GoldBright = Color(0xFFFFE49A)
private val PanelBg = Color(0xFB0B0D18)
private val PanelBorder = Color(0x60E7B549)
private val InkDim = Color(0xFFB8AE97)

@Composable
fun RulesDialog(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(PanelBg)
                .border(2.dp, PanelBorder, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "RULES OF THE ARENA",
                    color = Gold,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, Gold.copy(alpha = 0.5f), Color.Transparent)
                            )
                        )
                )
                Spacer(Modifier.height(14.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    RuleSection(
                        title = "Core Mechanics",
                        desc = "Zeus Tactical Swap is a tactical match-3 RPG. Tap two adjacent tiles to swap them. Swaps are only allowed if they create a match of 3 or more identical symbols vertically or horizontally."
                    )
                    RuleSection(
                        title = "Combat & Turns",
                        desc = "Each match triggers actions. Swapping takes 1 turn. After you swap, matches cascade, and then the Boss attacks or charges up their special ability."
                    )
                    RuleSection(
                        title = "Match Actions",
                        desc = "• Lightning: Deals damage to the Boss. Matches of 3+ charge Zeus' Ultimate.\n" +
                               "• Owl: Generates Shield points to block incoming Boss damage.\n" +
                               "• Trident: Triggers physical strikes on the Boss.\n" +
                               "• Helmet: Builds up ultimate defense charges.\n" +
                               "• Laurel: Restores critical energy.\n" +
                               "• Amphora: Restores Hero HP.\n" +
                               "• Medusa: Deals shadow/piercing damage."
                    )
                    RuleSection(
                        title = "Boss Abilities",
                        desc = "• Kronos: Enrages below 50% HP, doubling his attack damage.\n" +
                               "• Typhon: Every 5 turns, wipes out a random row on the grid.\n" +
                               "• Hades: Every 4 turns, places 3 cursed skull tiles that block matching and cause damage."
                    )
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gold,
                        contentColor = Color(0xFF1A1303)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text(
                        text = "Understood",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun RuleSection(title: String, desc: String) {
    Column {
        Text(
            text = title,
            color = GoldBright,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            letterSpacing = 1.sp,
            fontFamily = FontFamily.Monospace
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = desc,
            color = InkDim,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
    }
}
