package tile;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import creatures.Platform;
import tile.GameTile;
import player.Player;
import base.Creature;
import base.Item;

/**
 * 
 * The TileMap class contains all information for 
 * for a tile-based map
 *
 */

public class TileMap implements Serializable{
	
	private static final long serialVersionUID = 014L;
	
	// fields
	public GameTile[][] tiles;
	
	private TileMapSection[][] sections;
	public int sectionWidth = 25;
	public int sectionHeight = 25;
	
	private List<Platform> platforms; // List of Platforms on the current screen.
	private List<Creature> creatures; // Starts containing every Creature and decreases as they die.
	private List<Creature> relevantCreatures; // List of relevant Creatures to the current frame.
											  // This is a subset of creatures.
	private List<Creature> creaturesToAdd; // List of Creatures to be added inbetween frames.
	private List<GameTile> animatedTiles;
	private List<Item> items; // Every item on the map
	private List<Chest> chests; // All containers on the map

	//private List<SlopedTile> slopedTiles;
	private Player player; 
	public int playerX, playerY;
	
	/**
	 * Constructs a new TileMap with the specified width and height (in number of tiles)
	 * of the map.
	 */
	public TileMap(){
		//empty method
	}
	
	public TileMap(int width, int height) {	
		tiles = new GameTile[width][height];
		creatures = new LinkedList<Creature>();
		items = new LinkedList<Item>();
		chests = new LinkedList<Chest>();
		relevantCreatures = new ArrayList<Creature>();
		creaturesToAdd = new ArrayList<Creature>();
		platforms = new ArrayList<Platform>();
		animatedTiles = new ArrayList<GameTile>();
		
		sections = new TileMapSection[width/sectionWidth][height/sectionHeight];
		//build out each section
		//loop through section
		for(int i = 0; i < sections.length; i++){
			for(int j = 0; j < sections[0].length; j++){
				sections[i][j] = new TileMapSection();
			}
		}
	}
	
	public TileMap(int width, int height, GameTile[][] tiles){
		this(width, height);
		this.tiles = tiles;
	}
	
	public GameTile[][] getTiles() {
		return tiles;
	}
	
	public TileMapSection[][] getSections(){
		return sections;
	}
	
	/**
	 * @return the width of this TileMap in GameTiles.
	 */
	public int getWidth() {
		return tiles.length;
	}
	
	/**
	 * @return the height of this TileMap in GameTiles.
	 */
	public int getHeight() {
		return tiles[0].length;
	}
	
	/**
	 * @return the GameTiles at tiles[x][y]. If x or y is out of bounds
	 * or if tiles[x][y] == null, null is returned.
	 */
	public GameTile getTile(int x, int y) {
		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
			return null;
		} else {
			if(tiles[x][y] != null) {
				return tiles[x][y];
			} else {
				return null;
			}
		}
	}
	
	/**
	 * @return the image of the GameTiles at tiles[x][y]. If x or y is out of bounds
	 * or if tiles[x][y] == null, null is returned.
	 */
	public BufferedImage getImage(int x, int y) {
		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
			return null;
		} else {
			if(tiles[x][y] != null) {
			     return tiles[x][y].getImage();
			} else {
				return null;
			}
		}
	}
	
	/**
	 * Sets tiles[x][y] equal to parameter tile.
	 * This is used to set animated GameTiles.
	 */
	public void setTile(int x, int y, GameTile tile) {
		tiles[x][y] = tile;
	}
	
	/**
	 * Sets tiles[x][y] equal to a new Tile with no animation and the constant Image img.
	 * This is used to set non-animated GameTiles.
	 */
	public void setTile(int x, int y, BufferedImage img, boolean isCollidable) {
		tiles[x][y] = new GameTile(x, y, null, img, isCollidable);
	}
	
	/**
	 * @return the player sprite.
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * Sets the player sprite for this map.
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	
	/**
	 * @return a List containing every Platform in this map.
	 */
	public List<Platform> platforms() {
		return platforms;
	}	
	
	/**
	 * @return a List containing every Creature in this map.
	 */
	public List<Creature> creatures() {
		return creatures;
	}
	
	/**
	 * @return a List containing every Creature in this map.
	 */
	public List<Item> items() {
		return items;
	}
	
	/**
	 * @return a List containing every Chest in this map.
	 */
	public List<Chest> chests() {
		return chests;
	}
	
	
	/**
	 * @return a List containing Creatures to add to this map after the next game update.
	 */
	public List<Creature> creaturesToAdd() {
		return creaturesToAdd;
	}
	
	/**
	 * @return a List containing animated Tile in this map.
	 */
	public List<GameTile> animatedTiles() {
		return animatedTiles;
	}
	
	/**
	 * @return a List containing every relevant Creature in this map. 
	 * 
	 * A 'relevant Creature' is a Creature that the current frame cares about. 
	 * This is generally creatures on screen or creatures that need to be updated globally. 
	 */
	public List<Creature> relevantCreatures() {
		return relevantCreatures;
	}
}
