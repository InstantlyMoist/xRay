package me.kyllian.xRay.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.ChunkCoordIntPair;
import com.comphenix.protocol.wrappers.MultiBlockChangeInfo;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import me.kyllian.xRay.XRayPlugin;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class XRayHandler {

    private XRayPlugin plugin;

    public XRayHandler(XRayPlugin plugin) {
        this.plugin = plugin;
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

        if (!data.chunkList.isEmpty()) {
            List<Chunk> before = new ArrayList<>(data.chunkList);
            List<Chunk> after = new ArrayList<>();
            for (int x = xmin; x < xmax; x++) {
                for (int z = zmin; z < zmax; z++) {
                    after.add(loc.getWorld().getChunkAt(x, z));
                }
            }
            before.removeAll(after);
            before.forEach(chunk -> restore(player, chunk));
            data.chunkList.clear();
        }
        for (int x = xmin; x < xmax; x++) {
            for (int z = zmin; z < zmax; z++) {
                data.chunkList.add(loc.getWorld().getChunkAt(x, z));
            }
        }
        for (Chunk chunk : data.chunkList) {
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.MULTI_BLOCK_CHANGE);
            ChunkCoordIntPair chunkCoords = new ChunkCoordIntPair(chunk.getX(), chunk.getZ());
            MultiBlockChangeInfo[] change = new MultiBlockChangeInfo[65536];
            int i = 0;
            for (int x = 0; x <= 15; x++) {
                for (int y = 0; y <= 255; y++) {
                    for (int z = 0; z <= 15; z++) {

                        Location location = chunk.getBlock(x, y, z).getLocation();
                        if (plugin.blocks.contains(location.getBlock().getType().toString()) || location.getBlock().getType() == Material.AIR) {
                            if (Bukkit.getServer().getVersion().contains("1.13")) change[i++] = new MultiBlockChangeInfo(location, WrappedBlockData.createData(location.getBlock().getBlockData()));
                            else change[i++] = new MultiBlockChangeInfo(location, WrappedBlockData.createData(location.getBlock().getType(), location.getBlock().getData()));
                        } else {
                            change[i++] = new MultiBlockChangeInfo(location, WrappedBlockData.createData(Material.BARRIER));
                        }
                    }
                }
            }
            packet.getChunkCoordIntPairs().write(0, chunkCoords);
            packet.getMultiBlockChangeInfoArrays().write(0, change);
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
            } catch (Exception exc) {
                System.out.println("Something went wrong while creating the xRay vision!");
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
