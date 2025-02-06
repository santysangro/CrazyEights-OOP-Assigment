package nl.rug.ai.oop.crazyeights.view;
import nl.rug.ai.oop.crazyeights.controller.SelectSuitController;
import nl.rug.ai.oop.crazyeights.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
/**
 * Represents the GameView object for game (the main frame).
 */
public class GameView extends JFrame {
	/**
	 * @param cardLayout creates the panel in the game that contains the hand of the physical player.
	 * @param playersView creates the panel in the game that contains the messages of the number of
	 * cards of the players.
	 * @param userCards Array List of the cards in the hand of the physical player.
	 * @param players Array list of the players in the game.
	 * @param centerCard Label of the top card in the discard pile
	 * @param drawCard Button of the draw pile, gives a card to the player when clicked.
	 * @param suitSelected Boolean to check if the player has selected a suit or not when playing an 8.
	 */
	private CrazyEights game;
	private UserCrazyEightsPlayer userPlayer;
	private CardView cardLayout;
	private PlayersView playersView;
	private ArrayList<JButton> userCards = new ArrayList<>();
	private ArrayList<JLabel> players = new ArrayList<>();
	private JLabel centerCard;
	private JButton drawCard;
	private boolean suitSelected = true;
	Color darkGreen = new Color(60,141,40);
	/**
	 * Constructs a new GameView frame with the specified game and userPlayer.
	 * @param game The crazy eights game.
	 * @param userPlayer The physical player.
	 */
	public GameView(CrazyEights game, UserCrazyEightsPlayer userPlayer){
		this.game = game;
		this.userPlayer = userPlayer;
	}
	/**
	 * Creates the frame of the game. Sets all the components to be visible and accessible.
	 * @param game The crazy eights game.
	 */
	public void init(CrazyEights game){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(900, 800);
		setTitle("Crazy Eights");
		setLocationRelativeTo(null);
		setBackground(darkGreen);
		cardLayout = new CardView(game,userPlayer);
		setCardsUser();
		setPlayers();
		setOtherButtons();
		SelectSuitController selectSuitController = new SelectSuitController(game);
		setListenersOfGame(selectSuitController);
		setVisible(true);
	}

	/**
	 * Method to set the hand of the physical player, adding each card of the hand as a button.
	 */
	private void setCardsUser(){
		remove(cardLayout);
		userCards.clear();
		cardLayout = new CardView(game, userPlayer);
		cardLayout.setBackground(darkGreen);
		for(int index = 0; index < game.getHandSize(0); index++){
			cardLayout.addButton(game.getHandUser().get(index), userCards);
		}
		add(cardLayout, BorderLayout.SOUTH);
		revalidate();
		repaint();
	}
	/**
	 * Method to set the messages of the players' hand size and number of player.
	 */
	private void setPlayers() {
		players.clear();
		playersView = new PlayersView(game);
		playersView.setBackground(darkGreen);
		for (int index = 1; index < game.getNumberOfPlayers(); index++) {
			playersView.addPlayer(index, game.getHandSize(index), players);
		}
		add(playersView, BorderLayout.WEST);
	}

	/**
	 * Method to add a label for the card on top of the discard pile, a button 'next' to make the other players play,
	 * and a button to draw a card.
	 */
	private void setOtherButtons(){
		JPanel centerPanel = new JPanel();
		centerPanel.setBackground(darkGreen);
		String centerCardName = "/" + game.getTopCardOnDiscardPile().toString() + ".png";
		centerCard = new JLabel(new ImageIcon(GameView.class.getResource(centerCardName)));
		game.addCenterCardListener(evt -> updateCenterCard());
		centerPanel.add(centerCard, BorderLayout.CENTER);
		JButton nextButton =  new JButton("NEXT");
		centerPanel.add(nextButton, BorderLayout.EAST);
		nextButton.addActionListener(e -> game.nextPlayer());
		add(centerPanel,BorderLayout.CENTER);
		drawCard = new JButton(new ImageIcon(GameView.class.getResource("/00.png")));
		drawCard.addActionListener(e -> updateDrawCard());
		centerPanel.add(drawCard,BorderLayout.WEST);
	}
	/**
	 * Method to set the listeners of the game so that when the actions Draw or SelectSuit are made an adequate
	 * response will be executed.
	 * @param selectSuitController Controller to let the player select a new suit after playing an eight.
	 */
	private void setListenersOfGame(SelectSuitController selectSuitController){
		game.addDrawListener(evt -> setCardsUser());
		game.addSelectSuitListener(evt -> selectSuit(selectSuitController));
	}
	/**
	 * Method for the pop-up of the selectSuit button. This button will only appear after  player has
	 * played an eight and will disappear after a suit has been selected.
	 * @param selectSuitController Controller to let the player select a new suit after playing an eight.
	 */
	private void selectSuit(SelectSuitController selectSuitController){
		JPanel suits = new JPanel(new GridLayout(2,2));
		suits.setBackground(new Color(12, 64, 87));
		String[] suitsName = {"hearts", "spades", "diamonds", "clubs"};
		suitSelected = false;
		for(int index = 0; index < 4; index++){
			JButton newButton = new JButton(new ImageIcon(GameView.class.getResource("/" + suitsName[index] + ".png")));
			newButton.setActionCommand(suitsName[index]);
			newButton.addActionListener(selectSuitController);
			newButton.addActionListener(e -> suits.setVisible(false));
			newButton.addActionListener(e -> setCardsUser());
			newButton.addActionListener(e -> suitSelected = true);
			suits.add(newButton);
		}
		add(suits, BorderLayout.SOUTH);
		cardLayout.setVisible(false);
		suits.setVisible(true);
		revalidate();
	}
	/**
	 * Method to add the drawn card to the hand of the physical player.
	 */
	private void updateDrawCard(){
		boolean drawCard = userPlayer.checkTurnOfUser(game,null,suitSelected);
		if(drawCard) {
			setCardsUser();
			revalidate();
			repaint();
		}
	}
	/**
	 * Method to update the label of the card on top of the discard pile.
	 */
	private void updateCenterCard(){
		String centerCardName = "/" + game.getTopCardOnDiscardPile().toString() + ".png";
		centerCard.setIcon(new ImageIcon(GameView.class.getResource(centerCardName)));
		revalidate();
	}
}