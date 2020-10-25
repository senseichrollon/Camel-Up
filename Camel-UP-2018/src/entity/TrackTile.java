package entity;

import java.awt.Color;
import java.util.ArrayList;

import main.GameState;

public class TrackTile {
	
	private ArrayList<Camel> camels;
	private DesertTile desertTile = null;
	
	public TrackTile() {
		camels = new ArrayList<>();
	}
	
	public void addCamels(ArrayList<Camel> c, boolean back) {
		if(!back)
			camels.addAll(c);
		else
			camels.addAll(0, c);
		resetElevations();
	}
	
	public void resetElevations() {
		for(int i = 0; i < camels.size(); i++) {
			camels.get(i).setElevation(i);
		}
	}
		
	public DesertTile getDesertTile() {
		return desertTile;
	}
	
	public int getShift() {
		if(desertTile == null)
			return 0;
		else if(desertTile.getSide())
			return 1;
		else
			return -1;
	}
	
	public DesertTile removeDesertTile() {
		DesertTile temp = desertTile;
		this.desertTile = null;
		return temp;
	}
	
	public ArrayList<Camel> removeCamels(Color c) {
		int i;
		for(i = 0; i <= camels.size(); i++) {
			if(camels.get(i).getColor().equals(c))
				break;
		}
		ArrayList<Camel> temp = new ArrayList<Camel>(camels.subList(i, camels.size()));
		camels.removeAll(temp);
		return temp;
	}
	
	public void placeDesertTile(DesertTile tile, boolean face) {
		if(tile.getSide() != face)
			tile.flip();
		desertTile = tile;
	}
	
	public ArrayList<Camel> getCamels() {
		return camels;
	}
}