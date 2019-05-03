package me.kyllian.xRay.listeners;

import me.kyllian.xRay.XRayPlugin;
import me.kyllian.xRay.utils.PlayerData;
import me.kyllian.xRay.utils.Task;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    private XRayPlugin plugin;

    public PlayerMoveListener(XRayPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.getPlayerHandler().getPlayerData(player);
        if (data.inXray() && !event.getFrom().getChunk().equals(event.getTo().getChunk()) && data.getTask().getTaskType() == Task.TaskType.CHUNK) {
            data.getTask().setCancelled(true);
            data.getTask().cancel();
            data.setTask(null);
            plugin.getxRayHandler().send(player);
            return;
        }
        if (data.inXray() && (event.getTo().getBlockX() != event.getFrom().getBlockX() || event.getTo().getBlockY() != event.getFrom().getBlockY() || event.getTo().getBlockZ() != event.getFrom().getBlockZ())) {
            data.getTask().setCancelled(true);
            data.getTask().cancel();
            data.setTask(null);
            plugin.getxRayHandler().send(player);
            return;
        }
    }
}
