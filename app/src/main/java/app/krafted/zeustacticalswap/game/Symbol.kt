package app.krafted.zeustacticalswap.game

import app.krafted.zeustacticalswap.R

enum class Symbol(val drawableRes: Int, val label: String) {
    LIGHTNING(R.drawable.zeus_sym_1, "ATTACK"),
    OWL(R.drawable.zeus_sym_2, "HEAL"),
    TRIDENT(R.drawable.zeus_sym_3, "SHIELD"),
    HELMET(R.drawable.zeus_sym_4, "CHARGE"),
    LAUREL(R.drawable.zeus_sym_5, "CRITICAL"),
    AMPHORA(R.drawable.zeus_sym_6, "POISON"),
    MEDUSA(R.drawable.zeus_sym_7, "PETRIFY"),
    SKULL(R.drawable.zeus_sym_6, "CURSED")
}
