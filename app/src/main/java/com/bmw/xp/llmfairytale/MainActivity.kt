package com.bmw.xp.llmfairytale

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class TaleInputData(val btnText: String, val speechInput: String)

// state object to show results from LLM engine
val llmOutputModel = mutableStateOf("-- RESULT --")
var counter = 0;
private val scope = MainScope()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaleUI(TaleInputData (
                btnText = getString(R.string.tale1_button),
                speechInput = speechInput
            ))
        }
        scope.launch {
            FairyTaleParser.getGroqAndParse()
        }
    }
}

@Composable
fun TaleUI(msg: TaleInputData) {
    var boxColor = remember { mutableStateOf(Color.White) }

    Column {
        Column {
            OutlinedButton (
                onClick = {
                    if (FairyTaleParser.phraseList.size > counter) {
                        val next = FairyTaleParser.phraseList.get(counter)
                        counter += 1
                        println(next.text + " " + counter)
                        llmOutputModel.value = next.text
                        boxColor.value = Color(next.rgb.r, next.rgb.g, next.rgb.b, 255)
                    }
                }

            ) { Text(msg.btnText) }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                msg.speechInput,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(thickness = 2.dp)
        }
        Spacer(modifier = Modifier.height(40.dp))

        Row (
            modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
            Text (
                text = llmOutputModel.value,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(0.5f)
                    .heightIn(min = 100.dp), // Allow height to grow
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RectangleShape)
                    .background(boxColor.value)
            )
        }

    }
}