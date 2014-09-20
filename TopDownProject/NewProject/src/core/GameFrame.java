package core;

import java.awt.CardLayout;
import java.awt.MenuBar;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import admin.AdminPanel;
import core.GamePanel;

public class GameFrame extends JFrame {
	
	JPanel cardHolder;
	CardLayout cards;
	
	public MenuPanel menuPanel;
	public GamePanel gamePanel;
	public AdminPanel adminPanel;
	
	boolean menuPanelDisplayed, gamePanelDisplayed, adminPanelDisplayed;
	
	JMenuBar menuBar;
	
	public GameFrame() {	
		
		int w = 1300;
		int h = 740;
		setSize(w, h);
		//setResizable(false);
		setTitle("Game Frame");

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		menuBar.setVisible(false);
		
		menuPanel = new MenuPanel(w, h, this);
		gamePanel = new GamePanel(w, h, this);
		adminPanel = new AdminPanel(w, h, this);
		
        cardHolder = new JPanel();
        cards = new CardLayout();
        cardHolder.setLayout(cards);
        cardHolder.add(menuPanel, "menu");
        cardHolder.add(gamePanel, "game");
        cardHolder.add(adminPanel, "admin");
		
        this.add(cardHolder);
        cards.show(cardHolder, "menu");
        
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);	
	}
	
	public void setPanelDisplayed(String panelName){
		if(panelName == "menu"){
			gamePanel.setPanelDisplayed(false);
        	menuPanel.setPanelDisplayed(true);
        	adminPanel.setPanelDisplayed(false);
        	menuBar.setVisible(false);
        	
        	cards.show(cardHolder, "menu");
        	menuPanel.requestFocusInWindow();
		}
		else if(panelName == "game"){
			gamePanel.setPanelDisplayed(true);
        	menuPanel.setPanelDisplayed(false);
        	adminPanel.setPanelDisplayed(false);
        	menuBar.setVisible(false);
        	
        	cards.show(cardHolder, "game");
        	gamePanel.requestFocusInWindow();
        	gamePanel.startGame();
		}
		else if(panelName == "admin"){
			gamePanel.setPanelDisplayed(false);
        	menuPanel.setPanelDisplayed(false);
        	adminPanel.setPanelDisplayed(true);
        	menuBar.setVisible(true);
        	
        	cards.show(cardHolder, "admin");
        	adminPanel.requestFocusInWindow();
		}
	}
	
	public static void main(String[] args) {
		new GameFrame();
	}
	
}