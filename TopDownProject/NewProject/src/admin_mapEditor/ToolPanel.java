package admin_mapEditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import tile.GameTile;
import tile.Tile;

public class ToolPanel extends JPanel{
	
	MapEditor mapEditor;
	
	JLabel toolsLabel;
	JLabel IDLabel;
	
	ButtonGroup bGroup;
	JRadioButton bucketRB;
	JRadioButton paintBrushRB;
	JRadioButton selectorRB;
	
	JCheckBox collidableCB;
	JCheckBox actionTileCB;
	JCheckBox thresholdCB;
	JCheckBox floorMatCB;
	JCheckBox doorCB;
	JCheckBox chestCB;
	JTextField tileID;
	
	GameTile selectedTile;
	
	ToolPanelTileIndListener toolPanelActionListener;
	
	public ToolPanel(MapEditor mapEditor){
		
		//it looks like this is the only one of the three classes that needs to sizes set.
		setPreferredSize(new Dimension(185, 220));
		setMaximumSize(new Dimension(800, 100));
		setLayout(new GridLayout(9, 3, 5, 5));
		toolPanelActionListener = new ToolPanelTileIndListener(this);
		this.mapEditor = mapEditor;
		buildTileTools();
		setBackground(Color.WHITE);
	}
	
	public void buildTileTools(){
		toolsLabel = new JLabel("Tools");
		toolsLabel.setSize(250, 5);
		selectorRB = new JRadioButton("Selector");
		selectorRB.setBackground(Color.WHITE);
		selectorRB.addActionListener(toolPanelActionListener);
		paintBrushRB = new JRadioButton("PaintBrush");
		paintBrushRB.setBackground(Color.WHITE);
		bucketRB = new JRadioButton("Bucket");
		bucketRB.setBackground(Color.WHITE);
		//initialize tile indicator checkboxes
		collidableCB = new JCheckBox("Collidable"); collidableCB.setBackground(Color.WHITE);
		collidableCB.addActionListener(toolPanelActionListener);
		actionTileCB = new JCheckBox("Action Tile"); actionTileCB.setBackground(Color.WHITE);
		actionTileCB.addActionListener(toolPanelActionListener);
		thresholdCB = new JCheckBox("Threshold"); thresholdCB.setBackground(Color.WHITE);
		thresholdCB.addActionListener(toolPanelActionListener);
		floorMatCB = new JCheckBox("Floormat"); floorMatCB.setBackground(Color.WHITE);
		floorMatCB.addActionListener(toolPanelActionListener);
		doorCB = new JCheckBox("Door"); doorCB.setBackground(Color.WHITE);
		doorCB.addActionListener(toolPanelActionListener);
		chestCB = new JCheckBox("Chest"); chestCB.setBackground(Color.WHITE);
		chestCB.addActionListener(toolPanelActionListener);
		IDLabel = new JLabel("ID");
		tileID = new JTextField(); tileID.setBackground(Color.WHITE);
		//tileID.addActionListener(toolPanelActionListener);
		tileID.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent arg0) {
				String ID = tileID.getText();
				selectedTile.setID(ID);
			}
			public void insertUpdate(DocumentEvent arg0) {
				String ID = tileID.getText();
				selectedTile.setID(ID);
			}
			public void removeUpdate(DocumentEvent arg0) {
				String ID = tileID.getText();
				selectedTile.setID(ID);
			}
		});
		this.add(toolsLabel);
		this.add(new JLabel(""));
		this.add(selectorRB);
		this.add(paintBrushRB);
		this.add(bucketRB);
		//tile indicator checkboxes
		this.add(new JLabel(""));
		this.add(collidableCB);
		this.add(actionTileCB);
		this.add(thresholdCB);
		this.add(floorMatCB);
		this.add(doorCB);
		this.add(chestCB);
		this.add(IDLabel);
		this.add(tileID);
		
		collidableCB.setVisible(false);
		actionTileCB.setVisible(false);
		thresholdCB.setVisible(false);
		floorMatCB.setVisible(false);
		doorCB.setVisible(false);
		chestCB.setVisible(false);
		IDLabel.setVisible(false);
		tileID.setVisible(false);
		
		bGroup = new ButtonGroup();
		bGroup.add(paintBrushRB);
		bGroup.add(bucketRB);
		bGroup.add(selectorRB);
	}
	
	public void showTileProperties(GameTile selectedTile){
		this.selectedTile = selectedTile;
		collidableCB.setVisible(true);
		actionTileCB.setVisible(true);
		thresholdCB.setVisible(true);
		floorMatCB.setVisible(true);
		doorCB.setVisible(true);
		chestCB.setVisible(true);
		IDLabel.setVisible(true);
		tileID.setVisible(true);
		if(selectedTile.isCollidable()) {
			collidableCB.setSelected(true);
		}
		else{
			collidableCB.setSelected(false);
		}
		if(selectedTile.isActionTile()){
			actionTileCB.setSelected(true);
		}
		else{
			actionTileCB.setSelected(false);
		}
		if(selectedTile.isThreshold()) {
			thresholdCB.setSelected(true);
		}
		else{
			thresholdCB.setSelected(false);
		}
		if(selectedTile.isFloormat()) {
			floorMatCB.setSelected(true);
		}
		else{
			floorMatCB.setSelected(false);
		}
		if(selectedTile.isDoor()) {
			doorCB.setSelected(true);
		}
		else{
			doorCB.setSelected(false);
		}
		if(selectedTile.isChest()) {
			chestCB.setSelected(true);
		}
		else{
			chestCB.setSelected(false);
		}
		if(selectedTile.getID() != ""){
			tileID.setText(selectedTile.getID());
		}
		else{
			tileID.setText("");
		}
	}
	
	public void hideTileProperties(){
		collidableCB.setVisible(false); collidableCB.setSelected(false);
		actionTileCB.setVisible(false); actionTileCB.setSelected(false); 
		thresholdCB.setVisible(false); thresholdCB.setSelected(false);
		floorMatCB.setVisible(false); floorMatCB.setSelected(false);
		doorCB.setVisible(false); doorCB.setSelected(false);
		chestCB.setVisible(false); chestCB.setSelected(false);
		IDLabel.setVisible(false);
		tileID.setVisible(false); tileID.setText("");
	}
}

