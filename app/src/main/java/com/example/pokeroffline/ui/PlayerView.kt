package com.example.pokeroffline.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun PlayerView(
    onCheck: () -> Unit = {},
    onRaise: () -> Unit = {},
    onFold: () -> Unit = {},
    playerArrangement: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        PokerTableScreen()

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onCheck,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0.2f, 0.8f, 0.2f))
            ) {
                Text("Check")
            }
            Button(onClick = onRaise) {
                Text("Raise")
            }
            Button(
                onClick = onFold,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0.8f, 0.2f, 0.2f))
            ) {
                Text("Fold")
            }
        }
    }
}
