package core;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.sound.midi.Sequence;
import javax.sound.sampled.AudioFormat;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import core.GameLoader;
import core.GamePanel;
import core.GameRenderer;
import core.GamePanel.GameListener;
import creatures.SecondaryCreatures;
import creatures.Zombie;
import animation.SpriteListener;
import tile.GameTile;
import tile.TileMap;
import base.Creature;
import base.Item;
import player.Player;

public class GamePanel extends JPanel implements Runnable {

	private int panelWidth;
	private int panelHeight;
	private GameFrame frame;
	
	private Graphics dbg;
	private Image dbImage = null;

	private boolean running = false; 
	private boolean gameOver = false;
	private boolean gameFreeze = false;
	
	boolean gameInitialized = false;
	boolean gamePanelDisplayed;
	
	private Thread animator;
	private int period = 20; 
	
	private Player player;
	private TileMap[] maps = new TileMap[2];
	private TileMap map;
	private TileMap backgroundMap;
	private TileMap foregroundMap;
	public GameRenderer renderer;
	public GameLoader manager;
	public CreatureManager creatureManager;
	public ItemManager itemManager;
	public SecondaryCreatures secondaryCreatures;
	
	public GamePanel(int w, int h, GameFrame frame) {
		
		this.panelWidth = w;
		this.panelHeight = h;
		this.frame = frame;
		
 		secondaryCreatures = new SecondaryCreatures();
		
		try {
			manager = new GameLoader();
			renderer = new GameRenderer();
			creatureManager = new CreatureManager();
			itemManager = new ItemManager();
			renderer.setBackground(ImageIO.read(new File("background2/background2.png")));
			///////////////////////////////////////////////////////////////////////////////
			map = manager.loadMap("maps/newMap.txt"); // use the ResourceManager to load the game map
			player = new Player(map.playerX, map.playerY);
			map.setPlayer(player); // set the games main player to player
			this.addKeyListener(new SpriteListener(player));
			///////////////////////////////////////////////////////////////////////////////
		} catch (IOException e){
			System.out.println("Invalid Map.");
		}
		
		Sequence sequence;
		Random r = new Random();
		int rNum = r.nextInt(4);
		if(rNum == 0) {
			//A bunch of midi stuff went here
		}
		
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		//this.addKeyListener(new SpriteListener(player));
		this.addKeyListener(new GameListener());
		this.setFocusable(true); 
	}
	
	public void loadMaps(){
		System.out.println("loading maps");
		//this reinitializes all the stuff that is initialized in GamePanel() but doesn't take as long.
		this.maps = manager.loadMap2("./customMaps/currentMap.map");
		this.map = maps[0];
		this.foregroundMap = maps[1];
		player = new Player(map.playerX, map.playerY);
		map.setPlayer(player); // set the games main player to player
		this.addKeyListener(new SpriteListener(player));
	}
	
	public void setPanelDisplayed(boolean x){
		this.gamePanelDisplayed = x;
	}
	
	/**
	 * Automatically called as GamePanel is being added to its enclosing GUI component,
	 * and so is a good place to initiate the animation thread.
	 */
	public void addNotify() {
		super.addNotify(); // creates the peer
		//startGame(); // start the thread
	}
	
	/**
	 * Start the game thread.
	 */
	void startGame() {
		if(animator == null || !running) {
			//loadMaps();
			animator = new Thread(this, "The Animator V 3.0");
			animator.start();
		}
	}
	
	/**
	 * Stop the game.
	 */
	public void stopGame() { running = false; }
	
	/**
	 * Defines a single game loop.
	 */
	public void gameAction() {
		gameUpdate(); // Update game state.
		gameRender(); // Draw to the double buffer.
		paintScreen(); // Draw double buffer to screen.
	}
	
	/**
	 * The main game loop - repeatedly update, repaint, sleep.
	 */
	public void run() {
		running = true;
		while(running) {
			if(gamePanelDisplayed){
				if(!gameFreeze) {
					//System.out.println("game running");
					gameAction();
				}
				
				try {
					Thread.sleep(period);
				} catch(InterruptedException ex){}
			}
		}
		System.exit(0); // so enclosing JFrame/JApplet exits\
	}
	
