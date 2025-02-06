package nl.rug.ai.oop.crazyeights.controller;
import nl.rug.ai.oop.crazyeights.model.Card;
import nl.rug.ai.oop.crazyeights.model.CrazyEights;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 * Represents the Select Suit controller which allows a player to select a suit after playing an eight.
 */

public class SelectSuitController implements ActionListener {
    private CrazyEights game;
    /**
     * Constructs a Select Suit Controller class with specified game:
     * @param game The crazy eights game.
     */
    public SelectSuitController(CrazyEights game) {
        this.game = game;
    }

    /**
     * Method that changes the suit of the card depending on which one is selected.
     * @param e ActionEvent containing the ActionCommand which corresponds to one of the four suits.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "hearts":
                game.selectedSuit(Card.Suit.HEARTS);
                break;
            case "diamonds":
                game.selectedSuit(Card.Suit.DIAMONDS);
                break;
            case "clubs":
                game.selectedSuit(Card.Suit.CLUBS);
                break;
            case "spades":
                game.selectedSuit(Card.Suit.SPADES);
                break;
        }
    }
}
