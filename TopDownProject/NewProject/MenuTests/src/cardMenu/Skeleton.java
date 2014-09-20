package cardMenu;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;


public class Skeleton implements ActionListener{

	JPanel cardHolder;
	CardLayout cards;
	String cardA = "A";
	String cardB = "B";
	
	JPanel Jboard;
	JPanel Jmenu;
	
	JFrame frame2;
	
	Board board = new Board();
	Menu menu = new Menu();
	
	boolean menuSet = true;
	boolean boardSet = false;
	
	Timer timer;
	
public class Switcher implements ActionListener{
	String card;

	Switcher(String card){
		this.card = card;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
			cards.show(cardHolder, card);
	}
}
	
	public Skeleton(JFrame frame){
		
		JFrame frame2 = frame;
		
		timer = new Timer(5, this);
		timer.start();
		
        cardHolder = new JPanel();
        cards = new CardLayout();
        cardHolder.setLayout(cards);
        cardHolder.add(menu, cardA);
        cardHolder.add(board, cardB);
        frame2.add(cardHolder);
        frame2.setVisible(true);
        
	}
	
	public JFrame getSkeleton(){
		return frame2;
	}
	
	public JPanel getCardHolder(){
		return cardHolder;
	}
	
	public void checkStatus(){
		if (menuSet == false){
			cards.show(cardHolder, cardB);
			board.setBoardset(true);
		}
		
		if (boardSet == false){
			cards.show(cardHolder, cardA);
			menu.setMenuset(true);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		menuSet = menu.getMenuset();
		boardSet = board.getBoardset();
		checkStatus();
	}
	
}
