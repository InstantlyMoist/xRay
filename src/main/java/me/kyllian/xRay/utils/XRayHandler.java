package me.kyllian.xRay.utils;

import me.kyllian.xRay.XRayPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class XRayHandler {

    private XRayPlugin plugin;

    public XRayHandler(XRayPlugin plugin) {
        this.plugin = plugin;
    }

    public List<Chunk> getRestore(List<Chunk> oldChunks, List<Chunk> newChunks) {
        oldChunks.removeAll(newChunks);
        return oldChunks;
    }

    public List<Chunk> getxRay(List<Chunk> oldChunks, List<Chunk> newChunks) {
        newChunks.removeAll(oldChunks);
        return newChunks;
    }


    public void send(Player player) {
        Location loc = player.getLocation();
        int beforerange = plugin.getConfig().getInt("Settings.Range");
        int range = (beforerange / 2) * 2 == beforerange ? beforerange : beforerange + 1;
        PlayerData data = plugin.getPlayerData(player.getUniqueId());
        int xmin = loc.getChunk().getX() - range;
        int xmax = loc.getChunk().getX() + range;
        int zmin = loc.getChunk().getZ() - range;
        int zmax = loc.getChunk().getZ() + range;

        ArrayList<Chunk> currentChunks = new ArrayList<>();
        for (int x = xmin; x < xmax; x++) {
            for (int z = zmin; z < zmax; z++) {
                currentChunks.add(loc.getWorld().getChunkAt(x, z));
            }
        }

        data.task = new ChunkTask(plugin, player, getxRay(data.chunkList, (ArrayList<Chunk>) currentChunks.clone()));
        getRestore(data.chunkList, (ArrayList<Chunk>) currentChunks.clone()).forEach(chunk -> restore(player, chunk));
        data.chunkList.clear();
        for (int x = xmin; x < xmax; x++) {
            for (int z = zmin; z < zmax; z++) {
                data.chunkList.add(loc.getWorld().getChunkAt(x, z));
            }
        }
    }


    public void firstPrepare(Player player) {
        PlayerData data = plugin.getPlayerData(player.getUniqueId());
        if (plugin.getConfig().getBoolean("Settings.SpectatorGamemode")) {
            data.gameMode = player.getGameMode();
            player.setGameMode(GameMode.SPECTATOR);
        }
        data.xray = true;
        send(player);
    }

    public void restore(Player player, Chunk chunk) {
        player.getWorld().refreshChunk(chunk.getX(), chunk.getZ());
    }

    public void restoreAll(Player player) {
        PlayerData data = plugin.getPlayerData(player.getUniqueId());
        if (plugin.getConfig().getBoolean("Settings.SpectatorGamemode")) player.setGameMode(data.gameMode);
        data.chunkList.forEach(chunk -> restore(player, chunk));
        data.chunkList.clear();
        data.xray = false;
    }
}