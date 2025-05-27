package com.example.pokeroffline.game

class GameEngine(private val players: List<Player>, val bigBlind: Int, val smallBlind: Int) {
    private val deck = Deck()
    private val communityCards = mutableListOf<Card>()
    private var pot: Int = 0
    private var gameStage = GameStage.PREFLOP
    private var currentPlayerIndex = 0
    private var isGameActive = false
    private var remainingPlayers = players

    enum class GameStage{
        PREFLOP, FLOP, TURN, RIVER, SHOWDOWN
    }

    fun startGame(){
        deck.reset()
        communityCards.clear()
        pot = 0
        gameStage = GameStage.PREFLOP
        currentPlayerIndex = 0
        isGameActive = true
        dealHands()
        play()
    }

    private fun dealHands(cardsPerPlayer: Int = 2){
        for (i in 0 until cardsPerPlayer){
            for (player in players){
                player.hand.add(deck.drawCard()!!)
            }
        }
    }

    fun doTheBetting(){
        var bettingRound = BettingRound(remainingPlayers, currentPlayerIndex)
        while(!bettingRound.isRoundOver()){
            val player = bettingRound.currentPlayer()
            val action = getPlayerActionFromUI(player)
            PlayerActionHandler.handlePlayerAction(player, action, bettingRound)
            remainingPlayers = bettingRound.getRemainingPlayers()
            pot += bettingRound.getPotIncrement()
        }

    }

    fun showHands(){
        for(player in players){
            println("${player.name}: ${player.hand}")
        }
    }


    fun play(){
        while(!isGameOver()){
            when(gameStage){
                GameStage.PREFLOP -> {
                    doTheBetting()
                    gameStage = GameStage.FLOP
                }
                GameStage.FLOP -> {
                    repeat(3) {communityCards.add(deck.drawCard()!!)}
                    doTheBetting()
                    gameStage = GameStage.TURN
                }
                GameStage.TURN -> {
                    communityCards.add(deck.drawCard()!!)
                    doTheBetting()
                    gameStage = GameStage.RIVER
                }
                GameStage.RIVER -> {
                    communityCards.add(deck.drawCard()!!)
                    doTheBetting()
                    gameStage = GameStage.SHOWDOWN
                }
                GameStage.SHOWDOWN -> {
                    showHands()
                    val winner = HandEvaluator.determineWinner(remainingPlayers, communityCards)
                    winner.chips += pot
                    announceWinner(winner)
                    break
                }
            }
        }
    }
    fun announceWinner(player: Player): String = "The winner is:" + player.name

    fun isGameOver(): Boolean = remainingPlayers.size == 1
}