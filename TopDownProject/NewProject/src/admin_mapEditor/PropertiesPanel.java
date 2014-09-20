package admin_mapEditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import tile.ActionTile;
import tile.Chest;
import base.Creature;
import base.Item;

public class PropertiesPanel extends JPanel implements Runnable{

	MapEditor mapEditor;
	JLabel propertiesLabel = new JLabel("Properties:");
	
	private Thread thread;
	public boolean running;
	
	public boolean chestPropertiesDisplayed;
	public boolean itemViewDisplayed;
	public boolean creatureViewDisplayed;
	
	public int width, height;
	
	public ArrayList<Item> gameItems;
	public int itemSpacingX = 0;
	public int itemSpacingY = 30;
	
	public ArrayList<Creature> gameCreatures;
	public int creatureSpacingX = 0;
	public int creatureSpacingY = 30;
	
	public Font font = new Font("default", Font.PLAIN, 14);
	
	public Item selectedItem;
	public Creature selectedCreature;
	
	public PropertiesPanel(MapEditor mapEditor){
		this.mapEditor = mapEditor;
		this.setBackground(Color.WHITE);
		this.add(propertiesLabel);
		gameItems = mapEditor.adminPanel.frame.gamePanel.itemManager.possibleItems;
		gameCreatures = mapEditor.adminPanel.frame.gamePanel.creatureManager.possibleCreatures;
		addMouseListener(new PropertiesPanelMouseListener(this));
		setLayout(new GridLayout(40, 2));
		thread = new Thread(this, "propertiesAnimator");
		thread.start();
	}
	
	public void showChestProperties() {
		chestPropertiesDisplayed = true;
		itemViewDisplayed = false;
		creatureViewDisplayed = false;
	}
	
	public void hideChestProperties() {
		chestPropertiesDisplayed = false;
	}
	
	public void manageItemView(){
		chestPropertiesDisplayed = false;
		creatureViewDisplayed = false;
		if(mapEditor.adminPanel.itemView.isSelected()){
			itemViewDisplayed = true;
		}
		else{
			itemViewDisplayed = false;
		}
	}
	
	public void manageCreatureView(){
		chestPropertiesDisplayed = false;
		itemViewDisplayed = false;
		if(mapEditor.adminPanel.creatureView.isSelected()){
			creatureViewDisplayed = true;
		}
		else{
			creatureViewDisplayed = false;
		}
	}
	
	public Item pointerToItem(){
		Item item = null;
		Point p = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(p, this);
		int pX = (int) p.getX();
		int pY = (int) p.getY();
		if(pY > 20 && pY < 45){
			if(!gameItems.get(0).getSelected() || (mapEditor.mapPanel.selectedTiles.size() > 0 && mapEditor.mapPanel.selectedTiles.get(0).isChest())){
				gameItems.get(0).select();
				item = gameItems.get(0);
				//deselect others
				for(int i = 1; i < gameItems.size(); i++){
					gameItems.get(i).setSelected(false);
				}
			}
			else {
				gameItems.get(0).setSelected(false);
			}
		}
		else if(pY >= 45){
			int i = (pY - 15);
			i = (int) Math.floor(i/itemSpacingY);
			if(i < gameItems.size()){
				if(!gameItems.get(i).getSelected() || (mapEditor.mapPanel.selectedTiles.size() > 0 && mapEditor.mapPanel.selectedTiles.get(0).isChest())){
					gameItems.get(i).select();
					item = gameItems.get(i);
					for(int k = 0; k < gameItems.size(); k++){
						if(k != i){
							gameItems.get(k).setSelected(false);
						}
					}
				}
				else {
					gameItems.get(i).setSelected(false);
				}
			}
		}
		return item;
	}
	
	public Creature pointerToCreature(){
		Creature creature = null;
		Point p = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(p, this);
		int pX = (int) p.getX();
		int pY = (int) p.getY();
		if(pY > 20 && pY < 45){
			if(!gameCreatures.get(0).getSelected()){
				gameCreatures.get(0).select();
				creature = gameCreatures.get(0);
				//deselect others
				for(int i = 1; i < gameCreatures.size(); i++){
					gameCreatures.get(i).setSelected(false);
				}
			}
			else {
				gameCreatures.get(0).setSelected(false);
			}
		}
		else if(pY >= 45){
			int i = (pY - 15);
			i = (int) Math.floor(i/creatureSpacingY);
			if(i < gameCreatures.size()){
				if(!gameCreatures.get(i).getSelected()){
					gameCreatures.get(i).select();
					System.out.println("selected item "+i);
					creature = gameCreatures.get(i);
					for(int k = 0; k < gameCreatures.size(); k++){
						if(k != i){
							gameCreatures.get(k).setSelected(false);
						}
					}
				}
				else {
					gameCreatures.get(i).setSelected(false);
				}
			}
		}
		return creature;
	}
	
	public void setSelectedItem(Item item){
		this.selectedItem = item;
	}
	
	public Item getSelectedItem(){
		return selectedItem;
	}
	
	public void setSelectedCreature(Creature creature){
		this.selectedCreature = creature;
	}
	
	public Creature getSelectedCreature(){
		return selectedCreature;
	}
	
