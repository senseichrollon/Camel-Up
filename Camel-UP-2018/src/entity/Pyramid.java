package entity;

import java.util.ArrayList;
import java.util.Arrays;

import main.GameState;

public class Pyramid {
	private ArrayList<Die> rollableDice;
	private ArrayList<Die> rolledDice;
	
	public Pyramid() {
		rollableDice = new ArrayList<Die>();
		rolledDice = new ArrayList<Die>();
		Arrays.stream(GameState.colors).forEach(n -> rollableDice.add(new Die(n)));
	}
	
	public Die chooseDie() {
		int n = (int)(Math.random()*rollableDice.size());
		Die d = rollableDice.remove(n);
		rolledDice.add(d);
		return d;
	}
	
	public ArrayList<Die> getRolledDice(){
		return rolledDice;
	}
	
	public ArrayList<Die> getRollableDice() {
		return rollableDice;
	}
	
	public void resetDice() {
		for(int i= rolledDice.size() - 1; i>=0; i--) {
			rollableDice.add(rolledDice.get(i));
			rolledDice.remove(i);
		}
	}
		
}