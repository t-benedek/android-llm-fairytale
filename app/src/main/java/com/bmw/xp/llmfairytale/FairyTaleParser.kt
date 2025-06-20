package com.bmw.xp.llmfairytale

import com.bmw.xp.llmfairytale.data.Phrases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// create Parser as a singleton object
object FairyTaleParser {
    val phraseList = arrayListOf<Phrases.Phrase>()

    fun parse(llmOutput: String) {
        val multiCharDelimiter = llmOutput.split("+")
        for (phrase in multiCharDelimiter) {
            val item = phrase.split("&")
            val text = item.get(0)
            val rgbText = item.get(1)
            val rgb = parseRGB(rgbText)
            val p = Phrases.Phrase(text, rgb)
            phraseList.add(p)
        }
        phraseList.toString()
    }

    suspend fun getGroqAndParse(): String = withContext(Dispatchers.IO) {
        try {
            val out = GroqCloudCall().callServer()
            parse(out)
            return@withContext out
        } catch (e: Exception) {
            return@withContext "Fehler: ${e.message}"
        }
    }

    private fun parseRGB(rgbInput: String) : Phrases.RGB {
        // example value: 255:255:200
        val rgbVals = rgbInput.trim().split(":")
        val r: Int = rgbVals[0].toInt()
        val g: Int = rgbVals[1].toInt()
        val b:Int  = rgbVals[2].toInt()
        return Phrases.RGB(r,g,b)
    }
}