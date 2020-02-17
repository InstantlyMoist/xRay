package me.kyllian.xRay.threads;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.ChunkCoordIntPair;
import com.comphenix.protocol.wrappers.MultiBlockChangeInfo;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import me.kyllian.xRay.XRayPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class ChunkThread extends Thread {

    private XRayPlugin plugin;
    private Chunk chunk;
    private Player player;

    private WrappedBlockData barrierBlock;
    private WrappedBlockData airBlock;

    public ChunkThread(XRayPlugin plugin, Chunk chunk, Player player) {
        this.plugin = plugin;
        this.chunk = chunk;
        this.player = player;
        barrierBlock = WrappedBlockData.createData(Material.BARRIER);
        airBlock = WrappedBlockData.createData(Material.AIR);
    }

    public void startTask() {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.MULTI_BLOCK_CHANGE);
        ChunkCoordIntPair chunkCoords = new ChunkCoordIntPair(chunk.getX(), chunk.getZ());
        MultiBlockChangeInfo[] change = new MultiBlockChangeInfo[65536];
        int i = 0;
        ChunkSnapshot snapshot = chunk.getChunkSnapshot();
        for (int x = 0; x <= 15; x++) {
            for (int y = 0; y <= 255; y++) {
                for (int z = 0; z <= 15; z++) {
                    Block foundBlock = chunk.getBlock(x, y, z);
                    Location location = foundBlock.getLocation();
                    String foundMaterial = snapshot.getBlockType(x, y ,z).toString();
                    change[i++] = new MultiBlockChangeInfo(location, plugin.getData().getOrDefault(foundMaterial, barrierBlock));
                }
            }
        }
        packet.getChunkCoordIntPairs().write(0, chunkCoords);
        packet.getMultiBlockChangeInfoArrays().write(0, change);
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        } catch (InvocationTargetException exception) {
            exception.printStackTrace();
        }
    }
}
