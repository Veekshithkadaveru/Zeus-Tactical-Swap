package app.krafted.zeustacticalswap.game

import androidx.compose.ui.graphics.Color
import app.krafted.zeustacticalswap.R

enum class Symbol(val drawableRes: Int, val label: String, val glowColor: Color) {
    LIGHTNING(R.drawable.zeus_sym_1, "ATTACK", Color(0xFF5DC9FF)),
    OWL(R.drawable.zeus_sym_2, "HEAL", Color(0xFF4FD49A)),
    TRIDENT(R.drawable.zeus_sym_3, "SHIELD", Color(0xFF3AA1FF)),
    HELMET(R.drawable.zeus_sym_4, "CHARGE", Color(0xFFD23C3C)),
    LAUREL(R.drawable.zeus_sym_5, "CRITICAL", Color(0xFFFFD770)),
    AMPHORA(R.drawable.zeus_sym_6, "POISON", Color(0xFF9BE23A)),
    MEDUSA(R.drawable.zeus_sym_7, "PETRIFY", Color(0xFFB6B6B6)),
    SKULL(R.drawable.zeus_sym_skull, "CURSED", Color(0xFF7A745F))
}
