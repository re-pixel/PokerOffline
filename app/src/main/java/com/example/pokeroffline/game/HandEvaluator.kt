package com.example.pokeroffline.game

import kotlin.math.max

enum class HandRank(val value: Int){
    HIGH_CARD(1), PAIR(2), TWO_PAIR(3), THREE_OF_A_KIND(4), STRAIGHT(5), FLUSH(6),
    FULL_HOUSE(7), FOUR_OF_A_KIND(8), STRAIGHT_FLUSH(9), ROYAL_FLUSH(10)
}

object HandEvaluator {

    fun evaluateHand(hand: List<Card>, communityCards: List<Card>): Int{
        return hand[0].rank.value + hand[1].rank.value
    }

    fun determineWinner(players: List<Player>, communityCards: List<Card>): Player{
        return players.maxByOrNull { evaluateHand(it.hand, communityCards) }!!
    }
}