package com.example.pokeroffline.ui

import androidx.compose.runtime.Composable

@Composable
fun GameScreen(viewModel: GameViewModel) {
    PlayerView(
        onCheck = {viewModel.onCheck() },
        onRaise = {viewModel.onRaise()},
        onFold = {viewModel.onFold() },
        playerArrangement = { viewModel.arrangePlayers() }
    )
}