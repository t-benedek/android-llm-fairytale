package com.bmw.xp.llmfairytale

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import kotlinx.coroutines.launch

data class TaleInputData(val btnText: String, val speechInput: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TaleUI(
                inputData = TaleInputData(
                    btnText = getString(R.string.tale1_button),
                    speechInput = speechInput
                )
            )
        }
    }
}

@Composable
fun TaleUI(inputData: TaleInputData) {
    var currentText by remember { mutableStateOf("-- RESULT --") }
    var currentColor by remember { mutableStateOf(Color.White) }
    var counter by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }
    var dataLoaded by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Load tale when UI is first composed
    LaunchedEffect(Unit) {
        isLoading = true
        currentText = "Märchen wird geladen..."

        FairyTaleParser.getGroqAndParse().fold(
            onSuccess = {
                println("Tale loaded successfully")
                dataLoaded = true
                currentText = "Klicke den Button um das Märchen zu starten!"
            },
            onFailure = { exception ->
                println("Error loading tale: ${exception.message}")
                currentText = "Fehler beim Laden: ${exception.message}"
            }
        )
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(currentColor)
            .padding(16.dp)
    ) {
        Column {
            OutlinedButton(
                onClick = {
                    if (!dataLoaded) {
                        currentText = "Märchen wird noch geladen, bitte warten..."
                        return@OutlinedButton
                    }

                    val phrases = FairyTaleParser.phraseList
                    println("Button geklickt! Phrases: ${phrases.size}, Counter: $counter")

                    if (phrases.isEmpty()) {
                        currentText = "Keine Märchen-Daten verfügbar. Versuche neu zu laden..."
                        // Reload data
                        scope.launch {
                            isLoading = true
                            currentText = "Märchen wird neu geladen..."
                            FairyTaleParser.getGroqAndParse().fold(
                                onSuccess = {
                                    dataLoaded = true
                                    currentText = "Märchen geladen! Klicke erneut."
                                },
                                onFailure = { exception ->
                                    currentText = "Fehler: ${exception.message}"
                                }
                            )
                            isLoading = false
                        }
                        return@OutlinedButton
                    }

                    if (counter < phrases.size) {
                        val nextPhrase = phrases[counter]
                        counter += 1
                        currentText = nextPhrase.text
                        currentColor = Color(
                            red = nextPhrase.rgb.r / 255f,
                            green = nextPhrase.rgb.g / 255f,
                            blue = nextPhrase.rgb.b / 255f,
                            alpha = 1f
                        )
                        println("Showing phrase $counter: ${nextPhrase.text}")
                    } else {
                        // Reset to beginning
                        counter = 0
                        currentText = "Märchen beendet! Klicke für Neustart."
                        currentColor = Color.White
                    }
                },
                enabled = !isLoading
            ) {
                Text(
                    when {
                        isLoading -> "Lädt..."
                        !dataLoaded -> "Warten..."
                        else -> inputData.btnText
                    }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                inputData.speechInput,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(thickness = 2.dp)
        }

        Spacer(modifier = Modifier.height(40.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = currentText,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 100.dp),
                color = Color.Black // Schwarzer Text für bessere Lesbarkeit
            )
        }

        // Debug info (remove in production)
        if (dataLoaded) {
            Text(
                text = "Debug: ${FairyTaleParser.phraseList.size} Sätze geladen",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}