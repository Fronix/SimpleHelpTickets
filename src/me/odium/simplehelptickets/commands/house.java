package me.odium.simplehelptickets.commands;

import me.odium.simplehelptickets.SimpleHelpTickets;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class house implements CommandExecutor {   

  public SimpleHelpTickets plugin;
  public house(SimpleHelpTickets plugin)  {
    this.plugin = plugin;
  }

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  {
	  
	  if (sender instanceof Player) {
		  Player player = (Player) sender;
		  
		  if(args.length == 0 && sender.hasPermission("sht.house")){
			  plugin.displayHouseHelp(sender);
			  return true;
		  }else{
			  player.sendMessage(plugin.getMessage("NoPermission"));
			  return true;
		  }
	  }
	  return true;
  }

}
