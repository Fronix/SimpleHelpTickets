package me.odium.simplehelptickets.listeners;

import java.sql.Connection;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.odium.simplehelptickets.DBConnection;
import me.odium.simplehelptickets.SimpleHelpTickets;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PListener implements Listener {

  public SimpleHelpTickets plugin;
  public PListener(SimpleHelpTickets plugin) {
    this.plugin = plugin;    
    Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
  }

  DBConnection service = DBConnection.getInstance();
  ResultSet rs;
  java.sql.Statement stmt;
  Connection con;

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerJoin(PlayerJoinEvent event) {      
    Player player = event.getPlayer();
    // IF PLAYER IS ADMIN
    if (player.hasPermission("sht.admin")) {
      boolean DisplayTicketAdmin = plugin.getConfig().getBoolean("OnJoin.DisplayTicketAdmin");      
      if (DisplayTicketAdmin == true) {

        try {        
          if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {
            con = plugin.mysql.getConnection();
          } else {
            con = service.getConnection();
          }
          stmt = con.createStatement();

          rs = stmt.executeQuery("SELECT COUNT(id) AS ticketTotal FROM SHT_Tickets WHERE status='OPEN' AND admin='NONE' AND is_house='0'");
          if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {
            rs.next(); //sets pointer to first record in result set
          }

          int ticketTotal = rs.getInt("ticketTotal");
          if (ticketTotal == 0) {
            // DO NOTHING
          } else if(ticketTotal > 0) {
            player.sendMessage(plugin.getMessage("AdminJoin").replace("&arg", ticketTotal+""));
          }
          
          rs = stmt.executeQuery("SELECT COUNT(id) AS ticketTotal FROM SHT_Tickets WHERE status='PENDING' AND admin='NONE' AND is_house='1'");
          if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {
            rs.next(); //sets pointer to first record in result set
          }

          ticketTotal = rs.getInt("ticketTotal");
          if (ticketTotal == 0) {
            // DO NOTHING
            rs.close();
            stmt.close();
          } else if(ticketTotal > 0) {
            player.sendMessage(plugin.getMessage("AdminJoinHouse").replace("&arg", ticketTotal+""));
            rs.close();
            stmt.close();
          }
        } catch(Exception e) {
          plugin.log.info(plugin.getMessage("Error").replace("&arg", e.toString()));
        }

      }
      // IF PLAYER IS USER      
    } else {
      boolean DisplayTicketUser = plugin.getConfig().getBoolean("OnJoin.DisplayTicketUser");
      try {   
        if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {
          con = plugin.mysql.getConnection();
        } else {
          con = service.getConnection();
        }

        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT COUNT(id) AS ticketTotal FROM SHT_Tickets WHERE owner='"+player.getName()+"' AND is_house='0'" );
                if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {
                  rs.next(); //sets pointer to first record in result set
                }
        int ticketTotal = rs.getInt("ticketTotal");
        if (ticketTotal == 0) {
          // DO NOTHING
          rs.close();
        } else if(ticketTotal > 0) {
          rs = stmt.executeQuery("SELECT COUNT(id) AS ticketTotal2 FROM SHT_Tickets WHERE owner='"+player.getName()+"' AND status='OPEN'" );
          if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {
            rs.next(); //sets pointer to first record in result set
          }
          int ticketTotaltwo = rs.getInt("ticketTotal2");
          
          if(ticketTotaltwo > 0){
          rs.close();
	          rs = stmt.executeQuery("SELECT * FROM SHT_Tickets WHERE owner='"+player.getName()+"' AND is_house='0'" );
	          if (plugin.getConfig().getBoolean("MySQL.USE_MYSQL")) {
	            rs.next(); //sets pointer to first record in result set
	          }
	          String adminreply = rs.getString("adminreply");
	          String id = rs.getString("id");
	          if (DisplayTicketUser == true) {
	
	            if (adminreply.equalsIgnoreCase("NONE")) {
	            	if(ticketTotal > 1){
	            		player.sendMessage(plugin.getMessage("UserJoin").replace("&arg", ticketTotal+""));
	            	}else{
	            		player.sendMessage(plugin.getMessage("UserJoinOne").replace("&arg", ticketTotal+""));
	            	}
	              rs.close();
	            } else {
	              player.sendMessage(ChatColor.GOLD + "Du har fått svar på din ticket, skriv "+ ChatColor.AQUA +"/tinfo " + id + ChatColor.GOLD + " för att se svaret");
	              rs.close();
	            }
	
	          }
          }

      	}
      
      } catch(Exception e) {
        plugin.log.info(plugin.getMessage("Error").replace("&arg", e.toString()));
      }
    }
  }
}

