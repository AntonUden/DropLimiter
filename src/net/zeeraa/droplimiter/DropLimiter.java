package net.zeeraa.droplimiter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import net.zeeraa.droplimiter.command.DropLimiterCommand;
import net.zeeraa.droplimiter.config.DropLimiterConfig;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.command.CommandRegistry;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;

public class DropLimiter extends JavaPlugin implements Listener {
	private static DropLimiter instance;

	private boolean dropItems = true;
	private File configFile;
	private DropLimiterConfig dropLimiterConfig;

	public static DropLimiter getInstance() {
		return instance;
	}

	public boolean isDropItems() {
		return dropItems;
	}

	public void setDropItems(boolean dropItems) {
		this.dropItems = dropItems;
	}

	public DropLimiterConfig getDropLimiterConfig() {
		return dropLimiterConfig;
	}

	@Override
	public void onEnable() {
		DropLimiter.instance = this;

		saveDefaultConfig();

		configFile = new File(getDataFolder() + File.separator + "config.json");

		dropItems = getConfig().getBoolean("DropItemsEnabledAtStart");

		DropLimiterConfig.saveDefaultConfig(configFile);
		try {
			dropLimiterConfig = DropLimiterConfig.readConfig(configFile, getConfig().getBoolean("IgnoreCase"));
		} catch (Exception e) {
			e.printStackTrace();
			Log.fatal(getName(), "Failed to read config.json");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		CommandRegistry.registerCommand(new DropLimiterCommand());

		Bukkit.getServer().getPluginManager().registerEvents(this, this);

		Log.info(getName(), getName() + " has been enabled");
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll((Plugin) this);
		Bukkit.getScheduler().cancelTasks(this);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (dropLimiterConfig.isWorldDisabled(e.getEntity().getWorld())) {
			return;
		}

		e.setKeepInventory(true);

		List<ItemStack> toDrop = new ArrayList<>();

		ItemStack offHand = VersionIndependentUtils.get().getItemInOffHand(e.getEntity());

		if (offHand != null) {
			if (offHand.getType() != Material.AIR) {
				if (dropLimiterConfig.shouldDropItem(offHand)) {

					if (dropItems) {
						toDrop.add(offHand.clone());
					}

					VersionIndependentUtils.get().setItemInOffHand(e.getEntity(), ItemBuilder.AIR);
				}
			}
		}

		ItemStack[] armor = e.getEntity().getInventory().getArmorContents();

		for (int i = 0; i < armor.length; i++) {
			ItemStack item = armor[i];

			if (item == null) {
				continue;
			}

			if (item.getType() != Material.AIR) {
				continue;
			}

			if (dropLimiterConfig.shouldDropItem(item)) {
				if (dropItems) {
					toDrop.add(item.clone());
				}

				armor[i] = ItemBuilder.AIR;
			}
		}

		e.getEntity().getInventory().setArmorContents(armor);

		for (ItemStack item : e.getEntity().getInventory().getContents()) {
			if (item == null) {
				continue;
			}

			if (item.getType() == Material.AIR) {
				continue;
			}

			Log.trace("Checking item " + item.getType() + " with name " + ItemBuilder.getItemDisplayName(item));

			if (dropLimiterConfig.shouldDropItem(item)) {
				Log.trace("Item " + item.getType() + " with name " + ItemBuilder.getItemDisplayName(item) + " should drop");

				if (dropItems) {
					toDrop.add(item.clone());
				}

				e.getEntity().getInventory().remove(item);
			}
		}

		for (ItemStack item : toDrop) {
			e.getEntity().getLocation().getWorld().dropItemNaturally(e.getEntity().getLocation(), item);
		}
	}
}