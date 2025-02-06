package nl.rug.ai.oop.crazyeights.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

/**
 * This main class controls the game of Crazy Eights.
 */
public class CrazyEights {
    /**
     * @param INITIAL_HAND_SIZE The initial hand size of players
     * @param playersListeners List of PropertyChangeListener for the number of cards on the players' hands.
     * @param centerCardListener PropertyChangeListener for the card on top of the discard pile.
     * @param selectSuit PropertyChangeListener for the suit selected suit
     * @param drawListener PropertyChangeListener for when the physical player draws a card.
     */
    public static int INITIAL_HAND_SIZE = 5;
    ArrayList<PropertyChangeListener> playersListeners = new ArrayList();
    PropertyChangeListener centerCardListener;
    PropertyChangeListener selectSuit;
    PropertyChangeListener drawListener;
    /**
     * Tracks the state of the game. Used to prevent players from
     * executing cards.
     */
    enum GameState {
        INACTIVE, PLAYING, WAITING_FOR_PLAYER_DECISION
    }
    private List<CrazyEightsPlayer> players = new ArrayList();
    private Map<CrazyEightsPlayer, List<Card>> hands = new HashMap();
    // Note that there is no outside access to card hands,
    // ensuring that players cannot see the cards of others
    // However, players can also not see how many cards other
    // players have. You may want to change this for a
    // GUI implementation.
    private List<Card> deck = new ArrayList();
    private List<Card> discardPile = new ArrayList();
    private Card topCardOnDiscardPile;
    private CrazyEightsPlayer currentPlayer;
    private GameState state = GameState.INACTIVE;
    private boolean reversed = false;

    /**
     * Builds a deck of cards, implementing the special rules
     * for special cards.
     */
    private void buildDeck() {
        deck.clear();
        discardPile.clear();
        for (CrazyEightsPlayer player : players) {
            hands.get(player).clear();
        }
        for (Card.Suit suit : Card.Suit.values()) {
            deck.add(new Card(suit, 1){
                /**
                 * An Ace changes reverses the order of players.
                 */
                @Override
                protected void execute() {
                    if (state == GameState.PLAYING) {
                        if(reversed){
                            reversed = false;
                        }else{
                            reversed = true;
                        }
                        moveToNextPlayer(reversed);
                    }
                }
            });
            deck.add(new Card(suit, 2){
                /**
                 * A two causes the next player to draw 2 cards
                 */
                @Override
                protected void execute() {
                    if (state == GameState.PLAYING) {
                        moveToNextPlayer(reversed);
                        drawCards(2);
                        if(currentPlayer.equals(players.size() - 1)){
                            notifyDrawListeners();
                        }
                    }
                }
            });
            deck.add(new Card(suit, 12){
                /**
                 * A Queen causes the next player to skip their turn
                 */
                @Override
                protected void execute() {
                    if (state == GameState.PLAYING) {
                        moveToNextPlayer(reversed);
                        System.out.println("Player " + players.indexOf(currentPlayer) + " gets skipped");
                        moveToNextPlayer(reversed);
                    }
                }
            });
            deck.add(new Card(suit, 8){
                /**
                 * An eight changes its suit to match the player's choice
                 */
                @Override
                protected void execute() {
                    if (state == GameState.PLAYING) {
                        if(currentPlayer instanceof UserCrazyEightsPlayer){
                            notifySelectSuitListeners();
                        }else{
                            selectSuit(null);
                            moveToNextPlayer(reversed);
                        }
                    }
                }

                /**
                 * An eight is wild, and can be played on top of any card
                 */
                @Override
                protected boolean isPlayableOn(Card other) {
                    return true;
                }
            });
            for (int i: new int[]{3,4,5,6,7,9,10,11,13}) {
                deck.add(new Card(suit, i){
                    /**
                     * Regular cards do nothing but
                     * pass the turn to the next player
                     */
                    @Override
                    protected void execute() {
                        if (state == GameState.PLAYING) {
                            moveToNextPlayer(reversed);
                        }
                    }
                });
            }
        }
    }

    /**
     * Method to allow the physical player to select a suit.
     */
    public void selectedSuit(Card.Suit suit){
        if(currentPlayer instanceof UserCrazyEightsPlayer){
            selectSuit(suit);
            moveToNextPlayer(reversed);
            notifyCardListeners();
        }
    }
    /**
     * Method to change the suit of a card if it is an eight.
     */
    private void selectSuit(Card.Suit selectedsuit) {
        Card.Suit suit;
        if (state == GameState.PLAYING) {
            state = GameState.WAITING_FOR_PLAYER_DECISION;
            if(currentPlayer instanceof UserCrazyEightsPlayer){
                suit = selectedsuit;
            }else{
                suit = currentPlayer.chooseSuit(this);
            }
            state = GameState.PLAYING;
            topCardOnDiscardPile = new Card(suit, topCardOnDiscardPile.getValue()) {
                @Override
                protected void execute() {
                }
            };
            System.out.println("Player " + players.indexOf(currentPlayer) + " changes color to " + suit);
        }
    }

