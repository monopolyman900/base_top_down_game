package admin;

import java.awt.MenuItem;

import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.MenuElement;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TabChangeListener implements ChangeListener{

	JTabbedPane tabbedPane;
	JMenuBar menuBar;
	AdminPanel adminPanel;
	MenuElement[] menuItems;
	
	public TabChangeListener(JTabbedPane pane, JMenuBar menuBar, AdminPanel adminPanel){
		this.adminPanel = adminPanel;
		this.tabbedPane = pane;
		this.menuBar = menuBar;
		menuItems = menuBar.getSubElements();
	}

	@Override
	//this can control what is enabled/disabled in the menu
	public void stateChanged(ChangeEvent e) {
		//loop through stuff to find specific menu items
		if(tabbedPane.getSelectedIndex() == 0){
			adminPanel.setMapEditorDisplayed(true);
		}
		else if(tabbedPane.getSelectedIndex() != 0){
			adminPanel.setMapEditorDisplayed(false);
			for(int i = 0; i < menuItems.length; i++){
				MenuElement[] subItems = menuItems[i].getSubElements();
				for(int j = 0; j < subItems.length; j++){
					MenuElement[] subSubItems = subItems[j].getSubElements();
					for(int k = 0; k < subSubItems.length; k++){
						if(subSubItems[k].getComponent().getName() == "importItems");
						subSubItems[k].getComponent().setEnabled(false);
					}
				}
			}
		}
	}
}
