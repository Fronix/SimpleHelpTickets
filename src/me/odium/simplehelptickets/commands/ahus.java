package me.odium.simplehelptickets.commands;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import me.ellbristow.mychunk.MyChunkChunk;
import me.odium.simplehelptickets.DBConnection;
import me.odium.simplehelptickets.SimpleHelpTickets;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.entity.Player;

public class ahus implements CommandExecutor {   

  public SimpleHelpTickets plugin;
  public ahus(SimpleHelpTickets plugin)  {
    this.plugin = plugin;
  }

  DBConnection service = DBConnection.getInstance();
  ResultSet rs;
  java.sql.Statement stmt;
  Connection con;

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  {    
    Player player = null;
    if (sender instanceof Player) {
      player = (Player) sender;
    }
    
    Chunk chunk = player.getLocation().getChunk();
    String ChunkOwner = MyChunkChunk.getOwner(chunk);
    
    if(args.length == 0 && player.hasPermission("sht.admin")) {      
    	plugin.displayHouseHelp(player);
        return true;
    } else if (args.length == 1 && args[0].equalsIgnoreCase("g") || args[0].equalsIgnoreCase("godk�nn") || args[0].equalsIgnoreCase("approve") && player.hasPermission("sht.admin")) {

	    	if(MyChunkChunk.isClaimed(chunk) && player != null) {
		    	String admin = player.getDisplayName();
		    	try {
		            con = plugin.mysql.getConnection();
		            stmt = con.createStatement();
		            
		            rs = stmt.executeQuery("SELECT COUNT(id) AS ticketTotal FROM SHT_Hus WHERE owner='"+ ChunkOwner +"' AND status != 'ACCEPTED' AND admin='NONE'");
		            if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {
		              rs.next(); //sets pointer to first record in result set
		            }
		            if (rs.getInt("ticketTotal") == 0) {
		              player.sendMessage(ChatColor.RED + "Detta hus �r redan accepterat!");
		              rs.close();
		              stmt.close();
		              return true;
		            }
		
		            rs = stmt.executeQuery("SELECT * FROM SHT_Hus WHERE owner='"+ ChunkOwner +"'");
		            if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {
		              rs.next(); //sets pointer to first record in result set
		            }
		            
		            stmt.executeUpdate("UPDATE SHT_Hus SET adminreply='Godk�nt', admin='"+ admin +"', status='ACCEPTED' WHERE owner='"+ ChunkOwner +"'");
		            player.sendMessage("Spelarens hus �r nu godk�nt!");
		            try {
		                rs = stmt.executeQuery("SELECT * FROM SHT_Hus WHERE owner='"+ ChunkOwner +"'");
		                if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {
		                  rs.next(); //sets pointer to first record in result set
		                }
		
		                Player target = Bukkit.getPlayer(rs.getString("owner"));          
		                if (target != null) {
		                  target.sendMessage(plugin.getMessage("HouseAcceptedOWNER").replace("&admin", admin));
		                  return true;
		                }
		            } catch(Exception e) {
		            	  player.sendMessage("Skicka till spelare");
		            	  player.sendMessage(plugin.getMessage("Error").replace("&arg", e.toString()));
		                  return true;        
		              }
	              } catch(Exception e) {
	            	  player.sendMessage(plugin.getMessage("Error").replace("&arg", e.toString()));
	                  return true;        
	              }
	            
		    } else if (!MyChunkChunk.isClaimed(chunk)){
		    	player.sendMessage(ChatColor.RED + "Du m�ste st� i en chunk som �gs av en spelare!");
				return true;
		    }
		    
	    } else if (args.length == 1 && args[0].equalsIgnoreCase("eg") || args[0].equalsIgnoreCase("ejgodk�nt") || args[0].equalsIgnoreCase("denied") && player.hasPermission("sht.admin")) {
	    	
	    	if(MyChunkChunk.isClaimed(chunk) && player != null) {
	    		player.sendMessage("Ej Godk�nt hus");
				return true;
		    } else {
		    	player.sendMessage(ChatColor.RED + "Du m�ste st� i en chunk som �gs av en spelare!");
				return true;
	  	    }
	    	
	    } else if (args.length == 1 && args[0].equalsIgnoreCase("d") || args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("tabort") && player.hasPermission("sht.house.delete")) {
	    	
	    	if(MyChunkChunk.isClaimed(chunk) && player != null) {
	    		player.sendMessage("Tabort hus ticket");
				return true;
		    } else {
		    	player.sendMessage(ChatColor.RED + "Du m�ste st� i en chunk som �gs av en spelare!");
				return true;
	  	    }
	    	
	    }
    return true;
    /*
     * TODO: Admin kommandon i denna fil
     * TODO: Kommandon som ska l�ggas till:
     * TODO: /ahus godk�nd(alias:g), ejgodk�nt(alias:eg)
     * TODO: G�ra s� att n�r man satt ett hus till ej godk�nt s� ska byggaren av huset kunna g�ra /klart i sin tomt igen utan att skapa en ny "husticket"
     */
  }
  
}