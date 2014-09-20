package admin_mapEditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import tile.GameTile;
import tile.Tile;
import utilities.SpriteMap;

public class TilePanel extends JPanel implements Runnable{

	final int DEFAULT_SPRITE_MAP_COLNUM = 12;
	final int DEFAULT_SPRITE_MAP_ROWNUM = 17;
	
	int tileBufferX; 
	int tileBufferY = 10;
	int minTileBuffer = 10;
	
	MapEditor mapEditor;
	SpriteMap spriteMap;
	
	private Thread thread;
	public boolean running;
	
	public int colNum, rowNum;
	public int width, height;
	
	public GameTile[] tiles;
	public boolean tilesImported;
	public boolean tilesLoaded;
	
	public ArrayList<GameTile> selectedTiles = new ArrayList<GameTile>();
	
	public TilePanel(MapEditor mapEditor){
		this.mapEditor = mapEditor;
		setPreferredSize(new Dimension(185, 400));
		setBackground(Color.WHITE);
		addMouseListener(new TilePanelMouseListener(this));
		thread = new Thread(this, "menuAnimator");
		thread.start();
	}
	
	public void setSpriteMap(SpriteMap img){
		spriteMap = img;
	}
	
	public SpriteMap getSpriteMap(){
		return spriteMap;
	}
	
	//build tiles from images
	public void setTiles(BufferedImage[] tiles){
		this.tiles = new GameTile[tiles.length];
		for(int i = 0; i < tiles.length; i++){
			this.tiles[i] = new GameTile(tiles[i]);
		}
	}
	
	public GameTile[] getTiles(){
		return tiles;
	}
	
	public int getSpriteMapCols(){
		if(colNum > 0){
			return colNum;
		}
		else{
			return DEFAULT_SPRITE_MAP_COLNUM;
		}
	}
	
	public int getSpriteMapRows(){
		if(colNum > 0){
			return rowNum;
		}
		else{
			return DEFAULT_SPRITE_MAP_ROWNUM;
		}
	}
	
	public int getTileWidth(){
		return tiles[0].getWidth();
	}
	
	public int getTileHeight(){
		return tiles[0].getHeight();
	}
	
	public void importTiles(){
		try{
			mapEditor.fileChooser.setCurrentDirectory(new java.io.File("./tiles"));
			int returnVal = mapEditor.fileChooser.showOpenDialog(this);
			File file = mapEditor.fileChooser.getSelectedFile();
			if(file != null){
				SpriteMap spriteMap = new SpriteMap(file.getPath(), getSpriteMapCols(), getSpriteMapRows());
				setSpriteMap(spriteMap);
				setTiles(spriteMap.getSprites());
				mapEditor.mapPanel.setTileGridSize(getTileWidth());
				mapEditor.adminPanel.saveTiles.setEnabled(true);
				tilesImported = true;
			}
		}
		catch(Exception e){
			tilesImported = false;
			e.printStackTrace();
		}
	}
	
	public void setTilesImported(boolean i){
		tilesImported = i;
	}
	
