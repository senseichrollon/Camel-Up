package entity;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import graphics.engine.RoundEndScreen;
import main.GameState;

public class Player {
	private String name;
	private DesertTile desertTile;
	private int rollCount;
	private int balance;
	private ArrayList<LegBetCard> legBets;
	private ArrayList<FinalBetCard> finalBet;
	
	private ArrayList<LegBetCard> previous;
	
	public Player(String s) {
		name = s;
		rollCount = 0;
		balance = 3;
		legBets = new ArrayList<LegBetCard>();
		finalBet = new ArrayList<FinalBetCard>();
		Arrays.stream(GameState.colors).forEach(n -> finalBet.add(new FinalBetCard(name, n)));
		desertTile = new DesertTile(name);
		previous = new ArrayList<>();
	}
	
	public String getName() {
		return name;
	}
	
	public DesertTile getDesertTile() {
		return desertTile;
	}
	
	public DesertTile removeDesertTile() {
		DesertTile temp = desertTile;
		desertTile = null;
		return temp;
	}
	
	public int getRollCount() {
		return rollCount;
	}
	
	public int getBalance() {
		return balance;
	}
	
	public FinalBetCard getFinalBet(Color c)  {
		FinalBetCard f = null;
		for(int i=finalBet.size() - 1; i>=0; i--) {
			if(finalBet.get(i).getColor().equals(c)) {
				f = finalBet.remove(i);
				break;
			}
		}
		return f;
	}
	
	public void setDesertTile(DesertTile tile) {
		this.desertTile = tile;
	}
	
	public void updateBalance(int balance) {
		this.balance += balance;
		this.balance = Math.max(0, this.balance);
	}
	
	public void resetRoll() {
		balance += rollCount;
		rollCount = 0;
	}
	
	public void updateRoll() {
		rollCount++;
	}
	
	public void addLegBet(LegBetCard card) {
		legBets.add(card);
	}
	
	public ArrayList<LegBetCard> emptyLegBets() {
		ArrayList<LegBetCard> temp = new ArrayList<>(legBets);
		System.out.println(temp.size() + " " + previous.size());
		legBets.clear();
		return temp;
	}
	
	public ArrayList<LegBetCard> getLegBets() {
		return legBets;
	}
	
	public ArrayList<LegBetCard> getPrevious() {
		return previous;
	}
	
	public ArrayList<FinalBetCard> getFinalBets() {
		return finalBet;
	}
	
	public String toString() {
		return super.toString();
	}
}