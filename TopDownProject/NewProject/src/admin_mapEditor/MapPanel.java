package admin_mapEditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import base.Creature;
import base.Item;
import core.GameRenderer;
import tile.ActionTile;
import tile.Chest;
import tile.Door;
import tile.GameTile;
import tile.Tile;
import tile.TileMap;
import tile.TileMapSection;

public class MapPanel extends JPanel implements Runnable{
	
	public MapEditor mapEditor;
	
	public TileMap currentMap;
	public TileMap tileMap;
	public TileMap foregroundMap;
	
	public JDialog dialog;
	
	public TileMap[] maps = new TileMap[2];
	
	private Thread thread;
	public boolean running;
	public boolean dragging;
	public boolean mapLoaded;
	
	public boolean playerLocSelected;
	public boolean playerLocSet;
	public boolean mapEdited = false;
	
	public boolean foregroundViewSet;
	
	public int colNum, rowNum;
	public int width, height;
	
	public int tileGridSize;
	public int tileSize;
	public GameTile[][] currentTiles;
	public GameTile[][] tiles;
	public GameTile[][] foregroundTiles;
	
	public TileMapSection[][] tileSections;
	public TileMapSection[][] foregroundSections;
	public int sectionWidth;
	public int sectionHeight;
	
	//Not in use but the idea is to use this to link multiple maps together -- like [][]
	public int mapIndexX;
	public int mapIndexY;
	
	public JButton ok, cancel;
	
	public JFormattedTextField colNumField;
	public JFormattedTextField rowNumField;
	
	public NumberFormat integerFieldFormatter;
	
	public MapEditorActionListener mapEditorActionListener;
	
	public boolean selected;
	public ArrayList<GameTile> selectedTiles = new ArrayList<GameTile>();
	
	public BufferedImage playerImg;
	
	//for bucket command
	ArrayList<GameTile> affectedTiles = new ArrayList<GameTile>();
	
