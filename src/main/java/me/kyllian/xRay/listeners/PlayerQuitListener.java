package me.kyllian.xRay.listeners;

import me.kyllian.xRay.XRayPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private XRayPlugin plugin;

    public PlayerQuitListener(XRayPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        plugin.getPlayerHandler().removeData(event.getPlayer());
    }
}
