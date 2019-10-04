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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ChunkTask extends Task {

    private XRayPlugin plugin;
    private Player player;
    private PlayerData playerData;
    private ArrayList<Chunk> chunkList;
    private ArrayList<Chunk> currentUsed;

    private WrappedBlockData barrierBlock;

    public ChunkTask(XRayPlugin plugin, Player player) {
        super(TaskType.CHUNK);

        this.plugin = plugin;
        this.player = player;
        this.playerData = plugin.getPlayerHandler().getPlayerData(player);

        chunkList = new ArrayList<>();
        currentUsed = new ArrayList<>();

        barrierBlock = WrappedBlockData.createData(Material.BARRIER);

        runTaskAsynchronously(plugin);
    }

    @Override
    public void restore() {
        if (playerData.getList() == null) return;
        calculateRestore(chunkList, (List<Chunk>) playerData.getList()).stream().forEach(chunk -> {
            new BukkitRunnable() {
                public void run() {
                    player.getWorld().refreshChunk(chunk.getX(), chunk.getZ());
                }
            }.runTask(plugin);
        });
    }

    @Override
    public void run() {
        updateNewChunks();
        playerData.setList(chunkList);
        if (isCancelled()) return;
        currentUsed = (ArrayList<Chunk>) chunkList.clone();
        updateChunkAsync(currentUsed.get(0));
    }

    public void sendNext() {
        if (!currentUsed.isEmpty()) updateChunkAsync(currentUsed.get(0));
    }

    public void updateChunkAsync(Chunk chunk) {
        new BukkitRunnable() {
            public void run() {
                PacketContainer packet = new PacketContainer(PacketType.Play.Server.MULTI_BLOCK_CHANGE);
                ChunkCoordIntPair chunkCoords = new ChunkCoordIntPair(chunk.getX(), chunk.getZ());
                MultiBlockChangeInfo[] change = new MultiBlockChangeInfo[65536];
                int i = 0;
                for (int x = 0; x <= 15; x++) {
                    for (int y = 0; y <= 255; y++) {
                        for (int z = 0; z <= 15; z++) {
                            if (isCancelled()) return;
                            Location location = chunk.getBlock(x, y, z).getLocation();
                            if (plugin.blocks.contains(location.getBlock().getType().toString()) || location.getBlock().getType() == Material.AIR) {
                                if (Bukkit.getServer().getVersion().contains("1.13") || Bukkit.getVersion().contains("1.14"))
                                    change[i++] = new MultiBlockChangeInfo(location, WrappedBlockData.createData(location.getBlock().getBlockData()));
                                else
                                    change[i++] = new MultiBlockChangeInfo(location, WrappedBlockData.createData(location.getBlock().getType(), location.getBlock().getData()));
                            } else {
                                change[i++] = new MultiBlockChangeInfo(location, barrierBlock);
                            }
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
                currentUsed.remove(chunk);
                sendNext();
                plugin.blocksXrayed += 65536;
            }
        }.runTaskAsynchronously(plugin);
    }

    public void updateNewChunks() {
        final Location location = player.getLocation();
        int beforeRange = plugin.getConfig().getInt("Settings.Range");
        int finalRange = (beforeRange / 2) * 2 == beforeRange ? beforeRange : beforeRange + 1;
        for (int x = location.getChunk().getX() - finalRange; x < location.getChunk().getX() + finalRange + 1; x++) {
            for (int z = location.getChunk().getZ() - finalRange; z < location.getChunk().getZ() + finalRange + 1; z++) {
                chunkList.add(location.getWorld().getChunkAt(x, z));
            }
        }
        restore();
    }


    public List<Chunk> calculateRestore(List<Chunk> newList, List<Chunk> oldList) {
        oldList.removeAll(newList);
        return oldList;
    }
}
