package com.bmw.xp.llmfairytale

import com.bmw.xp.llmfairytale.data.Phrases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// create Parser as a singleton object
object FairyTaleParser {
    private val _phraseList = mutableListOf<Phrases.Phrase>()
    val phraseList: List<Phrases.Phrase> get() = _phraseList.toList()

    fun parse(llmOutput: String): Result<Unit> {
        return try {
            _phraseList.clear()
            val multiCharDelimiter = llmOutput.split("+")

            for (phrase in multiCharDelimiter) {
                if (phrase.isBlank()) continue

                val item = phrase.split("&")
                if (item.size < 2) {
                    return Result.failure(IllegalArgumentException("Invalid phrase format: $phrase"))
                }

                val text = item[0].trim()
                val rgbText = item[1].trim()

                parseRGB(rgbText).fold(
                    onSuccess = { rgb ->
                        val p = Phrases.Phrase(text, rgb)
                        _phraseList.add(p)
                    },
                    onFailure = { return Result.failure(it) }
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGroqAndParse(): Result<String> = withContext(Dispatchers.IO) {
        try {
            println("Calling Groq API...")
            val out = GroqCloudCall().callServer()
            println("Groq response: $out")

            parse(out).fold(
                onSuccess = {
                    println("Parsing successful. Found ${_phraseList.size} phrases")
                    Result.success(out)
                },
                onFailure = {
                    println("Parsing failed: ${it.message}")
                    Result.failure(it)
                }
            )
        } catch (e: Exception) {
            println("Error in getGroqAndParse: ${e.message}")
            Result.failure(e)
        }
    }

    private fun parseRGB(rgbInput: String): Result<Phrases.RGB> {
        return try {
            // example value: 255:255:200
            val rgbVals = rgbInput.trim().split(":")
            if (rgbVals.size != 3) {
                return Result.failure(IllegalArgumentException("RGB format must be r:g:b, got: $rgbInput"))
            }

            val r = rgbVals[0].toIntOrNull() ?: return Result.failure(NumberFormatException("Invalid red value: ${rgbVals[0]}"))
            val g = rgbVals[1].toIntOrNull() ?: return Result.failure(NumberFormatException("Invalid green value: ${rgbVals[1]}"))
            val b = rgbVals[2].toIntOrNull() ?: return Result.failure(NumberFormatException("Invalid blue value: ${rgbVals[2]}"))

            if (r !in 0..255 || g !in 0..255 || b !in 0..255) {
                return Result.failure(IllegalArgumentException("RGB values must be between 0-255, got: $r:$g:$b"))
            }

            Result.success(Phrases.RGB(r, g, b))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}