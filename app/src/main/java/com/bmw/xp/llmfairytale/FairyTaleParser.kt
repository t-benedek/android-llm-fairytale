package com.bmw.xp.llmfairytale

// create Parser as a singleton object
object FairyTaleParser {
    data class RGB(val r: Byte, val g: Byte, val b: Byte)
    data class Phrase(val text: String, val rgb: RGB)
    private val phraseList = arrayListOf<Phrase>()

    fun parse(llmOutput: String) {
        val multiCharDelimiter = llmOutput.split("\n")
        for (phrase in multiCharDelimiter) {
            val item = llmOutput.split("&")
            val text = item.get(0)
            val rgbText = item.get(1)
            val rgb = parseRGB(rgbText)
            val p = Phrase(text, rgb)
            phraseList.add(p)
        }
    }

    fun getPhrase(i: Int) : String {
        return phraseList.get(i).text
    }

    fun getRGB(i: Int) : FairyTaleParser.RGB {
        return phraseList.get(i).rgb
    }

    private fun parseRGB(rgbInput: String) : RGB {
        // example value: 255:255:200
        val rgbVals = rgbInput.split(":")
        val r: Byte = rgbVals.get(0).trim().toInt().toByte()
        val g: Byte = rgbVals.get(1).trim().toInt().toByte()
        val b: Byte = rgbVals.get(2).trim().toInt().toByte()
        return RGB(r,g,b)
    }
}