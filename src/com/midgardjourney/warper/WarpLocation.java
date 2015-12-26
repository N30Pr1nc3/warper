// anzeigen f�r wie viel personen ein dungeon ist
// dungon 1 voin x spileer
//dungeon resetten wenn der spilere gekickt/get�tet wird wird
//dungeon reset


package com.midgardjourney.warper;
//test
import java.io.File;
import java.util.ArrayList;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


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
	private boolean started = false;

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
		if(this.dungeon>0){
			return true;
		}
		return false;
	}

	public void addPlayer(Player player) {
		if(this.users.contains(player)){
			return;
		}
		this.users.add(player);
		if(this.users.size() >= this.dungeon){
			this.accessible = false;
		}
	}
	public boolean removePlayer(Player player) {
		return this.removePlayer(player,true);
	}		
	
	private boolean removePlayer(Player player,Boolean reset ) {
		if (this.users.contains(player)){
			System.out.println("Spieler ist im dungeon");
			this.users.remove(this.users.indexOf(player));
			if(!this.started){
				this.accessible = true;
			}
			if(reset && this.users.size()==0 ){				
				this.reset();
			}
			try {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "warp Dungeonmaster "+player.getName());	
			} catch (Exception e2) {
			}
			
			
			return true;
		}
		return false;
	}

	public boolean canTravel(Player player) {
		if(this.users.contains(player)){
			return true;
		}
		return this.accessible;
	}


	public int getId() {
		return this.id;
	}

	public void close() {
		this.accessible = false;
		this.started = true;
	}
	
	public void reset(){
		if(! this.users.isEmpty()){
			for(Player p:this.users){
				this.removePlayer(p,false);
			}
		}
		MySQLConnection.reset(this.id, this.world);
		this.accessible = true;
		this.started = false;
	}

	public ArrayList<Player> getUser() {
		return this.users;		
	}
	
	
	
	@Override
	public String toString() {
		return this.toString(false);
	}
	
	public String toString(Boolean all) {
		String ret ="";
		if(this.accessible){
			ret = ChatColor.GREEN+"";
		}else{
			ret = ChatColor.RED+"";
		}
		
		if(all){
			ret =ret.concat(String.valueOf(this.getId())+": ");
		}
		ret =ret.concat(this.world);
	    if(all){
	    	ret =ret.concat(".");
	    	ret =ret.concat(String.valueOf(this.x)+".");
	    	ret =ret.concat(String.valueOf(this.y)+".");
	    	ret =ret.concat(String.valueOf(this.z));
	    }
	    ret =ret.concat(" "+this.bez+" - ");
	    ret =ret.concat(String.valueOf(this.users.size())+" von ");
	    ret =ret.concat(String.valueOf(this.dungeon)+" Spieler");
		
		return ret;
		
		
	}

	public boolean resettPlayer(Player player) {
		if (this.users.contains(player)){
			System.out.println("Spieler ist im dungeon");
			this.tp(player);			
			
			return true;
		}
		return false;
	}

	public void tp(Player player) {
		System.out.println("porte spieler "+player);
		Location location = this.getLocation();

		if(location == null){
			player.sendMessage("Warplocation ist ung�ltig");
			return;
		}
		if(!this.canTravel(player)){
			player.sendMessage("Dort sind schon zu viele Spieler. Bitte versuche es sp�ter erneut");
			return;
		}		
		player.teleport(location);
		if(this.isDungeon()){
			player.sendMessage("du wurdest zum Dungeon gesendet");
			this.addPlayer(player);
		}
		
	}
}
