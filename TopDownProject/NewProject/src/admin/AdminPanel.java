package admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import admin_mapEditor.MapEditor;
import core.GameFrame;

public class AdminPanel extends JPanel implements ActionListener{

	boolean adminPanelDisplayed;
	boolean mapEditorDisplayed;
	
	int height, width;
	final int DIVIDER_SIZE = 7;
	final int HORIZONTAL_DIVIDER_PLACE = 300;
	final int VERTICAL_DIVIDER_PLACE = 200;
	
	//need this to get menuBar
	public GameFrame frame;
	
	JTabbedPane tabbedPane;
	TabChangeListener tabChangeListener;
	//side bar divider separates main page and left (vertical divider)
	//side bar division separates top and bottom of sidebar (horizontal divider)
	JSplitPane sideBarDivider;
	JSplitPane sideBar;
	
	//scroll pane to hold tiles panel, map panel
	JScrollPane tileContainer;
	JScrollPane toolContainer;
	JScrollPane mapContainer;
	
	//each tabbed pane has an high level JPanel - contains other JPanels
	MapEditor mapEditor;
	
	//menu bar stuff
	public JMenuBar menuBar;
	public JMenu menu;
	public JMenuItem importTiles;
	public JMenuItem saveTiles;
	public JMenuItem loadTiles;
	public JMenuItem saveMap;
	public JMenuItem loadMap;
	public JMenuItem newMap;
	public JMenuItem playerLoc;
	
	//view menu
	public JCheckBoxMenuItem foregroundView;
	public JCheckBoxMenuItem itemView;
	public JCheckBoxMenuItem creatureView;
	
	public AdminPanel(int w, int h, GameFrame frame){
		
		this.height = h;
		this.width = w;
		
		setSize(width, height);
		this.frame = frame;
		this.setLayout(new BorderLayout());
		
		//build and add JTabbedPane
		tabbedPane = buildTabbedPane();
		add(tabbedPane);
		
		//menu bar
		buildFileMenu();
		buildViewMenu();
		frame.setJMenuBar(menuBar);
		
		//add change listener to tabs
		addTabChangeListener();
	}
	
	public JTabbedPane buildTabbedPane(){
		JTabbedPane pane = new JTabbedPane();
		pane.setSize(width, height);
		
		//panel 1
		mapEditor = new MapEditor(this, width, height);
		//panel2
		JComponent panel2 = new JPanel();
		panel2.setBackground(Color.RED);
		pane.addTab("Tab 1", mapEditor);
		pane.addTab("Tab 2", panel2);
		
		return pane;
	}
	
	public void setPanelDisplayed(boolean x){
		this.adminPanelDisplayed = x;
	}
	
	public void buildFileMenu(){
		menuBar = frame.getJMenuBar();
		menu = new JMenu("File");
		menu.setName("file");
		menuBar.add(menu);
		//menu items
		importTiles = buildImportTilesMenuItem();
		saveTiles = buildSaveTilesMenuItem();
		loadTiles = buildLoadTilesMenuItem();
		saveMap = buildSaveMapMenuItem();
		loadMap = buildLoadMapMenuItem();
		newMap = buildNewMapMenuItem();
		playerLoc = buildPlayerLocationMenuItem();
		menu.add(newMap);
		menu.add(importTiles);
		menu.add(saveTiles);
		menu.add(loadTiles);
		menu.add(saveMap);
		menu.add(loadMap);
		menu.add(playerLoc);
	}
	
	public void buildViewMenu(){
		menuBar = frame.getJMenuBar();
		menu = new JMenu("View");
		menu.setName("view");
		menuBar.add(menu);
		//menu items
		foregroundView = buildForegroundView();
		menu.add(foregroundView);
		itemView = buildItemView();
		menu.add(itemView);
		creatureView = buildCreatureView();
		menu.add(creatureView);
	}
	
