package me.kyllian.xRay.handlers;

import me.kyllian.xRay.XRayPlugin;
import me.kyllian.xRay.player.PlayerData;
import me.kyllian.xRay.tasks.BlockTask;
import me.kyllian.xRay.tasks.ChunkTask;
import me.kyllian.xRay.tasks.TaskType;
import org.bukkit.entity.Player;

public class XRayHandler {

    private XRayPlugin plugin;

    public XRayHandler(XRayPlugin plugin) {
        this.plugin = plugin;
    }

    public void send(Player player) {
        PlayerData playerData = plugin.getPlayerHandler().getPlayerData(player);
        playerData.setTask(TaskType.valueOf(plugin.getConfig().getString("Settings.Mode")) == TaskType.CHUNK ? new ChunkTask(plugin, player) : new BlockTask(plugin, player));
        playerData.getTask().send();
    }

    public void restore(Player player) {
        PlayerData playerData = plugin.getPlayerHandler().getPlayerData(player);
        playerData.getTask().restore(playerData.getTask().getRunning());
    }
}