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
    
    /*
     * TODO: Admin kommandon i denna fil
     * TODO: Kommandon som ska läggas till:
     * TODO: /ahus godkänn(alias:g), ejgodkänt(alias:eg)
     * TODO: Göra så att när man satt ett hus till ej godkänt så ska byggaren av huset kunna göra /klart i sin tomt igen utan att skapa en ny "husticket"
     */
    return true;
  }
  
}