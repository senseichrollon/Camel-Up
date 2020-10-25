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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.stream.IntStream;

import entity.Camel;
import entity.LegBetCard;
import entity.Player;
import graphics.rendering.CardDrawer;
import graphics.util.CoordinateLoader;
import graphics.util.Point;
import main.GraphicsFrame;

public class RoundEndScreen extends ScreenManager{
	private ArrayList<Camel> camels;
	private Player[] players;
	
	public static int[] desertTileMoney;
	
	private HashMap<String, BufferedImage> playerImg;
	
	public static int round = 1;
	private boolean winner;
	private int[] initialBalances;
	
	private Path2D camelShape;
	
	public RoundEndScreen(ArrayList<Camel> arrayList, Player[] player, Path2D camelShape) {
		this.camels = arrayList;
		players = new Player[player.length];
		desertTileMoney = new int[players.length];
		
				
		IntStream.range(0,player.length).forEach(i -> players[i] = player[i]);
		initialBalances = new int[players.length];
		Arrays.fill(initialBalances, 3);
		
		
		this.camelShape = camelShape;
		this.playerImg = CoordinateLoader.loadPlayerImg();
	}
	

	
	public void update(MouseEvent e, String key) {
		
		if(key.equals("mp")) {
			if(!ScreenManager.getGame().getGameState().hasWinner()) {
				round++;
				setInitial();
				ScreenManager.switchScreen(ScreenManager.GAME);
			} else {
				round = 1;
				ScreenManager.init();
			}
		} 

	}
	
	public void render(Graphics gg) {
		Graphics2D g = (Graphics2D)gg;
		g.setColor(new Color(222,184,135));
		g.fillRect(0, 0, GraphicsFrame.WIDTH, GraphicsFrame.HEIGHT);
		g.setFont(new Font("TimesRoman", Font.BOLD, 70));
		
		g.setColor(Color.black);
		String type = !winner? "Round " + round + " summary":"Game summary";
		g.drawString(type, 650, 50);
		renderCamels(g);
		renderPlayers(g);
	}
	
	public void setWinner() {
		winner = true;
	}
	
	
	public void renderPlayers(Graphics2D g) {
		Arrays.sort(players, (a,b) -> b.getBalance() - a.getBalance());
		
		int xPos = 30;
		
		String[] codes = {"1st", "2nd", "3rd", "4th", "5th"};
		int i = 0;
		for(Player p : players) {
			g.setColor(Color.BLACK);
			
			g.drawString(codes[i], xPos, 150);
			
			
			g.setFont(new Font("TimesRoman", Font.BOLD, 40));
			g.drawString(p.getName(),xPos, 200 );
			g.drawImage(playerImg.get(p.getName()),xPos ,250, null);
			g.setFont(new Font("TimesRoman", Font.BOLD, 30));
			g.drawString("Balance £ " + p.getBalance() , xPos, 400);
			xPos += 400;
			i++;
			
			int xLeg = xPos;
			for(LegBetCard c : p.getPrevious()) {
				g.setColor(c.getColor());
				g.fillRect(xPos, 480, 50, 80);
				
				g.setColor(Color.black);
				g.setStroke(new BasicStroke(2.0f));
				g.drawRect(xLeg, 480, 50, 80);
				
				CardDrawer.drawLegBetCard(g, new Point(xPos, 480), 200, 200, null, c.getMaxValue());
				xLeg += 50;
			}
		}
		
	}
	
	public void renderCamels(Graphics2D g) {
		
		String[] positions = {"1st", "2nd", "3rd", "4th", "5th"};
		
		int xPos = -150;
		for(int i = 0; i < camels.size(); i++) {
			
			g.setFont(new Font("TimesRoman", Font.BOLD, 30));
			g.setColor(Color.BLACK);
			g.drawString(positions[i], xPos + 250, 900);
			
			AffineTransform at = new AffineTransform();
			at.scale(3, 3);
			Path2D camel = (Path2D)camelShape.clone();
			camel.transform(at);
			g.setColor(camels.get(i).getColor());
			g.translate(xPos, 500);
			g.fill(camel);
			g.translate(-xPos, -500);
			xPos += 250;
		}
	}
	
	public void renderLegBets() {
		
	}
	
	public void setInitial() {
		IntStream.range(0, players.length).forEach(i -> initialBalances[i] = players[i].getBalance());
	}
	
}
