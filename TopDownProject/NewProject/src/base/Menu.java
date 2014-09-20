package base;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import utilities.ImageManipulator;

public abstract class Menu {

	public BufferedImage menu;
	public boolean displayed;
	Font font = new Font("Courier New", Font.PLAIN, 15);
	
	public Menu(){
		//this.setMenu("menus/inventory.png");
	}
	
	public abstract void draw(Graphics g, int pixelX, int pixelY);
	
	public BufferedImage getMenu(){
		return menu;
	}
	
	public void setMenu(String path){
		menu = ImageManipulator.loadImage(path);
	}
	
	public void displayMenu(){
		this.displayed = true;
	}
	
	public void hideMenu(){
		this.displayed = false;
	}
	
	public boolean isDisplayed(){
		return displayed;
	}
	
	public void setFont(Font font){
		this.font = font;
	}
	
	public Font getFont(){
		return font;
	}
	
}
