package com.example.pokeroffline.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.pokeroffline.game.*

class GameViewModel : ViewModel() {

    private val players = listOf(Player("Alice", mutableListOf(), 1000), Player("Bob", mutableListOf(), 1000))
    private var bettingRound = BettingRound(players, firstPlayerIndex = 0)


    var currentPlayer by mutableStateOf(bettingRound.currentPlayer())
        private set
    var pot by mutableStateOf(0)
        private set

    fun onCheck() {
        PlayerActionHandler.onCheck(currentPlayer, bettingRound)
        updateState()
    }

    fun onRaise(amount: Int = 100) {
        PlayerActionHandler.onRaise(currentPlayer, amount, minRaise = 100, bettingRound)
        updateState()
    }

    fun onFold() {
        PlayerActionHandler.onFold(currentPlayer, bettingRound)
        updateState()
    }

    fun arrangePlayers() {
        // This function can be used to rearrange players if needed
        // For now, it does nothing but can be extended later
    }

    private fun updateState() {
        currentPlayer = bettingRound.currentPlayer()
        pot += bettingRound.getPotIncrement()
    }
}