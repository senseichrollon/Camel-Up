	package graphics.engine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.IntStream;

import javax.swing.JOptionPane;

import entity.Camel;
import entity.FinalBetCard;
import entity.LegBetCard;
import entity.Player;
import graphics.rendering.CardDrawer;
import graphics.rendering.PlayerDrawer;
import graphics.rendering.TrackDrawer;
import graphics.util.CoordinateLoader;
import graphics.util.MyButton;
import graphics.util.Point;
import main.GameState;
import main.GraphicsPanel;
import sun.audio.AudioData;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import sun.audio.ContinuousAudioDataStream;

public class GameManager extends ScreenManager {

	
	private GameState game;
	private PlayerDrawer playerDrawer;
	private TrackDrawer trackDrawer;
	
	private HashMap<String, BufferedImage> oasisTiles;
	private HashMap<String, BufferedImage> desertTiles;
	
	private Point[] legBetPile;
	private MyButton[] winBetButtons;
	private MyButton[] lossBetButtons;
	private Point mousePos;
	
	private Player winner;
	private boolean change;
	
	private static final int PILE_WIDTH = 150;
	private static final int PILE_HEIGHT = 200;
	
	public static final int LEG_BET_WIDTH = 100;
	public static final int LEG_BET_HEIGHT = 130;
	
	public GameManager() {
		game = new GameState();
		legBetPile = CoordinateLoader.loadLegBets();
		
		winner = null;
		trackDrawer = new TrackDrawer(game.getTrack(), game.getPyramid());
		playerDrawer = new PlayerDrawer(game.getPlayers(),trackDrawer.getCamelShape()[0]);
		
		oasisTiles = CoordinateLoader.loadOasisTiles(playerDrawer.getPlayerImg());
		desertTiles = CoordinateLoader.loadDesertTiles(playerDrawer.getPlayerImg());
		
		playerDrawer.setOasisTiles(oasisTiles);
		trackDrawer.setOasisTiles(oasisTiles);
		trackDrawer.setDesertTiles(desertTiles);
		
		winBetButtons = CoordinateLoader.loadFinalBets(true);
		lossBetButtons = CoordinateLoader.loadFinalBets(false);
		
		mousePos = new Point(0,0);
	}
	
	
	@Override
	public void update(MouseEvent evt, String key) {
		if(winner != null) {
			return;
		}
		
		playerDrawer.update(evt, key, game);
		trackDrawer.update(evt, key, game);
		

		// mouse moved
		if(key.equals("mm")) {
			mousePos = new Point(evt.getX(), evt.getY());
		}
		
		//mouse pressed
		if(key.equals("mp")) {
			mousePos = new Point(evt.getX(), evt.getY());
			Color c = this.getLegBetClicked(new Point(evt.getX(), evt.getY()), game.getLegBets().keySet());
			if(c != null) {
				boolean take = game.takeLegBetCard(c);
				if(take)
					game.nextTurn();
				else
					JOptionPane.showMessageDialog(GraphicsPanel.GLOBAL_INSTANCE, "You have reached your maximum leg bet capacity!");
			}
		}
		
		// win/loss bet button clicked
		if(key.contains("winbet")) {
			game.placeFinalBetWin(stringToColor(key));
			game.nextTurn();
		} else if(key.contains("lossbet")) {
			game.placeFinalBetLoss(stringToColor(key));
			game.nextTurn();
		}
		
		
		if(game.hasWinner() && winner == null) {
			winner = game.endGame();
			game.endRound();
			removeButtons();
			IntStream.range(0, 16).forEach(i -> trackDrawer.removeButtons(i));
			ScreenManager.getRoundEnd().setWinner();
			ScreenManager.switchScreen(ScreenManager.ROUND_END);

		}
		// end round
		if(game.getPyramid().getRolledDice().size() == 5) {
			game.endRound();
			removeButtons();
			IntStream.range(0, 16).forEach(i -> trackDrawer.removeButtons(i));
			ScreenManager.switchScreen(ScreenManager.ROUND_END);
		}
		

	}