class ToolPanelTileIndListener implements ActionListener{

	ToolPanel toolPanel;
	
	public ToolPanelTileIndListener(ToolPanel toolPanel){
		this.toolPanel = toolPanel;
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == toolPanel.selectorRB){
			toolPanel.mapEditor.tilePanel.deselectAllTiles();
		}
		if(e.getSource() == toolPanel.collidableCB){
			if(toolPanel.collidableCB.isSelected()){
				toolPanel.selectedTile.setIsCollidable(true);
			}
			else{
				toolPanel.selectedTile.setIsCollidable(false);
			}
		}
		else if (e.getSource() == toolPanel.actionTileCB){
			if(toolPanel.actionTileCB.isSelected()){
				toolPanel.selectedTile.setIsActionTile(true);
			}
			else{
				toolPanel.selectedTile.setIsActionTile(false);
				toolPanel.chestCB.setSelected(false);
				toolPanel.selectedTile.setIsChest(false);
				if(toolPanel.mapEditor.propertiesPanel.chestPropertiesDisplayed){
					toolPanel.mapEditor.propertiesPanel.hideChestProperties();
				}
				toolPanel.doorCB.setSelected(false);
				toolPanel.selectedTile.setIsDoor(false);
			}
		}
		else if (e.getSource() == toolPanel.thresholdCB){
			if(toolPanel.thresholdCB.isSelected()){
				toolPanel.selectedTile.setIsThreshold(true);
			}
			else{
				toolPanel.selectedTile.setIsThreshold(false);
			}
		}
		else if (e.getSource() == toolPanel.floorMatCB){
			if(toolPanel.floorMatCB.isSelected()){
				toolPanel.selectedTile.setIsFloormat(true);
			}
			else{
				toolPanel.selectedTile.setIsFloormat(false);
			}
		}
		else if (e.getSource() == toolPanel.doorCB){
			if(toolPanel.doorCB.isSelected()){
				toolPanel.selectedTile.setIsDoor(true);
				toolPanel.actionTileCB.setSelected(true);
				toolPanel.selectedTile.setIsActionTile(true);
			}
			else{
				toolPanel.selectedTile.setIsDoor(false);
			}
		}
		else if (e.getSource() == toolPanel.chestCB){
			if(toolPanel.chestCB.isSelected()){
				toolPanel.selectedTile.setIsChest(true);
				if(toolPanel.mapEditor.mapPanel.selectedTiles.size() == 1){
					if(!toolPanel.mapEditor.adminPanel.itemView.isSelected() && !toolPanel.mapEditor.adminPanel.creatureView.isSelected()){
						toolPanel.mapEditor.propertiesPanel.showChestProperties();
					}
				}
				toolPanel.actionTileCB.setSelected(true);
				toolPanel.selectedTile.setIsActionTile(true);
			}
			else{
				toolPanel.selectedTile.setIsChest(false);
				toolPanel.mapEditor.propertiesPanel.hideChestProperties();
			}
		}
	}
}