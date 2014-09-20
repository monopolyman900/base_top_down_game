package core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;

import menus.Inventory;
import player.Player;
import core.GameRenderer;
import creatures.Platform;
import base.Creature;
import base.Item;
import tile.GameTile;
import animation.Sprite;
import tile.Chest;
import tile.Tile;
import tile.TileMap;
import tile.ActionTile;

//The TileMapRenderer class is responsible for all the drawing onto the screen.
//Also contains useful static methods for converting tiles->pixels, pixels->tiles
//and a method for locating which tile a sprite has collided with.

public class GameRenderer {

	// AdjustYScroll is used to record the previous value of player.getY(). This way I can 
	// continue to draw on the same y level if there is no significant change in Y. I use 
	// the player jumping as a measure of significant change. Hides errors in my animations, 
	// keeping the screen from bobbing when there is a change in height of the player animation. 
	private int AdjustYScroll = 0;
	private ArrayList<TileMap> maps = new ArrayList<TileMap>();
	private int lastLife = -5;
	private DecimalFormat df2 = new DecimalFormat("#,###,###,##0.00");
	
	private ArrayList<Chest> chest = new ArrayList<Chest>();
	
	//if a menu is displayed we want to pause the game in GamePanel
	public static boolean menuDisplayed;
	public static boolean chestInvDisplayed;
	
	// the size in bits of the tile
	private static final int TILE_SIZE = 64;
	// Math.pow(2, TILE_SIZE_BITS) == TILE_SIZE
	private static final int TILE_SIZE_BITS = 8;

	private Image background;

	// Converts a pixel position to a tile position.
	public static int pixelsToTiles(float pixels) {
	    return pixelsToTiles(Math.round(pixels));
	}

	// Converts a pixel position to a tile position.
	public static int pixelsToTiles(int pixels) {
	    // use shifting to get correct values for negative pixels
	    //return pixels >> TILE_SIZE_BITS;
	    // or, for tile sizes that aren't a power of two,
	    // use the floor function: 
	    return (int)Math.floor((float)pixels / TILE_SIZE);
	}

	// Converts a tile position to a pixel position.
	public static int tilesToPixels(int numTiles) {
	    // no real reason to use shifting here. it's slighty faster, but doesn't add up to much
	    // on modern processors.
	    //return numTiles << TILE_SIZE_BITS;
	    // use this if the tile size isn't a power of 2:
	    return numTiles * TILE_SIZE;
	}
		
    // Sets the background to draw.
    public void setBackground(BufferedImage background) {
        this.background = background;
    }
    
    public static boolean getMenuDisplayed(){
    	return menuDisplayed;
    }
    
    public void initializeChests(TileMap map){
    	for(int x = 0; x < map.getWidth(); x++){
    		for(int y = 0; y < map.getHeight(); y++){
    			if(map.getTile(x, y) instanceof Chest){
    				map.chests().add((Chest) (map.getTile(x, y)));
    			}
    		}
    	}
    }
    
	// Returns the tile that a Sprite has collided with. Returns null if no 
	// collision was detected. The last parameter, right, is used to check if multiple blocks
	// are hit when a sprite jumps.
	public static Point getTileCollision(TileMap map, Sprite sprite, float currX, float currY, float newX, float newY) {

	    float fromX = Math.min(currX, newX);
	    float fromY = Math.min(currY, newY);
	    float toX = Math.max(currX, newX);
	    float toY = Math.max(currY, newY);
	
	    // get the tile locations
	    int fromTileX = GameRenderer.pixelsToTiles(fromX);
	    int fromTileY = GameRenderer.pixelsToTiles(fromY);
	    int toTileX = GameRenderer.pixelsToTiles(toX + sprite.getWidth() - 1);
	    int toTileY = GameRenderer.pixelsToTiles(toY + sprite.getHeight() - 1);
	
	    // check each tile for a collision
	    for (int x=fromTileX; x<=toTileX; x++) {
	        for (int y=fromTileY; y<=toTileY; y++) {
	            if (x < 0 || x >= map.getWidth() || map.getImage(x, y) != null) {
	            	Tile tile = map.getTile(x,y);
	            	if(tile != null && map.getTile(x, y).isCollidable()) {
	                // collision found and the tile is collidable, return the tile
	            		return new Point(x,y);
	            	} 
	            }
	        }
	    }
	    // no collision found, return null
	    return null;
	}
	
