package com.example.pokeroffline.game

class GameRound(
    private val players: List<Player>,
    private val bigBlind: Int,
    private val smallBlind: Int
) {
    private val deck = Deck()
    private val communityCards = mutableListOf<Card>()
    private var pot: Int = bigBlind + smallBlind
    private var gameStage = RoundStage.PREFLOP
    private var currentPlayerIndex = 0
    private var remainingPlayers = players

    enum class RoundStage {
        PREFLOP, FLOP, TURN, RIVER, SHOWDOWN
    }

    fun play() {
        startHand()
        while (!isRoundOver()) {
            when (gameStage) {
                RoundStage.PREFLOP -> {
                    doTheBetting()
                    gameStage = RoundStage.FLOP
                }
                RoundStage.FLOP -> {
                    repeat(3) { communityCards.add(deck.drawCard()!!) }
                    doTheBetting()
                    gameStage = RoundStage.TURN
                }
                RoundStage.TURN -> {
                    communityCards.add(deck.drawCard()!!)
                    doTheBetting()
                    gameStage = RoundStage.RIVER
                }
                RoundStage.RIVER -> {
                    communityCards.add(deck.drawCard()!!)
                    doTheBetting()
                    gameStage = RoundStage.SHOWDOWN
                }
                RoundStage.SHOWDOWN -> {
                    showHands()
                    val winner = HandEvaluator.determineWinner(remainingPlayers, communityCards)
                    winner.chips += pot
                    announceWinner(winner)
                    break
                }
            }
        }
    }

    private fun startHand() {
        deck.reset()
        communityCards.clear()
        pot = bigBlind + smallBlind
        gameStage = RoundStage.PREFLOP
        currentPlayerIndex = 0
        dealHands()
    }

    private fun dealHands(cardsPerPlayer: Int = 2) {
        for (i in 0 until cardsPerPlayer) {
            for (player in players) {
                player.hand.add(deck.drawCard()!!)
            }
        }
    }

    private fun doTheBetting() {
        var bettingRound = BettingRound(remainingPlayers, currentPlayerIndex)
        while (!bettingRound.isRoundOver()) {
            val player = bettingRound.currentPlayer()
            val action = PlayerAction.Check // getPlayerActionFromUI(player)
            PlayerActionHandler.handlePlayerAction(player, action, bettingRound)
            remainingPlayers = bettingRound.getRemainingPlayers()
            pot += bettingRound.getPotIncrement()
        }
    }

    private fun showHands() {
        for (player in players) {
            println("${player.name}: ${player.hand}")
        }
    }

    private fun announceWinner(player: Player): String = "The winner is:" + player.name

    private fun isRoundOver(): Boolean = remainingPlayers.size == 1
}
