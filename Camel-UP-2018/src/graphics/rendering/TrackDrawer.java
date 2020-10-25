package graphics.rendering;

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
import java.util.HashMap;
import java.util.TreeSet;
import java.util.stream.IntStream;

import entity.Camel;
import entity.DesertTile;
import entity.Die;
import entity.Pyramid;
import entity.TrackTile;
import graphics.engine.ScreenManager;
import graphics.util.CoordinateLoader;
import graphics.util.ImageLoader;
import graphics.util.MyButton;
import graphics.util.Point;
import main.GameState;
import main.GraphicsFrame;
import main.GraphicsPanel;

public class TrackDrawer {

	private TrackTile[] track;
	private Pyramid pyramid;
	private ArrayList<Die> dieReference;

	private Point[] tiles;
	private Path2D.Double[] camelCoords;
	private HashMap<Color, Point> die;
	private HashMap<String, BufferedImage> oasisTiles;
	private HashMap<String, BufferedImage> desertTiles;
	
	private MyButton roll;
	private MyButton[][] desertTileModifier;
	

	public static final int TILE_WIDTH = (int) (GraphicsFrame.WIDTH * .0989);
	public static final int TILE_HEIGHT = (int) (.175 * GraphicsFrame.HEIGHT);

	public TrackDrawer(TrackTile[] track, Pyramid p) {
		this.track = track;
		this.pyramid = p;
		this.dieReference = new ArrayList<>(p.getRollableDice());
		
		tiles = CoordinateLoader.loadTiles();
		camelCoords = CoordinateLoader.loadCamels();
		die = CoordinateLoader.loadDie();
		
		roll = new MyButton("Roll", 100, 30, Color.CYAN, new Font("Garamond", Font.BOLD, 20), Color.WHITE);
		desertTileModifier = CoordinateLoader.loadDesertTileModifiers();
	}
	
	
	public void update(MouseEvent evt, String code, GameState game) {
		
		
		if(game.hasWinner()) {
			return;
		}
		
		// roll button clicked
		if(code.equals("Roll")) {
			game.roll();
			game.nextTurn();
		}
		
		// place oasis tile button clicked
		if(code.contains("Place Oasis Tile")) {
			String[] sp = code.split(" ");
			int pos = Integer.parseInt(sp[sp.length - 1]);
			 boolean placed = game.placeDesertTile(pos, true);
			 if(placed)
				 game.nextTurn();
		}
		
		// place mirage tile button clicked
		if(code.contains("Place Mirage Tile")) {
			String[] sp = code.split(" ");
			int pos = Integer.parseInt(sp[sp.length - 1]);
			boolean placed = game.placeDesertTile(pos, false);
			if(placed)
				game.nextTurn();
		}
		
		// place flip button clicked
		if(code.contains("Flip")) {
			String[] sp = code.split(" ");
			int pos = Integer.parseInt(sp[sp.length - 1]);
			game.flipDesertTile(pos);
			game.nextTurn();
		}
				
	}

	public void render(Graphics g) {
		
		for (int i = 0; i < track.length; i++) {
			renderTile(g, tiles[i], track[i],i);
		}
		int y = (int) (TILE_HEIGHT * 1.5);
		int x = (int) (GraphicsFrame.WIDTH/2 + TILE_WIDTH * 1.75);
		int pWidth = (int) ((TILE_WIDTH) * 2);
		int pHeight = (int)((TILE_HEIGHT) * 2);
		
		g.drawImage(ImageLoader.loadImage("pyramid.JPG"), x, y, pWidth, pHeight, null);
		
		if(!GraphicsPanel.GLOBAL_INSTANCE.containsComponent(roll)) {
			roll.setBounds(x + pWidth/3, y + pHeight + 20, roll.getWidth(), roll.getHeight());
			roll.setX(30);
			roll.setY(22);
			GraphicsPanel.GLOBAL_INSTANCE.add(roll);
		}
		
		renderDie(g);

	}


