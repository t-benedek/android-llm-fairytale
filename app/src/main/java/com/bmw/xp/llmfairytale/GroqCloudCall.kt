package com.bmw.xp.llmfairytale

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

// Input can come from a speech to text engine
const val speechInput = "Erzähle ein Weihnachts-Märchen für ein 12 jähriges Kind. Das Märchen soll ca. 10 Sätze beinhalten. Erzeuge nach jedem Satz eine RGB Angabe der den Satz als Farbe darstellt in der Form r:g:b. Jeder Satz soll durch ein & getrennt sein. Nach jeder RGB Angabe soll ein + stehen."

class GroqCloudCall {
    /** HTTP call elements for GROQ Cloud. For more details see: https://console.groq.com/home **/
    private var client = OkHttpClient()
    private val URL = "https://api.groq.com/openai/v1/chat/completions"
    private val API_KEY = BuildConfig.API_KEY
    private val MEDIA_TYPE = "application/json".toMediaType()
    private val reqBody = """
    {
      "model": "meta-llama/llama-4-scout-17b-16e-instruct",
      "messages": [
        {
          "role": "user",
          "content": "${speechInput}" 
        }
      ]
    }
""".trimIndent()

    private val requestHTTPMessage = Request.Builder()
        .url(URL)
        .post(reqBody.toRequestBody(MEDIA_TYPE))
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer ${API_KEY}")
        .build()

    // get LLM output in the calling thread
    fun callServer() : String {
            client.newCall(requestHTTPMessage).execute().use { response ->
                val result = response.body!!.string()
                println(result)
                val message  = JSONObject(result)
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                return message
            }
    }
}