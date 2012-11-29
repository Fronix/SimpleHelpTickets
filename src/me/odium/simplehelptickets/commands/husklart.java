package me.odium.simplehelptickets.commands;

import me.odium.simplehelptickets.SimpleHelpTickets;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class husklart implements CommandExecutor {   

	  public SimpleHelpTickets plugin;
	  public husklart(SimpleHelpTickets plugin)  {
	    this.plugin = plugin;
	  }

	  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  {    
	    Player player = null;
	    if (sender instanceof Player) {
	      player = (Player) sender;
	      
		    if(args.length == 0) {        
		    	sender.sendMessage(plugin.replaceColorMacros(plugin.getOutputConfig().getString("UserCommandsDescription-klart") + plugin.getOutputConfig().getString("UserCommandsMenu-klart")));
				return true;
		    }
	    }else{
	    	return true;
	    }
	    return true;
	  }
}