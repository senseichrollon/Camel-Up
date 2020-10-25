package graphics.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import graphics.engine.GameManager;
import graphics.rendering.TrackDrawer;
import main.GameState;
import main.GraphicsFrame;

public class CoordinateLoader {
	
	public static HashMap<String, BufferedImage> loadDesertTiles(HashMap<String, BufferedImage> playerImg) {
		HashMap<String, BufferedImage> map = new HashMap<>();
		BufferedImage dt = ImageLoader.loadImage("blankDT.png");
		for(String name : playerImg.keySet()) {
			BufferedImage img = playerImg.get(name);
			BufferedImage copyDT = new BufferedImage(dt.getWidth(), dt.getHeight(), BufferedImage.TYPE_INT_ARGB); 
			Graphics2D g1 = copyDT.createGraphics();
			g1.drawImage(dt, 0, 0, null);
			g1.drawImage(img, dt.getWidth()/8, dt.getHeight()/4,null);
			map.put(name, copyDT);
		}
		return map;
	}
	
	public static HashMap<String, BufferedImage> loadOasisTiles(HashMap<String, BufferedImage> playerImg) {
		HashMap<String, BufferedImage> map = new HashMap<>();
		BufferedImage ot = ImageLoader.loadImage("blankOT.png");
		for(String name : playerImg.keySet()) {
			BufferedImage img = playerImg.get(name);
			BufferedImage copyOT = new BufferedImage(ot.getWidth(), ot.getHeight(), BufferedImage.TYPE_INT_ARGB); 
			Graphics2D g1 = copyOT.createGraphics();
			g1.drawImage(ot, 0, 0, null);
			g1.drawImage(img, ot.getWidth()/8, ot.getHeight()/4,null);
			map.put(name, copyOT);
		}
		return map;
	}
		
	public static Point[] loadLegBets() {
		int startX = 570;
		int startY = 660;
		
		Point[] legBetPile = new Point[5];
		for(int i = 0; i < 3; i++) {
			legBetPile[i] = new Point(startX, startY);
			startX += GameManager.LEG_BET_WIDTH + 30;
		}
		startX = 640;
		startY = 850;
		
		for(int i = 3; i < legBetPile.length; i++) {
			legBetPile[i] = new Point(startX, startY);
			startX += GameManager.LEG_BET_WIDTH + 30;
		}
		
		return legBetPile;
	}
	
	public static HashMap<Color, Point> loadDie() {
		Scanner in = null;
		try {
			in = new Scanner(new File("dieCoordinates"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		HashMap<Color, Point> die = new HashMap<>();
		for (int i = 0; i < 5; i++) {
			Color c = null;
			switch (in.next()) {
			case "Green":
				c = Color.GREEN;
				break;
			case "Blue":
				c = Color.BLUE;
				break;
			case "Yellow":
				c = Color.YELLOW;
				break;
			case "White":
				c = Color.WHITE;
				break;
			case "Orange":
				c = new Color(255, 116, 0);
				break;

			}
			Point p = new Point(in.nextInt(), in.nextInt());
			die.put(c, p);
		}
		
		return die;

	}
	
	public static Point[] loadTiles() {
		Point[] tiles = new Point[16];
		int x = (int) (GraphicsFrame.WIDTH * .5) + TrackDrawer.TILE_WIDTH * 2;
		int y = TrackDrawer.TILE_HEIGHT * 4;
		for (int i = 0; i < 16; i++) {
			tiles[i] = new Point(x, y);
			if (i < 2 || i >= 14) {
				x += TrackDrawer.TILE_WIDTH;
			} else if (i < 6) {
				y -= TrackDrawer.TILE_HEIGHT;
			} else if (i < 10) {
				x -= TrackDrawer.TILE_WIDTH;
			} else if (i < 14) {
				y += TrackDrawer.TILE_HEIGHT;
			}
		}
		
		return tiles;
	}
	
	public static Path2D.Double[] loadCamels() {
		Scanner in = null;
		try {
			in = new Scanner(new File("CamelCoords.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Path2D.Double[] camelCoords = new Path2D.Double[5];
		Path2D.Double[] temp = new Path2D.Double[5];
		int[] defX = Arrays.stream(in.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
		int[] defY = Arrays.stream(in.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();

		temp[0] = new Path2D.Double();
		temp[0].moveTo(defX[0], defY[0]);
		for (int i = 1; i < defX.length; i++) {
			temp[0].lineTo(defX[i], defY[i]);
		}
		temp[0].closePath();
		for (int i = 1; i < temp.length; i++) {
			temp[i] = (Path2D.Double) temp[i - 1].clone();
			AffineTransform f = new AffineTransform();
			f.translate(0, 30);
			temp[i].transform(f);
		}
		for(int i = 0; i < temp.length; i++) {
			camelCoords[i] = temp[temp.length - i - 1];  
		}
		return camelCoords;
	}
	
	public static HashMap<String, BufferedImage> loadPlayerImg() {
		HashMap<String, BufferedImage> playerImg = new HashMap<>();
		playerImg.put("Gray Beard", ImageLoader.loadImage("GrayBeard.png"));
		playerImg.put("Jesse James", ImageLoader.loadImage("JesseJames.png"));
		playerImg.put("Montana Jones", ImageLoader.loadImage("MontanaJones.png"));
		playerImg.put("The Sultan", ImageLoader.loadImage("TheSultan.png"));
		playerImg.put("Violet", ImageLoader.loadImage("Violet.png"));
		
		return playerImg;
	}
	
	public static MyButton[] loadFinalBets(boolean win) {
		MyButton[] finalBetButtons = new MyButton[5];
		
		String first = win?"winbet ":"lossbet ";
		
		String[] codes = {"+ white","+ blue","+ green","+ yellow","+ orange"};
		for(int i = 0; i < 5; i++) {
			finalBetButtons[i] = new MyButton(first+ codes[i],90,30,GameState.colors[i],new Font("TimesRoman", Font.BOLD, 20), Color.black);
			finalBetButtons[i].setX(-60);
			finalBetButtons[i].setY(22);
		}
		return finalBetButtons;
	}
	
	public static MyButton[][] loadDesertTileModifiers() {
		MyButton[][] modifiers = new MyButton[3][16];
		for(int i = 0; i < 16; i++) {
			modifiers[0][i] = new MyButton("Place Oasis Tile " + i , 95,30,Color.GREEN, new Font("TimesRoman", Font.BOLD, 12), Color.black);
			modifiers[1][i] = new MyButton("Place Mirage Tile  " + i, 95,30,Color.RED, new Font("TimesRoman", Font.BOLD, 11), Color.black);
			modifiers[2][i] = new MyButton("Flip    " + i, 70,30,Color.PINK, new Font("TimesRoman", Font.BOLD, 24), Color.black);
			
			for(int j = 0; j < 3; j++) {
				modifiers[j][i].setX(4);
				modifiers[j][i].setY(22);
			}
		}
		
		return modifiers;
	}
}