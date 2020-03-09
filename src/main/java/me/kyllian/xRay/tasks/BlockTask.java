package me.kyllian.xRay.tasks;

import me.kyllian.xRay.XRayPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BlockTask implements Task {

    private XRayPlugin plugin;
    private Player player;

    private ArrayList<Block> runningBlocks;

    public BlockTask(XRayPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;

        runningBlocks = new ArrayList<>();
        update();
    }

    @Override
    public void send() {
        for (Block runningBlock : runningBlocks) {
            if (!plugin.getData().keySet().contains(runningBlock.getType().toString()))
                player.sendBlockChange(runningBlock.getLocation(), Material.BARRIER, (byte) 1);
        }
    }

    @Override
    public void restore(List<?> toRestore) {
        for (Object object : toRestore) {
            Block block = (Block) object;
            player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
        }
    }

    @Override
    public void update() {
        ArrayList<Block> newBlocks = (ArrayList<Block>) getRunning();
        ArrayList<Block> oldBlocks = (ArrayList<Block>) runningBlocks.clone();
        oldBlocks.removeAll(newBlocks);
        restore(oldBlocks);
        runningBlocks = newBlocks;
    }

    @Override
    public TaskType getType() {
        return TaskType.BLOCK;
    }

    @Override
    public ArrayList<?> getRunning() {
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
