package com.example.pokeroffline.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun PokerTableScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0,0,0,50)) // Dark green table
    ) {

        Table(modifier = Modifier.align(Alignment.Center))

        // Top players
        PlayerCircle(modifier = Modifier.align(Alignment.TopStart).padding(16.dp))
        PlayerCircle(modifier = Modifier.align(Alignment.TopEnd).padding(16.dp))

        // Side players
        PlayerCircle(modifier = Modifier.align(Alignment.CenterStart))
        PlayerCircle(modifier = Modifier.align(Alignment.CenterEnd))

        // Bottom players
        PlayerCircle(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp))
        PlayerCircle(modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp))
    }
}

@Composable
fun Table(modifier: Modifier = Modifier){
    Box(
        modifier = modifier
            .fillMaxHeight(0.5f)
            .fillMaxWidth(0.6f)
            .background(Color(0,255,0,50), shape=RoundedCornerShape(10.dp))
    ){

        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(5) {
                CardPlaceholder()
            }
        }
    }
}

@Composable
fun PlayerCircle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(60.dp)
            .background(Color.White, shape = CircleShape)
            .border(2.dp, Color.Black, CircleShape)
    )
}

@Composable
fun CardPlaceholder() {
    Box(
        modifier = Modifier
            .size(width = 40.dp, height = 60.dp)
            .background(Color.White, shape = RoundedCornerShape(6.dp))
            .border(2.dp, Color.Black, RoundedCornerShape(6.dp))
    )
}