	public void saveTiles(){
		if(tiles != null & tiles.length > 0){
        	mapEditor.fileChooser.setCurrentDirectory(new java.io.File("./customGameTiles"));
        	mapEditor.fileChooser.setSelectedFile(new File("currentTiles.tiles"));
        	int returnVal = mapEditor.fileChooser.showSaveDialog(this);
        	if(returnVal == JFileChooser.APPROVE_OPTION){
    	        try {
		    		FileOutputStream fos= new FileOutputStream(mapEditor.fileChooser.getSelectedFile());
		            ObjectOutputStream oos= new ObjectOutputStream(fos);
					oos.writeObject(tiles);
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
        	}
		}
	}
	
	public void loadTiles() {
		//pick file
        mapEditor.fileChooser.setCurrentDirectory(new java.io.File("./customGameTiles"));
		int returnVal = mapEditor.fileChooser.showOpenDialog(this);
		File file = mapEditor.fileChooser.getSelectedFile();
		//read file
		try{
			if(file != null){
				FileInputStream fis= new FileInputStream(file);
				ObjectInputStream ois= new ObjectInputStream(fis);
				GameTile[] tiles = (GameTile[]) ois.readObject();
				mapEditor.adminPanel.saveTiles.setEnabled(true);
				this.tiles = tiles;
				tilesLoaded = true;
			}
		}
		catch(IOException | ClassNotFoundException e){
			e.printStackTrace();
		}
	}
	
	public GameTile getSelectedTile(){
		GameTile selectedTile = null;
			if(tiles != null){
			for(int i = 0; i < tiles.length; i++){
				if(tiles[i].isSelected()){
					selectedTile = tiles[i];
				}
			}
		}
		return selectedTile;
	}
	
	public void deselectAllTiles(){
		if(tiles != null){
			for(int i = 0; i < tiles.length; i++){
				tiles[i].deselect();
			}
			for(int i = 0; i < selectedTiles.size(); i++){
				selectedTiles.remove(i);
			}
		}
	}
	
	public void paint(Graphics g){
		super.paint(g);
		g.setColor(Color.BLUE);
		if(tiles != null && tiles.length > 0){
			//minimum horizontal tile number
			int tileNumX = (int) Math.floor(this.width/(getTileWidth() + minTileBuffer));
			//horizontal buffer between tiles
			//width - (total X taken by tiles)/total number of Xbuffers - one for each tile + 1 for the start
			tileBufferX = (this.width - (getTileWidth()*tileNumX))/(tileNumX + 1);
			//initial tile positions
			int curX = tileBufferX;
			int curY = tileBufferY*2;
			for(int i = 0; i < tiles.length; i++){
				//set tile position data here because this is where we determine position
				tiles[i].setPixelX(curX);
				tiles[i].setPixelY(curY);
				//selection rectangle
				int rectBuffer = 3;
				int rectThickness = 2;
				Graphics2D g2d = (Graphics2D) g;
				g2d.setStroke(new BasicStroke(rectThickness));
				g2d.setColor(Color.BLUE);
				if(tiles[i].isSelected()){
					g2d.drawRect(curX - rectBuffer, curY - rectBuffer, (getTileWidth() + rectBuffer*2), (getTileHeight() + rectBuffer*2));
				}
				if(tiles[i].isSelected() && tiles[i].isDoor() && tiles[i].getSecondaryTile() != null){
					g2d.setColor(Color.RED);
					g2d.drawRect((tiles[i].getSecondaryTile().getPixelX()-rectBuffer), (tiles[i].getSecondaryTile().getPixelY() - rectBuffer), (getTileWidth() + rectBuffer*2), (getTileHeight() + rectBuffer*2));
				}
				g.drawImage(tiles[i].getImage(), curX, curY, this);
				if((curX + getTileWidth()*2 + tileBufferX) > this.width){
					curY += (getTileHeight() + tileBufferY);
					curX = tileBufferX;
				}
				else{
					curX += (getTileWidth() + tileBufferX);
				}
				if(curY > this.height){
					int newHeight = (this.height + getTileHeight() + tileBufferY);
					Dimension newSize = new Dimension(width, newHeight);
					this.setPreferredSize(newSize);
				}
			}
		}
	}

	public void run() {
		this.running = true;
		while(running){
			repaint();
			revalidate();
			this.width = this.getWidth();
			this.height = this.getHeight();
		}
	}
}

//mouse listener for tile panel
class TilePanelMouseListener implements MouseListener{

	public TilePanel tilePanel;

	public TilePanelMouseListener(TilePanel tilePanel){
		this.tilePanel = tilePanel;
	}

	public void mouseClicked(MouseEvent e) {

	}
	
	//this is just used for debugging right now
	public int numOfTilesSelected(){
		int num = 0;
		for(int i = 0; i < tilePanel.tiles.length; i++){
			if(tilePanel.tiles[i].isSelected()){
				num++;
			}
		}
		return num;
	}

	public void mouseEntered(MouseEvent e) {
		
		
	}

	public void mouseExited(MouseEvent e) {
		
		
	}

	public void mousePressed(MouseEvent e) {
			if(tilePanel.mapEditor.toolPanel.bGroup != null){
				tilePanel.mapEditor.mapPanel.deselectAllTiles();
				if(tilePanel.mapEditor.toolPanel.selectedTile != null){
					if((tilePanel.mapEditor.toolPanel.paintBrushRB.isSelected() || tilePanel.mapEditor.toolPanel.bucketRB.isSelected()) && !tilePanel.mapEditor.toolPanel.selectedTile.isDoor()){
						tilePanel.deselectAllTiles();
					}
				}
				Point p = MouseInfo.getPointerInfo().getLocation();
				SwingUtilities.convertPointFromScreen(p, tilePanel);
				int pX = (int) p.getX();
				int pY = (int) p.getY();
				if(tilePanel.tiles != null){
					GameTile[] tiles = tilePanel.tiles;
					for(int i = 0; i < tiles.length; i++){
						if(pX > tiles[i].getPixelX() && pX < (tiles[i].getPixelX() + tiles[i].getWidth())){
							if(pY > tiles[i].getPixelY() && pY < (tiles[i].getPixelY() + tiles[i].getHeight())){
								if(e.getButton() == e.BUTTON1){//left click
									if(!tiles[i].isSelected()){
										tilePanel.deselectAllTiles();
										tiles[i].select();
										tilePanel.selectedTiles.add(tiles[i]);
										tilePanel.mapEditor.toolPanel.toolsLabel.setText("Tile Panel");
									}
									else if(tilePanel.selectedTiles.size() > 0){
										tiles[i].deselect();
										//tilePanel.selectedTiles.remove(tiles[i]);
									}
								}
								else if(e.getButton() == e.BUTTON3){//right click
									if(tilePanel.selectedTiles.size() == 1 && tilePanel.selectedTiles.get(0).isDoor()){
										tilePanel.selectedTiles.get(0).setSecondaryTile(tiles[i]);
									}
								}
							}
						}
						tilePanel.mapEditor.mapPanel.deselectAllTiles();
					}
				//set tool panel indicators for single selected tile
				if(tilePanel.selectedTiles.size() != 1){
					tilePanel.mapEditor.toolPanel.hideTileProperties();
				}
				else{
					tilePanel.mapEditor.toolPanel.showTileProperties(tilePanel.selectedTiles.get(0));
				}
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
		
		
	}	
}