    /**
     * Moves a given card to the top of the discard pile
     * @param card Card to be discarded
     */
    private void discard(Card card) {
        if (state == GameState.PLAYING) {
            topCardOnDiscardPile = card;
            discardPile.add(card);
        }
    }

    /**
     * Passes the turn to the next player
     */
    private void moveToNextPlayer(boolean reversed) {
        if (state == GameState.PLAYING) {
            int currentPlayerId = players.indexOf(currentPlayer);
            if(reversed){
                if(currentPlayerId == 0){
                    currentPlayer = players.get(players.size() - 1);
                }else{
                    currentPlayer = players.get((currentPlayerId - 1));
                }
            }else{
                currentPlayer = players.get((currentPlayerId + 1) % players.size());
            }
        }
    }

    /**
     * Causes the current player to draw cards
     * @param n the number of cards to draw
     */
    private void drawCards(int n) {
        if (state == GameState.PLAYING) {
            for (int i = 0; i < n; i++) {
                drawCard();
                notifyDrawListeners();
            }
        }
    }

    /**
     * Causes the current player to draw a card. If the deck has
     * run out, the discard pile is shuffled into the deck.
     * @return Card that was drawn by the player
     */
    private Card drawCard() {
        if (state == GameState.PLAYING) {
            if (deck.size() <= 1) {
                while (discardPile.size() > 1) {
                    deck.add(discardPile.remove(1));
                }
                Collections.shuffle(deck);
            }
            Card cardDrawn = deck.remove(0);
            hands.get(currentPlayer).add(cardDrawn);
            System.out.println("Player " + players.indexOf(currentPlayer) + " draws " + cardDrawn);
            return cardDrawn;
        }
        return null;
    }

    /**
     * Adds a player to the game. Can only be performed when the game
     * is not currently being played.
     * @param player CardGamePlayer to add to the game
     */
    public void addPlayer(CrazyEightsPlayer player) {
        if (!isGameActive()) {
            players.add(player);
            hands.put(player, new ArrayList());
        }
    }

    /**
     * Removes a player from the game. Can only be performed when the game
     * is not currently being played.
     * @param player CardGamePlayer to remove from the game
     */
    public void removePlayer(CrazyEightsPlayer player) {
        if (!isGameActive()) {
            players.remove(player);
            hands.remove(player).clear();
        }
    }

    /**
     * Determines whether the game is currently being played
     * @return true iff a game is in progress
     */
    public boolean   isGameActive() {
        return (state != GameState.INACTIVE);
    }

    /**
     * Starts a new game
     */
    public void start() {
        addPlayer(new RandomCrazyEightsPlayer());
        addPlayer(new RandomCrazyEightsPlayer());
        addPlayer(new RandomCrazyEightsPlayer());
        if (!isGameActive()) {
            state = GameState.PLAYING;
            buildDeck();
            Collections.shuffle(deck);
            for (int i = 0; i < players.size(); i++) {
                List<Card> hand = hands.get(players.get(i));
                for (int j = 0; j < INITIAL_HAND_SIZE; j++) {
                    hand.add(deck.remove(0));
                }
                System.out.println(i + ":\t" + hand);
            }
            discard(deck.remove(0));
            System.out.println("Top card is " + topCardOnDiscardPile);
            currentPlayer = players.get(0);
        }
    }

