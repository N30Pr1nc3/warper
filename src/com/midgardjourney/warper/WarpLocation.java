package com.midgardjourney.warper;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WarpLocation {
	private Integer id;
	//public String server;
	private String world;
	public String bez;
	public ArrayList<String>lore;
	private int x;
	private int y;
	private int z;
	private float pitch;
	private float yaw;
	private int dungeon; 
	private ArrayList<Player> users; 
	private boolean accessible;

	WarpLocation(int id, String world, int x, int y, int z, String bez,float pitch,float yaw ,Integer dungeon){
		this.x=x;
		this.y=y;
		this.z=z;
		this.world=world;
		//this.server= server;
		this.id = id;
		//Bukkit.getWorld(world);
		this.bez = bez;
		this.pitch=pitch;
		this.yaw=yaw;
		this.generateLore();
		this.dungeon=dungeon;
		this.accessible = true;
		this.users = new ArrayList<Player>();
	}
	
	public Location getLocation(){
		World world= Bukkit.getWorld(this.world);
		if(world==null){
			return null;
		}
		return new Location(world, this.x, this.y, this.z,this.yaw,this.pitch);
	}
	
	public void generateLore(){
		//System.out.println(this.bez);
		this.lore=null;
		this.lore = new ArrayList<String>();
		String split[] = this.bez.split("\\n");
		for(int i = 0; i < split.length; i++){
		    lore.add(split[i]);
		}
	}
	
	public ArrayList<String>  getBez(){				
		return this.lore;
	}

	public Boolean isDungeon(){
//		if(this.dungeon>0){
//			return true;
//		}
		return false;
	}

	public void addPlayer(Player player) {
		this.users.add(player);
		if(this.users.size() < this.dungeon){
			this.accessible= false;
		}
	}
	
	public boolean removePlayer(Player player) {
		if (this.users.contains(player)){
			this.users.remove(this.users.indexOf(player));
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "warp Dungeonmaster "+player.getName());
			if(this.users.size()==0){
				this.accessible = true;
			}
			return true;
		}
		return false;
	}

	public boolean canTravel() {
		return this.accessible;
	}
}
