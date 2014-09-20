package creatures;

import java.util.List;
import java.util.Random;

import tile.TileMap;
import base.Creature;

public class SecondaryCreatures extends Creature{

	TileMap map;
	List<Creature> creatures;
	
	int high = 430; //for calculating random spawning point for tumbleweeds
	int low = 10;
	
	public SecondaryCreatures(){
		
	}
	
	public void update(TileMap map){
		
		this.map = map;
		creatures = map.creatures();
		updateTumbleWeeds();
	}
	
	public void updateTumbleWeeds(){
		int numberOfTumbleWeeds = 0;
		for(int i = 0; i < map.creatures().size(); i++){
			if(creatures.get(i).getID() == "tumbleweed"){
				numberOfTumbleWeeds++;
			}
		}
		if(numberOfTumbleWeeds < 20){
			Random r = new Random();
	//		int R = r.nextInt(high-low) + low;
	//		map.creatures().add(new TumbleWeed(50, R, "tumbleweed"));
		}
	}
}
