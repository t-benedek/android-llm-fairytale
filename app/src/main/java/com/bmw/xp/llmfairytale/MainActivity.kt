package com.bmw.xp.llmfairytale

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*

data class TaleInputData(val btnText: String, val speechInput: String)

// state object to show results from LLM engine
val llmOutputModel = mutableStateOf("-- RESULT --")
private val groqCloud = GroqCloudCall()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaleUI(TaleInputData (
                btnText = getString(R.string.tale1_button),
                speechInput = speechInput
            ))
        }
    }
}

@Composable
fun TaleUI(msg: TaleInputData) {
    Column {
        Column {
            OutlinedButton (
                onClick = { onClick() }

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
        Column {
            Text (
                text = llmOutputModel.value,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun onClick() {
    groqCloud.callServer(llmOutputModel)
}