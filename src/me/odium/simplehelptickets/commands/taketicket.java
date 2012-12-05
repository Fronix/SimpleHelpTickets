package me.odium.simplehelptickets.commands;

import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

import me.odium.simplehelptickets.DBConnection;
import me.odium.simplehelptickets.SimpleHelpTickets;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class taketicket implements CommandExecutor {   

  public SimpleHelpTickets plugin;
  public taketicket(SimpleHelpTickets plugin)  {
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

    if(args.length == 0 && player != null) {        
    	sender.sendMessage(plugin.replaceColorMacros(plugin.getOutputConfig().getString("AdminCommandsDescription-taketicket") + plugin.getOutputConfig().getString("AdminCommandsMenu-taketicket")));
		return true;
    }else if(args.length == 0 && player == null){
    	sender.sendMessage(plugin.replaceColorMacros(plugin.getOutputConfig().getString("ConsoleCommandsDescription-taketicket") + plugin.getOutputConfig().getString("ConsoleCommandsMenu-taketicket")));
    	return true;
    }
    
    for (char c : args[0].toCharArray()) {
      if (!Character.isDigit(c)) {
        sender.sendMessage(plugin.getMessage("InvalidTicketNumber").replace("&arg", args[0]));
        return true;
      }
    }

    int ticketNumber = Integer.parseInt( args[0] );
    String ConsoleAdmin = "";
    
    if(player == null){
    	
	if(args.length != 2){
		sender.sendMessage("Usage: /tansvar <id> <admin>");
		return true;
	}
        ConsoleAdmin = args[1];
        Player AssignToAdmin = plugin.getServer().getPlayer(ConsoleAdmin);
    //Console command
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
		      String worldName = null;
		      String date;
		      
		      // compile location     
		      World world = Bukkit.getWorld(rs.getString("world"));
		      double x = rs.getDouble("x");        
		      double y = rs.getDouble("y");
		      double z = rs.getDouble("z");
		      float p = (float) rs.getDouble("p");
		      float f = (float) rs.getDouble("f");
		      final Location locc = new Location(world, x, y, z, f, p);
		      // Display Ticket
		      rs = stmt.executeQuery("SELECT * FROM SHT_Tickets WHERE id='"+ticketNumber+"'");
		      if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {
		        rs.next(); //sets pointer to first record in result set
		      }
		
		      String id = rs.getString("id");
		      String owner = rs.getString("owner");
		      if (plugin.getConfig().getBoolean("MultiWorld") == true) {
		        worldName = rs.getString("world");
		      }
		
		      if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {
		        date = new SimpleDateFormat("dd/MMM/yy HH:mm").format(rs.getTimestamp("date"));  
		      } else {
		        date = rs.getString("date");
		      }      
		
		      String status = rs.getString("status");
		
		      if (status.equalsIgnoreCase("CLOSED")) {        
		        sender.sendMessage(plugin.getMessage("CannotTakeClosedTicket").replace("&arg", id));
		        stmt.close();
		        rs.close();
		        return true;
		      }
		      // NOTIFY ADMIN AND USERS
		      String admin = AssignToAdmin.getDisplayName();
		      Player target = plugin.getServer().getPlayer(owner);
		      
		      // TELEPORT THE ASSIGNED ADMIN AND NOTIFY HIM
		      if (!owner.equalsIgnoreCase("CONSOLE") && AssignToAdmin != target && AssignToAdmin.isOnline()) {
		    	  AssignToAdmin.sendMessage(plugin.getMessage("TakeTicketAssignedADMIN").replace("&arg", id));
		    	  AssignToAdmin.sendMessage(plugin.getMessage("TakeTicketAssignedADMINTP"));
		    	  AssignToAdmin.teleport(locc);
		      }
		      // ASSIGN ADMIN
		      stmt.executeUpdate("UPDATE SHT_Tickets SET admin='"+admin+"' WHERE id='"+id+"'");
		      // NOTIFY -OTHER- ADMINS 
		      Player[] players = Bukkit.getOnlinePlayers();
		      for(Player op: players){
		        if(op.hasPermission("sht.admin") && op != AssignToAdmin && AssignToAdmin != target) {
		          op.sendMessage(plugin.getMessage("TakeTicketADMIN").replace("&arg", id).replace("&admin", admin));
		        }
		      }
		      // NOTIFY USER
		      if (target != null && target != AssignToAdmin) {
		        target.sendMessage(plugin.getMessage("TakeTicketOWNER").replace("&arg", id).replace("&admin", admin));   
		
		        stmt.close();
		        rs.close();
		        return true;
		      }
		
		      stmt.close();
		      rs.close();
		      return true;
		    } catch(Exception e) {
		      if (e.toString().contains("ResultSet closed")) {
		    	AssignToAdmin.sendMessage(plugin.getMessage("TicketNotExist").replace("&arg", args[0]));
		        return true;
		      } else if (e.toString().contains("java.lang.ArrayIndexOutOfBoundsException")) {
		    	  AssignToAdmin.sendMessage(plugin.getMessage("TicketNotExist").replace("&arg", args[0]));
		        return true;
		      } else  if (e.toString().contains("empty result set.")) {
		    	  AssignToAdmin.sendMessage(plugin.getMessage("TicketNotExist").replace("&arg", args[0]));
		        return true;          
		      } else {
		    	  AssignToAdmin.sendMessage(plugin.getMessage("Error").replace("&arg", e.toString()));
		        return true;
		      }
		    }
    //Console command end
    }else{
    //Player command start
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
	      String worldName = null;
	      String date;
	
	      // compile location     
	      World world = Bukkit.getWorld(rs.getString("world"));
	      double x = rs.getDouble("x");        
	      double y = rs.getDouble("y");
	      double z = rs.getDouble("z");
	      float p = (float) rs.getDouble("p");
	      float f = (float) rs.getDouble("f");
	      final Location locc = new Location(world, x, y, z, f, p);
	      // Display Ticket
	      rs = stmt.executeQuery("SELECT * FROM SHT_Tickets WHERE id='"+ticketNumber+"'");
	      if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {
	        rs.next(); //sets pointer to first record in result set
	      }
	
	      String id = rs.getString("id");
	      String owner = rs.getString("owner");
	      if (plugin.getConfig().getBoolean("MultiWorld") == true) {
	        worldName = rs.getString("world");
	      }
	
	      if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {
	        date = new SimpleDateFormat("dd/MMM/yy HH:mm").format(rs.getTimestamp("date"));  
	      } else {
	        date = rs.getString("date");
	      }      
	
	      String Assignedadmin = rs.getString("admin");
	      String adminreply = rs.getString("adminreply");
	      String userreply = rs.getString("userreply");
	      String description = rs.getString("description");
	      String status = rs.getString("status");
	
	      if (status.equalsIgnoreCase("CLOSED")) {        
	        sender.sendMessage(plugin.getMessage("CannotTakeClosedTicket").replace("&arg", id));
	        stmt.close();
	        rs.close();
	        return true;
	      }
	      
          sender.sendMessage(plugin.getMessage("PrefixWithID").replace("&arg", id));
          sender.sendMessage(plugin.getMessage("CheckListOwner").replace("&arg", owner)); 
          sender.sendMessage(plugin.getMessage("CheckListDate").replace("&arg", date)); 
          if (plugin.getConfig().getBoolean("MultiWorld") == true) {
        	  sender.sendMessage(plugin.getMessage("CheckListWorld").replace("&arg", worldName)); 
          }
          if (status.contains("OPEN")) {
        	  sender.sendMessage(plugin.getMessage("CheckListStatus").replace("&arg", status));
          } else {
        	  sender.sendMessage(plugin.getMessage("CheckListStatus").replace("&arg", status));
          }
          sender.sendMessage(plugin.getMessage("CheckListAssigned").replace("&arg", Assignedadmin));
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
	
	      // TELEPORT ADMIN
	      if (!owner.equalsIgnoreCase("CONSOLE")) {
	        player.teleport(locc);
	      }
	      // NOTIFY ADMIN AND USERS
	      String admin = player.getDisplayName();
	      Player target = plugin.getServer().getPlayer(owner);
	      // ASSIGN ADMIN
	      stmt.executeUpdate("UPDATE SHT_Tickets SET admin='"+admin+"' WHERE id='"+id+"'");
	      // NOTIFY -OTHER- ADMINS 
	      Player[] players = Bukkit.getOnlinePlayers();
	      for(Player op: players){
	        if(op.hasPermission("sht.admin") && op != player) {
	          op.sendMessage(plugin.getMessage("TakeTicketADMIN").replace("&arg", id).replace("&admin", admin));
	        }
	      }
	      // NOTIFY USER
	      if (target != null && target != player) {
	        target.sendMessage(plugin.getMessage("TakeTicketOWNER").replace("&arg", id).replace("&admin", admin));   
	
	        stmt.close();
	        rs.close();
	        return true;
	      }
	
	      stmt.close();
	      rs.close();
	      return true;
	    } catch(Exception e) {
	      if (e.toString().contains("ResultSet closed")) {
	        sender.sendMessage(plugin.getMessage("TicketNotExist").replace("&arg", args[0]));
	        return true;
	      } else if (e.toString().contains("java.lang.ArrayIndexOutOfBoundsException")) {
	        sender.sendMessage(plugin.getMessage("TicketNotExist").replace("&arg", args[0]));
	        return true;
	      } else  if (e.toString().contains("empty result set.")) {
	        sender.sendMessage(plugin.getMessage("TicketNotExist").replace("&arg", args[0]));
	        return true;          
	      } else {
	        sender.sendMessage(plugin.getMessage("Error").replace("&arg", e.toString()));
	        return true;
	      }
	    }
    }//Player command end


  }
}


