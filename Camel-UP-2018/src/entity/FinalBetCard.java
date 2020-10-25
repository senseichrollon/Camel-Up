package entity;

import java.awt.Color;

public class FinalBetCard extends ColoredItem {
	
	private String playerName;
	
	
	public FinalBetCard(String a, Color w) {
		super(w);
		playerName = a;	
	}
	
	public String getPlayerName() {
		return playerName;
	}
		
}