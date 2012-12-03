package me.odium.simplehelptickets.commands;

import me.odium.simplehelptickets.SimpleHelpTickets;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class house implements CommandExecutor {   

  public SimpleHelpTickets plugin;
  public house(SimpleHelpTickets plugin)  {
    this.plugin = plugin;
  }

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  {
	  
	  if(args.length == 0){
		  plugin.displayHouseHelp(sender);
		  return true;
	  }
	  return true;
  }

}
