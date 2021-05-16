package net.zeeraa.droplimiter.command;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.md_5.bungee.api.ChatColor;
import net.zeeraa.droplimiter.DropLimiter;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class DropLimiterCommand extends NovaCommand {

	public DropLimiterCommand() {
		super("droplimiter", DropLimiter.getInstance());

		setAllowedSenders(AllowedSenders.ALL);
		setRequireOp(true);
		setPermission("droplimiter.command.droplimiter");
		setPermissionDefaultValue(PermissionDefault.OP);
		setEmptyTabMode(true);

		addSubCommand(new EnableDrops());
		addSubCommand(new DisableDrops());

		addHelpSubCommand();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		sender.sendMessage(ChatColor.GOLD + "Use " + ChatColor.AQUA + "/droplimiter help " + ChatColor.GOLD + "for help");
		return false;
	}
}