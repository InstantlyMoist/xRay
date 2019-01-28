package me.kyllian.xRay.utils;

import org.bukkit.ChatColor;

public class ColorTranslate {

	public static String cc(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
}
