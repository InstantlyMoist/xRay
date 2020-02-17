package me.kyllian.xRay.handlers;
import me.kyllian.xRay.XRayPlugin;
import me.kyllian.xRay.tasks.BlockTask;
import me.kyllian.xRay.tasks.ChunkTask;
import me.kyllian.xRay.utils.PlayerData;
import me.kyllian.xRay.utils.TaskType;
import org.bukkit.entity.Player;

public class XRayHandler {

    private XRayPlugin plugin;

    public XRayHandler(XRayPlugin plugin) {
        this.plugin = plugin;
    }

    public void send(Player player) {
        PlayerData playerData = plugin.getPlayerHandler().getPlayerData(player);
        playerData.setTask(TaskType.valueOf(plugin.getConfig().getString("Settings.Mode")) == TaskType.CHUNK ? new ChunkTask(plugin, player) : new BlockTask(plugin, player));
    }

    public void restore(Player player) {
        PlayerData playerData = plugin.getPlayerHandler().getPlayerData(player);
        if (playerData.getTask() instanceof ChunkTask) {
            ChunkTask task = (ChunkTask) playerData.getTask();
            task.restore(task.getRunningChunks());
        } else {
            BlockTask blockTask = (BlockTask) playerData.getTask();
            blockTask.restore(blockTask.getRunningBlocks());
        }
    }
}