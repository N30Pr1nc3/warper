package com.midgardjourney.warper;


//test
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;

public class Warper extends Trait {

	private Inventory inventar;
	WarperPlugin plugin = null;
	boolean SomeSetting = false;
	static HashMap<Material,WarpLocation> itemLocation ;	
	static String server ;
	public static ArrayList<String> serverList;
	public static ArrayList<Warper> warperList;
	
	public Warper() {		
		super("warper");	
		plugin = (WarperPlugin) Bukkit.getServer().getPluginManager().getPlugin("Warper");		
		warperList.add(this);
		//alle warperlocations aus der db laden
	}
	
	@EventHandler
	public void onClick (net.citizensnpcs.api.event.NPCRightClickEvent e) {
		//WarperPlugin.loadWarpsNWarpers();
		NPC m = e.getNPC();		
		if(m == null ){
			return;
		}
		if(this.getNPC()!=m){
			return;
		}

		if(this.inventar==null){
			this.fillInventory();
		}
		e.getClicker().openInventory(inventar);		
	}
	
	public void fillInventory(){
		this.inventar = null;
		this.inventar=Bukkit.createInventory(null,9,"Warper "+this.npc.getFullName());
		
		ArrayList<Material> locations = MySQLConnection.getWarperLocations(String.valueOf(this.npc.getId()));
		
		for (int j=0; j<locations.size();j++){
			if(!Warper.itemLocation.containsKey(locations.get(j))){
				continue;
			}
			WarpLocation w = Warper.itemLocation.get(locations.get(j));
			ItemStack i = new ItemStack(locations.get(j), 1);
			ItemMeta meta = i.getItemMeta();
			meta.setDisplayName("Warp");			
			ArrayList<String> lore = w.getBez();
			meta.setLore(lore);			
			i.setItemMeta(meta);			
			this.inventar.addItem(i);			
		}
	}
		
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
			
		if(event.getClickedInventory()==null || this.inventar == null){
			return;
		}        
		System.out.println(event.getClickedInventory().getName());
		System.out.println(this.npc.getFullName());
		System.out.println(this.inventar.getName());
		
		if(!event.getClickedInventory().equals(this.inventar)){
			return;
		}
        event.setCancelled(true);
		Player player = (Player) event.getWhoClicked();
		
		WarpLocation warplocation = Warper.itemLocation.get(event.getCurrentItem().getType());
		if(warplocation==null){
			return;
		}
		warplocation.tp(player);
	
	}	
	
	@Override
	public void onAttach() {
		WarperPlugin.loadWarpsNWarpers();
	}

	// Run code when the NPC is despawned. This is called before the entity actually despawns so npc.getBukkitEntity() is still valid.
	@Override
	public void onDespawn() {
	}

	//Run code when the NPC is spawned. Note that npc.getBukkitEntity() will be null until this method is called.
	//This is called AFTER onAttach and AFTER Load when the server is started.
	@Override
	public void onSpawn() {

	}

	//run code when the NPC is removed. Use this to tear down any repeating tasks.
	@Override
	public void onRemove() {
	}

}