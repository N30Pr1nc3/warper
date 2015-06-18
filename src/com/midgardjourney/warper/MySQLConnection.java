package com.midgardjourney.warper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
 
public class MySQLConnection{
private static Connection con = null;
private static String dbHost = "myralia.de"; // Hostname
private static String dbPort = "3306";      // Port -- Standard: 3306
private static String dbName = "minecraft_warper";   // Datenbankname
private static String dbUser = "minecraft_warper";     // Datenbankuser
private static String dbPass = "swE5DvYzbdWqmqtA6cXpzATD";      // Datenbankpasswort
 
private MySQLConnection(){
    try {
        Class.forName("com.mysql.jdbc.Driver"); // Datenbanktreiber für JDBC Schnittstellen laden.
 
        // Verbindung zur JDBC-Datenbank herstellen.
        con = DriverManager.getConnection("jdbc:mysql://"+dbHost+":"+ dbPort+"/"+dbName+"?"+"user="+dbUser+"&"+"password="+dbPass);
    } catch (ClassNotFoundException e) {
        System.out.println("Treiber nicht gefunden");
    } catch (SQLException e) {
        System.out.println("Verbindung nicht moglich");
        System.out.println("SQLException: " + e.getMessage());
        System.out.println("SQLState: " + e.getSQLState());
        System.out.println("VendorError: " + e.getErrorCode());
    }
  }
 
private static Connection getInstance(){
    if(con == null){
        new MySQLConnection();
    }
    try {
		if(con.isClosed()){
		    new MySQLConnection();
		}
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    return con;
}
 
  //Gebe Tabelle in die Konsole aus
  public static void getLocations(){

      con = getInstance();

      if(con != null){
      // Abfrage-Statement erzeugen.
      Statement query;
     
      try {
          query = con.createStatement();
 
          // Tabelle anzeigen
          String sql =
                "SELECT * FROM locations where geloescht = ''";
          ResultSet result = query.executeQuery(sql);
 
          //bisherige liste leeren
          Warper.itemLocation.clear();
        // Ergebnisstabelle durchforsten
          while (result.next()) {
        	  Material material = Material.getMaterial(result.getString("material"));
        	  if(material==null){
        		  System.out.println("Das Material '"+result.getString("material")+"' konnte nicht gefunden werden");
        		  continue;
        	  }
        	  String world = result.getString("world");
        	  Integer x = result.getInt("x");
        	  Integer y = result.getInt("y");
        	  Integer z = result.getInt("z");
        	  Integer id = result.getInt("id");
        	  String bez = result.getString("bez");
        	  float pitch = result.getFloat("pitch");
        	  float yaw = result.getFloat("yaw");
        	  Integer dungeon = result.getInt("yaw");
        	  
	          String info = "lege neue Location an " + material + ", " + world ;
	          Warper.itemLocation.put(material, new WarpLocation(id,world, x,y,z ,bez,pitch,yaw,dungeon));
	          
	          System.out.println(info);
          }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }
  
  public static ArrayList<Material> getWarperLocations(String npcId ){
      con = getInstance();
      
      if(con == null){
    	  return null;
      }
      // Abfrage-Statement erzeugen.
      Statement query;
     
      try {
          query = con.createStatement();
 
          // Tabelle anzeigen
          String sql =
                "SELECT * FROM warperLocations where npcID = '"+npcId+"'";
          ResultSet result = query.executeQuery(sql);
          
          ArrayList<Material> ret = new ArrayList<Material>();
          
          while (result.next()) {
        	  Material material = Material.getMaterial(result.getString("location"));
        	  if(material==null){
        		  System.out.println("Das Material '"+result.getString("location")+"' konnte nicht gefunden werden");
        		  continue;
        	  }        	  
	          ret.add(material);
          }
          return ret;
      } catch (SQLException e) {
        e.printStackTrace();
      }
	return null;
  }

  public static void addLocation(String string, Location location) {
	con = getInstance();

	if(con != null){
	Statement query;
	
	try {
	    query = con.createStatement();
	    String sql =  "insert into locations set ";
	    sql = sql +  " material= '"+string+"', ";
	    sql = sql +  " server= 'myralia', ";
	    sql = sql +  " world= '"+location.getWorld().getName()+"', ";
	    sql = sql +  " x= '"+location.getBlockX()+"', ";
	    sql = sql +  " y= '"+location.getBlockY()+"', ";
	    sql = sql +  " z= '"+location.getBlockZ()+"', ";
	    sql = sql +  " pitch= '"+location.getPitch()+"', ";
	    sql = sql +  " yaw= '"+location.getYaw()+"', ";
	    sql = sql +  " bez= '' ";
	    query.execute(sql);
	} catch (SQLException e) {
	  e.printStackTrace();
	}
	}
	
	}
}
