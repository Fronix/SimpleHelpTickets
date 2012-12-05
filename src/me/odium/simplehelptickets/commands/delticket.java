package me.odium.simplehelptickets.commands;

import java.sql.Connection;
import java.sql.ResultSet;

import me.odium.simplehelptickets.DBConnection;
import me.odium.simplehelptickets.SimpleHelpTickets;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class delticket implements CommandExecutor {   

  public SimpleHelpTickets plugin;
  public delticket(SimpleHelpTickets plugin)  {
    this.plugin = plugin;
  }

  DBConnection service = DBConnection.getInstance();
  ResultSet rs;
  java.sql.Statement stmt;
  java.sql.Statement stmt2;
  Connection con;

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  {    
    Player player = null;
    if (sender instanceof Player) {
      player = (Player) sender;
    }

    if(args.length == 0) {        
    	sender.sendMessage(plugin.replaceColorMacros(plugin.getOutputConfig().getString("UserCommandsDescription-delticket") + plugin.getOutputConfig().getString("UserCommandsMenu-delticket")));
		return true;
    } else if(args.length == 1) {

      for (char c : args[0].toCharArray()) {
        if (!Character.isDigit(c)) {
          sender.sendMessage(plugin.getMessage("InvalidTicketNumber").replace("&arg", args[0]));
          return true;
        }
      }
      
      int id = Integer.parseInt(args[0]);

//CONSOLE COMMANDS
      if (player == null) {
        try {
          if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {
            con = plugin.mysql.getConnection();
          } else {
          con = service.getConnection();
          }
          stmt = con.createStatement();
          //CHECK IF TICKET EXISTS
          rs = stmt.executeQuery("SELECT COUNT(id) AS ticketTotal FROM SHT_Tickets WHERE id='"+ id +"'");
          if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {
            rs.next(); //sets pointer to first record in result set
          }
          if (rs.getInt("ticketTotal") == 0) {
            sender.sendMessage(plugin.getMessage("TicketNotExist").replace("&arg", args[0]));
            rs.close();
            stmt.close();
            return true;
          }
          stmt.executeUpdate("DELETE FROM SHT_Tickets WHERE id='"+ id +"'");
          sender.sendMessage(plugin.getMessage("TicketDeleted"));

          stmt.close();
          return true;

        } catch(Exception e) {
          if (e.toString().contains("ResultSet closed")) {
            sender.sendMessage(plugin.getMessage("TicketNotExist").replace("&arg", args[0]));
            return true;
          } else {
          }
          sender.sendMessage(plugin.getMessage("Error").replace("&arg", e.toString()));
          return true;
        }
// PLAYER COMMANDS
    } else {
      try {
        if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {
          con = plugin.mysql.getConnection();
        } else {
        con = service.getConnection();
        }
        stmt = con.createStatement();
        //CHECK IF TICKET EXISTS
        rs = stmt.executeQuery("SELECT COUNT(id) AS ticketTotal FROM SHT_Tickets WHERE id='"+ id +"'");
        if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {
          rs.next(); //sets pointer to first record in result set
        }
        if (rs.getInt("ticketTotal") == 0) {
          sender.sendMessage(plugin.getMessage("TicketNotExist").replace("&arg", args[0]));
          rs.close();
          stmt.close();
          return true;
        }
        rs.close();
        rs = stmt.executeQuery("SELECT * FROM SHT_Tickets WHERE id='" + id + "'");
        if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {
          rs.next(); //sets pointer to first record in result set
        }
        String Playername = player.getName();
        if (!rs.getString("owner").contains(Playername) && !player.hasPermission("sht.admin")) {
        	sender.sendMessage(plugin.getMessage("NotYourTicketToDelete"));
          return true;
        } else {

            stmt.executeUpdate("DELETE FROM SHT_Tickets WHERE id='"+ id +"'");
            sender.sendMessage(plugin.getMessage("TicketDeleted"));
            rs.close();
            stmt.close();            
            return true;
        }
      } catch(Exception e) {
        sender.sendMessage(plugin.getMessage("Error").replace("&arg", e.toString()));
          return true;
        }     
      }
    }


 

  
    return true;
  }
}
