package me.kyllian.xRay.listeners;

import me.kyllian.xRay.XRayPlugin;
import me.kyllian.xRay.tasks.BlockTask;
import me.kyllian.xRay.tasks.ChunkTask;
import me.kyllian.xRay.utils.PlayerData;
import me.kyllian.xRay.utils.TaskType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
        if (data.inXray() && !event.getFrom().getChunk().equals(event.getTo().getChunk()) && data.getTask().getTaskType() == TaskType.CHUNK) {
            ChunkTask chunkTask = (ChunkTask) data.getTask();
            chunkTask.update();
            return;
        }
        if (data.inXray() && (event.getTo().getBlockX() != event.getFrom().getBlockX() || event.getTo().getBlockY() != event.getFrom().getBlockY() || event.getTo().getBlockZ() != event.getFrom().getBlockZ()) && data.getTask().getTaskType() == TaskType.BLOCK) {
            BlockTask blockTask = (BlockTask) data.getTask();
            blockTask.update();
            return;
        }
    }
}