	@Override
	public void render(Graphics g) {
		
		if(winner != null) {
			renderWinScreen((Graphics2D)g);
		}
		
		g.setColor(new Color(255, 229, 153));
		g.fillRect(0, 0, 1920, 1080);
		g.setColor(Color.BLACK);
		g.drawRect(0,  0, 190, 190);
		playerDrawer.render(g);

		trackDrawer.render(g);
		renderFinalBetPile(g, game.getWinBets(), new Point(700,30), true);
		renderFinalBetPile(g, game.getLossBets(), new Point(700,330), false);

		int cX = 600;
		int cY = 40;
		
		removeButtons();
		for(int i = 0; i <winBetButtons.length; i++) {
			boolean drawButton = false;
			Player cPlayer = game.getPlayers()[game.getCurrentPlayer()];
			for(FinalBetCard c : cPlayer.getFinalBets()) {
				if(GameState.colors[i].equals(c.getColor())) {
					drawButton = true;
				}
			}
				if(drawButton) {
					winBetButtons[i].setBounds(cX, cY, winBetButtons[i].getWidth(), winBetButtons[i].getHeight());
					GraphicsPanel.GLOBAL_INSTANCE.add(winBetButtons[i]);
					lossBetButtons[i].setBounds(cX, cY + 300, lossBetButtons[i].getWidth(), lossBetButtons[i].getHeight());
					GraphicsPanel.GLOBAL_INSTANCE.add(lossBetButtons[i]);
				}

			cY += 40;

		}
		
		if(winner != null) {
			g.setColor(new Color(82, 176, 225));
			g.fillRect(500, 600, 450, 400);
			g.setColor(Color.black);
			g.drawRect(500, 600, 450, 400);
			Graphics2D g2D = (Graphics2D)g;
			g2D.setFont(new Font("Garamond", Font.BOLD, 40));
			g.drawString("Winner is : " + winner.getName() + "!", 500, 800);
			return;
		}
		int idx = 0;
		for(Color c : game.getLegBets().keySet()) {
			renderLegBet(g, legBetPile[idx++], game.getLegBets().get(c));
		}
	}
	
	private void renderFinalBetPile(Graphics gg, LinkedList<FinalBetCard> list, Point offset, boolean win) {
		Graphics2D g = (Graphics2D)gg;

		if(list.isEmpty()) {
			renderEmptyPile(g, offset, win);
		} else {
			String topName = list.getLast().getPlayerName();
			g.drawRect(offset.getX(), offset.getY(), PILE_WIDTH, PILE_HEIGHT);
			g.setColor(new Color(160,82,45));
			g.fillRect(offset.getX(), offset.getY(), PILE_WIDTH, PILE_HEIGHT);
			CardDrawer.drawFinalBetCard(g, offset,PILE_WIDTH , PILE_WIDTH, playerDrawer.getPlayerImg().get(topName), topName);
		}
		
		
	}
	
	public void renderEmptyPile(Graphics2D g, Point offset, boolean win) {
		g.setColor(new Color(160,82,45));
		g.fillRect(offset.getX(), offset.getY(), PILE_WIDTH, PILE_HEIGHT);
		
		Path2D[] camels = Arrays.stream(trackDrawer.getCamelShape()).map(n -> (Path2D)(n.clone())).toArray(Path2D[]::new);
		AffineTransform at = new AffineTransform();
		at.translate(offset.getX() - 20 ,  + offset.getY());
		Arrays.stream(camels).forEach(n -> n.transform(at));
		
		int fillIdx = !win?0: camels.length - 1;
		
		for(int i = 0; i < camels.length; i++) {
			g.setColor(Color.GRAY);
			if(i == fillIdx) {
				g.setColor(Color.BLACK);
			}
			g.fill(camels[i]);
			g.setStroke(new BasicStroke(1.0f));
			g.setColor(Color.BLACK);
			g.draw(camels[i]);
		}
		
		AffineTransform at2 = new AffineTransform();
		at2.translate(65, 135);
	}
	
