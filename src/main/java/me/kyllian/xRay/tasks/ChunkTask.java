package me.kyllian.xRay.tasks;

import me.kyllian.xRay.XRayPlugin;
import me.kyllian.xRay.threads.ChunkThread;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class ChunkTask implements Task {

    private XRayPlugin plugin;
    private Player player;

    private ArrayList<Chunk> runningChunks;

    public ChunkTask(XRayPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;

        runningChunks = new ArrayList<>();
        update();
    }

    @Override
    public void send() {
        runningChunks.forEach(chunk -> new ChunkThread(plugin, chunk, player).startTask());
    }

    @Override
    public void restore(List<?> toRestore) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Object object : toRestore) {
                    Chunk chunk = (Chunk) object;
                    player.getWorld().unloadChunk(chunk);
                    player.getWorld().loadChunk(chunk);
                }
            }
        }.runTask(plugin);
    }

    @Override
    public void update() {
        ArrayList<Chunk> newChunks = (ArrayList<Chunk>) getRunning();
        ArrayList<Chunk> oldChunks = (ArrayList<Chunk>) runningChunks.clone();
        oldChunks.removeAll(newChunks);
        restore(oldChunks);
        runningChunks = newChunks;
    }

    @Override
    public TaskType getType() {
        return TaskType.CHUNK;
    }

    @Override
    public ArrayList<?> getRunning() {
        ArrayList<Chunk> newChunks = new ArrayList<>();
        Location location = player.getLocation();
        int beforeRange = plugin.getConfig().getInt("Settings.Range");
        int finalRange = (beforeRange / 2) * 2 == beforeRange ? beforeRange : beforeRange + 1;
        for (int x = location.getChunk().getX() - finalRange; x < location.getChunk().getX() + finalRange + 1; x++) {
            for (int z = location.getChunk().getZ() - finalRange; z < location.getChunk().getZ() + finalRange + 1; z++) {
                newChunks.add(location.getWorld().getChunkAt(x, z));
            }
        }
        return newChunks;
    }
}
