package entity;

import java.awt.Color;

public class Camel extends ColoredItem {
	private int loc;
	private int elevation;
	
	public Camel(Color c, int elevation) {
		super(c);
		loc = 0;
		this.elevation = elevation;
	}
	
	public int getElevation() {
		return elevation;
	}
	
	public int getLocation() {
		return loc;
	}
	
	public void setElevation(int elevation) {
		this.elevation = elevation;
	}
	
	public void moveCamel(int n) {
		loc = n;
	}
	
	public String toString() {
		return super.toString() + " "  +loc;
	}
}