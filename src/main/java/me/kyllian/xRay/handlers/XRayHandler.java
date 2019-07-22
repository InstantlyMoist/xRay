package me.kyllian.xRay.handlers;
import me.kyllian.xRay.XRayPlugin;
import me.kyllian.xRay.utils.BlockTask;
import me.kyllian.xRay.utils.ChunkTask;
import me.kyllian.xRay.utils.PlayerData;
import me.kyllian.xRay.utils.TaskType;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

public class XRayHandler {

    private XRayPlugin plugin;

    public XRayHandler(XRayPlugin plugin) {
        this.plugin = plugin;
    }

    public void send(Player player) {
        PlayerData playerData = plugin.getPlayerHandler().getPlayerData(player);
        playerData.setTask(TaskType.valueOf(plugin.getConfig().getString("Settings.Mode")) == TaskType.CHUNK ? new ChunkTask(plugin, player) : new BlockTask(plugin, player));
    }

    public void restoreAll(Player player) {
        PlayerData playerData = plugin.getPlayerHandler().getPlayerData(player);
        boolean chunkList = false;
        for (Object object : (List) playerData.getList()) {
            if (object instanceof Chunk) chunkList = true;
        }
        if (chunkList) ((List<Chunk>) playerData.getList()).forEach(chunk -> player.getWorld().refreshChunk(chunk.getX(), chunk.getZ()));
        else ((List<Block>)playerData.getList()).forEach(block -> player.sendBlockChange(block.getLocation(), block.getType(), block.getData()));
    }
}