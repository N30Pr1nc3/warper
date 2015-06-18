package com.midgardjourney.warper;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class WarperPlugin extends JavaPlugin implements Listener {
	
	@Override
	public void onEnable() {		    
		if(getServer().getPluginManager().getPlugin("Citizens") == null || getServer().getPluginManager().getPlugin("Citizens").isEnabled() == false) {
			getLogger().log(java.util.logging.Level.SEVERE, "Citizens 2.0 not found or not enabled");
			getServer().getPluginManager().disablePlugin(this);	
			return;
		}	
		Warper.itemLocation = new HashMap<Material, WarpLocation>();
		Warper.warperList = new ArrayList<Warper>();
		Warper.server="";
		
		getServer().getPluginManager().registerEvents(this, this);		

		//Register your trait with Citizens.
		net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(Warper.class).withName("warper"));
		
	}

	static void loadWarpsNWarpers(){
		MySQLConnection.getLocations();
		for(int i= 0;i< Warper.warperList.size();i++){
			System.out.println("l�sche inventar von warper:"+Warper.warperList.get(i).getNPC().getFullName());
			Warper.warperList.get(i).fillInventory();
		}	
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if(cmd.getName().equalsIgnoreCase("warper")){		
			ItemMeta meta = ((Player)sender).getInventory().getItemInHand().getItemMeta();
			List<String> lore = meta.getLore();			
			
			System.out.println(lore);

			lore.add(HiddenStringUtils.encodeString("{MobsKilled: 0}"));

			lore.add("moep");
			
			meta.setLore(lore);
			((Player)sender).getInventory().getItemInHand().setItemMeta(meta);
			
			
			
			if(args.length==0){
				sender.sendMessage("M�gliche Kommandos f�r Warper:");
				sender.sendMessage("/warper reload - L�d die warplocations aus der datenbank neu");
				return true;
			}
			if(args[0].equalsIgnoreCase("reload")){
				WarperPlugin.loadWarpsNWarpers();	
				return true;
			}
			if(args[0].equalsIgnoreCase("create")){
				if(args.length!=2){
					sender.sendMessage("warper create <item>");
					return true;
				}
				Material material = Material.getMaterial(args[1]);
				if(material == null){
					sender.sendMessage("ung�ltige itemid / item nicht m�glich");
					return true;
				}
				if(Warper.itemLocation.containsKey(material)){
					sender.sendMessage("Es gibt bereits einen warp mit diesem item ");
					return true;
				}
				Player player = (Player)sender;
				MySQLConnection.addLocation(args[1],player.getLocation());
				WarperPlugin.loadWarpsNWarpers();
				return true;					
			}
		}
		if(cmd.getName().equalsIgnoreCase("dungeon")){
			if(args.length==0){
				sender.sendMessage("M�gliche Kommandos f�r Dungeon:");
				sender.sendMessage("/dungeon leave - Du verl�sst den aktuellen Dungeon");
				return true;
			}
			if(args.length==1){
				if(args[0].equals("leave")){
					if(sender instanceof Player ){
						WarperPlugin.removePlayerFromDungeon((Player) sender);
						return true;
					}
					sender.sendMessage("Bitte Spielernamen angeben");
					return true;
				}
			}
			if(args.length==2){
				if(args[0] == "leave"){
					
					sender.sendMessage("Bitte Spielernamen angeben");
					return true;
				}
			}			
		}
		return false; // do this if you didn't handle the command.
	}  
	
	public static void removePlayerFromDungeon(Player p){
		for (WarpLocation location : Warper.itemLocation.values()) {
		    if(location.removePlayer(p)){
		    	return;
		    }
		}
		p.sendMessage("Du bist derzeit in keinem Dungeon");
	}
}



