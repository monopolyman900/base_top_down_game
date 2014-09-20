package utilities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class SpriteMap {

    private BufferedImage spriteMap;
    private BufferedImage[] sprites; 
	
    //start of object
    public SpriteMap(String filename, int c, int r) {
        this.loadSprites(filename, c, r);
    }
    
    //load image (filename) and place into large image of spriteMap
    //split spritemap into individual sprites
	private void loadSprites(String filename, int c, int r){
		spriteMap = loadImage(filename);
		sprites = splitSprites(c, r);
	}
	
    // returns the sprites array
	public BufferedImage[] getSprites() {
		return sprites;
	}
    
	//gets and returns file specified as 'ref' -- this should be the spritemap
    private BufferedImage loadImage(String ref){
    	BufferedImage bimg = null;
    	try{
    		bimg = ImageIO.read(new File(ref));
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	} 
    	return bimg;
    }
    
	// returns the array containing each sprite from left to right, top to bottom.
	// This is acomplished by drawing a portion of the larger image onto a new BufferedImage
	// by calling the graphics of each new BufferedImage.
    //c is number of collumns, r is number of rows
    private BufferedImage[] splitSprites(int c, int r){
    	int pWidth = spriteMap.getWidth() / c;		//width of individual sprites
    	int pHeight = spriteMap.getHeight() / r;	//height of individual sprites
    	BufferedImage[] sprites = new BufferedImage[c*r]; //array of individual sprites
    	
    	int n = 0; //used to count sprites
    	
		//int xOff = 0; if needed to adjust cutting precision
		int yOff = 0; //used for cutting precision
		
		for(int y = 0; y < r; y++){		//cycle through each row
			for(int x = 0; x < c; x++){	//cycle through each column in ^row^
				sprites[n] = new BufferedImage(pWidth, pHeight, 2); //width of sprites, height, imagetype
				Graphics2D g = sprites[n].createGraphics();
				
				/*draws part of the spritemap
				dx1 - the x coordinate of the first corner of the destination rectangle.
				dy1 - the y coordinate of the first corner of the destination rectangle.
				dx2 - the x coordinate of the second corner of the destination rectangle.
				dy2 - the y coordinate of the second corner of the destination rectangle.
				sx1 - the x coordinate of the first corner of the source rectangle.
				sy1 - the y coordinate of the first corner of the source rectangle.
				sx2 - the x coordinate of the second corner of the source rectangle.
				sy2 - the y coordinate of the second corner of the source rectangle.*/
				g.drawImage(spriteMap, 0, 0, pWidth, pHeight, pWidth*x, pHeight*y, pWidth*x+pWidth, pHeight*y+pHeight-yOff, null);
				g.dispose();
				n++;
			}
		}
		return sprites;
    }
}
