package com.example.pokeroffline.game

object HandEvaluator {

    fun evaluateHand(hand: List<Card>, communityCards: List<Card>): Int{
        return hand[0].rank.value + hand[1].rank.value
    }

    fun determineWinner(players: List<Player>, communityCards: List<Card>): Player{
        return players.maxByOrNull { evaluateHand(it.hand, communityCards) }!!
    }
}