package core;

import items.Bucket;

import java.util.ArrayList;

import player.Player;
import base.Creature;
import base.Item;
import creatures.Zombie;
import tile.TileMap;
import weapons.Axe;

public class CreatureManager {

	private int period = 20;
	private Player player;
	
	public ArrayList<Creature> possibleCreatures;
	
	/**
	 * 
	 * When adding creatures we have to manually add to the possible creatures
	 * here as well as add to the newClonedCreature functionality.
	 *
	 */
	public CreatureManager(){
		possibleCreatures = new ArrayList<Creature>();
		possibleCreatures.add(new Zombie());
		
		possibleCreatures.add(new Zombie());
		possibleCreatures.add(new Zombie());
		possibleCreatures.add(new Zombie());
		possibleCreatures.add(new Zombie());
		possibleCreatures.add(new Zombie());
		possibleCreatures.add(new Zombie());
		possibleCreatures.add(new Zombie());
		possibleCreatures.add(new Zombie());
	}
	
	public void initializeCreatures(TileMap map){
		map.creatures().add(new Zombie(2500, 1700, "zombie", player));
	}
	
	public Creature newClonedCreature(Creature creature){
		Creature newCreature = null;
		switch (creature.getID()){
		case "zombie" : newCreature = new Zombie();
		break;
		}
		return newCreature;
	}
	
	public void updateCreatures(TileMap map, Player player){
		for(int i = 0; i < map.relevantCreatures().size(); i++) {
			Creature c = map.relevantCreatures().get(i);
			c.updateCreature(map, period);
			player.playerCollision(map, c);
		}
	}
}
