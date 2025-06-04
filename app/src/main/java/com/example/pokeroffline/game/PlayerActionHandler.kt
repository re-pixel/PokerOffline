package com.example.pokeroffline.game

object PlayerActionHandler {
    fun handlePlayerAction(player: Player, action: PlayerAction, bettingRound: BettingRound) {
        bettingRound.performAction(player, action)
    }

    fun onCheck(player: Player, bettingRound: BettingRound) {
        handlePlayerAction(player, PlayerAction.Check, bettingRound)
    }

    fun onRaise(player: Player, amount: Int, minRaise: Int, bettingRound: BettingRound) {
        handlePlayerAction(player, PlayerAction.Raise(amount, minRaise), bettingRound)
    }

    fun onFold(player: Player, bettingRound: BettingRound) {
        handlePlayerAction(player, PlayerAction.Fold, bettingRound)
    }

}
