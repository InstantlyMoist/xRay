package me.kyllian.xRay.threads;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.ChunkCoordIntPair;
import com.comphenix.protocol.wrappers.MultiBlockChangeInfo;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import me.kyllian.xRay.XRayPlugin;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class ChunkThread extends Thread {

    private XRayPlugin plugin;
    private Chunk chunk;
    private Player player;

    private WrappedBlockData barrierBlock;
    private WrappedBlockData airBlock;
    private ChunkSnapshot snapshot;

    public ChunkThread(XRayPlugin plugin, Chunk chunk, Player player) {
        this.plugin = plugin;
        this.chunk = chunk;
        this.player = player;
        //boolean old = Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.7");
        barrierBlock = WrappedBlockData.createData(Material.BARRIER);
        airBlock = WrappedBlockData.createData(Material.AIR);
        //barrierBlock = old ? WrappedBlockData.createData(Material.BARRIER, 1) : WrappedBlockData.createData(Material.BARRIER);
        //airBlock = old ? WrappedBlockData.createData(Material.AIR, 1) : WrappedBlockData.createData(Material.AIR);

        snapshot = chunk.getChunkSnapshot();
    }

    public void startTask(boolean reverse) {
        if (Bukkit.getVersion().contains("1.16.2") || Bukkit.getVersion().contains("1.16.3") || Bukkit.getVersion().contains("1.16.4") || Bukkit.getVersion().contains("1.16.5")) {
            startTaskWithNewPackets(reverse); // Yeah.. this should do the job
            return;
        }
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.MULTI_BLOCK_CHANGE);
        ChunkCoordIntPair chunkCoords = new ChunkCoordIntPair(chunk.getX(), chunk.getZ());
        MultiBlockChangeInfo[] change = new MultiBlockChangeInfo[65536];
        int i = 0;
        for (int x = 0; x <= 15; x++) {
            for (int y = 0; y <= 255; y++) {
                for (int z = 0; z <= 15; z++) {
                    Block foundBlock = chunk.getBlock(x, y, z);
                    Location location = foundBlock.getLocation();
                    String foundMaterial = chunk.getBlock(x, y, z).getType().toString();
                    WrappedBlockData foundWrappedBlockData = reverse ? getFromMaterial(foundMaterial, foundBlock.getData()) : plugin.getData().getOrDefault(foundMaterial, barrierBlock);
                    change[i++] = new MultiBlockChangeInfo(location, foundWrappedBlockData);
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

    public WrappedBlockData getFromMaterial(String material, int data) {
        return WrappedBlockData.createData(Material.valueOf(material), data);
        /*if (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.7") || Bukkit.getVersion().contains("1.12") || Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.9"))
            return WrappedBlockData.createData(Material.valueOf(material), 1);
        else return WrappedBlockData.createData(Material.valueOf(material));*/
    }

    public void startTaskWithNewPackets(boolean reverse) {
        for (int i = 0; i <= 15; i++) {
            PacketContainer container = new PacketContainer(PacketType.Play.Server.MULTI_BLOCK_CHANGE);
            ArrayList<WrappedBlockData> wrappedBlockDataArrayList = new ArrayList<>();
            ArrayList<Short> shortArrayList = new ArrayList<>();
            container.getSectionPositions().write(0, new BlockPosition(chunk.getX(), i, chunk.getZ()));
            int newY = i * 16;
            for (int x = 0; x <= 15; x++) {
                for (int y = newY; y <= newY + 15; y++) {
                    for (int z = 0; z <= 15; z++) {
                        Block foundBlock = chunk.getBlock(x, y, z);
                        Material foundMaterial = snapshot.getBlockType(x, y, z);
                        WrappedBlockData foundWrappedBlockData = reverse ? getFromMaterial(foundMaterial.toString(), foundBlock.getData()) : plugin.getData().getOrDefault(foundMaterial.toString(), barrierBlock);
                        wrappedBlockDataArrayList.add(foundWrappedBlockData);
                        shortArrayList.add(setShortLocation(foundBlock.getLocation()));
                    }
                }
            }
            container.getBlockDataArrays().writeSafely(0, wrappedBlockDataArrayList.toArray(new WrappedBlockData[0]));
            container.getShortArrays().writeSafely(0, ArrayUtils.toPrimitive(shortArrayList.toArray(new Short[0])));
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, container);
            } catch (InvocationTargetException exception) {
                exception.printStackTrace();
            }
        }
    }

    public short setShortLocation(Location loc) {
        return (setShortLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
    }

    public short setShortLocation(int x, int y, int z) {
        x = x & 0xF;
        y = y & 0xF;
        z = z & 0xF;
        return (short) (x << 8 | z << 4 | y << 0);
    }
}
