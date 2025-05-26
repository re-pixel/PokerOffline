package com.example.pokeroffline.game

class Deck {
    private val cards: MutableList<Card> = mutableListOf()

    init {
        reset()
    }

    fun reset() {
        cards.clear()
        for (suit in Suit.entries) {
            for (rank in Rank.entries) {
                cards.add(Card(rank, suit))
            }
        }
        shuffle()
    }

    fun shuffle() {
        cards.shuffle()
    }

    fun drawCard(): Card? = if (cards.isNotEmpty()) cards.removeAt(0) else null

    fun remainingCards(): Int = cards.size
}