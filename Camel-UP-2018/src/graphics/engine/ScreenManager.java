package graphics.engine;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

import main.GraphicsPanel;

public abstract class ScreenManager {
	
	public static final int GAME = 0;
	public static final int MENU = 1;
	public static final int ROUND_END = 2;
	
	private static GameManager game;
	private static MenuManager menu;
	private static RoundEndScreen roundEnd;
	
	private static ScreenManager currentScreen;
	
	public static void init() {
		game = new GameManager();
		menu = new MenuManager();
		roundEnd = new RoundEndScreen(game.getGameState().getCamels(), game.getGameState().getPlayers(), game.getTrackDrawer().getCamelShape()[0]);
		currentScreen = menu;
	}
	
	public static GameManager getGame() {
		return game;
	} 
	
	public static MenuManager getMenu() {
		return menu;
	}
	
	public static RoundEndScreen getRoundEnd() {
		return roundEnd;
	}
		
	public static ScreenManager getCurrentScreen() {
		return currentScreen;
	}
	
	public static void switchScreen(int key) {
		if(key == GAME) 
			currentScreen = game;
		else if(key == MENU) 
			currentScreen = menu;
		else if(key == ROUND_END)
			currentScreen = roundEnd;
		
		GraphicsPanel.GLOBAL_INSTANCE.repaint();
	}
	
	
	// extension methods
	public abstract void update(MouseEvent e, String key);
	public abstract void render(Graphics g);
}
