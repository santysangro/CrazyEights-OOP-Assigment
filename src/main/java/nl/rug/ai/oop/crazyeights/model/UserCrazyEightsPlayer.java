package nl.rug.ai.oop.crazyeights.model;

import java.util.List;

/**
 * Player that plays Crazy Eights controlled by physical player
 * legal actions to play
 */
public class UserCrazyEightsPlayer implements CrazyEightsPlayer {
    @Override
    public Card takeTurn(List<Card> hand, CrazyEights game) {
        return null;
    }
    @Override
    public Card.Suit chooseSuit(CrazyEights game) {
        return null;
    }

    /** Checking if the current player has done its turn. When no card is selected, card == null.
     * @param card Card selected
     * @param suitSelected True if a suit has been selected (in case of playing and 8), otherwise false.
     * @return if it's not the physical player's turn return false, else compute the if statement
     *         and return true.
     */
    public boolean checkTurnOfUser(CrazyEights game, Card card, boolean suitSelected){
        if(game.getIndexOfCurrentPlayer() == 0 & suitSelected){
            if(card == null){
                game.playRound(null);
                return true;
            } else if (game.isPlayable(card)) {
                game.playRound(card);
                return true;
            }
        }
        return false;
    }

}
