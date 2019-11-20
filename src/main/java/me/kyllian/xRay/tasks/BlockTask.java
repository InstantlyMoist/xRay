package me.kyllian.xRay.tasks;

import me.kyllian.xRay.XRayPlugin;
import me.kyllian.xRay.utils.TaskType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class BlockTask extends Task {

    private XRayPlugin plugin;
    private Player player;

    private ArrayList<Block> runningBlocks;

    public BlockTask(XRayPlugin plugin, Player player) {
        super(TaskType.BLOCK);
        this.plugin = plugin;
        this.player = player;

        prepare();
        start();
    }

    public void run() {
        throwXray();
    }

    public void throwXray() {
        for (Block runningBlock : runningBlocks) {
            if (!plugin.getData().keySet().contains(runningBlock.getType().toString()))
                player.sendBlockChange(runningBlock.getLocation(), Material.BARRIER, (byte) 1);
        }
    }

    public void prepare() {
        runningBlocks = getRunningBlocks();
    }

    public void update() {
        new BukkitRunnable() {
            public void run() {
                ArrayList<Block> newBlocks = getRunningBlocks();
                ArrayList<Block> oldBlocks = (ArrayList<Block>) runningBlocks.clone();
                oldBlocks.removeAll(newBlocks);
                restore(oldBlocks);
                runningBlocks = newBlocks;
                throwXray();
            }
        }.runTaskLaterAsynchronously(plugin, 1);
    }

    public void restore(ArrayList<Block> restoring) {
        for (Block block : restoring) {
            player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
        }
    }

    public ArrayList<Block> getRunningBlocks() {
        ArrayList newBlocks = new ArrayList<>();
        Location location = player.getLocation();
        int beforeRange = plugin.getConfig().getInt("Settings.Range");
        int finalRange = (beforeRange / 2) * 2 == beforeRange ? beforeRange : beforeRange + 1;
        for (int x = location.getBlockX() - finalRange; x < location.getBlockX() + finalRange + 1; x++) {
            for (int y = location.getBlockY() - finalRange; y < location.getBlockY() + finalRange + 1; y++) {
                for (int z = location.getBlockZ() - finalRange; z < location.getBlockZ() + finalRange + 1; z++) {
                    newBlocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        return newBlocks;
    }
}
