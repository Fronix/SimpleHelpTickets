package me.odium.simplehelptickets.commands;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.odium.simplehelptickets.DBConnection;
import me.odium.simplehelptickets.SimpleHelpTickets;



public class housestatus implements CommandExecutor {   

  public SimpleHelpTickets plugin;
  public housestatus(SimpleHelpTickets plugin)  {
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

	    if (args.length > 0) {
	     	sender.sendMessage(plugin.replaceColorMacros(plugin.getOutputConfig().getString("UserCommandsDescription-checkticket") + plugin.getOutputConfig().getString("UserCommandsMenu-checkticket")));
	    	return true;
	    } else {
	    	
	    	if(player.hasPermission("sht.house.klart")){
	    		
	    	try {
		        if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {
		          con = plugin.mysql.getConnection();
		        } else {
		          con = service.getConnection();
		        }
		        stmt = con.createStatement();
	
		        rs = stmt.executeQuery("SELECT * FROM SHT_Tickets WHERE owner='" + player.getName() + "' AND is_house='1'");
		        if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {
		          rs.next(); //sets pointer to first record in result set
		        }
		        
		        if(rs.getString("status").equalsIgnoreCase("ACCEPTED")){
		        	player.sendMessage(ChatColor.RED + "Ditt hus �r redan accepterat!");
		        	rs.close();
		        	return true;
		        }else if (rs.getString("owner").equalsIgnoreCase(player.getName())) {
		          String date;
		          String expiration;
	
		          String id = rs.getString("id");
		          String owner = rs.getString("owner");
	
		          if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {
		            date = new SimpleDateFormat("dd/MMM/yy HH:mm").format(rs.getTimestamp("date"));  
		          } else {
		            date = rs.getString("date");
		          }
	
	
		          String admin = rs.getString("admin");
		          String adminreply = rs.getString("adminreply");
		          String userreply = rs.getString("userreply");
		          String description = rs.getString("description");
		          String status = rs.getString("status");
	
		          sender.sendMessage(plugin.getMessage("HousePrefixWithID").replace("&arg", id));
		          sender.sendMessage(plugin.getMessage("HouseCheckListOwner").replace("&arg", owner)); 
		          sender.sendMessage(plugin.getMessage("HouseCheckListDate").replace("&arg", date)); 
		          if (status.contains("PENDING")) {
		        	  sender.sendMessage(plugin.getMessage("HouseCheckListStatus").replace("&arg", plugin.getMessage("HousePending")));
		          }
		          else if(status.contains("DENIED")){
		        	  sender.sendMessage(plugin.getMessage("HouseCheckListStatus").replace("&arg", plugin.getMessage("HouseDenied")));
		          }
		          else{
		        	  sender.sendMessage(plugin.getMessage("HouseCheckListStatus").replace("&arg", plugin.getMessage("HouseAccepted")));
		          }
		          if (admin.equalsIgnoreCase("NONE")){
		        	  sender.sendMessage(plugin.getMessage("HouseCheckListAssigned").replace("&arg", plugin.getMessage("CheckListNoAdminAssigned")));
		          }else{
		        	  sender.sendMessage(plugin.getMessage("HouseCheckListAssigned").replace("&arg", admin));
		          }
		          sender.sendMessage(plugin.getMessage("HouseCheckListDescription").replace("&arg", description));
		          if (adminreply.equalsIgnoreCase("NONE")) {
		        	  sender.sendMessage(plugin.getMessage("HouseCheckListAdminReply").replace("&arg", plugin.getMessage("HouseCheckListNoReply")));
		          } else {
		        	  sender.sendMessage(plugin.getMessage("HouseCheckListAdminReply").replace("&arg", adminreply));
		          }
		          if (userreply.equalsIgnoreCase("NONE")) {
		        	  sender.sendMessage(plugin.getMessage("HouseCheckListUserReply").replace("&arg", plugin.getMessage("HouseCheckListNoReply")));
		          } else {
		        	  sender.sendMessage(plugin.getMessage("HouseCheckListUserReply").replace("&arg", userreply));
		          }
		          /* IF AN EXPIRATION HAS BEEN APPLIED 
		           * Not sure if we should use this for house tickets
		           */
		          if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {
		            if (rs.getTimestamp("expiration") != null) {
		              expiration = new SimpleDateFormat("dd/MMM/yy HH:mm").format(rs.getTimestamp("expiration"));
		              sender.sendMessage(plugin.getMessage("HouseCheckListStatus").replace("&arg", expiration));
		            }
		          } else {
		            if (rs.getTimestamp("expiration") != null) {
		            expiration = rs.getString("expiration");
		            sender.sendMessage(plugin.getMessage("HouseCheckListStatus").replace("&arg", expiration));
		            }
		          }
		          rs.close();          
		        }
	
		      } catch (SQLException e) {
		        sender.sendMessage(ChatColor.RED + "Du har ingen hus ticket!");
		      }
	    	}else{
	    		player.sendMessage(plugin.getMessage("NoPermission"));
	    		return true;
	    	}
	      return true;

	    }
	  }
  }