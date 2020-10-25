package entity;

import java.awt.Color;

public class LegBetCard extends ColoredItem implements Comparable<LegBetCard> {
	
	private int maxValue;
	
	public LegBetCard(int max, Color c) {
		super(c);
		maxValue = max;
	}
	
	public int getValue(int place) {
		if(place == 1)
			return maxValue;
		else if(place == 2)
			return 1;
		return -1;
	}
		
	public int getMaxValue() {
		return maxValue;
	}
	
	@Override
	public int compareTo(LegBetCard card) {
		return Integer.compare(card.getMaxValue(), maxValue);
	}
	
	public String toString( ) {
		return color + " " + maxValue;
	}

}