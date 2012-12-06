package me.odium.simplehelptickets.commands;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Arrays;

import me.ellbristow.mychunk.MyChunkChunk;
import me.odium.simplehelptickets.DBConnection;
import me.odium.simplehelptickets.SimpleHelpTickets;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
    } else if (args.length == 1 && args[0].equalsIgnoreCase("g") || args[0].equalsIgnoreCase("godkänn") || args[0].equalsIgnoreCase("approve") && player.hasPermission("sht.admin")) {

	    	if(MyChunkChunk.isClaimed(chunk) && player != null) {
		    	String admin = player.getName();
		            
		    	try {
		            con = plugin.mysql.getConnection();
		            stmt = con.createStatement();
		            
		            rs = stmt.executeQuery("SELECT COUNT(id) AS ticketTotal FROM SHT_Tickets WHERE owner='"+ ChunkOwner +"' AND status = 'DENIED' OR status = 'PENDING' AND admin='NONE' AND is_house='1'");
		            if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {
		              rs.next(); //sets pointer to first record in result set
		            }
		            if (rs.getInt("ticketTotal") == 0) {
		              player.sendMessage(ChatColor.RED + "Detta hus är redan accepterat!");
		              rs.close();
		              stmt.close();
		              return true;
		            }
		
		            rs = stmt.executeQuery("SELECT * FROM SHT_Tickets WHERE owner='"+ ChunkOwner +"'");
		            if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {
		              rs.next(); //sets pointer to first record in result set
		            }
		            
		            stmt.executeUpdate("UPDATE SHT_Tickets SET adminreply='Huset är godkänt', admin='"+ admin +"', status='ACCEPTED' WHERE owner='"+ ChunkOwner +"'");
		            player.sendMessage("Spelarens hus är nu godkänt!");
		            try {
		                rs = stmt.executeQuery("SELECT * FROM SHT_Tickets WHERE owner='"+ ChunkOwner +"'");
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
		    	player.sendMessage(ChatColor.RED + "Du måste stå i en chunk som ägs av en spelare!");
				return true;
		    }
		    
	    } else if (args.length >= 1 && args[0].equalsIgnoreCase("eg") || args[0].equalsIgnoreCase("ejgodkänt") || args[0].equalsIgnoreCase("denied") && player.hasPermission("sht.admin")) {
	    	
	    	if(args.length < 2){
	    		player.sendMessage(ChatColor.RED + "Du måste skriva en anledning!");
	    		return true;
	    	}
	    	
	    	if(MyChunkChunk.isClaimed(chunk) && player != null) {
		    	String admin = player.getName();
		        StringBuilder sb = new StringBuilder();
		        for (String arg : args)
		          sb.append(arg + " ");            
		            String[] temp = sb.toString().split(" ");
		            String[] temp2 = Arrays.copyOfRange(temp, 1, temp.length);
		            sb.delete(0, sb.length());
		            for (String details : temp2)
		            {
		              sb.append(details);
		              sb.append(" ");
		            }
		            String details = sb.toString().replace("'", "''");
		            
		    	try {
		            con = plugin.mysql.getConnection();
		            stmt = con.createStatement();
		            
		            rs = stmt.executeQuery("SELECT COUNT(id) AS ticketTotal FROM SHT_Tickets WHERE owner='"+ ChunkOwner +"' AND status = 'PENDING' AND is_house='1'");
		            if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {
		              rs.next(); //sets pointer to first record in result set
		            }
		            if (rs.getInt("ticketTotal") == 0) {
		              player.sendMessage(ChatColor.RED + "Detta hus är redan ej godkänt");
		              rs.close();
		              stmt.close();
		              return true;
		            }
		
		            rs = stmt.executeQuery("SELECT * FROM SHT_Tickets WHERE owner='"+ ChunkOwner +"'");
		            if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {
		              rs.next(); //sets pointer to first record in result set
		            }
		            
		            stmt.executeUpdate("UPDATE SHT_Tickets SET adminreply='"+ details +"', admin='"+ admin +"', status='DENIED' WHERE owner='"+ ChunkOwner +"'");
		            player.sendMessage("Spelarens hus är ej godkänt!");
		            try {
		                rs = stmt.executeQuery("SELECT * FROM SHT_Tickets WHERE owner='"+ ChunkOwner +"'");
		                if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {
		                  rs.next(); //sets pointer to first record in result set
		                }
		
		                Player target = Bukkit.getPlayer(rs.getString("owner"));          
		                if (target != null) {
		                  target.sendMessage(plugin.getMessage("HouseDeniedOWNER").replace("&admin", admin));
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
		    	player.sendMessage(ChatColor.RED + "Du måste stå i en chunk som ägs av en spelare!");
				return true;
		    }
	    	
	    }
    return true;
    /*
     * TODO: Göra så att när man satt ett hus till ej godkänt så ska byggaren av huset kunna göra /klart i sin tomt igen utan att skapa en ny "husticket"
     */
  }
  
}