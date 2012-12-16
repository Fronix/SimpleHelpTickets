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


public class teleport implements CommandExecutor {   

  public SimpleHelpTickets plugin;
  public teleport(SimpleHelpTickets plugin)  {
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
    	sender.sendMessage("/tickettp <id>");
		return true;
    }else if(args.length == 0 && player == null){
    	sender.sendMessage("/tickettp <id> <admin>");
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
    		sender.sendMessage("Usage: /tickettp <admin> <id>");
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
    		      
    		      // compile location
    		      String id = rs.getString("id");
    		      World world = Bukkit.getWorld(rs.getString("world"));
    		      double x = rs.getDouble("x");        
    		      double y = rs.getDouble("y");
    		      double z = rs.getDouble("z");
    		      float p = (float) rs.getDouble("p");
    		      float f = (float) rs.getDouble("f");
    		      final Location locc = new Location(world, x, y, z, f, p);
    		      
    		      /* Don't teleport when run from console. Would complicate stuff with the website.
    		       */
    		      if (AssignToAdmin.isOnline()) {
    		    	  AssignToAdmin.sendMessage(plugin.getMessage("TakeTicketAssignedADMINTP"));
    		    	  AssignToAdmin.teleport(locc);
    		      }else{
    		    	  sender.sendMessage("Spelaren var inte online eller så gick något fel");
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
    	if(args.length < 1){
    		sender.sendMessage("Usage: /tickettp <id>");
    		return true;
    	}
    	if(player.hasPermission("sht.admin")){
        //Player command
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
    		      
    		      // compile location
    		      String id = rs.getString("id");
    		      World world = Bukkit.getWorld(rs.getString("world"));
    		      double x = rs.getDouble("x");        
    		      double y = rs.getDouble("y");
    		      double z = rs.getDouble("z");
    		      float p = (float) rs.getDouble("p");
    		      float f = (float) rs.getDouble("f");
    		      final Location locc = new Location(world, x, y, z, f, p);

		    	  player.sendMessage(plugin.getMessage("TakeTicketAssignedADMINTP"));
		    	  player.teleport(locc);
    		
    		      stmt.close();
    		      rs.close();
    		      return true;
    		    } catch(Exception e) {
    		      if (e.toString().contains("ResultSet closed")) {
    		    	  player.sendMessage(plugin.getMessage("TicketNotExist").replace("&arg", args[0]));
    		        return true;
    		      } else if (e.toString().contains("java.lang.ArrayIndexOutOfBoundsException")) {
    		    	  player.sendMessage(plugin.getMessage("TicketNotExist").replace("&arg", args[0]));
    		        return true;
    		      } else  if (e.toString().contains("empty result set.")) {
    		    	  player.sendMessage(plugin.getMessage("TicketNotExist").replace("&arg", args[0]));
    		        return true;          
    		      } else {
    		    	  player.sendMessage(plugin.getMessage("Error").replace("&arg", e.toString()));
    		        return true;
    		      }
    		    }
    	}else{
    		sender.sendMessage(plugin.getMessage("NoPermission"));
    		return true;
    	}
    }    
  }

}