	public void paint(Graphics g){
		super.paint(g);
		g.setColor(Color.BLACK);
		g.setFont(font);
		if(chestPropertiesDisplayed && mapEditor.mapPanel.selectedTiles.size() == 1){
			propertiesLabel.setText("Chest Components");
			//if(mapEditor.mapPanel.selectedTiles != null && mapEditor.mapPanel.selectedTiles.size() == 1){
				Chest selectedChest = (Chest) mapEditor.mapPanel.selectedTiles.get(0);
				for(int i = 0; i < gameItems.size(); i++){
					if(i == 0) {
						g.drawLine(0, 20, width, 20);
						g.drawString(gameItems.get(i).getID(), itemSpacingX, (40*i)+40);
						g.drawString("x"+selectedChest.chestInventory.getNumberOfItem(gameItems.get(i).getID()), 110, (40*i)+40);
					}
					else { 
						g.drawLine(0, (15+(itemSpacingY)*i), width, (15+(itemSpacingY)*i));
						g.drawString(gameItems.get(i).getID(), itemSpacingX, (itemSpacingY*i)+itemSpacingY);
						g.drawString("x"+selectedChest.chestInventory.getNumberOfItem(gameItems.get(i).getID()), 110, (itemSpacingY*i)+itemSpacingY);
					}
				}
			//}
		}
		else if(itemViewDisplayed){
			propertiesLabel.setText("Single Item Placement");
			for(int i = 0; i < gameItems.size(); i++){
				g.setColor(Color.BLACK);
				if(i == 0) {
					g.drawLine(0, 20, width, 20);
					g.drawString(gameItems.get(i).getID(), itemSpacingX, (40*i)+40);
					if(gameItems.get(i).getSelected()){
						g.setColor(Color.BLUE);
						g.drawRect(0, 25, gameItems.get(i).getID().length()*8, 18);
					}
				}
				else {
					g.drawLine(0, (15+(itemSpacingY)*i), width, (15+(itemSpacingY)*i));
					g.drawString(gameItems.get(i).getID(), itemSpacingX, (itemSpacingY*i)+itemSpacingY);
					if(gameItems.get(i).getSelected()){
						g.setColor(Color.BLUE);
						g.drawRect(0, (18+(itemSpacingY)*i), gameItems.get(i).getID().length()*8, 18);
					}
				}
			}
		}
		else if(creatureViewDisplayed){
			propertiesLabel.setText("Single Creature Placement");
			for(int i = 0; i < gameCreatures.size(); i++){
				g.setColor(Color.BLACK);
				if(i == 0) {
					g.drawLine(0, 20, width, 20);
					g.drawString(gameCreatures.get(i).getID(), creatureSpacingX, (40*i)+40);
					if(gameCreatures.get(i).getSelected()){
						g.setColor(Color.BLUE);
						g.drawRect(0, 25, gameCreatures.get(i).getID().length()*8, 18);
					}
				}
				else {
					g.drawLine(0, (15+(creatureSpacingY)*i), width, (15+(creatureSpacingY)*i));
					g.drawString(gameCreatures.get(i).getID(), creatureSpacingX, (creatureSpacingY*i)+creatureSpacingY);
					if(gameCreatures.get(i).getSelected()){
						g.setColor(Color.BLUE);
						g.drawRect(0, (18+(creatureSpacingY)*i), gameCreatures.get(i).getID().length()*8, 18);
					}
				}
			}
		}
	}

	public void run() {
		running = true;
		while(running){
			repaint();
			revalidate();
			this.width = this.getWidth();
			this.height = this.getHeight();
		}
	}
}

//mouse listener for tile panel
class PropertiesPanelMouseListener implements MouseListener{
	
	public PropertiesPanel propertiesPanel;

	public PropertiesPanelMouseListener(PropertiesPanel propertiesPanel){
		this.propertiesPanel = propertiesPanel;
	}
	
	public void mouseClicked(MouseEvent e) {
		if(propertiesPanel.chestPropertiesDisplayed){
			Item item = propertiesPanel.pointerToItem();
			Item clonedItem = propertiesPanel.mapEditor.adminPanel.frame.gamePanel.itemManager.newClonedItem(item);
			if(propertiesPanel.mapEditor.mapPanel.selectedTiles.size() == 1 && propertiesPanel.mapEditor.mapPanel.selectedTiles.get(0).isChest()){
					Chest selectedChest = (Chest) propertiesPanel.mapEditor.mapPanel.selectedTiles.get(0);
				if(e.getButton() == e.BUTTON1){
					selectedChest.chestInventory.addItem(clonedItem);
				}
				else if(e.getButton() == e.BUTTON3){
					selectedChest.chestInventory.removeItem(item);
				}
			}
		}
		else if(propertiesPanel.itemViewDisplayed){
			Item item = propertiesPanel.pointerToItem();
			if(item != null && item.getSelected()){
				propertiesPanel.setSelectedItem(item);
			}
			else {
				propertiesPanel.setSelectedItem(null);
			}
		}
		else if(propertiesPanel.creatureViewDisplayed){
			Creature creature = propertiesPanel.pointerToCreature();
			if(creature != null && creature.getSelected()){
				propertiesPanel.setSelectedCreature(creature);
			}
			else {
				propertiesPanel.setSelectedCreature(null);
			}
		}
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}

	public void mousePressed(MouseEvent e) {
		
	}

	public void mouseReleased(MouseEvent e) {
		
	}
	
}