	private void renderTile(Graphics g, Point p, TrackTile tile, int tileNum) {
		g.setColor(new Color(246, 195, 103));
		g.fillRect(p.getX(), p.getY(), TILE_WIDTH, TILE_HEIGHT);
		g.setColor(new Color(145, 103, 36));
		Graphics2D g2D = (Graphics2D) g;
		g2D.setStroke(new BasicStroke(3));
		g.drawRect(p.getX(), p.getY(), TILE_WIDTH, TILE_HEIGHT);

		GameState game = ScreenManager.getGame().getGameState();
		
		MyButton oasis = desertTileModifier[0][tileNum];
		MyButton mirage = desertTileModifier[1][tileNum];
		MyButton flip = desertTileModifier[2][tileNum];
		
		
		TreeSet<Integer> validIndices = game.getValidIndcies();
		removeButtons(tileNum);

		if (!tile.getCamels().isEmpty()) {
			for (int i = 0; i < tile.getCamels().size(); i++) {
				renderCamel(g, i, p, tile.getCamels().get(i));
			}

		} else if(tile.getDesertTile() != null) {
			DesertTile t = tile.getDesertTile();
			BufferedImage img = t.getSide()?oasisTiles.get(t.getPlayerName()) : desertTiles.get(t.getPlayerName());
			g.drawImage(img,p.getX() + 10, p.getY() + 30, (int)(200 * .85), (int)(137 * .85), null);
			
			if(t.getPlayerName() == game.getPlayers()[game.getCurrentPlayer()].getName()) {
				flip.setBounds(p.getX() + TILE_WIDTH/4,  p.getY() + 155, flip.getWidth(), flip.getHeight());
				GraphicsPanel.GLOBAL_INSTANCE.add(flip);
			}
			
		} else if(tileNum != 0 && validIndices.contains(tileNum)) {
				oasis.setBounds(p.getX() + TILE_WIDTH/4,p.getY() + TILE_WIDTH/4 - 30, oasis.getWidth(), oasis.getHeight());
				GraphicsPanel.GLOBAL_INSTANCE.add(oasis);
			
				mirage.setBounds(p.getX() + TILE_WIDTH/4, p.getY() + (3 * TILE_WIDTH/4), mirage.getWidth(), mirage.getHeight());
				GraphicsPanel.GLOBAL_INSTANCE.add(mirage);
		} 
		if(tileNum == 15)
			renderFinishLine(g);
	}
 
	private void renderCamel(Graphics gg, int elevation, Point offset, Camel c) {
		Graphics2D g = (Graphics2D) gg;
		g.setColor(c.getColor());
		Path2D path = camelCoords[elevation];
		AffineTransform old = g.getTransform();
		g.translate(offset.getX(), offset.getY());
		int loc = c.getLocation() % 16;
		
		if (loc < 2 || loc >= 14) {
			g.fill(path);
		} else if (loc < 6) {
						
			g.translate(190, 190);
			g.rotate(Math.PI / 2);
			g.scale(-1, 1);
			g.fill(path);
		} else if (loc < 10) {
			g.translate(190, 0);
			g.scale(-1, 1);
			g.fill(path);
		} else if(loc < 14) {
			g.translate(190, 0);
			g.rotate(Math.PI/2);
			g.fill(path);
		}
		g.setTransform(old);
	}

	private void renderDie(Graphics gg) {
		Graphics2D g = (Graphics2D)gg;
		
		for(Die d : dieReference) {
			g.setColor(d.getColor());
			int x = die.get(d.getColor()).getX();
			int y = die.get(d.getColor()).getY();
			g.fillRect(x, y, 80, 80);
			
			if(pyramid.getRolledDice().contains(d)) {
				Point p = die.get(d.getColor());
				int roll = d.getRoll();
				g.setColor(Color.BLACK);
				if(roll == 1 || roll == 3) {
					g.fillOval(26 + p.getX(), 26 + p.getY(), 26 , 26);
				}
				
				if(roll == 2 || roll == 3) {
					g.fillOval(p.getX(), p.getY(), 26, 26);
					g.fillOval(p.getX() + 52, p.getY() + 52, 26, 26);
				}
			}
			

		}
	   
	}
	private void renderFinishLine(Graphics g) {

		int SquareX = tiles[15].getX() + 152; // x value of top left corner of square
		int SquareY = tiles[15].getY(); // x value of top left corner of square

		for (int col = SquareX; col < SquareX + 38; col += 19) {
			if (col == SquareX)
				g.setColor(Color.BLACK);
			else
				g.setColor(Color.WHITE);

			for (int row = SquareY; row < SquareY + 190; row += 38)
				g.fillRect(col, row, 19, 19);

			if (col == SquareX)
				g.setColor(Color.WHITE);
			else
				g.setColor(Color.BLACK);

			for (int row = SquareY + 19; row < SquareY + 190; row += 38)
				g.fillRect(col, row, 19, 19);
		}

	}
	
	public void removeButtons(int tileNum) {
		for(int i = 0; i < 3; i++) {
			MyButton b = desertTileModifier[i][tileNum];
			if(GraphicsPanel.GLOBAL_INSTANCE.containsComponent(b)) {
				GraphicsPanel.GLOBAL_INSTANCE.remove(b);
			}
		}
		
		if(GraphicsPanel.GLOBAL_INSTANCE.containsComponent(roll)) {
			GraphicsPanel.GLOBAL_INSTANCE.remove(roll);
		}
	}	
	public Path2D[] getCamelShape() {
		Path2D[] ret = new Path2D[camelCoords.length];
		IntStream.range(0, camelCoords.length).forEach(i -> ret[i]  = (Path2D)camelCoords[i].clone());
		return ret;
	}
	
	public void setDesertTiles(HashMap<String, BufferedImage> map) {
		this.desertTiles = map;
	}
	
	public void setOasisTiles(HashMap<String, BufferedImage> map) {
		this.oasisTiles = map;
	}
}