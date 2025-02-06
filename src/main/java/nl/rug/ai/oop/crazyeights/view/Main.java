package nl.rug.ai.oop.crazyeights.view;
import nl.rug.ai.oop.crazyeights.model.CrazyEights;
import nl.rug.ai.oop.crazyeights.model.UserCrazyEightsPlayer;

/**
 * The Main method is the starting point for executing the program.
 * It creates a new game, a new player of the class UserCrazyEightsPlayer (the person playing the game), and initializes the main frame for the GUI.
 */
public class Main {
    public static void main(String[] args) {
        CrazyEights game = new CrazyEights();
        UserCrazyEightsPlayer userPlayer = new UserCrazyEightsPlayer();
        game.addPlayer(userPlayer);
        game.start();
        GameView gameView = new GameView(game,userPlayer);
        gameView.init(game);
    }
}
