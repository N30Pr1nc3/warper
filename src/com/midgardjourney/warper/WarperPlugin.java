package com.midgardjourney.warper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

//import de.myralia.jsonConfigReader;
import de.myralia.jsonConfigReader.ConfigParser;

import org.apache.commons.lang.math.NumberUtils;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.Files;



public class WarperPlugin extends JavaPlugin implements Listener {
	
	public static WarperPluginConfiguration config; 
	
	@Override	
	public void onEnable() {
		
		File folder = getDataFolder();
		
		if(!folder.exists())
		{
			folder.mkdirs();	
			//List<String> lines = Arrays.asList("The first line", "The second line");
			PrintWriter writer;
			try {
				writer = new PrintWriter(folder.getAbsolutePath()+"\\config.json", "UTF-8");writer.println("{\r\n");
				writer.println("\"dbPort\":\"\",");
				writer.println("\"dbName\":\"\",");
				writer.println("\"dbUser\":\"\",");
				writer.println("\"dbPass\":\"\"");
				writer.println("\r\n}");
				writer.close();
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			
			
						
		}
		WarperPlugin.config = (WarperPluginConfiguration)ConfigParser.parseFile(folder.getAbsolutePath()+"\\config.json", WarperPluginConfiguration.class);
		
				
		
//		//WarperPluginConfiguration config = (WarperPluginConfiguration)ConfigParser.parseFile(_filename, _type)
//		
//		if(getServer().getPluginManager().getPlugin("Citizens") == null || getServer().getPluginManager().getPlugin("Citizens").isEnabled() == false) {
//			getLogger().log(java.util.logging.Level.SEVERE, "Citizens 2.0 not found or not enabled");
//			getServer().getPluginManager().disablePlugin(this);	
//			return;
//		}	
//		Warper.itemLocation = new HashMap<Material, WarpLocation>();
//		Warper.warperList = new ArrayList<Warper>();
//		Warper.server="";
//		
//		getServer().getPluginManager().registerEvents(this, this);	
//		
//		net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(Warper.class).withName("warper"));

		System.out.println("Warper loaded");
	}

	static void loadWarpsNWarpers(){
		MySQLConnection.getLocations();
		for(int i= 0;i< Warper.warperList.size();i++){
			//System.out.println("lösche inventar von warper:"+Warper.warperList.get(i).getNPC().getFullName());
			Warper.warperList.get(i).fillInventory();
		}	
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if(cmd.getName().equalsIgnoreCase("warper")){					
			if(args.length==0){
				sender.sendMessage("Mögliche Kommandos für Warper:");
				sender.sendMessage("/warper reload - Läd die warplocations aus der datenbank neu");			
				
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
					sender.sendMessage("ungültige itemid / item nicht möglich");
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
				sender.sendMessage("Mögliche Kommandos für Dungeon:");
				sender.sendMessage("/dungeon leave - Du verlässt den aktuellen Dungeon");
				sender.sendMessage("/dungeon list  - Zeigt eine Liste aller Dungeons");
				return true;
			}
						
			if(args.length==1){
				System.out.println("es gibt 1 parameter");
				if(args[0].equals("leave")){
					System.out.println("es ist leave");
					if(sender instanceof Player ){
						System.out.println("der sender ist ein spieler");
						WarperPlugin.removePlayerFromDungeon((Player) sender);
						return true;
					}
					sender.sendMessage("Bitte Spielernamen angeben");
					return true;
				}
				if(args[0].equals("list")){
					for (WarpLocation location : Warper.itemLocation.values()) {
						if(!location.isDungeon()){
							continue;
						}
						ArrayList<Player> user = location.getUser();
						sender.sendMessage(location.toString(true));
						for(Player p :user){
							sender.sendMessage(" - "+p.getName());
						}					    
					}
					return true;
				}
			}
			if(args.length==2){
				System.out.println("es gibt 2 parameter");
				System.out.println(args[0]);
				System.out.println(args[1]);
				if(args[0].equals("leave")){
					System.out.println("es ist leave");
					System.out.println("parameter 2 ist "+args[1]);
					Player p = getServer().getPlayer(args[1]);
					if(p==null ){
						System.out.println("spilername passt wol nicht");
						sender.sendMessage("Bitte Spielernamen angeben");
						return true;
					}
					WarperPlugin.removePlayerFromDungeon(p);
					return true;
				}
				if(args[0].equals("start")){
					System.out.println("es ist start");
					System.out.println("parameter 2 ist "+args[1]);
					
					if(! NumberUtils.isNumber(args[1])){
						sender.sendMessage("die dungeonid ("+args[1]+") ist keine Zahl");
						return true;
					}
					int id = Integer.parseInt(args[1]);
					for (WarpLocation location : Warper.itemLocation.values()) {
						if(location.getId()!=id){
							continue;
						}
						if(!location.isDungeon()){
							sender.sendMessage("die dungeonid "+args[1]+"("+location.bez+") ist kein dungeon");
						}
						location.close();
					}
					return true;
				}
			}
			//warper reset myralia 10 10 10 5
			if(args.length == 6){
				if(args[0].equals("reset")){
					World world = Bukkit.getWorld(args[1]);
					if(world==null){
						sender.sendMessage("die Welt "+args[1]+" konnte nicht gefunden werden");
						return true;
					}
					if(! NumberUtils.isNumber(args[2])){
						sender.sendMessage("die Koordinate x ("+args[2]+") ist keine Zahl");
						return true;
					}
					if(! NumberUtils.isNumber(args[3])){
						sender.sendMessage("die Koordinate y ("+args[3]+") ist keine Zahl");
						return true;
					}
					if(! NumberUtils.isNumber(args[4])){
						sender.sendMessage("die Koordinate z ("+args[4]+") ist keine Zahl");
						return true;
					}
					if(! NumberUtils.isNumber(args[4])){
						sender.sendMessage("der radius ("+args[5]+") ist keine Zahl");
						return true;
					}
//					sender.sendMessage("Beginne mit der tötung");
					int x = Integer.parseInt(args[2]);
					int y = Integer.parseInt(args[3]);
					int z = Integer.parseInt(args[4]);
					int radius = Integer.parseInt(args[5]);
					
					Location center = new Location(world,x,y,z);
					
//					System.out.println(world.getName()+x+y+z+radius);
					
					for (Chunk chunk : world.getLoadedChunks())
					{
//						System.out.println("prüfe chunk");
						for (Entity e : chunk.getEntities())							
						{
//							System.out.println("prüfe entity"+e.toString());
							if (radius > 0)
							{
								if (center.distance(e.getLocation()) > radius)									
								{

//									System.out.println("Ist nicht im radius");
									continue;
								}
							}
							if (e instanceof HumanEntity)
							{

//								System.out.println("Ist menschlich");
								continue;
							}
							if (e instanceof Painting)
							{

//								System.out.println("Ist ein bild");
								continue;
							}
							if (e instanceof ItemFrame)
							{

//								System.out.println("Ist nicht ein itemframe");
								continue;
							}
							if (e instanceof EnderCrystal)
							{

//								System.out.println("Ist nicht ein endercristall");
								continue;
							}
//							sender.sendMessage("Töte "+e.toString());
							e.remove();
						}
					}
				}
			}
		}
		return false; // do this if you didn't handle the command.
	}  
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e){
		WarperPlugin.removePlayerFromDungeon(e.getPlayer());
	}
	
	@Override
	public void onDisable(){
		for (WarpLocation location : Warper.itemLocation.values()) {
			location.reset();
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e){
		for (WarpLocation location : Warper.itemLocation.values()) {
			System.out.println("versuche "+e.getEntity().getName()+" in "+location.bez+" zu resetten");
		    if(location.resettPlayer(e.getEntity())){
		    	return;
		    }
		}
	}
	
	public static void removePlayerFromDungeon(Player p){
		for (WarpLocation location : Warper.itemLocation.values()) {
			System.out.println("versuche "+p.getName()+" aus "+location.bez+" zu entfernen");
		    if(location.removePlayer(p)){
		    	return;
		    }
		}
		p.sendMessage("Du bist derzeit in keinem Dungeon");
	}
}



