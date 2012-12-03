package me.odium.simplehelptickets.commands;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import me.odium.simplehelptickets.DBConnection;
import me.odium.simplehelptickets.SimpleHelpTickets;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class checkticket implements CommandExecutor {   

  public SimpleHelpTickets plugin;
  public checkticket(SimpleHelpTickets plugin)  {
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

    if (args.length != 1) {
    	sender.sendMessage(plugin.replaceColorMacros(plugin.getOutputConfig().getString("UserCommandsDescription-checkticket") + plugin.getOutputConfig().getString("UserCommandsMenu-checkticket")));
    	return true;
    } else {

      for (char c : args[0].toCharArray()) {
        if (!Character.isDigit(c)) {
          //          sender.sendMessage(plugin.GRAY+"[SimpleHelpTickets] "+ChatColor.RED + "Invalid Ticket Number: " + ChatColor.WHITE + args[0]);
          sender.sendMessage(plugin.getMessage("InvalidTicketNumber").replace("&arg", args[0]));
          return true;
        }
      }

      int ticketNumber = Integer.parseInt(args[0]);


      try {
        if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {
          con = plugin.mysql.getConnection();
        } else {
          con = service.getConnection();
        }
        stmt = con.createStatement();

        rs = stmt.executeQuery("SELECT * FROM SHT_Tickets WHERE id='"+ticketNumber+"'");
        if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {
          rs.next(); //sets pointer to first record in result set
        }

        if (player == null || player.hasPermission("sht.admin") || rs.getString("owner").equalsIgnoreCase(player.getName())) {
          String world = null;
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
          if (plugin.getConfig().getBoolean("MultiWorld") == true) {
            world = rs.getString("world");
          }
          String description = rs.getString("description");
          String status = rs.getString("status");


          sender.sendMessage(plugin.getMessage("PrefixWithID").replace("&arg", id));
          sender.sendMessage(plugin.getMessage("CheckListOwner").replace("&arg", owner)); 
          sender.sendMessage(plugin.getMessage("CheckListDate").replace("&arg", date)); 
          if (plugin.getConfig().getBoolean("MultiWorld") == true) {
        	  sender.sendMessage(plugin.getMessage("CheckListWorld").replace("&arg", world)); 
          }
          if (status.contains("OPEN")) {
        	  sender.sendMessage(plugin.getMessage("CheckListStatus").replace("&arg", status));
          } else {
        	  sender.sendMessage(plugin.getMessage("CheckListStatus").replace("&arg", status));
          }
          if (admin.equalsIgnoreCase("NONE")){
        	  sender.sendMessage(plugin.getMessage("HouseCheckListAssigned").replace("&arg", plugin.getMessage("CheckListNoAdminAssigned")));
          }else{
        	  sender.sendMessage(plugin.getMessage("HouseCheckListAssigned").replace("&arg", admin));
          }
          sender.sendMessage(plugin.getMessage("CheckListDescription").replace("&arg", description));
          if (adminreply.equalsIgnoreCase("NONE")) {
        	  sender.sendMessage(plugin.getMessage("CheckListAdminReply").replace("&arg", plugin.getMessage("CheckListNoReply")));
          } else {
        	  sender.sendMessage(plugin.getMessage("CheckListAdminReply").replace("&arg", adminreply));
          }
          if (userreply.equalsIgnoreCase("NONE")) {
        	  sender.sendMessage(plugin.getMessage("CheckListUserReply").replace("&arg", plugin.getMessage("CheckListNoReply")));
          } else {
        	  sender.sendMessage(plugin.getMessage("CheckListUserReply").replace("&arg", userreply));
          }
          // IF AN EXPIRATION HAS BEEN APPLIED 
          if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {
            if (rs.getTimestamp("expiration") != null) {
              expiration = new SimpleDateFormat("dd/MMM/yy HH:mm").format(rs.getTimestamp("expiration"));
              sender.sendMessage(plugin.getMessage("CheckListStatus").replace("&arg", expiration));
            }
          } else {
            if (rs.getTimestamp("expiration") != null) {
            expiration = rs.getString("expiration");
            sender.sendMessage(plugin.getMessage("CheckListStatus").replace("&arg", expiration));
            }




            // COMPARE STRINGS
            //              int HasExpired = date.compareTo(expiration);
            //              if (HasExpired >= 0) {
            //                // plugin.log.info("ticket HAS expired!");
            //              } else {
            //                // plugin.log.info("ticket HAS NOT expired!");
            //              }
          }

          rs.close();          
        }

      } catch (SQLException e) {
        if (e.toString().contains("empty result set.")) {
          sender.sendMessage(plugin.getMessage("TicketNotExist").replace("&arg", args[0]));
          return true;          
        } else {
          sender.sendMessage(plugin.getMessage("Error").replace("&arg", e.toString()));
          return true;
        }
      }

      return true;

    }
  }
}


//    
//    
//    java.util.List<String> Tickets = plugin.getStorageConfig().getStringList("Tickets");
//    if (!Tickets.contains(args[0])) {
//      sender.sendMessage(plugin.GRAY+"[SimpleHelpTickets] "+ChatColor.RED + "Ticket " + ChatColor.WHITE + args[0] + ChatColor.RED + " does not exist.");
//      return true;
//    }      
//    int ticketno = Integer.parseInt( args[0] );
//    //      java.util.List<String> tickets = plugin.getStorageConfig().getStringList("Tickets");  
//    String placedby =  plugin.getStorageConfig().getString(ticketno+".placedby");
//    if (player != null && !placedby.contains(player.getDisplayName()) && !player.hasPermission("sht.admin")) {
//      sender.sendMessage(plugin.GRAY+"[SimpleHelpTickets] "+plugin.RED+"This is not your ticket to check");
//    } else {
//      String tickdesc = plugin.getStorageConfig().getString(ticketno+".description");
//      String date = plugin.getStorageConfig().getString(ticketno+".dates");        
//      //          String loc =  plugin.getStorageConfig().getString(ticketno+".location");
//      String reply = plugin.getStorageConfig().getString(ticketno+".reply");
//      String admin = plugin.getStorageConfig().getString(ticketno+".admin");        
//
//      sender.sendMessage(ChatColor.GOLD + "[ " + ChatColor.WHITE + "Ticket " + ticketno + ChatColor.GOLD + " ]");
//      sender.sendMessage(" " + ChatColor.BLUE + "Placed By: " + ChatColor.WHITE + placedby);
//      sender.sendMessage(" " + ChatColor.BLUE + "Date: " + ChatColor.WHITE + date);
//      sender.sendMessage(" " + ChatColor.BLUE + "Assigned Admin: " + ChatColor.WHITE + admin);
//      sender.sendMessage(" " + ChatColor.BLUE + "Ticket: " + ChatColor.GREEN + tickdesc);
//      sender.sendMessage(" " + ChatColor.BLUE + "Reply: " + ChatColor.YELLOW + reply);
//      return true;
//    }
//    return true;
//  }
//}
