package me.odium.simplehelptickets.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import me.odium.simplehelptickets.SimpleHelpTickets;
import me.odium.simplehelptickets.DBConnection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ticket implements CommandExecutor {   

  public SimpleHelpTickets plugin;
  public ticket(SimpleHelpTickets plugin)  {
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

  DBConnection service = DBConnection.getInstance();


  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  {    
    Player player = null;
    if (sender instanceof Player) {
      player = (Player) sender;
    }

    if(args.length == 0) {
      sender.sendMessage(plugin.GOLD+"[ SimpleHelpTickets ]");
      sender.sendMessage(plugin.getMessage("HelpMe_Line1"));
      sender.sendMessage(plugin.getMessage("HelpMe_Line2"));
    } else if(args.length > 0) {
    
    	
      StringBuilder sb = new StringBuilder();
      for (String arg : args)
        sb.append(arg + " ");            
          String[] temp = sb.toString().split(" ");
          String[] temp2 = Arrays.copyOfRange(temp, 0, temp.length);
          sb.delete(0, sb.length());
          for (String details : temp2)
          {
            sb.append(details);
            sb.append(" ");
          }
          String details = sb.toString();  
          if(details.startsWith("new")){
        	  sender.sendMessage(ChatColor.GOLD + "Du behöver inte längre skriva " + ChatColor.RED + "new" + ChatColor.GOLD + " i början när du gör en ticket.");
        	  return true;
          }
          
          Connection con;
          @SuppressWarnings("unused")
          java.sql.Statement stmt;

          // CONSOLE COMMANDS
          if (player == null) {
        	  
    	  return true;
        // PLAYER COMMANDs
          } else { 
        	  
        if(player.hasPermission("sht.ticket")){
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
            String status = "OPEN";
            String admin = "NONE";
            String expire = null;
            String is_house = "0";
            // REFERENCE CONNECTION AND ADD DATA
            if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {

              
              try {
                con = plugin.mysql.getConnection();
                
                Statement stmtCOUNT = con.createStatement();
                ResultSet rs = stmtCOUNT.executeQuery("SELECT COUNT(owner) AS MaxTickets FROM SHT_Tickets WHERE owner='"+owner+"' AND status='OPEN' AND is_house='0'");
                rs.next();
                final int ticketCount = rs.getInt("MaxTickets");
                int MaxTickets = plugin.getConfig().getInt("MaxTickets");
                
                if (ticketCount >= MaxTickets && !player.hasPermission("sht.admin")) {
                  sender.sendMessage(plugin.getMessage("TicketMax").replace("&arg", MaxTickets+""));
                  stmtCOUNT.close();
                  return true;                
                }
                
                if(details.length() <= 10){
                    sender.sendMessage(plugin.getMessage("TicketNotEnough"));
                    return true;    
                }
                
                stmt = con.createStatement();
                PreparedStatement statement = con.prepareStatement("INSERT INTO SHT_Tickets(description, date, owner, world, x, y, z, p, f, adminreply, userreply, status, admin, expiration, is_house) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
               
                    statement.setString(1, details);              
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
                    sender.sendMessage(plugin.getMessage("TicketOpen"));
                    
                    // Notify admin of new ticket
                    Player[] players = Bukkit.getOnlinePlayers();
                    for(Player op: players){
                      if(op.hasPermission("sht.admin") && op != player) {
                        String pl = owner;
                        op.sendMessage(plugin.getMessage("TicketOpenADMIN").replace("%player", pl));
                      }
                    }

              } catch (SQLException e) {
                sender.sendMessage(plugin.getMessage("Error").replace("&arg", e.toString()));
              }
              
            }else{
            	return true;
            }
          }else{
        	  player.sendMessage(plugin.getMessage("NoPermission"));
        	  return true;
          }
    }
    }

    return true;
  }
}