//    
//    
//    
//    
//    // Make sure ticket exists
//    if (!plugin.getStorageConfig().contains(args[0])) {
//      sender.sendMessage(plugin.GRAY+"[SimpleHelpTickets] "+plugin.WHITE+"Ticket " + ChatColor.GOLD + ticketno + ChatColor.WHITE + " Does Not Exist");
//      return true;
//    } else {
//
//      String tickdesc = plugin.getStorageConfig().getString(ticketno+".description");
//      String date = plugin.getStorageConfig().getString(ticketno+".dates");
//      String placedby =  plugin.getStorageConfig().getString(ticketno+".placedby");
//      String loc =  plugin.getStorageConfig().getString(ticketno+".location");
//      String reply = plugin.getStorageConfig().getString(ticketno+".reply");
//      String admin = plugin.getStorageConfig().getString(ticketno+".admin");
//
//      plugin.getStorageConfig().set(ticketno+".admin", player.getDisplayName());      
//      plugin.saveStorageConfig();
//
//      if (loc.contains("none")) { // if console ticket
//        sender.sendMessage(ChatColor.GOLD + "[ " + ChatColor.WHITE + "Ticket " + ticketno + ChatColor.GOLD + " ]");
//        sender.sendMessage(" " + ChatColor.BLUE + "Placed By: " + ChatColor.WHITE + placedby);
//        sender.sendMessage(" " + ChatColor.BLUE + "Date: " + ChatColor.WHITE + date);
//        sender.sendMessage(" " + ChatColor.BLUE + "Location: " + ChatColor.RED + "None [Console Ticket]");
//        sender.sendMessage(" " + ChatColor.BLUE + "Assigned Admin: " + ChatColor.WHITE + admin);
//        sender.sendMessage(" " + ChatColor.BLUE + "Ticket: " + ChatColor.GREEN + tickdesc);
//        sender.sendMessage(" " + ChatColor.BLUE + "Reply: " + ChatColor.YELLOW + reply);          
//        String tickuser = plugin.myGetPlayerName(placedby);
//        if(plugin.getServer().getPlayer(tickuser) == null) {
//          return true;  
//        } else {
//          String admin1 = player.getDisplayName();
//          Player target = plugin.getServer().getPlayer(tickuser);
//          String TicketReview = plugin.getConfig().getString("TicketBeingReviewedMsg");
//          if (TicketReview.equalsIgnoreCase("DEFAULT")) {
//            target.sendMessage(plugin.GRAY+"[SimpleHelpTickets] "+ChatColor.GOLD + admin1 + ChatColor.WHITE + " is reviewing your help ticket");
//            return true;
//          } else {
//            target.sendMessage(ChatColor.GREEN + TicketReview);
//            return true;
//          }
//
//        }
//      } else {
//        // compile location
//        String[] vals = loc.split(",");
//        World world = Bukkit.getWorld(vals[0]);
//        double x = Double.parseDouble(vals[1]);        
//        double y = Double.parseDouble(vals[2]);
//        double z = Double.parseDouble(vals[3]);
//        Location locc = new Location(world, x, y, z);
//        player.teleport(locc);
//        sender.sendMessage(ChatColor.GOLD + "[ " + ChatColor.WHITE + "Ticket " + ticketno + ChatColor.GOLD + " ]");
//        sender.sendMessage(" " + ChatColor.BLUE + "Placed By: " + ChatColor.WHITE + placedby);
//        sender.sendMessage(" " + ChatColor.BLUE + "Date: " + ChatColor.WHITE + date);
//        sender.sendMessage(" " + ChatColor.BLUE + "Assigned Admin: " + ChatColor.WHITE + admin);
//        sender.sendMessage(" " + ChatColor.BLUE + "Ticket: " + ChatColor.GREEN + tickdesc);
//        sender.sendMessage(" " + ChatColor.BLUE + "Reply: " + ChatColor.YELLOW + reply);
//        //      sender.sendMessage(" " + ChatColor.BLUE + "Location: " + ChatColor.GREEN + tickLOC);
//        String tickuser = plugin.myGetPlayerName(placedby);
//        if(plugin.getServer().getPlayer(tickuser) == null) {
//          return true;  
//        } else {
//          String admin1 = player.getDisplayName();
//          Player target = plugin.getServer().getPlayer(tickuser);
//          String TicketReview = plugin.getConfig().getString("TicketBeingReviewedMsg");
//          if (TicketReview.equalsIgnoreCase("DEFAULT")) {
//            target.sendMessage(plugin.GRAY+"[SimpleHelpTickets] "+ChatColor.GOLD + admin1 + ChatColor.WHITE + " is reviewing your help ticket");
//            return true;
//          } else {
//            target.sendMessage(ChatColor.GREEN + TicketReview);
//            return true;
//          }
//
//        }
//      }
//    }
//  }
//}