    /**
     * Plays a single round of the game if it is not the turn of the physical player.
     */
    public void nextPlayer(){
        if(currentPlayer instanceof RandomCrazyEightsPlayer){
            playRound(null);
        }
    }
    /**
     * Method used to play a round of the current crazy eights game. The next round begins when a player
     * draws or plays a card.
     * @param userCard Card that has been played by the user. Can be null if no card has been selected or the
     * card the user has selected.
     */
    public void playRound(Card userCard) {
        if (isGameActive()) {
            List<Card> hand = hands.get(currentPlayer);
            List<Card> cards = new ArrayList();
            cards.addAll(hand);
            // By handing the player a copy of their hand,
            // players are unable to change the cards in
            // their actual hand
            Card cardPlayed;
            do {
                state = GameState.WAITING_FOR_PLAYER_DECISION;
                if(currentPlayer instanceof UserCrazyEightsPlayer){
                    cardPlayed = userCard;
                }else{
                    cardPlayed = currentPlayer.takeTurn(cards, this);
                }
                state = GameState.PLAYING;

            } while (!(cardPlayed == null || (hand.contains(cardPlayed) && isPlayable(cardPlayed))));
            // The while loop above ensures that the current player
            // can actually play the card they have chosen
            if (cardPlayed == null) {
                drawCard();
                notifyPlayerListeners();
                moveToNextPlayer(reversed);
            } else {
                System.out.println("Player "+players.indexOf(currentPlayer)+" plays "+cardPlayed);
                hand.remove(cardPlayed);
                notifyPlayerListeners();
                discard(cardPlayed);
                cardPlayed.execute();
            }
            checkEndGame();
            notifyCardListeners();
        }
    }

    /**
     * Determines whether the game has ended because some player
     * no longer has any cards
     */
    private void checkEndGame() {
        for (List<Card> hand : hands.values()) {
            if (hand.size() < 1) {
                System.out.println("The game has ended");
                state = GameState.INACTIVE;
                return;
            }
        }
    }

    /**
     * Retrieves the current card on top of the discard pile.
     * Since Cards are immutable, players are unable to change
     * the top card of the discard pile
     * @return Card on top of the discard pile
     */
    public Card getTopCardOnDiscardPile() {
        return topCardOnDiscardPile;
    }

    /**
     * Gets the integer of the number of players
     * @return size of players
     */
    public int getNumberOfPlayers() {
        return players.size();
    }

    /**
     * Get the index of the current player 0 to 3
     * @return the index of the current player
     */
    public int getIndexOfCurrentPlayer(){
        return players.indexOf(currentPlayer);
    }

    /**
     * Gets the hand of the physical player
     * @return returns the hands of the physical players
     */
    public List<Card> getHandUser() {
        List<Card> hand = new ArrayList<>();
        hand.addAll(hands.get(players.get(0)));
        return hand;
    }
    /**
     * Gets the size of the hand of every player except the physical player.
     * @param i integer of the hand size
     * @return returns size of the hands of the players
     */

    public Integer getHandSize(int i){
        return hands.get(players.get(i)).size();
    }
    /**
     * Determines whether a card is playable in the current situation.
     * @param card Card held by the current player
     * @return true iff the current player can play the given Card
     */
    public boolean isPlayable(Card card) {
        return card.isPlayableOn(topCardOnDiscardPile);
    }
    /**
     * Adds a listener Player.
     * @param listener Contains the information of the action.
     */
    public void addPlayerListener(PropertyChangeListener listener) {
        playersListeners.add(listener);
    }
    /**
     * Adds a listener to CenterCard.
     * @param listener Contains the information of the action.
     */
    public void addCenterCardListener(PropertyChangeListener listener) {
        centerCardListener = listener;
    }
    /**
     * Adds a listener to Draw.
     * @param listener Contains the information of the action.
     */

    public void addDrawListener(PropertyChangeListener listener){
        drawListener = listener;
    }
    /**
     * Adds a listener to SelectSuit.
     * @param listener contains the information of the action.
     */
    public void addSelectSuitListener(PropertyChangeListener listener){
        selectSuit = listener;
    }
    /**
     * Notifies everything that classifies as a player listener
     */
    private void notifyPlayerListeners() {
        Iterator<PropertyChangeListener> playersList = playersListeners.iterator();
        for(int index = 1; index < getNumberOfPlayers(); index++){
            PropertyChangeEvent handOfPlayers = new PropertyChangeEvent(this, "Number of cards in hand", null, getHandSize(index));
            if (playersList.hasNext()) {
                playersList.next().propertyChange(handOfPlayers);
            }
        }
    }
    /**
     * Notifies everything that classifies as a card listener
     */
    private void notifyCardListeners() {
        PropertyChangeEvent centerCard = new PropertyChangeEvent(this, "Center card", null, getTopCardOnDiscardPile());
        centerCardListener.propertyChange(centerCard);
    }
    /**
     * Notifies everything that classifies as a SelectSuit listener
     */
    private void notifySelectSuitListeners(){
        PropertyChangeEvent suit = new PropertyChangeEvent(this, "Center card", null, getTopCardOnDiscardPile());
        selectSuit.propertyChange(suit);
    }
    /**
     * Notifies everything that classifies as a Draw listener
     */

    private void notifyDrawListeners(){
        PropertyChangeEvent card = new PropertyChangeEvent(this, "card one", null,  getHandUser());
        drawListener.propertyChange(card);
    }
}
