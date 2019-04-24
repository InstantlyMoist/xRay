package me.kyllian.xRay.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.ChunkCoordIntPair;
import com.comphenix.protocol.wrappers.MultiBlockChangeInfo;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import me.kyllian.xRay.XRayPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.List;

public class ChunkTask extends BukkitRunnable {

    private XRayPlugin plugin;
    private Player player;
    private List<Chunk> chunkList;

    public ChunkTask(XRayPlugin plugin, Player player, List<Chunk> chunkList) {
        this.plugin = plugin;
        this.player = player;
        this.chunkList = Collections.synchronizedList(chunkList);

        runTaskAsynchronously(plugin);
    }

    public void run() {
        if (isCancelled()) return;
        chunkList.forEach(chunk -> {
            if (chunk == null) return;
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.MULTI_BLOCK_CHANGE);
            ChunkCoordIntPair chunkCoords = new ChunkCoordIntPair(chunk.getX(), chunk.getZ());
            MultiBlockChangeInfo[] change = new MultiBlockChangeInfo[65536];
            int i = 0;
            for (int x = 0; x <= 15; x++) {
                for (int y = 0; y <= 255; y++) {
                    for (int z = 0; z <= 15; z++) {
                        if (isCancelled()) return;
                        plugin.blocksXrayed++;
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
        });
    }
}
