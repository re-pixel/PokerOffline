package com.example.pokeroffline.game

sealed class PlayerAction {
    data class Raise(val amount: Int, val minRaise: Int): PlayerAction()
    data object Call : PlayerAction()
    data object Fold : PlayerAction()
    data object Check : PlayerAction()
}