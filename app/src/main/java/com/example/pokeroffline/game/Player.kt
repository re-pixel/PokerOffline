package com.example.pokeroffline.game

data class Player(
    val name: String,
    val hand: MutableList<Card> = mutableListOf(),
    var chips: Int = 1000
)