	public JMenuItem buildSaveTilesMenuItem(){
		saveTiles = new JMenuItem("Save Tiles");
		saveTiles.setName("saveTiles");
		saveTiles.addActionListener(this);
		saveTiles.setEnabled(false);
		return saveTiles;
	}
	
	public JMenuItem buildLoadTilesMenuItem(){
		loadTiles = new JMenuItem("Load Tiles");
		loadTiles.setName("loadTiles");
		loadTiles.addActionListener(this);
		return loadTiles;
	}
	
	public JMenuItem buildSaveMapMenuItem(){
		saveMap = new JMenuItem("Save Map");
		saveMap.setName("saveMap");
		saveMap.addActionListener(this);
		saveMap.setEnabled(false);
		return saveMap;
	}
	
	public JMenuItem buildLoadMapMenuItem(){
		loadMap = new JMenuItem("Load Map");
		loadMap.setName("loadMap");
		loadMap.addActionListener(this);
		return loadMap;
	}
	
	public JMenuItem buildImportTilesMenuItem(){
		importTiles = new JMenuItem("Import Tiles");
		importTiles.setName("importTiles");
		importTiles.addActionListener(this);
		return importTiles;
	}
	
	public JMenuItem buildNewMapMenuItem(){
		newMap = new JMenuItem("New Map");
		newMap.setName("newMap");
		newMap.addActionListener(this);
		return newMap;
	}
	
	public JMenuItem buildPlayerLocationMenuItem(){
		playerLoc = new JMenuItem("Player Location");
		playerLoc.setName("playerLoc");
		playerLoc.addActionListener(this);
		return playerLoc;
	}
	
	public JCheckBoxMenuItem buildForegroundView(){
		foregroundView = new JCheckBoxMenuItem("Foreground Tiles");
		foregroundView.setName("foregroundView");
		foregroundView.addActionListener(this);
		foregroundView.setEnabled(true);
		return foregroundView;
	}
	
	public JCheckBoxMenuItem buildItemView(){
		itemView = new JCheckBoxMenuItem("Display Item Panel");
		itemView.setName("itemView");
		itemView.addActionListener(this);
		itemView.setEnabled(true);
		return itemView;
	}
	
	public JCheckBoxMenuItem buildCreatureView(){
		creatureView = new JCheckBoxMenuItem("Display Creature Panel");
		creatureView.setName("creatureView");
		creatureView.addActionListener(this);
		creatureView.setEnabled(true);
		return creatureView;
	}
	
	public void addTabChangeListener(){
		tabChangeListener = new TabChangeListener(tabbedPane, menuBar, this);
		tabbedPane.getModel().addChangeListener(tabChangeListener);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if(src == importTiles){
			mapEditor.tilePanel.importTiles();
		}
		else if(src == saveTiles){
			mapEditor.tilePanel.saveTiles();
		}
		else if(src == loadTiles){
			mapEditor.tilePanel.loadTiles();
		}
		else if(src == newMap){
			mapEditor.mapPanel.newMapDialog();
		}
		else if(src == saveMap){
			mapEditor.mapPanel.saveMap();
		}
		else if(src == loadMap){
			mapEditor.mapPanel.loadMap();
		}
		else if(src == playerLoc){
			mapEditor.mapPanel.playerLocSelected();
		}
		else if(src == foregroundView){
			mapEditor.mapPanel.changeForegroundView();
		}
		else if(src == itemView){
			mapEditor.propertiesPanel.manageItemView();
			creatureView.setSelected(false);
		}
		else if(src == creatureView){
			mapEditor.propertiesPanel.manageCreatureView();
			itemView.setSelected(false);
		}
	}
	
	public boolean mapEditorDisplayed(){
		return mapEditorDisplayed;
	}
	
	public void setMapEditorDisplayed(boolean mapEditorDisplayed){
		this.mapEditorDisplayed = mapEditorDisplayed;
	}
}
