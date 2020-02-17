package me.kyllian.xRay.tasks;

import me.kyllian.xRay.XRayPlugin;
import me.kyllian.xRay.threads.ChunkThread;
import me.kyllian.xRay.utils.TaskType;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ChunkTask extends Task {

    private XRayPlugin plugin;
    private Player player;

    private ArrayList<Chunk> runningChunks;

    public ChunkTask(XRayPlugin plugin, Player player) {
        super(TaskType.CHUNK);
        this.plugin = plugin;
        this.player = player;

        runningChunks = getRunningChunks();

        this.start();
    }

    public void run() {
        throwXray();
    }

    public void throwXray() {
        runningChunks.forEach(chunk -> new ChunkThread(plugin, chunk, player).startTask());
    }

    public void update() {
        new BukkitRunnable() {
            public void run() {
                ArrayList<Chunk> oldChunks = (ArrayList<Chunk>) runningChunks.clone();
                ArrayList<Chunk> newChunks = getRunningChunks();
                oldChunks.removeAll(newChunks);
                runningChunks = newChunks;
                restore(oldChunks);
                throwXray();
            }
        }.runTaskLaterAsynchronously(plugin, 1);
    }

    public void restore(List<Chunk> restoring) {
        new BukkitRunnable() {
            public void run() {
                restoring.forEach(chunk -> player.getWorld().refreshChunk(chunk.getX(), chunk.getZ()));
            }
        }.runTask(plugin);
    }

    public ArrayList<Chunk> getRunningChunks() {
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
