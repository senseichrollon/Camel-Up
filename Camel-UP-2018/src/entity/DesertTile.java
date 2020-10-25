package entity;

public class DesertTile {
	
	private boolean whichSide;
	private String playerName;
	
	public DesertTile(String name) {
		playerName = name;
	}
	
	public boolean getSide() {
		return whichSide;
	}

	public void flip() {
		whichSide = !whichSide;
	}
	public String getPlayerName() {
		return playerName;
	}
}