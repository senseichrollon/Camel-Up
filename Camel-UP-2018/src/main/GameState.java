package main;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.TreeSet;

import entity.Camel;
import entity.DesertTile;
import entity.Die;
import entity.FinalBetCard;
import entity.LegBetCard;
import entity.Player;
import entity.Pyramid;
import entity.TrackTile;
import graphics.engine.RoundEndScreen;
import graphics.engine.ScreenManager;

public class GameState {
	
	private Pyramid pyramid;
	
	private Player[] players;
	private ArrayList<Camel> camels;
	
	private TrackTile[] track;
	private int currentPlayer;
	
	
	private LinkedList<FinalBetCard> winBets;
	private LinkedList<FinalBetCard> lossBets;
	private LinkedHashMap<Color, TreeSet<LegBetCard>> legBets;
		
	public static String[] names = { "Jesse James", "Montana Jones", "The Sultan", "Violet", "Gray Beard"};
	public static Color newOrange = new Color(255, 116, 0);
	public static Color[] colors;
	public static int[] maxValues = {5,3,2};
	public static int DEFAULT_TRACK_SIZE = 16;
	
	private boolean winner;
	
	public GameState() {
		colors = new Color[]{Color.WHITE, Color.BLUE, Color.GREEN, Color.YELLOW, newOrange};
		winner = false;
		pyramid = new Pyramid();
		currentPlayer = 0;
		
		// initializes players
		players = new Player[5];
		for(int i = 0; i < players.length; i++) {
			players[i] = new Player(names[i]);
		}
		
		//initializes camels
		camels = new ArrayList<>();;
		for(int i = 0; i < colors.length; i++) {
			camels.add(new Camel(colors[i], i));
		}
		
		
		// initializes legbets
		legBets = new LinkedHashMap<>();
		for(Color c : colors) {
			TreeSet<LegBetCard> pile = new TreeSet<>();
			Arrays.stream(maxValues).forEach(n -> pile.add(new LegBetCard(n, c)));
			legBets.put(c, pile);
		}
		//initializes final bets
		winBets = new LinkedList<>();
		lossBets = new LinkedList<>();
		
		
		// initializes track 
		track = new TrackTile[DEFAULT_TRACK_SIZE];
		
		for(int i = 0; i < track.length; i++) {
			track[i] = new TrackTile();
		}
		Collections.shuffle(camels);
		track[0].addCamels(camels, false);
	}
	
	public boolean roll() {
		Die die = pyramid.chooseDie();
		die.rollDie();
		int roll = die.getRoll();
		
		int location = -1;
		for(Camel c : camels) {
			if(c.getColor().equals(die.getColor())) {
				location = c.getLocation();
				break;
			}
		}
		ArrayList<Camel> camels = track[location].removeCamels(die.getColor());
		int newLoc = location + roll;
		
		
		int shift = track[newLoc % 16].getShift();
		
		if(shift != 0) {
			getPlayer(track[newLoc].getDesertTile().getPlayerName()).updateBalance(1);
		}
		
		boolean back = false;
		
		int finalPos = newLoc + shift;
		
		int pos = finalPos;
		
		if(finalPos >= track.length) {
			winner = true;
			finalPos %= track.length;
		}
		
		if(shift == -1) {
			back = true;
		}
		
		int oo = finalPos;
		camels.stream().forEach(n -> n.moveCamel(pos));
		track[finalPos].addCamels(camels, back);
		players[currentPlayer].updateRoll();
		return true;
	}
	
	public boolean placeDesertTile(int tileNum, boolean face) {
	 	DesertTile tile = players[currentPlayer].removeDesertTile();
		
		if(tile == null) {
			for(TrackTile t : track) {
				if(t.getDesertTile() != null && t.getDesertTile().getPlayerName().equals(players[currentPlayer].getName())) {
					tile = t.removeDesertTile();
				}
			}
		}
		track[tileNum].placeDesertTile(tile, face);		
		return true;
	}
		
	public boolean takeLegBetCard(Color color) {
		if(players[currentPlayer].getLegBets().size() == 6 || legBets.get(color).isEmpty()) {
			return false;
		}
		
		players[currentPlayer].addLegBet(legBets.get(color).first());
		legBets.get(color).remove(legBets.get(color).first());
		return true;
	}
	
