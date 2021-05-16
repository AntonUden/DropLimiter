package net.zeeraa.droplimiter.config;

import org.bukkit.Material;

public class MaterialEntry {
	private Material material;
	private byte dataValue;
	
	public MaterialEntry(Material material, byte dataValue) {
		this.material = material;
		this.dataValue = dataValue;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public byte getDataValue() {
		return dataValue;
	}
}