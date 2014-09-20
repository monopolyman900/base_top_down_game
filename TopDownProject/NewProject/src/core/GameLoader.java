package core;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;

import core.GameRenderer;
import creatures.Platform;
import creatures.TumbleWeed;
import tile.ActionTile;
import tile.Chest;
import tile.Door;
import tile.GameTile;
import tile.TileMap;
import utilities.SpriteMap;

public class GameLoader {

	//base map
	private ArrayList<BufferedImage> plain;
	//foreground
	private ArrayList<BufferedImage> plain2;
	private BufferedImage[] plainTiles;
	private BufferedImage[] plainTiles2;
	
	public GameLoader(){
		plain = new ArrayList<BufferedImage>();
		plainTiles = (new SpriteMap("tiles/tileMap.png", 12, 17)).getSprites(); //get array of sprites
		for(BufferedImage bImage : plainTiles){
			plain.add(bImage);
		}
		plain2 = new ArrayList<BufferedImage>();
		plainTiles2 = (new SpriteMap("tiles/foregoundmap.png", 12, 17)).getSprites(); //get array of sprites
		for(BufferedImage bImage2 : plainTiles2){
			plain2.add(bImage2);
		}
	}
	
	//gets file from filname, loads into a buffered image
	public BufferedImage loadImage(String filename){
		BufferedImage img = null;
		try{
			img = ImageIO.read(new File(filename));
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return img;
	}
	
	//converts buffered image to image
	public static Image toImage(BufferedImage bufferedImage){
		return Toolkit.getDefaultToolkit().createImage(bufferedImage.getSource()); 
	}
	
	// loads a tile map, given a map to load..
    // use this to load the background and foreground. Note: the status of the tiles (ie collide etc)
    // is irrelevant. Why? I don't check collision on maps other than the main map. 
    public TileMap loadOtherMaps(String filename) throws IOException {
		// lines is a list of strings, each element is a row of the map
		ArrayList<String> lines = new ArrayList<String>();
		int width = 0;
		int height = 0;
		
		// read in each line of the map into lines
		Scanner reader = new Scanner(new File(filename));
		while(reader.hasNextLine()) {
			String line = reader.nextLine();
			if(!line.startsWith("#")) {
				lines.add(line);
				width = Math.max(width, line.length());
			}
		}
		height = lines.size(); // number of elements in lines is the height
		
		TileMap newMap = new TileMap(width, height);
		for (int y=0; y < height; y++) {
			String line = lines.get(y);
			for (int x=0; x < line.length(); x++) {
				char ch = line.charAt(x);
				
				if (ch == 'R') {	//Top of roof
					newMap.setTile(x, y, plain2.get(1), false);
				} else if (ch == '<') {	//left side of top of roof
					newMap.setTile(x, y, plain2.get(0), false);
				} else if (ch == '>') {	//right side of top of roof
					newMap.setTile(x, y, plain2.get(2), false);
				} else if (ch == 'r') {	//roof
					newMap.setTile(x, y, plain2.get(13), false);
				} else if (ch == '[') {	//right side of roof
					newMap.setTile(x, y, plain2.get(12), false);
				} else if (ch == ']') {	//left side of roof
					newMap.setTile(x, y, plain2.get(14), false);
				} else if (ch == 'h') {	//shed
					newMap.setTile(x, y, plain2.get(3), false);
				}
			}
		}
		return newMap;	
	}
    
    public TileMap[] loadMap2(String filename){
    	TileMap[] maps = new TileMap[2];	//main and foreground maps
		//read file
    	File file = new File(filename);
		try{
			if(file != null){
				FileInputStream fis= new FileInputStream(file);
				ObjectInputStream ois= new ObjectInputStream(fis);
				maps = (TileMap[]) ois.readObject();
			}
		}
		catch(IOException | ClassNotFoundException e){
			e.printStackTrace();
		}
    	return maps;
    }
    
    // Use this to load the main map
	public TileMap loadMap(String filename) throws IOException {
		// lines is a list of strings, each element is a row of the map
		ArrayList<String> lines = new ArrayList<String>();
		int width = 0;
		int height = 0;
		// read in each line of the map into lines
		Scanner reader = new Scanner(new File(filename));
		while(reader.hasNextLine()) {
			String line = reader.nextLine();
			if(!line.startsWith("#")) {
				lines.add(line);
				width = Math.max(width, line.length());
			}
		}
		height = lines.size(); // number of elements in lines is the height
		
		TileMap newMap = new TileMap(width, height);
		for (int y=0; y < height; y++) {
			String line = lines.get(y);
			for (int x=0; x < line.length(); x++) {
				char ch = line.charAt(x);
				
				int pixelX = GameRenderer.tilesToPixels(x);
				int pixelY = GameRenderer.tilesToPixels(y);
				//Specific tiles relevant to the tilemap & text file need to be added here.
				// enumerate the possible tiles...
				
				if (ch == ' ') {		//dirt
					GameTile t = new GameTile(pixelX, pixelY, plain.get(0), false);
					newMap.setTile(x, y, t);
				}
				else if (ch == '0') {	//wood floor
					GameTile t = new GameTile(pixelX, pixelY, plain.get(5), false);
					newMap.setTile(x, y, t);
				}
				else if (ch == 'd') {	//door top
					GameTile t = new Door(pixelX, pixelY, plain.get(3), plain.get(4), "doortop", true, false, false, true);
					newMap.setTile(x, y, t);
				}
				else if (ch == 'D') {	//door bottom
					GameTile t = new Door(pixelX, pixelY, plain.get(15), plain.get(16), "doorbottom", true, false, false, true);
					newMap.setTile(x, y, t);
				}
				else if (ch == 'W') {	//house wall base
					GameTile t = new GameTile(pixelX, pixelY, plain.get(36), true);
					newMap.setTile(x, y, t);
				}
				else if (ch == 'w') {	//house wall side
					GameTile t = new GameTile(pixelX, pixelY, plain.get(11), true);
					newMap.setTile(x, y, t);
				}
				else if (ch == 'o') {	//house window
					GameTile t = new GameTile(pixelX, pixelY, plain.get(24), true);
					newMap.setTile(x, y, t);
				}
				else if (ch == 'T') {	//goes on the inside -- foreground stuff
					GameTile t = new GameTile(pixelX, pixelY, plain.get(6), "thresholdhouse", false, true, false, false);
					newMap.setTile(x, y, t);
				}
				else if (ch == 'F') {	//goes on the outside -- foreground stuff
					GameTile t = new GameTile(pixelX, pixelY, plain.get(26), "floormatstone", false, false, true, false);
					newMap.setTile(x, y, t);
				}
				else if (ch == '=') {	//horizontal wooden fence - top fence
					GameTile t = new GameTile(pixelX, pixelY, plain.get(21), true);
					newMap.setTile(x, y, t);
				}
				else if (ch == '-') {	//horizontal wooden fence - bottom fence
					GameTile t = new GameTile(pixelX, pixelY, plain.get(45), true);
					newMap.setTile(x, y, t);
				}
				else if (ch == ']') {	//left gate opening - bottom fence
					GameTile t = new GameTile(pixelX, pixelY, plain.get(9), true);
					newMap.setTile(x, y, t);
				}
				else if (ch == '[') {	//right gate opening - bottom fence
					GameTile t = new GameTile(pixelX, pixelY, plain.get(10), true);
					newMap.setTile(x, y, t);
				}
				else if (ch == '|') {	//vertical wooden fence - left side
					GameTile t = new GameTile(pixelX, pixelY, plain.get(32), true);
					newMap.setTile(x, y, t);
				}
				else if (ch == 'I') {	//vertical wooden fence - right side
					GameTile t = new GameTile(pixelX, pixelY, plain.get(35), true);
					newMap.setTile(x, y, t);
				}
				else if (ch == 's') {	//bottom left corner of fence
					GameTile t = new GameTile(pixelX, pixelY, plain.get(44), true);
					newMap.setTile(x, y, t);
				}
				else if (ch == 'S') {	//bottom right corner of fence
					GameTile t = new GameTile(pixelX, pixelY, plain.get(47), true);
					newMap.setTile(x, y, t);
				}
				else if (ch == 'n') {	//top left corner of fence
					GameTile t = new GameTile(pixelX, pixelY, plain.get(20), true);
					newMap.setTile(x, y, t);
				}
				else if (ch == 'N') {	//top right corner of fence
					GameTile t = new GameTile(pixelX, pixelY, plain.get(23), true);
					newMap.setTile(x, y, t);
				}
				else if (ch == 'h') {	//shed
					GameTile t = new GameTile(pixelX, pixelY, plain.get(37), true);
					newMap.setTile(x, y, t);
				}
				else if (ch == 'c') {	//top of cabinet
					GameTile t = new Chest(pixelX, pixelY, plain.get(28), true, "cabinetTop", 1);
					newMap.setTile(x, y, t);
				}
				else if (ch == 'C') {	//bottom of cainet
					GameTile t = new Chest(pixelX, pixelY, plain.get(40), false, "cabinetBottom", 1);
					newMap.setTile(x, y, t);
				}
				else if (ch == 't') {	//small table
					GameTile t = new GameTile(pixelX, pixelY, plain.get(39), false);
					newMap.setTile(x, y, t);
				}
			}
		}
		return newMap;	
	}
	
}
