package com.example.pokeroffline.game

import kotlin.math.max

class BettingRound(val players: List<Player>, var firstPlayerIndex: Int, var startingBet: Int = 0) {
    private var foldedPlayers = mutableSetOf<Player>()
    private var allInPlayers = mutableSetOf<Player>()
    private var currentBet = startingBet
    private var potIncrement = 0
    private var currentPlayerIndex = firstPlayerIndex
    private var playerBets = mutableMapOf<Player, Int>()


    fun currentPlayer() : Player = players[currentPlayerIndex]

    fun performAction(player: Player, action: PlayerAction){
        when(action){
            is PlayerAction.Fold -> foldedPlayers.add(player)
            is PlayerAction.Check -> {

            }
            is PlayerAction.Call -> {
                val toCall = currentBet - (playerBets[player] ?: 0)
                playerBets[player] = currentBet
                potIncrement += toCall
                player.chips -= toCall
            }
            is PlayerAction.Raise -> {
                val total = currentBet + action.amount
                val toRaise = total - (playerBets[player] ?: 0)
                playerBets[player] = total
                currentBet = total
                player.chips -= toRaise
                potIncrement += toRaise
            }
            is PlayerAction.AllIn -> {
                val total = player.chips + (playerBets[player] ?: 0)
                playerBets[player] = total
                currentBet = max(currentBet, total)
                potIncrement += player.chips
                player.chips = 0
                allInPlayers.add(player)
            }
        }
        nextPlayer()
    }
    fun nextPlayer(){
        do{
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size
        }while (currentPlayer() in foldedPlayers || currentPlayer() in allInPlayers)
    }

    fun getPotIncrement(): Int = potIncrement

    fun isRoundOver(): Boolean{
        val activePlayers = players.filter{it !in foldedPlayers}
        return activePlayers.all{(playerBets[it] ?: 0) == currentBet || it in allInPlayers}
    }

    fun getRemainingPlayers(): List<Player> = players.filter{it !in foldedPlayers}
}