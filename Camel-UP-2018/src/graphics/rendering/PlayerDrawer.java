package graphics.rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import entity.DesertTile;
import entity.FinalBetCard;
import entity.LegBetCard;
import entity.Player;
import graphics.util.CoordinateLoader;
import graphics.util.Point;
import main.GameState;

public class PlayerDrawer {
	private Player[] players;
	private int currentPlayer;
	private HashMap<String, BufferedImage> playerImg;
	private HashMap<String, BufferedImage> oasisTiles;

	public static final int PLAYER_SECTION_WIDTH = 240;
	public static final int PLAYER_SECTION_HEIGHT = 150;
	
	private static final int CARD_WIDTH = 65;
	private static final int CARD_HEIGHT = (int) (PLAYER_SECTION_HEIGHT * 2 /3);
	
	private Path2D legBetCamel;

	public PlayerDrawer(Player[] players, Path2D legBetCamel) {
		this.players = players;
		playerImg = CoordinateLoader.loadPlayerImg();
		this.legBetCamel = legBetCamel;
		AffineTransform at = new AffineTransform();
		at.translate(-65, -135);
		legBetCamel.transform(at);
	}
	
	public void update(MouseEvent evt, String key, GameState game) {
		setCurrentPlayer(game.getCurrentPlayer());
	}
	

	public void setCurrentPlayer(int currentPlayer) {
		this.currentPlayer = currentPlayer;
	}
	
	public void setOasisTiles(HashMap<String, BufferedImage> map) {
		oasisTiles = map;
	}

	public void render(Graphics gg) {
		int y = 0;
		for (int i = 0; i < players.length; i++) {
			Rectangle r = new Rectangle(0, y, PLAYER_SECTION_WIDTH, PLAYER_SECTION_HEIGHT);
			boolean current = false;
			if (i == currentPlayer) {
				r.setBounds((int) r.getX(), (int) r.getY(), (int) r.getWidth() * 2, (int) (r.getHeight() * 2.8));
				current = true;
			}
			renderPlayerSection(gg, r, players[i], current);
			y += r.getHeight();
		}
	}

	public void renderPlayerSection(Graphics gg, Rectangle r, Player p, boolean current) {
		Graphics2D g = (Graphics2D) gg;
		g.setColor(new Color(82, 176, 225));
		g.fill(r);
		
		BasicStroke old = (BasicStroke) g.getStroke();
		g.setStroke(new BasicStroke(3.0f));
		g.setColor(Color.BLACK);
		g.draw(r);
		g.setStroke(old);
		g.setFont(new Font("Garamond", Font.BOLD, 22));
		
		g.drawString(p.getName(), (int) (r.getX()), (int) (30 + r.getY()));
		g.drawImage(playerImg.get(p.getName()), (int) (r.getX()), (int) (50 + r.getY()), 60, 60, null);
		g.drawString("£ " + p.getBalance(), (int) (r.getX() + 70), (int) (90 + r.getY()));
		
		g.drawString("Rolls", 170, (int)r.getY()  +30);
		g.drawString("" + p.getRollCount(), 170, (int)(r.getY() +70));

		if(!current) {
			return;
		}

		
		DesertTile d = p.getDesertTile();
		
		if(d != null) {
			g.drawImage(oasisTiles.get(p.getName()),(int) (r.getX() + 300), (int) (r.getY() +35), (int)(200 * .85), (int)(137 * .75), null);
			p.setDesertTile(d);
		}
		
		g.setFont(new Font("Garamond", Font.BOLD, 25));
		g.drawString("Leg Bets: ", 10, (int) (r.getHeight()/2.5 + r.getY()));
		int xLeg = (int) (r.getWidth()* .03) ;
		int yLeg = (int) ((r.getHeight()/3 + r.height * .09) + r.getY());
		
		for (LegBetCard c : p.getLegBets()) {
			renderLegBet(g, c, new Point(xLeg, yLeg));
			xLeg += CARD_WIDTH + 10;
		}
		
		g.drawString("Final Bets: ", 10, (int) (2 * r.getHeight()/2.8 + r.getY()));
		int xFinal = (int) (r.getWidth()* .03) - 5 ;
		int yFinal = (int) ((2 * r.getHeight()/3 + r.height * .07) + r.getY());
		
		for(FinalBetCard c : p.getFinalBets()) {
			renderFinalBet(g,c,new Point(xFinal, yFinal));
			xFinal += CARD_WIDTH + 30;
		}

	}
	
	public HashMap<String, BufferedImage> getPlayerImg() { 
		return playerImg;
	}

	private void renderLegBet(Graphics2D g, LegBetCard c, Point offset) {
		g.setColor(Color.black);
		g.setStroke(new BasicStroke(5.0f));
		g.drawRect(offset.getX(), offset.getY(), CARD_WIDTH, CARD_HEIGHT);
		g.setColor(new Color(119,136,153));
		g.fillRect(offset.getX(), offset.getY(), CARD_WIDTH,CARD_HEIGHT);
		g.setColor(c.getColor());
		CardDrawer.drawLegBetCard(g,offset,CARD_WIDTH , CARD_HEIGHT, legBetCamel, c.getMaxValue());
	}
	
	private void renderFinalBet(Graphics2D g, FinalBetCard c, Point offset) {
		g.setColor(Color.black);
		g.setStroke(new BasicStroke(5.0f));
		g.drawRect(offset.getX(), offset.getY(), CARD_WIDTH + 20, CARD_HEIGHT);
		g.setColor(c.getColor());
		g.fillRect(offset.getX(), offset.getY(), CARD_WIDTH + 20,CARD_HEIGHT);
		CardDrawer.drawFinalBetCard(g, offset, CARD_WIDTH + 20, CARD_HEIGHT, playerImg.get(c.getPlayerName()), c.getPlayerName());
	}
}