package com.bmw.xp.llmfairytale.data

class Phrases {
    data class RGB(val r: Int, val g: Int, val b: Int)
    data class Phrase(val text: String, val rgb: RGB)

}