	public boolean placeFinalBetWin(Color color) {
		FinalBetCard c = null;
		try {
			 c = players[currentPlayer].getFinalBet(color);
		} catch(Exception e) {
			
		}

		winBets.add(c);
		return true;
	}
	
	public boolean placeFinalBetLoss(Color color) {
		FinalBetCard c = players[currentPlayer].getFinalBet(color);
		if(c == null) {
			players[currentPlayer].getFinalBets().add(c);
			return false;
		}
		lossBets.add(c);
		return true;
	}
	
	public boolean flipDesertTile(int tileNum) {
		track[tileNum].getDesertTile().flip();
		return true;
	}
	
	public void distributeLegBets() {
		Collections.sort(camels, (a,b) -> a.getLocation() != b.getLocation()? b.getLocation() - a.getLocation():b.getElevation() - a.getElevation());
		for(Player p : players) {
			ArrayList<LegBetCard> cards = p.emptyLegBets();
			for(int i = cards.size() - 1; i >= 0; i--) {
				LegBetCard card = cards.remove(i);
				p.updateBalance(card.getValue(getPlace(card.getColor())));
				legBets.get(card.getColor()).add(card);
			}
			p.resetRoll();
		}
	}
	
	
	public void endRound() {
		distributeLegBets();
		for(int i = track.length - 1; i>=0; i--) {
			if(track[i].getDesertTile() != null) {
				DesertTile tile = track[i].removeDesertTile();
				Player p = getPlayer(tile.getPlayerName());
				p.setDesertTile(tile);
			}
		}
		pyramid.resetDice();
	}
	
	public Player endGame() {
		distributeLegBets();
		int cnt = 0;
		int[] order = {8,5,3,2,1};
		for(FinalBetCard card : winBets) {
			Player p = getPlayer(card.getPlayerName());
			System.out.println(p);
			if(camels.get(0).getColor().equals(card.getColor())) {
				p.updateBalance(order[cnt >= order.length? order.length -1:cnt]);
				cnt++;
			} else {
				System.out.println("two");
				p.updateBalance(-1);
			}
		}
		
		cnt = 0;
		for(FinalBetCard card : lossBets) {
			Player p = getPlayer(card.getPlayerName());
			if(camels.get(camels.size() - 1).getColor().equals(card.getColor())) {
				p.updateBalance(order[cnt >= order.length? order.length -1:cnt]);
				cnt++;
			} else {
				p.updateBalance(-1);
			}
		}
		Arrays.sort(players, (a,b) -> Integer.compare(b.getBalance(), a.getBalance()));
		
		return players[0];
	}
	
	public boolean hasWinner() {
		return winner;
	}
	public void nextTurn() {
		currentPlayer = (currentPlayer == players.length - 1)?0: currentPlayer + 1;
	}
	
	public int getCurrentPlayer() {
		return currentPlayer;
	}
	
	private int getPlace(Color c) {
		for(int i = 0; i < camels.size(); i++) {
			if(camels.get(i).getColor().equals(c)) {
				return i + 1;
			}
		}
		return 0;
	}
	
	private Player getPlayer(String name) {
		for(int i = 0; i < players.length; i++) {
			if(players[i].getName().equals(name)) {
				return players[i];
			}
		}
		return null;
	}
	
	
	public TreeSet<Integer> getValidIndcies() {
		TreeSet<Integer> validIndecies = new TreeSet<>();	
		for(int t = 1; t < track.length-1; t++) {
			if(track[t].getCamels().isEmpty() && track[t].getDesertTile()==null && track[t+1].getDesertTile()==null && track[t-1].getDesertTile()==null)
				validIndecies.add(t);
		}
		
		if(track[15].getCamels().isEmpty() && track[15].getDesertTile() == null && track[14].getDesertTile() == null)
			validIndecies.add(15);
		return validIndecies;
	}

	public Pyramid getPyramid() {
		return pyramid;
	}

	public Player[] getPlayers() {
		return players;
	}


	public ArrayList<Camel> getCamels() {
		return camels;
	}

	public TrackTile[] getTrack() {
		return track;
	}

	public LinkedList<FinalBetCard> getWinBets() {
		return winBets;
	}

	public LinkedList<FinalBetCard> getLossBets() {
		return lossBets;
	}

	public LinkedHashMap<Color, TreeSet<LegBetCard>> getLegBets() {
		return legBets;
	}
}