package com.example.pokeroffline.ui

import android.graphics.Rect
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.pokeroffline.R
import kotlin.math.roundToInt

@Preview
@Composable
fun PokerTableScreen() {
    val tableBounds = remember{mutableStateOf<Rect?>(null)}

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0, 0, 0, 50))
    ) {

        Table(
            modifier = Modifier
                .align(Alignment.Center)
                .onGloballyPositioned { coords ->
                    val pos = coords.positionInRoot()
                    val size = coords.size
                    tableBounds.value = Rect(
                        pos.x.toInt(),
                        pos.y.toInt(),
                        (pos.x + size.width).toInt(),
                        (pos.y + size.height).toInt()
                    )
                }
        )
        tableBounds.value?.let { bounds ->
            val density = LocalDensity.current
            val centerX = (bounds.left + bounds.right) / 2
            val centerY = (bounds.top + bounds.bottom) / 2
            val width = bounds.width()
            val height = bounds.height()

            // Top row
            PlayerSeat(x = centerX - width * 0.25f, y = bounds.top - 60f)
            PlayerSeat(x = centerX + width * 0.25f, y = bounds.top - 60f)

            // Bottom row
            PlayerSeat(x = centerX - width * 0.25f, y = bounds.bottom + 20f)
            PlayerSeat(x = centerX + width * 0.25f, y = bounds.bottom + 20f)

            // Left side
            PlayerSeat(x = bounds.left - 80f, y = centerY - height * 0.25f)

            // Right side
            PlayerSeat(x = bounds.right + 20f, y = centerY - height * 0.25f)
        }
    }
}


@Composable
fun Table(modifier: Modifier = Modifier){
    Box(
        modifier = modifier
            .fillMaxHeight(0.5f)
            .fillMaxWidth(0.6f)
            .background(Color(0, 255, 0, 50), shape = RoundedCornerShape(10.dp))
    ){
//        Image(
//            painter = painterResource(id = R.drawable.table),
//            contentDescription = null,
//            contentScale = ContentScale.Crop,
//            modifier = Modifier.matchParentSize()
//        )

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
fun PlayerSeat(x: Float, y: Float) {
    Box(
        modifier = Modifier
            .offset { IntOffset(x.roundToInt(), y.roundToInt()) }
            .size(60.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.card_backside),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.matchParentSize()
        )
    }
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
