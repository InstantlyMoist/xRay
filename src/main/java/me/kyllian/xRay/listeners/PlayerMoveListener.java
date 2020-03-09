package me.kyllian.xRay.listeners;

import me.kyllian.xRay.XRayPlugin;
import me.kyllian.xRay.player.PlayerData;
import me.kyllian.xRay.tasks.TaskType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    private XRayPlugin plugin;

    public PlayerMoveListener(XRayPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.getPlayerHandler().getPlayerData(player);
        boolean update = false;
        if (data.inXray() && !event.getFrom().getChunk().equals(event.getTo().getChunk()) && data.getTask().getType() == TaskType.CHUNK) update = true;
        if (data.inXray() && (event.getTo().getBlockX() != event.getFrom().getBlockX() || event.getTo().getBlockY() != event.getFrom().getBlockY() || event.getTo().getBlockZ() != event.getFrom().getBlockZ()) && data.getTask().getType() == TaskType.BLOCK) update = true;
        if (update) {
            data.getTask().update();
            data.getTask().send();
            return;
        }
    }
}
