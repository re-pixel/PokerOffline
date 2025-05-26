package com.example.pokeroffline.game

class GameEngine(private val players: List<Player>) {
    private val deck = Deck()
    private val communityCards = mutableListOf<Card>()
    private var pot: Int = 0
    private var gameStage = GameStage.PREFLOP
    private var currentPlayerIndex = 0
    private var isGameActive = false

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
    }

    private fun dealHands(cardsPerPlayer: Int = 2){
        for (i in 0 until cardsPerPlayer){
            for (player in players){
                player.hand.add(deck.drawCard()!!)
            }
        }
    }



    fun dealFlop(){
        if(gameStage == GameStage.PREFLOP){
            repeat(3 ) { communityCards.add(deck.drawCard()!!) }
            gameStage = GameStage.FLOP
        }
    }

    fun dealTurn(){
        if(gameStage == GameStage.FLOP){
            communityCards.add(deck.drawCard()!!)
            gameStage = GameStage.TURN
        }
    }

    fun dealRiver(){
        if(gameStage == GameStage.TURN){
            communityCards.add(deck.drawCard()!!)
            gameStage = GameStage.SHOWDOWN
        }
    }

    fun showHands(){
        for(player in players){
            println("${player.name}: ${player.hand}")
        }
    }
}