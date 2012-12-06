package me.odium.simplehelptickets.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.ellbristow.mychunk.MyChunkChunk;
import me.odium.simplehelptickets.DBConnection;
import me.odium.simplehelptickets.SimpleHelpTickets;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class husklart implements CommandExecutor {   

	  public SimpleHelpTickets plugin;
	  public husklart(SimpleHelpTickets plugin)  {
	    this.plugin = plugin;
	  }
	  
	  String date;
	  String owner;
	  String world;
	  double locX;
	  double locY;
	  double locZ;
	  double locP;
	  double locF;
	  String reply;
	  String status;
	  String admin;
	  Block block;
	  
	  DBConnection service = DBConnection.getInstance();

	  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  {    
	    Player player = null;
	    if (sender instanceof Player) {
	      player = (Player) sender;
	    }
      	
	    if(player != null && player.hasPermission("sht.house.klart")) {

            Connection con;
            @SuppressWarnings("unused")
            java.sql.Statement stmt;
	            
            // SET VARIABLES
            String date = plugin.getCurrentDTG("date");
            String owner = player.getName();
            String world = player.getWorld().getName();
            double locX = player.getLocation().getX();
            double locY = player.getLocation().getY();
            double locZ = player.getLocation().getZ();
            double locP = player.getLocation().getPitch();
            double locF = player.getLocation().getYaw();
            String adminreply = "NONE";
            String userreply = "NONE";
            String status = "PENDING";
            String admin = "NONE";
            String expire = null;
            String is_house = "1";
            
            Chunk chunk = player.getLocation().getChunk();
            String ChunkOwner = MyChunkChunk.getOwner(chunk);
            
            
            // REFERENCE CONNECTION AND ADD DATA
            if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {

              
              try {
                con = plugin.mysql.getConnection();
                
                Statement stmtCOUNT = con.createStatement();
                ResultSet rs = stmtCOUNT.executeQuery("SELECT COUNT(owner) AS MaxTickets FROM SHT_Tickets WHERE owner='"+owner+"' AND is_house='1' AND status='PENDING' OR status='DENIED' OR status='ACCEPTED' ");
                rs.next();
                final int ticketCount = rs.getInt("MaxTickets");
                int MaxTickets = plugin.getConfig().getInt("MaxHouseInspect");
                
                if (ticketCount >= MaxTickets && !player.hasPermission("sht.admin")) {
                  sender.sendMessage(plugin.getMessage("HouseMaxInspect"));
                  stmtCOUNT.close();
                  return true;                
                }
                
                if(MyChunkChunk.isClaimed(chunk)){
	                if(ChunkOwner.equalsIgnoreCase(player.getName())){
		                stmt = con.createStatement();
		                PreparedStatement statement = con.prepareStatement("insert into SHT_Tickets(description, date, owner, world, x, y, z, p, f, adminreply, userreply, status, admin, expiration, is_house) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
		               
		                    statement.setString(1, plugin.getMessage("HouseInspection"));              
		                    statement.setString(2, date);             
		                    statement.setString(3, owner);
		                    statement.setString(4, world);
		                    statement.setDouble(5, locX);
		                    statement.setDouble(6, locY);
		                    statement.setDouble(7, locZ);
		                    statement.setDouble(8, locP);
		                    statement.setDouble(9, locF);
		                    statement.setString(10, adminreply);
		                    statement.setString(11, userreply);
		                    statement.setString(12, status);
		                    statement.setString(13, admin);
		                    statement.setString(14, expire);
		                    statement.setString(15, is_house);
		
		                    statement.executeUpdate();
		                    statement.close();
		                    // Message player and finish
		                    sender.sendMessage(plugin.getMessage("HouseNew_Line1"));
		                    sender.sendMessage(plugin.getMessage("HouseNew_Line2"));
		                    
		                    // Notify admin of new ticket
		                    Player[] players = Bukkit.getOnlinePlayers();
		                    for(Player op: players){
		                      if(op.hasPermission("sht.admin") && op != player) {
		                    	  String pl = owner;
		                        op.sendMessage(plugin.getMessage("HouseNewInspect").replace("%player", pl));
		                      }
		                    }
	                }else{
	                	sender.sendMessage(plugin.getMessage("ChunkNotOwn"));
	                	return true;
	                }
                }else{
                	sender.sendMessage(plugin.getMessage("ChunkNotOwn"));
                	return true;
                }

              } catch (SQLException e) {
                sender.sendMessage(plugin.getMessage("Error").replace("&arg", e.toString()));
              }
              
            }
            /* THIS REQUIRES MYSQL!!!!!! */
	    }else{
	    	player.sendMessage(plugin.getMessage("NoPermission"));
	    	return true;
	    }
	    return true;
	  }
}