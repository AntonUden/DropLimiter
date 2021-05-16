package net.zeeraa.droplimiter.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.md_5.bungee.api.ChatColor;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;

public class DropLimiterConfig {
	private List<String> disabledWorlds;
	private List<String> itemNamesToDrop;
	private List<MaterialEntry> materialToDrop;

	private boolean ignoreCase;

	public static DropLimiterConfig readConfig(File file, boolean ignoreCase) throws JSONException, IOException {
		return new DropLimiterConfig(JSONFileUtils.readJSONObjectFromFile(file), ignoreCase);
	}

	public DropLimiterConfig(JSONObject json, boolean ignoreCase) {
		this.disabledWorlds = new ArrayList<>();
		this.itemNamesToDrop = new ArrayList<>();
		this.materialToDrop = new ArrayList<>();

		this.ignoreCase = ignoreCase;

		if (json.has("disabled_worlds")) {
			JSONArray disabledWorldsJson = json.getJSONArray("disabled_worlds");

			for (int i = 0; i < disabledWorldsJson.length(); i++) {
				disabledWorlds.add(disabledWorldsJson.getString(i));
			}
		} else {
			// Sample data
			disabledWorlds.add("example_world_1");
			disabledWorlds.add("example_world_2");
			disabledWorlds.add("example_world_3");
		}

		if (json.has("item_names_to_drop")) {
			JSONArray itemNamesToDropJson = json.getJSONArray("item_names_to_drop");

			for (int i = 0; i < itemNamesToDropJson.length(); i++) {
				itemNamesToDrop.add(itemNamesToDropJson.getString(i));
			}
		} else {
			// Sample data
			itemNamesToDrop.add("Example item name 1");
			itemNamesToDrop.add("Example item name 2");
			itemNamesToDrop.add("Example item name 3");
		}

		if (json.has("material_to_drop")) {
			JSONArray materialToDropJson = json.getJSONArray("material_to_drop");

			for (int i = 0; i < materialToDropJson.length(); i++) {
				JSONObject entry = materialToDropJson.getJSONObject(i);

				String name = entry.getString("material");

				byte data = -1;

				if (entry.has("data")) {
					data = (byte) entry.getInt("data");
				}

				try {
					Material material = Material.valueOf(name);

					materialToDrop.add(new MaterialEntry(material, data));
				} catch (Exception e) {
					Log.error("DropLimiter", "Could not find material: " + name);
				}
			}
		} else {
			// Sample data
			materialToDrop.add(new MaterialEntry(Material.BEDROCK, (byte) -1));
			materialToDrop.add(new MaterialEntry(Material.BARRIER, (byte) -1));
		}
	}

	public JSONObject jsonEncode() {
		JSONObject result = new JSONObject();

		JSONArray disabledWorldsJson = new JSONArray();
		JSONArray itemNamesToDropJson = new JSONArray();
		JSONArray materialToDropJson = new JSONArray();

		for (String disabledWorld : disabledWorlds) {
			disabledWorldsJson.put(disabledWorld);
		}

		for (String itemNameToDrop : itemNamesToDrop) {
			itemNamesToDropJson.put(itemNameToDrop);
		}

		for (MaterialEntry data : materialToDrop) {
			JSONObject entry = new JSONObject();

			entry.put("material", data.getMaterial().name());

			if (data.getDataValue() >= 0) {
				entry.put("data", data.getDataValue());
			}

			materialToDropJson.put(entry);
		}

		result.put("disabled_worlds", disabledWorldsJson);
		result.put("item_names_to_drop", itemNamesToDropJson);
		result.put("material_to_drop", materialToDropJson);

		return result;
	}

	public List<String> getDisabledWorlds() {
		return disabledWorlds;
	}

	public List<String> getItemNamesToDrop() {
		return itemNamesToDrop;
	}

	public List<MaterialEntry> getMaterialToDrop() {
		return materialToDrop;
	}

	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	@SuppressWarnings("deprecation")
	public boolean shouldDropItem(ItemStack item) {
		for (MaterialEntry entry : materialToDrop) {
			if (item.getType() == entry.getMaterial()) {
				if (entry.getDataValue() > -1) {
					if (item.getData().getData() == entry.getDataValue()) {
						return true;
					}
				} else {
					return true;
				}
			}
		}

		String displayName = ChatColor.stripColor(ItemBuilder.getItemDisplayName(item));

		if (ItemBuilder.hasItemDisplayName(item)) {
			for (String name : itemNamesToDrop) {
				if (compareStrings(displayName, name)) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean isWorldDisabled(World world) {
		for (String worldName : disabledWorlds) {
			if (compareStrings(world.getName(), worldName)) {
				return true;
			}
		}
		return false;
	}

	public boolean compareStrings(String str1, String str2) {
		return ignoreCase ? str1.equalsIgnoreCase(str2) : str1.equals(str2);
	}

	public boolean saveConfig(File file) {
		try {
			JSONFileUtils.saveJson(file, this.jsonEncode(), 4);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean saveDefaultConfig(File file) {
		if (file.exists()) {
			return false;
		}

		return new DropLimiterConfig(new JSONObject(), false).saveConfig(file);
	}
}