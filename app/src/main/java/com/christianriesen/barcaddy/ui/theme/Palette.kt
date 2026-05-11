package com.christianriesen.barcaddy.ui.theme

import androidx.compose.ui.graphics.Color

data class CardPalette(
    val name: String,
    val bg: Color,
    val deep: Color,
    val tint: Color,
)

object Palettes {
    val all = listOf(
        CardPalette("Coral",    Color(0xFFFFE3DC), Color(0xFF7A2418), Color(0xFFFF5C3C)),
        CardPalette("Forest",   Color(0xFFD8E8DA), Color(0xFF1F3A24), Color(0xFF3F8552)),
        CardPalette("Sunshine", Color(0xFFFBE6A2), Color(0xFF6B4A0B), Color(0xFFE0A917)),
        CardPalette("Lagoon",   Color(0xFFCFE3EC), Color(0xFF1A3A48), Color(0xFF2A7A95)),
        CardPalette("Plum",     Color(0xFFE6D8E8), Color(0xFF3F1F4A), Color(0xFF7B4A8C)),
        CardPalette("Ink",      Color(0xFFD8DDE6), Color(0xFF101626), Color(0xFF2C3654)),
        CardPalette("Clay",     Color(0xFFEAD9CC), Color(0xFF4A2A18), Color(0xFF9F5A3A)),
        CardPalette("Mint",     Color(0xFFCFEAE2), Color(0xFF0F3C30), Color(0xFF2D8C70)),
        CardPalette("Rose",     Color(0xFFF4D7DD), Color(0xFF5B1A2A), Color(0xFFC24A66)),
        CardPalette("Slate",    Color(0xFFDCDDE0), Color(0xFF26282E), Color(0xFF5A5E68)),
    )
    private val index = all.associateBy { it.name }
    fun byName(name: String): CardPalette = index[name] ?: all[0]
    fun random(): CardPalette = all.random()
}
