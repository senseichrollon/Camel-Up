package graphics.util;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;

import main.GraphicsPanel;

public class MyButton extends JComponent implements MouseListener {
	
	private Color background;
	private Color textColor;
	
	private String text;
	private int width, height;
	private Font font;
	private boolean hover;
	private boolean click;
	
	private int x, y;
	
	public MyButton(String text, int width, int height, Color background, Font font, Color textColor) {
		this.text = text;
		
		hover = false;
		click = false;
		x = 0;
		y = 0;
				
		this.background = background;
		this.textColor = textColor;
		this.width  = width;
		this.height = height;
		this.font = font;
		
		addMouseListener(this);
	}
	
	public void paintComponent(Graphics gg) {		
		Graphics2D g = (Graphics2D)gg;
		
		g.setColor(click ? background.darker() : background);
		g.fillRect(0, 0, width, height);
				
		if(hover) {
			if(background == Color.yellow) {
				g.setColor(Color.WHITE);
			} else {
				g.setColor(Color.YELLOW);
			}
		} else {
			g.setColor(Color.BLACK);
		}
			
		g.setStroke(new BasicStroke(3.0f));
		g.drawRect(0, 0, width, height);
		
		g.setFont(font);
		g.drawString(text, x, y);
	}
	
	public void mouseClicked(MouseEvent evt) {
		click = true;
		hover = true;
		GraphicsPanel.GLOBAL_INSTANCE.repaint();
		repaint();
	}
	
	public void mouseReleased(MouseEvent evt) {
		click = false;
		hover = false;
		repaint();
		if(evt.getX() >= 0 && evt.getX() <= width && evt.getY() >= 0 && evt.getY() <= height)  {
			GraphicsPanel.GLOBAL_INSTANCE.update(evt, text);
			GraphicsPanel.GLOBAL_INSTANCE.repaint();
		}
	}

	public void mouseEntered(MouseEvent evt) {
		hover = true;
		repaint();
	}

	public void mouseExited(MouseEvent evt) {
		hover = false;
		click = false;
		repaint();
	}
	
	public void resetClick() {
		hover = false;
		click = false;
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	public void mousePressed(MouseEvent evt) {}
}