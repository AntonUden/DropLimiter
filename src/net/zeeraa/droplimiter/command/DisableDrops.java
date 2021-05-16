package net.zeeraa.droplimiter.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.zeeraa.droplimiter.DropLimiter;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaSubCommand;

public class DisableDrops extends NovaSubCommand {

	public DisableDrops() {
		super("disabledrops");

		setAllowedSenders(AllowedSenders.ALL);
		setRequireOp(true);
		setPermission("droplimiter.command.droplimiter.disabledrops");
		setPermissionDefaultValue(PermissionDefault.OP);
		setEmptyTabMode(true);
		setDescription("Delete items instead of dropping them");
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		String message = DropLimiter.getInstance().isDropItems() ? "Drops disabled" : "Drops already disabled";

		DropLimiter.getInstance().setDropItems(false);

		sender.sendMessage(ChatColor.GREEN + message);
		return true;
	}
}
