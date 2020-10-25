 package graphics.rendering;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

import graphics.util.Point;

public class CardDrawer {
	
	
	public static void drawLegBetCard(Graphics2D g, Point offset, int width, int height, Path2D camel, int max) {
		if(camel != null) {
			AffineTransform at = new AffineTransform();
			
			at.translate(offset.getX() + width/11, offset.getY());
			at.scale(width / (camel.getBounds().getWidth() * 1.2), height/(camel.getBounds().getHeight() * 2));
			
			camel.transform(at);
			g.fill(camel);
			
			at = new AffineTransform();
			at.scale(1.0/(width / (camel.getBounds().getWidth() * 1.2)), 1.0/(height/(camel.getBounds().getHeight() * 2)));
			at.translate(-offset.getX() - width/11, -offset.getY());
			
			camel.transform(at);
		}

		g.setColor(Color.BLACK);
		g.drawString(Integer.toString(max), (int)(offset.getX() + width/3), (int)(offset.getY() + height/1.2));
		
	}
	
	public static void drawFinalBetCard(Graphics2D g, Point offset, int width, int height, BufferedImage icon, String name) {
		g.drawImage(icon, offset.getX() + width/4, offset.getY(), Math.min(width/2, icon.getWidth()), Math.min(height/2, icon.getHeight()), null);
		g.setColor(Color.BLACK);
		g.setFont(new Font("Garamond", Font.BOLD, (int)(width * .25)));
		String[] sp = name.split(" ");
		g.drawString(sp[0], offset.getX(), (int)(offset.getY() + width/1.1));
		if(sp.length > 1)
			g.drawString(sp[1], offset.getX(), (int)(offset.getY() + width * 1.1));
	}


}
