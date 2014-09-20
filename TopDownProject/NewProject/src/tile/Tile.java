package tile;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.imageio.ImageIO;

import core.GameRenderer;
import animation.Animatable;
import animation.Animation;

public class Tile extends Animatable implements Serializable{
	
	private static final long serialVersionUID = 010L;

	private int tileX;
	private int tileY;
	private int pixelX;
	private int pixelY;
	transient protected BufferedImage img;
	
	//map editor variables
	public boolean justAdded = false;
	public int[] mapEditorIndex;
	
	//admin variables
	boolean adminSelected;
	
	public Tile(int pixelX, int pixelY, Animation anim, BufferedImage img) {
		tileX = GameRenderer.pixelsToTiles(pixelX);
		tileY = GameRenderer.pixelsToTiles(pixelY);
		this.pixelX = pixelX;
		this.pixelY = pixelY;
		this.img = img;
		setAnimation(anim);
		justAdded = false;
	}
	
	public Tile(int pixelX, int pixelY, BufferedImage img) {
		this(pixelX, pixelY, null, img);
	}
	
	public Tile(BufferedImage img){
		this(0, 0, img);
	}
	
	public Tile(){
		this(null);
	}
	
	public void draw(Graphics g, int pixelX, int pixelY) {
		g.drawImage(getImage(), pixelX, pixelY, null);
	}
	
	public void draw(Graphics g, int pixelX, int pixelY, int offsetX, int offsetY) {
		draw(g, pixelX + offsetX, pixelY + offsetY);
	}
	
	public BufferedImage getImage() {
		return (currentAnimation() == null) ? img : currentAnimation().getImage();
	}
	
	public void setImage(BufferedImage img){
		this.img = img;
	}
	
	public void setPixelX(int x){
		this.pixelX = x;
	}
	
	public void setPixelY(int y){
		this.pixelY = y;
	}
	
	public int getPixelX() {
		return pixelX;
	}
	
	public int getPixelY() {
		return pixelY;
	}
	
	public int getWidth() {
		return getImage().getWidth(null);
	}
	
	public int getHeight() {
		return getImage().getHeight(null);
	}
	
	public boolean isSelected(){
		return adminSelected;
	}
	
	public void select(){
		this.adminSelected = true;
	}
	
	public void deselect(){
		this.adminSelected = false;
	}
	
	public boolean added(){
		return justAdded;
	}
	
	public void justAdded(){
		this.justAdded = true;
	}
	
	public void setJustAdded(boolean justAdded){
		this.justAdded = justAdded;
	}
	
	public void setMapEditorIndex(int[] mapEditorIndex){
		this.mapEditorIndex = mapEditorIndex;
	}
	
	public int[] getMapEditorIndex(){
		return mapEditorIndex;
	}
	
	//return bounds, top, bottom, left, right
	public int[] getBounds(){
		int[] bounds = {(this.getPixelY()), (this.getPixelY()+this.getHeight()), 
				(this.getPixelX()), (this.getPixelX()+getWidth())}; 
		return bounds;
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		ImageIO.write(this.getImage(), "png", out);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		setImage(ImageIO.read(in));
	}
	
} // Tile

