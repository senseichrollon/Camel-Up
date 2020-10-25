package entity;

import java.awt.Color;

public abstract class ColoredItem {
	
	protected Color color;
	
	public ColoredItem(Color c) {
		color = c;
	}
	
	public Color getColor() {
		return color;
	}
	
	public String toString() {
		if(color.equals(Color.WHITE))
			return "WHITE";
		else if(color.equals(Color.BLUE))
			return "BLUE";
		else if(color.equals(Color.GREEN))
			return "GREEN";
		else if(color.equals(Color.YELLOW))
			return "YELLOW";
		else
			return "ORANGE";
	}
}