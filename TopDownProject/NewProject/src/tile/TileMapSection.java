package tile;

import java.io.Serializable;

public class TileMapSection implements Serializable{
	
	private static final long serialVersionUID = 012L;

	public GameTile[][] tiles;
	
	public int sectionWidth = 25;
	public int sectionHeight = 25;
	
	public TileMapSection(int width, int height){
		tiles = new GameTile[sectionWidth][sectionHeight];
	}
	
	public TileMapSection(int width, int height, GameTile[][] tiles){
		tiles = new GameTile[sectionWidth][sectionHeight];
	}
	
	public TileMapSection(){
		
	}
	
	public void setSectionWidth(int width){
		this.sectionWidth = width;
	}
	
	public void setSectionHeight(int height){
		this.sectionHeight = height;
	}
	
	public int getSectionWidth(){
		return this.sectionWidth;
	}
	
	public int getSectionHeight(){
		return this.sectionHeight;
	}
	
	public void setTile(int x, int y, GameTile tile){
		this.tiles[x][y] = tile;
	}
	
	public void setTiles(GameTile[][] tiles){
		this.tiles = tiles;
	}
	
}
