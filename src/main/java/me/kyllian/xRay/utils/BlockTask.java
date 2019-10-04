package me.kyllian.xRay.utils;

import me.kyllian.xRay.XRayPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BlockTask extends Task {

    private XRayPlugin plugin;
    private Player player;
    private PlayerData playerData;
    private List<Block> blockList;

    public BlockTask(XRayPlugin plugin, Player player) {
        super(TaskType.BLOCK);

        this.plugin = plugin;
        this.player = player;
        this.playerData = plugin.getPlayerHandler().getPlayerData(player);

        blockList = new ArrayList<>();

        runTaskAsynchronously(plugin);
    }

    @Override
    public void restore() {
        if (playerData.getList() == null) return;
        calculateRestore(blockList, (List<Block>) playerData.getList()).stream().forEach(block -> {
            player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
        });
    }

    @Override
    public void run() {
        final Location location = player.getLocation();
        int beforeRange = plugin.getConfig().getInt("Settings.Range");
        int finalRange = (beforeRange / 2) * 2 == beforeRange ? beforeRange : beforeRange + 1;
        for (int x = location.getBlockX() - finalRange; x < location.getBlockX() + finalRange + 1; x++) {
            for (int y = location.getBlockY() - finalRange; y < location.getBlockY() + finalRange + 1; y++) {
                for (int z = location.getBlockZ() - finalRange; z < location.getBlockZ() + finalRange + 1; z++) {
                    blockList.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        restore();
        playerData.setList(blockList);
        Iterator iterator = blockList.iterator();
        while (iterator.hasNext()) {
            if (isCancelled()) return;
            Block block = (Block) iterator.next();
            if (!plugin.blocks.contains(block.getType().toString()) && block.getType() != Material.AIR) {
                player.sendBlockChange(block.getLocation(), Material.BARRIER, (byte) 1);
                plugin.blocksXrayed++;
            }
        }
    }

    public List<Block> calculateRestore(List<Block> newList, List<Block> oldList) {
        oldList.removeAll(newList);
        return oldList;
    }
}
