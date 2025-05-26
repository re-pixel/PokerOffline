package com.example.pokeroffline.game

data class Player(
    val name: String,
    val hand: MutableList<Card> = mutableListOf(),
    val chips: Int = 1000
)