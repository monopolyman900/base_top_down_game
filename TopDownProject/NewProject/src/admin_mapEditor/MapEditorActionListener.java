package admin_mapEditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MapEditorActionListener implements ActionListener{

	public MapEditor mapEditor;
	
	public MapEditorActionListener(MapEditor mapEditor){
		this.mapEditor = mapEditor;
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == mapEditor.mapPanel.ok){
			mapEditor.mapPanel.newMap();
			if(mapEditor.mapPanel.colNum > 0 && mapEditor.mapPanel.rowNum > 0){
				mapEditor.mapPanel.dialog.setVisible(false);
			}
		}
		else if(e.getSource() == mapEditor.mapPanel.cancel){
			mapEditor.mapPanel.dialog.setVisible(false);
		}
	}
	
}
