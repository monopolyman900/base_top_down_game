package admin_mapEditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.MenuElement;
import javax.swing.filechooser.FileNameExtensionFilter;

import admin.AdminPanel;
import utilities.ImageManipulator;
import utilities.SpriteMap;
import core.GameFrame;

public class MapEditor extends JPanel{
	
	int height, width;
	final int DIVIDER_SIZE = 7;
	final int HORIZONTAL_DIVIDER_PLACE = 1000;
	final int VERTICAL_DIVIDER_PLACE = 200;
	
	//panel above this one - so we can tell what's displayed from anywhere
	AdminPanel adminPanel;
	
	//side bar divider separates main page and left (vertical divider)
	//side bar division separates top and bottom of sidebar (horizontal divider)
	JSplitPane sideBarDivider1;
	JSplitPane sideBar1;
	
	//right side of the screen
	JSplitPane sideBarDivider2;
	public PropertiesPanel propertiesPanel;
	
	//scroll pane to hold tiles panel, map panel
	JScrollPane toolContainer;
	JScrollPane tileContainer;
	JScrollPane mapContainer;
	JScrollPane propertiesContainer;
	
	//panels held inside JScrollPanes above
	public ToolPanel toolPanel;
	public TilePanel tilePanel;
	public MapPanel mapPanel;
	
	ImageManipulator imageManipulator;
	
	//File chooser - filter to only accept *.png
	JFileChooser fileChooser;
	FileNameExtensionFilter filter;
	
	public MapEditor(AdminPanel adminPanel, int w, int h){
		this.adminPanel = adminPanel;
		this.width = w;
		this.height = h;
		buildFileChooser();
		setLayout(new BorderLayout());
		sideBarDivider1 = splitScreen1();
		add(sideBarDivider1);
		sideBarDivider2 = splitScreen2();
		sideBarDivider1.setRightComponent(sideBarDivider2);
	}
	
	public AdminPanel getAdminPanel(){
		return adminPanel;
	}
	
	public void buildFileChooser(){
		fileChooser = new JFileChooser();
		filter = new FileNameExtensionFilter("PNG Image", "png");	//only accept .png images for now
		filter = new FileNameExtensionFilter("Tiles", "tiles");	//allow custom ".tiles" files to be chosen
		fileChooser.addChoosableFileFilter(filter);
	}
	
	//main split between map and tools/tiles
	public JSplitPane splitScreen1(){
		mapPanel = new MapPanel(this);
		mapContainer = new JScrollPane(mapPanel);
		mapContainer.getVerticalScrollBar().setUnitIncrement(20);
		mapContainer.getHorizontalScrollBar().setUnitIncrement(20);
		sideBar1 = buildSideBar();
		//add map container and sidebar
		sideBarDivider1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sideBar1, mapContainer);
		sideBarDivider1.setSize(width, height);
		//set divider size and position
		sideBarDivider1.setDividerSize(DIVIDER_SIZE);
		return sideBarDivider1;
	}
	
	//other split between map and properties
	public JSplitPane splitScreen2(){
		propertiesPanel = new PropertiesPanel(this);
		propertiesContainer = new JScrollPane(propertiesPanel);
		//add map container and sidebar
		sideBarDivider2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mapContainer, propertiesContainer);
		sideBarDivider2.setSize(width, height);
		//set divider size and position
		sideBarDivider2.setDividerSize(DIVIDER_SIZE);
		return sideBarDivider2;
	}
	
	//small split between tools and tiles
	public JSplitPane buildSideBar(){
		//we can just pass the final variables in here because it's in the top left
		toolPanel = new ToolPanel(this);
		toolContainer = new JScrollPane(toolPanel);
		tilePanel = new TilePanel(this);
		tileContainer = new JScrollPane(tilePanel);
		tileContainer.getVerticalScrollBar().setUnitIncrement(20);
		//add tile & tool containers
		sideBar1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, toolContainer, tileContainer);
		//set divider size and position
		sideBar1.setDividerSize(DIVIDER_SIZE);
		//sideBar.setDividerLocation(VERTICAL_DIVIDER_PLACE);
		return sideBar1;
	}
}
