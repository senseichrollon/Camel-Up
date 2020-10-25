package graphics.engine;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import graphics.util.MyButton;
import main.GraphicsFrame;
import main.GraphicsPanel;

public class MenuManager extends ScreenManager {

	private MyButton[] buttons;
		
	public MenuManager() {
		buttons = new MyButton[2];
		buttons[0] = new MyButton("Play" , 200,70,Color.CYAN, new Font("TimesRoman", Font.BOLD, 40), Color.black);
		buttons[0].setBounds(860, 375, buttons[0].getWidth(), buttons[0].getHeight());
		buttons[1] = new MyButton("Exit" , 200,70,Color.CYAN, new Font("TimesRoman", Font.BOLD, 40), Color.black);
		buttons[1].setBounds(860, 565, buttons[1].getHeight(), buttons[1].getHeight());
		
		
		for(int i = 0; i < buttons.length; i++) {
			buttons[i].setX(60);
			buttons[i].setY(45);
			GraphicsPanel.GLOBAL_INSTANCE.add(buttons[i]);
		}
		
		
	}
	
	@Override
	public void update(MouseEvent evt, String key) {
		if(key.equals("Play")) {
			removeComponenets();
			ScreenManager.switchScreen(ScreenManager.GAME);
		}
		
		if(key.equals("Exit")) {
			System.exit(0);
		}
	}


	@Override
	public void render(Graphics gg) {
		Graphics2D g  = (Graphics2D)gg;
	
		g.setColor(new Color(255,0,127));
		g.fillRect(0, 0, GraphicsFrame.WIDTH, GraphicsFrame.HEIGHT);

		
		g.setFont(new Font("TimesRoman", Font.BOLD, 100));
		g.setColor(Color.BLACK);
		g.drawString("Camel UP", 700, 170);
	}
	
	public void removeComponenets() {
		for(MyButton mb : buttons) {
			GraphicsPanel.GLOBAL_INSTANCE.remove(mb);
		}
	}
	
}