	/**
	 * @return A List of Points, where each Point corresponds to the location of a tile the sprite is 
	 * colliding with in map.tiles().
	 */
	public static ArrayList<ArrayList<Point>> getTileCollisionAll(TileMap map, Sprite sprite, float currX, float currY, float newX, float newY) {
		ArrayList<ArrayList<Point>> allPoints = new ArrayList<ArrayList<Point>>();
		ArrayList<Point> collisionPoints = new ArrayList<Point>();
		ArrayList<Point> floormatPoints = new ArrayList<Point>();
		ArrayList<Point> thresholdPoints = new ArrayList<Point>();
	    float fromX = Math.min(currX, newX);
	    float fromY = Math.min(currY, newY);
	    float toX = Math.max(currX, newX);
	    float toY = Math.max(currY, newY);
	
	    // get the tile locations
	    int fromTileX = GameRenderer.pixelsToTiles(fromX);
	    int fromTileY = GameRenderer.pixelsToTiles(fromY);
	    int toTileX = GameRenderer.pixelsToTiles(toX + sprite.getWidth() - 1);
	    int toTileY = GameRenderer.pixelsToTiles(toY + sprite.getHeight() - 1);
	
	    // check each tile for a collision
	    for (int x=fromTileX; x<=toTileX; x++) {
	        for (int y=fromTileY; y<=toTileY; y++) {
	            if (x < 0 || x >= map.getWidth() || map.getImage(x, y) != null) {
	            	Tile tile = map.getTile(x,y);
	            	if(tile != null && map.getTile(x, y).isCollidable()) {
	            		// collision found and the tile is collidable, return the tile
	            		collisionPoints.add(new Point(x,y));
	            	} 
            		if(tile != null && map.getTile(x, y).isFloormat()) {
		                // collision found and the tile is collidable, return the tile
            			floormatPoints.add(new Point(x,y));
		            }
            		if(tile != null && map.getTile(x, y).isThreshold()) {
		                // collision found and the tile is collidable, return the tile
            			thresholdPoints.add(new Point(x,y));
		            }
	            }
	        }
	    }
	    allPoints.add(collisionPoints);
	    allPoints.add(floormatPoints);
	    allPoints.add(thresholdPoints);
	    // no collision found, return null
	    return allPoints;
	}
	
	public static ArrayList<GameTile> getActionTiles(int x, int y, TileMap map, Sprite sprite){
		ArrayList<GameTile> actionTiles = new ArrayList<GameTile>();

		//get tile holding action point passed in
		int x1 = GameRenderer.pixelsToTiles(x);
		int y1 = GameRenderer.pixelsToTiles(y);	
		
		GameTile tile = map.getTile(x1, y1);
    	if(tile != null && map.getTile(x1, y1).isActionTile()) {
    		actionTiles.add(tile);
    		
    		//get connecting action tiles to this tile (allows for multiple blocks to act as one action tile)
    		GameTile tile1 = map.getTile(x1+1, y1);
        	if(tile1 != null && map.getTile(x1+1, y1).isActionTile()) {
        		actionTiles.add(tile1);
        	}
    		GameTile tile2 = map.getTile(x1-1, y1);
        	if(tile2 != null && map.getTile(x1-1, y1).isActionTile()) {
        		actionTiles.add(tile2);
        	}
    		GameTile tile3 = map.getTile(x1, y1+1);
        	if(tile3 != null && map.getTile(x1, y1+1).isActionTile()) {
        		actionTiles.add(tile3);
        	}
    		GameTile tile4 = map.getTile(x1, y1-1);
        	if(tile3 != null && map.getTile(x1, y1-1).isActionTile()) {
        		actionTiles.add(tile4);
        	}
    	}
    	//if none return null
		return actionTiles;
	}
	
