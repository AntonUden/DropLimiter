package net.zeeraa.droplimiter.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.zeeraa.droplimiter.DropLimiter;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaSubCommand;

public class EnableDrops extends NovaSubCommand {

	public EnableDrops() {
		super("enabledrops");

		setAllowedSenders(AllowedSenders.ALL);
		setRequireOp(true);
		setPermission("droplimiter.command.droplimiter.enabledrops");
		setPermissionDefaultValue(PermissionDefault.OP);
		setEmptyTabMode(true);
		setDescription("Enable item drops");
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		String message = DropLimiter.getInstance().isDropItems() ? "Drops already enabled" : "Drops enabled";

		DropLimiter.getInstance().setDropItems(true);

		sender.sendMessage(ChatColor.GREEN + message);
		return true;
	}
}