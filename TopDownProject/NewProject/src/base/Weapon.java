package base;

public class Weapon extends Item{

	double damageLevel;
	
	public Weapon(){
		super(0, 0, null);
	}
	
	public Weapon(String ID) { 
		super(0, 0, ID);
	}
	
	public Weapon(int pixelX, int pixelY, String ID){
		super(pixelX, pixelY, ID);
		super.ID = ID;
	}
	
	public void setDamageLevel(double dl){
		this.damageLevel = dl;
	}
	
	public double getDamageLevel(){
		return damageLevel;
	}
}