	public void renderLegBet(Graphics gg, Point offset, TreeSet<LegBetCard> cards) {
		if(cards.size() == 0)
			return;
		
		Graphics2D g = (Graphics2D)gg;
		Path2D camel = Arrays.stream(trackDrawer.getCamelShape()).map(n -> (Path2D)(n.clone())).toArray(Path2D[]::new)[0];
		AffineTransform at = new AffineTransform();
		at.translate(-65, -135);
		camel.transform(at);
		
		LegBetCard c = cards.first();
		boolean hover = mousePos.getX() >= offset.getX() && mousePos.getX() <= offset.getX() + LEG_BET_WIDTH;
		hover &= mousePos.getY() >= offset.getY() && mousePos.getY() <= offset.getY() + LEG_BET_HEIGHT;
		
		g.setColor(hover?Color.YELLOW:Color.BLACK);
		g.setStroke(new BasicStroke(5.0f));
		g.drawRect(offset.getX(), offset.getY(), LEG_BET_WIDTH, LEG_BET_HEIGHT);
		
		Color background = new Color(57, 204, 204);
		g.setColor(background);
		g.fillRect(offset.getX(), offset.getY(), LEG_BET_WIDTH,LEG_BET_HEIGHT);
		
		g.setColor(c.getColor());
		CardDrawer.drawLegBetCard(g,offset,LEG_BET_WIDTH , LEG_BET_HEIGHT, camel, c.getMaxValue());
	}
	
	public Color stringToColor(String code) {
		if(code.contains("white"))
			return Color.WHITE;
		else if(code.contains("orange"))
			return new Color(255, 116, 0);
		else if(code.contains("blue"))
			return Color.BLUE;
		else if(code.contains("yellow"))
			return Color.yellow;
		else if(code.contains("green"))
			return Color.GREEN;
		return null;
	}
	
	public Color getLegBetClicked(Point click, Set<Color> keySet) {
		ArrayList<Color> colorOrder = new ArrayList<>(keySet);
		for(int i = 0; i < legBetPile.length; i++) {
			Point p = legBetPile[i];
			boolean within = click.getX() >= p.getX() && click.getX() <= p.getX() + LEG_BET_WIDTH;
			within &= click.getY() >= p.getY() && click.getY() <= p.getY() + LEG_BET_WIDTH;
			
			if(within) {
				return colorOrder.get(i);
			}
		}
		return null;
	}
	
	public void renderWinScreen(Graphics2D g) {
		removeButtons();
		ArrayList<Camel> camels  = game.getCamels();
		String[] positions = {"1st", "2nd", "3rd", "4th", "5th"};
		int xPos = -150;
		for(int i = 0; i < camels.size(); i++) {
			g.setFont(new Font("TimesRoman", Font.BOLD, 30));
			g.setColor(Color.BLACK);
			g.drawString(positions[i], xPos + 250, 900);
			
			AffineTransform at = new AffineTransform();
			at.scale(3, 3);
			Path2D camel = (Path2D)trackDrawer.getCamelShape()[0].clone();
			camel.transform(at);
			g.setColor(camels.get(i).getColor());
			g.translate(xPos, 500);
			g.fill(camel);
			g.translate(-xPos, -500);
			xPos += 250;
		}

	}
	
	public void removeButtons() {
		for(int i = 0; i < winBetButtons.length; i++) {
			if(GraphicsPanel.GLOBAL_INSTANCE.containsComponent(winBetButtons[i])) {
				GraphicsPanel.GLOBAL_INSTANCE.remove(winBetButtons[i]);
			}
			
			if(GraphicsPanel.GLOBAL_INSTANCE.containsComponent(lossBetButtons[i])) {
				GraphicsPanel.GLOBAL_INSTANCE.remove(lossBetButtons[i]);
			}
		}
	}
	
	
	public TrackDrawer getTrackDrawer() {
		return trackDrawer;
	}
	
	public GameState getGameState() {
		return game;
	}

}