	public static ArrayList<Creature> getNearCreatures(Sprite sprite, TileMap map){
		ArrayList<Creature> nearCreatures = new ArrayList<Creature>();
		float x = sprite.getX();
		float y = sprite.getY();
		for(int i = 0; i < map.creatures().size(); i++){
			Creature c = map.creatures().get(i);
			float lcx = (c.getX());
			float rcx = (c.getX()+c.getWidth());
			float ucy = (c.getY());
			float dcy = (c.getY()+c.getHeight());
			Player player = ((Player) sprite);
			float[] actionPoint = player.getActionPoint();
			if(actionPoint[0] > lcx && actionPoint[0] < rcx && actionPoint[1] > ucy && actionPoint[1] < dcy){
				nearCreatures.add(c);
			}
		}
		return nearCreatures;
	}
	
	 public void draw(Graphics2D g, TileMap mainMap, TileMap backgroundMap, TileMap foregroundMap, int screenWidth, int screenHeight) {
		 
	    	// add the three maps to the list of maps to draw, only mainMap is interactive
	    	maps.add(backgroundMap);
	    	maps.add(mainMap);
	    	maps.add(foregroundMap);
	        Player player = mainMap.getPlayer();
	        
	        if(player.isIndoors()){
	        	maps.remove(foregroundMap);
	        }
	        
	        int mapWidth = tilesToPixels(mainMap.getWidth());
	        int mapHeight = tilesToPixels(mainMap.getHeight());
	        
	        // get the scrolling position of the map based on player's position...
	        
	        int offsetX = screenWidth/2 - Math.round(player.getX()) - TILE_SIZE;
	        offsetX = Math.min(offsetX, 0); // if this gets set to 0, player is within a screen width
	        offsetX = Math.max(offsetX, screenWidth - mapWidth);
	        
	        int offsetY = screenHeight/2 - Math.round(player.getY()) - TILE_SIZE;
	        offsetY = Math.min(offsetY, 0); // if this gets set to 0, player is within a screen width
	        offsetY = Math.max(offsetY, screenHeight - mapHeight);
	        
	        int round = Math.round(player.getY());
	        
	        // initialize AdjustYScroll
	        if (AdjustYScroll == 0) {
	        	AdjustYScroll = round;
	        }
	        
	        //I don't think this one is necessary
	        // if the player is jumping, change the level at which the screen is drawn.
	        if(player.isAbovePlatform() || player.isOnSlopedTile()) {
	        	AdjustYScroll = round;
	        }		
	        
	        //int offsetY = screenHeight/2 - AdjustYScroll - TILE_SIZE;
	        offsetY = Math.min(offsetY, 0);
	        offsetY = Math.max(offsetY, screenHeight - mapHeight - 25); // 25 fixes the JPanel height error

	        // draw parallax background image
	        if (background != null) {
	        	// x and y are responsible for fitting the background image to the size of the map
	            int x = offsetX * (screenWidth - background.getWidth(null)) / (screenWidth - mapWidth);
	            int y = offsetY * (screenHeight - background.getHeight(null)) / (screenHeight - mapHeight);
	            g.drawImage(background, x, y, null);
	        }
	        
	        int firstTileX = pixelsToTiles(-offsetX);
	        int lastTileX = firstTileX + pixelsToTiles(screenWidth) + 1;
	        int firstTileY = pixelsToTiles(-offsetY);
	        int lastTileY = firstTileY + pixelsToTiles(screenHeight) + 1;
	        
	        for(TileMap map : maps) {
	            // draw the visible tiles
	        	if(map != null) {
	        		for (int y=firstTileY; y<= lastTileY; y++) {
	                    for (int x=firstTileX; x <= lastTileX; x++) {
	                    	GameTile tile = map.getTile(x, y);
	        	            if(tile != null) {
	        	            	tile.draw(g, tilesToPixels(x), tilesToPixels(y), 
	        	            			tile.getOffsetX() + offsetX, tile.getOffsetY() + offsetY);
	                        }
	                    }
	                }
	        	}
	        	if(map == mainMap) {
	        		for(int i = 0; i < map.creatures().size(); i++) {
		    			Creature c = map.creatures().get(i);
		                int x = Math.round(c.getX()) + offsetX;
		                int y = Math.round(c.getY()) + offsetY;
		                int tileX = pixelsToTiles(x);
		                int tileY = pixelsToTiles(y);
		                
		                if(!c.isAlive()) {
		                	map.creatures().remove(i);
		                	i--;
		                } else {
			                if(Creature.WAKE_UP_VALUE_UP_LEFT <= tileX && Creature.WAKE_UP_VALUE_DOWN_RIGHT >= tileX && 
			                		Creature.WAKE_UP_VALUE_UP_LEFT <= tileY && Creature.WAKE_UP_VALUE_DOWN_RIGHT >= tileY ) {
			                	
			                	// Only want to deal with platforms that are awake.
			                	//I don't think this line is necessary
				                //if(c instanceof Platform) { map.platforms().add((Platform) c); }
		                        // Wake up the creature the first time the sprite is in view.
			                	if(c.isSleeping()) { c.wakeUp(); }
				                
			                	c.setIsOnScreen(true);
			                	if(!c.isInvisible()) {
			                		c.draw(g, x, y); // draw the creature
			                	}
				                map.relevantCreatures().add(c);
				                
			                } else {
			                	if(c.isAlwaysRelevant()) { map.relevantCreatures().add(c); }
			                	c.setIsOnScreen(false);
			                }
		                }
	        		}
	                // Draw the player.
	                if(!(((Player) player).isInvisible())) {
	                player.draw(g, Math.round(player.getX()) + offsetX, Math.round(player.getY()) + offsetY,
	                		player.getOffsetX(), player.getOffsetY());
	                }
	        		for(int k = 0; k < map.items().size(); k++){
	        			Item i = map.items().get(k);
		                int x = Math.round(i.getX()) + offsetX;
		                int y = Math.round(i.getY()) + offsetY;
	        			i.draw(g, x, y);
	        		}
	        	}
	        	//draw menus
	        	if(map != null){
	        		chestInvDisplayed = false;
	        		int chestInvNumber = -1;
		        	if(player.getInventory().isDisplayed()){
		        		menuDisplayed = true;
		        	}
		        	else{
		        		for(int i = 0; i < map.chests().size(); i++){
		        			if(map.chests().get(i).getInventory().isDisplayed()){
		        				chestInvDisplayed = true;
		        				chestInvNumber = i;
		    	        		menuDisplayed = true;
		        				break;
		        			}
		        			else{
		        				menuDisplayed = false;	
		        			}
		        		}
		        	}
		        	if(!player.getInventory().isDisplayed() && chestInvDisplayed && chestInvNumber >= 0){
        				map.chests().get(chestInvNumber).getInventory().draw(g, 45, 17);
        				player.displayChestInv(map.chests().get(chestInvNumber), chestInvNumber);
		        	}
		        	else if(player.getInventory().isDisplayed() && !chestInvDisplayed){
		        		player.getInventory().draw(g, 45, 17);
		        	}
	        	}
	        }
	        float dd2dec = new Float(df2.format(player.getdX())).floatValue();
	        g.drawString("dx: " + dd2dec, 300, 17);
	        
	        //not sure if this is necessary yet
	        if(lastLife != player.getHealth()); {
		        lastLife = player.getHealth();
	        	Color myColor = new Color(50, 50, 50, 50);
		        g.setColor(myColor);
		        g.draw3DRect(2, 2, screenWidth - 10, 18, true);
		        g.fill3DRect(2, 2, screenWidth - 10, 18, true);
		        g.setColor(Color.BLACK);
		        int hbStart = 4;
		        int hbWidth = 35;
		        g.draw3DRect(hbStart, 4, hbWidth, 13, true);
		        g.draw3DRect(hbStart + hbWidth, 4, hbWidth, 13, true);
		        g.draw3DRect(hbStart + 2*hbWidth, 4, hbWidth, 13, true);
		        
		        g.setColor(Color.RED);
		        for(int i=0; i < player.getHealth(); i++) {
		        	g.fill3DRect(hbStart + i*hbWidth, 4, hbWidth, 13, true);
		        } 
	        }
	        maps.clear(); 
	 }
}
