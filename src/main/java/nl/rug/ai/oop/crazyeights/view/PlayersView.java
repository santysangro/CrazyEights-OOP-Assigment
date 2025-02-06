package nl.rug.ai.oop.crazyeights.view;
import nl.rug.ai.oop.crazyeights.model.CrazyEights;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
/**
 * Represents an object PlayersView that encapsulates the characteristics of the players panel.
 * These methods allow the player to see the number of players in the game, their number of cards,
 * and show when a player wins.
 */

public class PlayersView extends JPanel {
    GridLayout playersPanel;
    private CrazyEights game;
    /**
     * Constructs a new PlayersView panel with the specified playersPanel and game.
     * @param game The crazy eights game.
     */
    public PlayersView(CrazyEights game){
        playersPanel = new GridLayout(game.getNumberOfPlayers(),1);
        setLayout(playersPanel);
        this.game = game;
    }
    /**
     * Adding the text to the layout. It outputs the numbers of cards left for each player in the game.
     * @param index Integer of the index of the player, goes from 1 to 3 (0 is the physical player).
     * @param handSize Integer size of the hands of the player.
     * @param players The array list of labels of the players in the game and their hand size.
     */
    public void addPlayer(int index, int handSize, ArrayList<JLabel> players){
        JLabel player = new JLabel("PLAYER " + index + ": " +handSize+ " cards left.");
        player.setFont(new Font("Forte", 16,18));
        player.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        players.add(player);
        game.addPlayerListener(evt -> updatePlayersLabel( index, players));
        add(players.get(index - 1));
    }
    /**
     * Updates the label of the players when they play or draw cards. It also prints a message
     * if a player wins.
     * @param index Integer of the index of the player.
     * @param players The array list of labels of the players in the game and their hand size.
     */
    private void updatePlayersLabel( int index, ArrayList<JLabel> players) {
        if(game.getHandSize(index) == 0){
            players.get(index - 1).setText("PLAYER " + index + ": WINS!");;
        }else{
            players.get(index - 1).setText("PLAYER " + index + ": " + game.getHandSize(index) + " cards left.");
        }
    }
}
