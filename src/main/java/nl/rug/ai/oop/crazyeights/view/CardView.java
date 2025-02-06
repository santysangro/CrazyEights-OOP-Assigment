package nl.rug.ai.oop.crazyeights.view;
import nl.rug.ai.oop.crazyeights.model.Card;
import nl.rug.ai.oop.crazyeights.model.CrazyEights;
import nl.rug.ai.oop.crazyeights.model.UserCrazyEightsPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
/**
 * Represents the CardView object. It makes the hand of the physical player visible and reactive
 * to the game.
 */
public class CardView extends JPanel {
    /**
     * @param CARD_WIDTH integer of the size of the cards
     * @param cardLayout layout of the hands of the physical player
     * @param game The crazy eights game.
     */
    public static int CARD_WIDTH = 255 + 50;
    FlowLayout cardLayout;
    private CrazyEights game;
    private UserCrazyEightsPlayer userPlayer;
    /**
     * Constructs a new CardView panel with the specified CARD_WIDTH, cardLayout, and game:
     * @param game The crazy eights game.
     * @param userPlayer The physical player.
     */
    public CardView(CrazyEights game, UserCrazyEightsPlayer userPlayer) {
        cardLayout = new FlowLayout(FlowLayout.CENTER,10, 0);
        setLayout(cardLayout);
        this.game = game;
        this.userPlayer = userPlayer;
    }
    /**
     * Method to make a card a button, which gets deleted once clicked.
     * @param card a card of the hand of the physical player.
     * @param userCards List of buttons containing the hand of the physical player.
     */
    public void addButton(Card card, ArrayList<JButton> userCards) {
        String cardName = "/" + card.toString() + ".png";
        JButton newButton = new JButton(new ImageIcon(CardView.class.getResource(cardName)));
        add(newButton);
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean deleteCard =  userPlayer.checkTurnOfUser(game, card, true);
                if(deleteCard){
                    userCards.remove(newButton);
                    remove(newButton);
                    revalidate();
                    repaint();
                }
            }
        });
        userCards.add(newButton);
        add(newButton);
    }
    /**
     * Method to make cards visible by staking them when they do not fit in the frame.
     */
    @Override
    public void invalidate() {
        if (getComponentCount() > 0) {
            cardLayout.setHgap(Math.min(CARD_WIDTH / 10, getWidth()/getComponentCount() - CARD_WIDTH));
        }
        super.invalidate();
    }
}
