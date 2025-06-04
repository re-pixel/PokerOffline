package com.example.pokeroffline.game

class GameEngine(private val players: List<Player>, val bigBlind: Int, val smallBlind: Int) {
    private var isGameActive = false


    fun startRound() {
        val gameRound = GameRound(players, bigBlind, smallBlind)
        gameRound.play()
    }
}
