
package net.eduard.money.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.eduard.api.lib.storage.manager.CommandManager;

public class TemplateCommand extends CommandManager {

	public TemplateCommand() {
		super("comando");
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
		String[] args) {
	
		return true;
	}

}