	/**
	 * Update the state of all game objects. In the future this game logic
	 * should probably be abstracted out of this class.
	 */
	private void gameUpdate() {
		if (!gameOver) {
			if(gameInitialized != true){
			creatureManager.initializeCreatures(map);
			itemManager.initializeItems(map);
			renderer.initializeChests(map);
			player.setDamageLevel();
			gameInitialized = true;
			}
			// Update all relevant Creatures.
			for(int i = 0; i < map.relevantCreatures().size(); i++) {
				Creature c = map.relevantCreatures().get(i);
				//Structure s = map.structures().get(i);
				/*if(!(c instanceof Coin)) {
					c.updateCreature(map, period);
					player.playerCollision(map, c);
					for(Creature other : map.relevantCreatures()) {
						c.creatureCollision(other);
					}
				} else {*/
		//			c.updateCreature(map, period);
		//			player.playerCollision(map, c);
				}
			if(!renderer.getMenuDisplayed()){
				creatureManager.updateCreatures(map, player);
				for(int j = 0; j < map.items().size(); j++){
					Item i = map.items().get(j);
					player.playerCollision(map, i);
				}
			}
		}
			for(GameTile tile : map.animatedTiles()) {
	            tile.collidingCreatures().clear();  // clear the colliding sprites on the tile
	            tile.update(20);
			}
        
			// Add creatures that need to be created. They are added here to avoid concurrent modifcation errors.
            for(Creature c : map.creaturesToAdd()) {
            	map.creatures().add(c);
            }
            
            map.creaturesToAdd().clear(); // This line MUST be called BEFORE mario.update(). Why?
            							  // If it is called after, all the creatures that are created
            							  // as a result of mario colliding are not added next update because
            							  // they are cleared immediately afterwards.
            if(!renderer.getMenuDisplayed()){
				player.update(map, period);
            }
			map.relevantCreatures().clear();
			map.platforms().clear();
			secondaryCreatures.update(map);
	}
	
	/**
	 * Draws the game image to the buffer.
	 */
	private void gameRender() {
		if(dbImage == null) {
			dbImage = createImage(this.panelWidth, this.panelHeight);
			return;
		}
	    dbg = dbImage.getGraphics();
		renderer.draw((Graphics2D) dbg, map, backgroundMap, foregroundMap, panelWidth, panelHeight);
	}
	
	/**
	 * Draws the game image to the screen by drawing the buffer.
	 */
	private void paintScreen() {	
		Graphics g;
		try {
			g = this.getGraphics();
			if ((g != null) && (dbImage != null))  {
				g.drawImage(dbImage, 0, 0, null);
				g.dispose();
			} 
		} catch (Exception e) { System.out.println("Graphics context error: " + e); }
	}
	
	/**
	 * Adds debugging features so it is possible to single step a game loop one by one.
	 * 'Z' pauses the game.
	 * 'X' resumes the game.
	 * '1' runs a single game loop if the game if paused.
	 * 'L' runs a single game loop if pressed and continously runs the game loop if held.
	 */
	class GameListener extends KeyAdapter {
		
	    public void keyReleased(KeyEvent e) {
	    	int key = e.getKeyCode();
			
	    	// 'P' is pressed.
	    	//pause if unpaused, unpause if pause - should only work when no menu is displayed
	        if (key == KeyEvent.VK_P) { // pause
	        	if(GamePanel.this.gameFreeze == false && !renderer.getMenuDisplayed()) {
		        	GamePanel.this.gameFreeze = true;
	        	}
	        	else if(!renderer.getMenuDisplayed()){ //resume
	        		GamePanel.this.gameFreeze = false;
	        	}
	        }

	        
	        // '1' is pressed.
	        if (key == KeyEvent.VK_1) {
	        	if(GamePanel.this.gameFreeze == true) {
	        		System.out.println();
	        		System.out.println("Game Update (1) Starting...");
	        		GamePanel.this.gameAction();
	        		System.out.println();
	        		System.out.println("Game Update (1) Completed.");
	        	}
	        }

	    } 
	    
	    // 'L' is pressed or held.
	    public void keyPressed(KeyEvent e) {
	    	int key = e.getKeyCode();
	    	if (key == KeyEvent.VK_L) {
	    		GamePanel.this.gameAction();
	    	}
	    	if (key == KeyEvent.VK_ESCAPE) {
	    		frame.setPanelDisplayed("menu");
	    	}

	    }
		
	}

}