	public MapPanel(MapEditor mapEditor){
		this.mapEditor = mapEditor;
		setPreferredSize(new Dimension(900, 220));
		setBackground(Color.WHITE);
		mapEditorActionListener = new MapEditorActionListener(this.mapEditor);
		try {	//for player location indicator
			playerImg = ImageIO.read(new File("./player/player_facing_down.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		addMouseListener(new MapPanelMouseListener(this));
		currentTiles = tiles;
		thread = new Thread(this, "menuAnimator");
		thread.start();
	}

	public void setTileGridSize(int size){
		this.tileGridSize = size;
	}
	
	public void newMapDialog(){
		dialog = new JDialog();
		dialog.setSize(200, 165);
		dialog.setResizable(false);
		dialog.setTitle("New Map Options");
		dialog.setLocationRelativeTo(mapEditor.adminPanel.frame);
		JPanel newMapDialog = buildNewMapDialog();
		dialog.add(newMapDialog);
		dialog.setVisible(true);
	}
	
	public void newMap(){
		System.out.println("Creating New Map...");
		colNum = Integer.parseInt(colNumField.getValue().toString());
		rowNum = Integer.parseInt(rowNumField.getValue().toString());
		tiles = new GameTile[colNum][rowNum];
		foregroundTiles = new GameTile[colNum][rowNum];
		tileMap = new TileMap(colNum, rowNum, tiles);
		foregroundMap = new TileMap(colNum, rowNum, foregroundTiles);
		tileSections = tileMap.getSections();
		sectionWidth = tileMap.sectionWidth;
		sectionHeight = tileMap.sectionHeight;
		foregroundSections = foregroundMap.getSections();
		currentMap = tileMap;
		currentTiles = tiles;
		mapEditor.adminPanel.saveMap.setEnabled(true);
		System.out.println("New Map Created");
	}
	
	public JPanel buildNewMapDialog(){
		JPanel newMapDialog = new JPanel();
		newMapDialog.setLayout(new GridLayout(6, 2));
		setBackground(Color.WHITE);
		JLabel colNumPrompt = new JLabel("Width in Tiles: ");
		JLabel rowNumPrompt = new JLabel("Height in Tiles: ");
		
		integerFieldFormatter = NumberFormat.getIntegerInstance();
		integerFieldFormatter.setMaximumFractionDigits(0);
		
		colNumField = new JFormattedTextField(integerFieldFormatter);
		rowNumField = new JFormattedTextField(integerFieldFormatter);
		
		ok = new JButton("Ok");
		cancel = new JButton("Cancel");
		ok.addActionListener(mapEditorActionListener);
		cancel.addActionListener(mapEditorActionListener);
		newMapDialog.add(colNumPrompt);
		newMapDialog.add(colNumField);
		newMapDialog.add(rowNumPrompt);
		newMapDialog.add(rowNumField);
		newMapDialog.add(ok);
		newMapDialog.add(cancel);
		return newMapDialog;
	}
	
	public int[] pointerToTilePos(){
		Point p = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(p, this);
		int pX = (int) p.getX();
		int pY = (int) p.getY();
		int[] returnTilePos = new int[2];
		returnTilePos[0] = -1;
		returnTilePos[1] = -1;
		if(currentTiles != null){
			if(currentTiles.length > 0 && (mapEditor.tilePanel.selectedTiles.size() == 1 || mapEditor.toolPanel.selectorRB.isSelected())){
				//get position of mouse pointer relative to grid.
				for(int j = 0; j < colNum; j++){
					for(int k = 0; k < rowNum; k++){
						if(pX > tileSize*j && pX < (tileSize*j)+tileSize){
							if(pY > tileSize*k && pY < (tileSize*k)+tileSize){
								if(mapEditor.tilePanel.getSelectedTile() != null || mapEditor.toolPanel.selectorRB.isSelected()){
									returnTilePos[0] = j;
									returnTilePos[1] = k;
								}
							}
						}
					}
				}
			}
		}
		return returnTilePos;
	}
	
	public void addItem(){
		Item selectedItem = mapEditor.propertiesPanel.selectedItem;
		Item newItem = mapEditor.adminPanel.frame.gamePanel.itemManager.newClonedItem(selectedItem);
		Point p = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(p, this);
		int pX = (int) p.getX();
		int pY = (int) p.getY();
		newItem.setX(pX-(newItem.getWidth()/2));
		newItem.setY(pY-(newItem.getHeight()/2));
		newItem.setSelected(false);
		currentMap.items().add(newItem);
	}
	
	public void addCreature(){
		Creature selectedCreature = mapEditor.propertiesPanel.selectedCreature;
		Creature newCreature = mapEditor.adminPanel.frame.gamePanel.creatureManager.newClonedCreature(selectedCreature);
		Point p = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(p, this);
		int pX = (int) p.getX();
		int pY = (int) p.getY();
		newCreature.setX(pX-(newCreature.getWidth()/2));
		newCreature.setY(pY-(newCreature.getHeight()/2));
		newCreature.setSelected(false);
		currentMap.creatures().add(newCreature);
	}
	
	public void removeItem(){
		Point p = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(p, this);
		int pX = (int) p.getX();
		int pY = (int) p.getY();
		for(int i = 0; i < currentMap.items().size(); i++){
			Item item = currentMap.items().get(i);
			if(item.getX() < pX && (item.getX()+item.getWidth()) > pX){
				if(item.getY() < pY && (item.getY()+item.getHeight()) > pY){
					currentMap.items().remove(item);
				}
			}
		}
	}
	
	public void removeCreature(){
		Point p = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(p, this);
		int pX = (int) p.getX();
		int pY = (int) p.getY();
		for(int i = 0; i < currentMap.creatures().size(); i++){
			Creature creature = currentMap.creatures().get(i);
			if(creature.getX() < pX && (creature.getX()+creature.getWidth()) > pX){
				if(creature.getY() < pY && (creature.getY()+creature.getHeight()) > pY){
					currentMap.creatures().remove(creature);
				}
			}
		}
	}
	
	public void drawTilesDrag(){
		int tilePos[] = pointerToTilePos();
		if(tilePos[0] >= 0 && tilePos[1] >= 0){
			int pixelX = GameRenderer.tilesToPixels(tilePos[0]);
			int pixelY = GameRenderer.tilesToPixels(tilePos[1]);
			if(mapEditor.tilePanel.getSelectedTile() != null){
				mapEdited = true;
				if(mapEditor.toolPanel.selectedTile.isActionTile() && !mapEditor.toolPanel.selectedTile.isDoor() && !mapEditor.toolPanel.selectedTile.isChest()){
					currentTiles[tilePos[0]][tilePos[1]] = new ActionTile(pixelX, pixelY, mapEditor.toolPanel.selectedTile.getImage(), mapEditor.toolPanel.selectedTile.getID());
					currentTiles[tilePos[0]][tilePos[1]].setIsCollidable(mapEditor.toolPanel.selectedTile.isCollidable());
				}
				else if(mapEditor.toolPanel.selectedTile.isDoor() && mapEditor.toolPanel.selectedTile.getSecondaryTile().getImage() != null){
					currentTiles[tilePos[0]][tilePos[1]] = new Door(pixelX, pixelY, mapEditor.toolPanel.selectedTile.getImage(), mapEditor.toolPanel.selectedTile.getSecondaryTile().getImage(), mapEditor.toolPanel.selectedTile.getID());
					currentTiles[tilePos[0]][tilePos[1]].setIsDoor(mapEditor.toolPanel.selectedTile.isDoor());
					currentTiles[tilePos[0]][tilePos[1]].setIsCollidable(mapEditor.toolPanel.selectedTile.isCollidable());
				}
				else if(mapEditor.toolPanel.selectedTile.isChest()){
					currentTiles[tilePos[0]][tilePos[1]] = new Chest(pixelX, pixelY, mapEditor.toolPanel.selectedTile.getImage(), mapEditor.toolPanel.selectedTile.getID(), 0);
					currentTiles[tilePos[0]][tilePos[1]].setIsChest(mapEditor.toolPanel.selectedTile.isChest());
					currentTiles[tilePos[0]][tilePos[1]].setIsActionTile(mapEditor.toolPanel.selectedTile.isActionTile());
					currentTiles[tilePos[0]][tilePos[1]].setIsCollidable(mapEditor.toolPanel.selectedTile.isCollidable());
				}
				else {
					currentTiles[tilePos[0]][tilePos[1]] = new GameTile(pixelX, pixelY, mapEditor.tilePanel.getSelectedTile().getImage());
					//set attributes
					currentTiles[tilePos[0]][tilePos[1]].setIsCollidable(mapEditor.toolPanel.selectedTile.isCollidable());
					currentTiles[tilePos[0]][tilePos[1]].setIsActionTile(mapEditor.toolPanel.selectedTile.isActionTile());
					currentTiles[tilePos[0]][tilePos[1]].setIsFloormat(mapEditor.toolPanel.selectedTile.isFloormat());
					currentTiles[tilePos[0]][tilePos[1]].setIsThreshold(mapEditor.toolPanel.selectedTile.isThreshold());
					currentTiles[tilePos[0]][tilePos[1]].setIsDoor(mapEditor.toolPanel.selectedTile.isDoor());
					currentTiles[tilePos[0]][tilePos[1]].setIsChest(mapEditor.toolPanel.selectedTile.isChest());
					currentTiles[tilePos[0]][tilePos[1]].setID(mapEditor.toolPanel.selectedTile.getID());
				}
			}
		}
	}
	
	public void deselectAllTiles(){
		for(int i = 0; i < selectedTiles.size(); i++){
			selectedTiles.get(i).deselect();
		}
		for(int i = 0; i < selectedTiles.size(); i++){
			selectedTiles.remove(i);
		}
	}
	
	public void bucketCommand(int[] initialTilePos){
		bucketCommand(initialTilePos, false);
	}
	
	public void bucketCommand(int[] initialTilePos, boolean firstLevel){
		if(currentTiles != null){
			GameTile selectedTile = mapEditor.tilePanel.selectedTiles.get(0);
			GameTile initialTile = null;
			if(mapEditor.tilePanel.selectedTiles.size() > 0){
				if(currentTiles.length > 0 && mapEditor.tilePanel.selectedTiles.size() == 1){
					//don't want to use actual bucket function for blank maps in order to avoid stack overflow
					if(mapEdited){
						if(firstLevel || (initialTilePos[0] < 0 && initialTilePos[1] < 0)){
							affectedTiles = new ArrayList<GameTile>();
							initialTilePos = pointerToTilePos();
							int [] mapEditorIndex = {initialTilePos[0],initialTilePos[1]};
							if(currentTiles[initialTilePos[0]][initialTilePos[1]] != null){
								currentTiles[initialTilePos[0]][initialTilePos[1]].setMapEditorIndex(mapEditorIndex);
							}
							else{
								int pixelX = GameRenderer.tilesToPixels(initialTilePos[0]);
								int pixelY = GameRenderer.tilesToPixels(initialTilePos[1]);
								currentTiles[initialTilePos[0]][initialTilePos[1]] = new GameTile(pixelX, pixelY, null);
								currentTiles[initialTilePos[0]][initialTilePos[1]].setMapEditorIndex(mapEditorIndex);
							}
						}
						int initialTileX = initialTilePos[0];
						int initialTileY = initialTilePos[1];
						initialTile = currentTiles[initialTileX][initialTileY];
						affectedTiles.add(initialTile);
						initialTile.justAdded();
						//tiles with images
						if(initialTile != null && initialTile.getImage() != null){
							if(initialTileX+1 < colNum){
								//right
								if(currentTiles[initialTileX+1][initialTileY] != null && currentTiles[initialTileX+1][initialTileY].getImage() == initialTile.getImage() && !tiles[initialTileX+1][initialTileY].justAdded){
									int [] mapEditorIndex = {initialTileX+1,initialTileY};
									currentTiles[initialTileX+1][initialTileY].setMapEditorIndex(mapEditorIndex);
									bucketCommand(mapEditorIndex);
								}
							}
							if(initialTileX-1 >= 0){
								//left
								if(currentTiles[initialTileX-1][initialTileY] != null && currentTiles[initialTileX-1][initialTileY].getImage() == initialTile.getImage() && !tiles[initialTileX-1][initialTileY].justAdded){
									int [] mapEditorIndex = {initialTileX-1,initialTileY};
									currentTiles[initialTileX-1][initialTileY].setMapEditorIndex(mapEditorIndex);
									bucketCommand(mapEditorIndex);
								}
							}
							if(initialTileY+1 < rowNum){
								//down
								if(currentTiles[initialTileX][initialTileY+1] != null && currentTiles[initialTileX][initialTileY+1].getImage() == initialTile.getImage() && !tiles[initialTileX][initialTileY+1].justAdded){
									int [] mapEditorIndex = {initialTileX,initialTileY+1};
									currentTiles[initialTileX][initialTileY+1].setMapEditorIndex(mapEditorIndex);
									bucketCommand(mapEditorIndex);
								}
							}
							if(initialTileY-1 >= 0){
								//up
								if(currentTiles[initialTileX][initialTileY-1] != null && currentTiles[initialTileX][initialTileY-1].getImage() == initialTile.getImage() && !tiles[initialTileX][initialTileY-1].justAdded){
									int [] mapEditorIndex = {initialTileX,initialTileY-1};
									currentTiles[initialTileX][initialTileY-1].setMapEditorIndex(mapEditorIndex);
									bucketCommand(mapEditorIndex);
								}
							} 
						}
						else {
							//blank tiles
							if(initialTileX+1 < colNum){
								//right
								if(currentTiles[initialTileX+1][initialTileY] == null){// && tiles[initialTileX+1][initialTileY].getImage() == null && !tiles[initialTileX+1][initialTileY].justAdded){
									int [] mapEditorIndex = {initialTileX+1,initialTileY};
									int pixelX = GameRenderer.tilesToPixels(initialTileX+1);
									int pixelY = GameRenderer.tilesToPixels(initialTileY);
									currentTiles[initialTileX+1][initialTileY] = new GameTile(pixelX, pixelY, null);
									currentTiles[initialTileX+1][initialTileY].setMapEditorIndex(mapEditorIndex);
									bucketCommand(mapEditorIndex);
								}
							}
							if(initialTileX-1 >= 0){
								//left
								if(currentTiles[initialTileX-1][initialTileY] == null){// && tiles[initialTileX-1][initialTileY].getImage() == null && !tiles[initialTileX-1][initialTileY].justAdded){
									int [] mapEditorIndex = {initialTileX-1,initialTileY};
									int pixelX = GameRenderer.tilesToPixels(initialTileX-1);
									int pixelY = GameRenderer.tilesToPixels(initialTileY);
									currentTiles[initialTileX-1][initialTileY] = new GameTile(pixelX, pixelY, null);
									currentTiles[initialTileX-1][initialTileY].setMapEditorIndex(mapEditorIndex);
									bucketCommand(mapEditorIndex);
								}
							}
							if(initialTileY+1 < rowNum){
								//down
								if(currentTiles[initialTileX][initialTileY+1] == null){// && tiles[initialTileX][initialTileY+1].getImage() == null && !tiles[initialTileX][initialTileY+1].justAdded){
									int [] mapEditorIndex = {initialTileX,initialTileY+1};
									int pixelX = GameRenderer.tilesToPixels(initialTileX);
									int pixelY = GameRenderer.tilesToPixels(initialTileY+1);
									currentTiles[initialTileX][initialTileY+1] = new GameTile(pixelX, pixelY, null);
									currentTiles[initialTileX][initialTileY+1].setMapEditorIndex(mapEditorIndex);
									bucketCommand(mapEditorIndex);
								}
							}
							if(initialTileY-1 >= 0){
								//up
								if(currentTiles[initialTileX][initialTileY-1] == null){// && tiles[initialTileX][initialTileY-1].getImage() == null && !tiles[initialTileX][initialTileY-1].justAdded){
									int [] mapEditorIndex = {initialTileX,initialTileY-1};
									int pixelX = GameRenderer.tilesToPixels(initialTileX);
									int pixelY = GameRenderer.tilesToPixels(initialTileY-1);
									currentTiles[initialTileX][initialTileY-1] = new GameTile(pixelX, pixelY, null);
									currentTiles[initialTileX][initialTileY-1].setMapEditorIndex(mapEditorIndex);
									bucketCommand(mapEditorIndex);
								}
							}
						}
						for(int i = 0; i < affectedTiles.size(); i++){
							int [] index = affectedTiles.get(i).getMapEditorIndex();
							if(index != null){
								int x = index[0];
								int y = index[1];
								int pixelX = GameRenderer.tilesToPixels(x);
								int pixelY = GameRenderer.tilesToPixels(y);
								if(mapEditor.toolPanel.selectedTile.isActionTile() && !mapEditor.toolPanel.selectedTile.isDoor() && !mapEditor.toolPanel.selectedTile.isChest()){
									currentTiles[x][y] = new ActionTile(pixelX, pixelY, selectedTile.getImage(), mapEditor.toolPanel.selectedTile.getID());
								}
								//else if(mapEditor.toolPanel.selectedTile.isDoor()){
									//currentTiles[x][y] = new Door(pixelX, pixelY, selectedTile.getImage(), mapEditor.toolPanel.selectedTile.getID());
								//}
								else if(mapEditor.toolPanel.selectedTile.isChest()){
									currentTiles[x][y] = new Chest(pixelX, pixelY, selectedTile.getImage(), mapEditor.toolPanel.selectedTile.getID(), 0);
									currentTiles[x][y].setIsActionTile(mapEditor.toolPanel.selectedTile.isActionTile());
								}
								else {
									currentTiles[x][y] = new GameTile(pixelX, pixelY, selectedTile.getImage());
									//set attributes
									currentTiles[x][y].setIsCollidable(mapEditor.toolPanel.selectedTile.isCollidable());
									currentTiles[x][y].setIsActionTile(mapEditor.toolPanel.selectedTile.isActionTile());
									currentTiles[x][y].setIsFloormat(mapEditor.toolPanel.selectedTile.isFloormat());
									currentTiles[x][y].setIsThreshold(mapEditor.toolPanel.selectedTile.isThreshold());
									currentTiles[x][y].setIsDoor(mapEditor.toolPanel.selectedTile.isDoor());
									currentTiles[x][y].setIsChest(mapEditor.toolPanel.selectedTile.isChest());
									currentTiles[x][y].setID(mapEditor.toolPanel.selectedTile.getID());
									currentTiles[x][y].setJustAdded(false);
								}
							}
						}
					}
					else if(!mapEdited){
						for(int x = 0; x < colNum; x++){
							for(int y = 0; y < rowNum; y++){
								int pixelX = GameRenderer.tilesToPixels(x);
								int pixelY = GameRenderer.tilesToPixels(y);
								if(mapEditor.toolPanel.selectedTile.isActionTile() && !mapEditor.toolPanel.selectedTile.isDoor() && !mapEditor.toolPanel.selectedTile.isChest()){
									currentTiles[x][y] = new ActionTile(pixelX, pixelY, selectedTile.getImage(), mapEditor.toolPanel.selectedTile.getID());
								}
								//else if(mapEditor.toolPanel.selectedTile.isDoor()){
									//currentTiles[x][y] = new Door(pixelX, pixelY, selectedTile.getImage(), mapEditor.toolPanel.selectedTile.getID());
								//}
								else if(mapEditor.toolPanel.selectedTile.isChest()){
									currentTiles[x][y] = new Chest(pixelX, pixelY, selectedTile.getImage(), mapEditor.toolPanel.selectedTile.getID(), 0);
									currentTiles[x][y].setIsActionTile(mapEditor.toolPanel.selectedTile.isActionTile());
								}
								else {
									currentTiles[x][y] = new GameTile(pixelX, pixelY, selectedTile.getImage());
									//set attributes
									currentTiles[x][y].setIsCollidable(mapEditor.toolPanel.selectedTile.isCollidable());
									currentTiles[x][y].setIsActionTile(mapEditor.toolPanel.selectedTile.isActionTile());
									currentTiles[x][y].setIsFloormat(mapEditor.toolPanel.selectedTile.isFloormat());
									currentTiles[x][y].setIsThreshold(mapEditor.toolPanel.selectedTile.isThreshold());
									currentTiles[x][y].setIsDoor(mapEditor.toolPanel.selectedTile.isDoor());
									currentTiles[x][y].setIsChest(mapEditor.toolPanel.selectedTile.isChest());
									currentTiles[x][y].setID(mapEditor.toolPanel.selectedTile.getID());
									currentTiles[x][y].setJustAdded(false);
								}
							}
						}
						mapEdited = true;
					}
				}
			}
		}
	}
	
	public void paint(Graphics g){
		super.paint(g);
		g.setColor(Color.BLUE);
		if(rowNum > 0 && colNum > 0 && (mapEditor.tilePanel.tilesImported || mapEditor.tilePanel.tilesLoaded || mapLoaded)){
			if(mapEditor.tilePanel.tilesImported || mapEditor.tilePanel.tilesLoaded){
				tileSize = mapEditor.tilePanel.getTileWidth();	
			}
			else{	//map was loaded but no tiles imported
				if(mapLoaded){tileSize = tiles[0][0].getWidth();}
			}
			this.setPreferredSize(new Dimension(colNum*tileSize, rowNum*tileSize));
			//draw vertical lines first
			g.setColor(Color.BLACK);
			for(int i = 0; i < colNum; i++){
				g.drawLine(tileSize*(i+1), 0, tileSize*(i+1), tileSize*rowNum);
			}
			//draw horizontal lines
			for(int i = 0; i < rowNum; i++){
				g.drawLine(0, tileSize*(i+1), tileSize*colNum,tileSize*(i+1));
			}
			for(int x = 0; x < colNum; x++){
				for(int y = 0; y < rowNum; y++){
					if(tiles[x][y] != null){
						//selection rectangle
						int rectThickness = 1;
						Graphics2D g2d = (Graphics2D) g;
						g2d.setStroke(new BasicStroke(rectThickness));
						g2d.setColor(Color.BLUE);
						g.drawImage(tiles[x][y].getImage(), (x*tileSize),(y*tileSize), this);
						if(tiles[x][y].isSelected()){
							int width = tiles[x][y].getWidth();
							int height = tiles[x][y].getHeight();
							int x1 = x*width;
							int y1 = y*height;
							g2d.drawRect(x1 -1, y1 - 1, width, height);
						}
					}
					if(foregroundTiles[x][y] != null && foregroundViewSet){
						//selection rectangle
						int rectThickness = 1;
						Graphics2D g2d = (Graphics2D) g;
						g2d.setStroke(new BasicStroke(rectThickness));
						g2d.setColor(Color.BLUE);
						g.drawImage(foregroundTiles[x][y].getImage(), (x*tileSize),(y*tileSize), this);
					}
				}
			}
			for(int i = 0; i < currentMap.items().size(); i++){
				Item item = currentMap.items().get(i);
				int x = (int) currentMap.items().get(i).getX();
				int y = (int) currentMap.items().get(i).getY();
				item.draw(g, x, y);
			}
			for(int i = 0; i < currentMap.creatures().size(); i++){
				Creature creature = currentMap.creatures().get(i);
				int x = (int) currentMap.creatures().get(i).getX();
				int y = (int) currentMap.creatures().get(i).getY();
				creature.draw(g, x, y);
			}
			if(playerLocSet){
				g.drawImage(playerImg, currentMap.playerX, currentMap.playerY, this);
			}
		}
	}

	public void run() {
		this.running = true;
		while(running){
			if(dragging && currentTiles != null && !mapEditor.toolPanel.bucketRB.isSelected() && mapEditor.tilePanel.getSelectedTile() != null){
				drawTilesDrag();
			}
			repaint();
			revalidate();
			this.width = this.getWidth();
			this.height = this.getHeight();
		}
	}
	
	public void saveMap(){
		if(tiles != null & tiles.length > 0){
			
			//build out each section
			//loop through section
			for(int i = 0; i < tileSections.length; i++){
				for(int j = 0; j < tileSections[0].length; j++){
					tileSections[i][j] = new TileMapSection();
					//for each section loop through section width/height
					GameTile[][] sectionTiles = new GameTile[sectionWidth][sectionHeight];
					int xCount = 0;
					for(int k = (i*sectionWidth); k < ((i+1)*sectionWidth); k++){
						int yCount = 0;
						for(int l = (j*sectionHeight); l < ((j+1)*sectionHeight); l++){
							sectionTiles[xCount][yCount] = tiles[k][l];
							yCount++;
						}
						xCount++;
					}
					tileSections[i][j].tiles = sectionTiles;
				}
			}
			
			//before we can save clean up some stuff
			deselectAllTiles();
			//removing this functionality in preference of using tileSectiond instead of entire map.
			/*
			 * Save Single Map
			 */
			maps[0] = tileMap;
			maps[1] = foregroundMap;
			mapEditor.fileChooser.setCurrentDirectory(new java.io.File("./customMaps"));
        	mapEditor.fileChooser.setSelectedFile(new File("currentMap.map"));
        	int returnVal = mapEditor.fileChooser.showSaveDialog(this);
        	if(returnVal == JFileChooser.APPROVE_OPTION){
        		//message
	        	System.out.println("Saving...");
        		try {
		    		FileOutputStream fos = new FileOutputStream(mapEditor.fileChooser.getSelectedFile());
		            ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(maps);
					System.out.println("Save Successful");
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Save Failed");
				}
        	}
        	
			/*
			 * Save Sections
			 */
        	for(int i = 0; i < tileSections.length; i++){
        		for(int j = 0; j < tileSections[0].length; j++){
        			File sectionFile = new File("./tileSections/section["+i+"]["+j+"].tileSection");
        			try {
						FileOutputStream fos = new FileOutputStream(sectionFile);
						ObjectOutputStream oos = new ObjectOutputStream(fos);
						oos.writeObject(tileSections[i][j]);	
						System.out.println("Tile Section Saved");					
					} catch (IOException e) {
						e.printStackTrace();
						System.out.println("Tile Section ["+i+"]["+j+"] Saved");
					}
        		}
        	}
        	for(int i = 0; i < foregroundSections.length; i++){
        		for(int j = 0; j < foregroundSections[0].length; j++){
        			File sectionFile = new File("./tileSections/foregroundSection["+i+"]["+j+"].tileSection");
        			try {
						FileOutputStream fos = new FileOutputStream(sectionFile);
						ObjectOutputStream oos = new ObjectOutputStream(fos);
						oos.writeObject(foregroundSections[i][j]);	
						System.out.println("Foreground Section Saved");					
					} catch (IOException e) {
						e.printStackTrace();
						System.out.println("Foreground Section ["+i+"]["+j+"] Saved");
					}
        		}
        	}
        	System.out.println("Saving Complete");
        	
		}
	}
	
	public void loadMap(){
		//pick file
        mapEditor.fileChooser.setCurrentDirectory(new java.io.File("./customMaps"));
		int returnVal = mapEditor.fileChooser.showOpenDialog(this);
		File file = mapEditor.fileChooser.getSelectedFile();
		//read file
		try{
			if(file != null){
				System.out.println("Loading Map...");
				FileInputStream fis= new FileInputStream(file);
				ObjectInputStream ois= new ObjectInputStream(fis);
				maps = (TileMap[]) ois.readObject();
				tileMap = maps[0];
				this.tileSections = tileMap.getSections();
				this.sectionWidth = tileMap.sectionWidth;
				this.sectionHeight = tileMap.sectionHeight;
				foregroundMap = maps[1];
				this.foregroundSections = foregroundMap.getSections();
				currentMap = tileMap;
				this.tiles = tileMap.getTiles();
				this.foregroundTiles = foregroundMap.getTiles();
				this.currentTiles = tiles;
				foregroundViewSet = false;
				this.colNum = tiles.length;
				this.rowNum = tiles[0].length;
				mapEditor.adminPanel.saveMap.setEnabled(true);
				mapLoaded = true;
				if(tileMap.playerX > 0 && tileMap.playerY > 0){
					playerLocSet = true;
				}
				for(int i = 0; i < currentMap.items().size(); i++){
					currentMap.items().get(i).setAnimation(currentMap.items().get(i).getDefaultAnim());
				}
				System.out.println("Load Successful");
			}
		}
		catch(IOException | ClassNotFoundException e){
			e.printStackTrace();
			System.out.println("Load Failed");
		}

		/*
		 * Logic to load sections - Not finished/in use
		//Find number of tile section files in directory
		int tileSectionCount = new File("./tileSections/").list().length;
		int xCount = 0;
		int yCount = 0;
		//find x count of tile sections
		File sectionFile = new File("./tileSections/section["+ xCount +"][0].tileSection");
		while(sectionFile.exists()){
			xCount++;
			sectionFile = new File("./tileSections/section["+ xCount +"][0].tileSection");
		}
		//find y count of tile sections
		sectionFile = new File("./tileSections/section[0]["+ yCount +"].tileSection");
		while(sectionFile.exists()){
			yCount++;
			sectionFile = new File("./tileSections/section[0]["+ yCount +"].tileSection");
		}
		//build section array
		this.tileSections = new TileMapSection[xCount][yCount];
		System.out.println("Loading Sections...");
		for(int x = 0; x < xCount; x++){
			for(int y = 0; y < yCount; y++){
				sectionFile = new File("./tileSections/section["+ x +"]["+ y +"].tileSection");
				if(sectionFile.exists()){
					try{
						FileInputStream fis= new FileInputStream(sectionFile);
						ObjectInputStream ois= new ObjectInputStream(fis);
						tileSections[x][y] = (TileMapSection) ois.readObject();
					} catch(IOException | ClassNotFoundException e){
						e.printStackTrace();
					}
				}
				System.out.println("Section Loaded");
			}
		}
		mapLoaded = true;
		System.out.println("Load Complete");
		*/
	}
	
	public void playerLocSelected(){
		playerLocSelected = true;
	}
	
	public void setPlayerLocation(){
		Point p = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(p, this);
		int pX = (int) p.getX();
		int pY = (int) p.getY();
		tileMap.playerX = pX - (playerImg.getWidth()/2); 
		tileMap.playerY = pY - (playerImg.getHeight()/2);
		playerLocSet = true;
		playerLocSelected = false;
	}
	
	public void changeForegroundView(){
		if(foregroundViewSet){
			foregroundViewSet = false;
			currentTiles = tiles;
		}
		else{
			foregroundViewSet = true;
			currentTiles = foregroundTiles;
		}
	}
}

class MapPanelMouseListener implements MouseListener{

	MapPanel mapPanel;
	
	public MapPanelMouseListener(MapPanel mapPanel){
		this.mapPanel = mapPanel;
	}
	
	public void mouseClicked(MouseEvent e) {
		//insert single item into map
		if(e.getButton() == e.BUTTON1){
			if(mapPanel.mapEditor.propertiesPanel.itemViewDisplayed && mapPanel.mapEditor.propertiesPanel.selectedItem != null){
				mapPanel.addItem();
			}
			else if(mapPanel.mapEditor.propertiesPanel.creatureViewDisplayed && mapPanel.mapEditor.propertiesPanel.selectedCreature != null){
				mapPanel.addCreature();
			}
		}
		else if(e.getButton() == e.BUTTON3){
			if(mapPanel.mapEditor.propertiesPanel.itemViewDisplayed && mapPanel.mapEditor.propertiesPanel.selectedItem != null){
				mapPanel.removeItem();
			}
			else if(mapPanel.mapEditor.propertiesPanel.creatureViewDisplayed && mapPanel.mapEditor.propertiesPanel.selectedCreature != null){
				mapPanel.removeCreature();
			}
		}
	}	

	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}

	public void mousePressed(MouseEvent e) {
		if(mapPanel.playerLocSelected){
			mapPanel.setPlayerLocation();
		}
		else{
			mapPanel.dragging = true;
			if(mapPanel.mapEditor.toolPanel.bucketRB.isSelected() && mapPanel.mapEditor.tilePanel.getSelectedTile() != null) {
				int[] initialBucketPos = new int[2];
				initialBucketPos[0] = -1;
				initialBucketPos[1] = -1;
				mapPanel.bucketCommand(initialBucketPos, true);
				mapPanel.dragging = false;
			}
			else if(mapPanel.mapEditor.toolPanel.selectorRB.isSelected() && mapPanel.mapEditor.propertiesPanel.selectedItem == null) {
				int[] selectedTilePos = mapPanel.pointerToTilePos();
				if(selectedTilePos[0] >= 0 && selectedTilePos[1] >= 0){
					GameTile selectedTile = mapPanel.tiles[selectedTilePos[0]][selectedTilePos[1]];
					if(selectedTile instanceof Chest){
						if(!mapPanel.mapEditor.adminPanel.itemView.isSelected() && !mapPanel.mapEditor.adminPanel.creatureView.isSelected()){
							mapPanel.mapEditor.propertiesPanel.showChestProperties();
						}
					}
					else{
						mapPanel.mapEditor.propertiesPanel.hideChestProperties();
					}
					if(!selectedTile.isSelected()){
						selectedTile.select();
						mapPanel.deselectAllTiles();
						mapPanel.mapEditor.tilePanel.deselectAllTiles();
						selectedTile.select();
						mapPanel.selectedTiles.add(selectedTile);
						mapPanel.mapEditor.toolPanel.showTileProperties(selectedTile);
						mapPanel.mapEditor.toolPanel.toolsLabel.setText("Map Panel");
					}
					else{
						selectedTile.deselect();
						mapPanel.selectedTiles.remove(selectedTile);
						mapPanel.mapEditor.toolPanel.hideTileProperties();
					}
				}
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
		mapPanel.dragging = false;
	}
}
