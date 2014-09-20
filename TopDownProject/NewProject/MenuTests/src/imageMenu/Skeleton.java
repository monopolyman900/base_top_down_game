package imageMenu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Skeleton{

	Game game;
	
	public Skeleton(){
		
		new Game();	
	}

	public static void main(String args[]){
		new Skeleton();
	}
	
}
