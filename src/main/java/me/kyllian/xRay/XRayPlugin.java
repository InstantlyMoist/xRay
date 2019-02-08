package me.kyllian.xRay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import me.kyllian.xRay.listeners.PlayerMoveListener;
import me.kyllian.xRay.utils.MessageHandler;
import me.kyllian.xRay.utils.PlayerData;
import me.kyllian.xRay.utils.XRayHandler;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.kyllian.xRay.commands.CMD_xRay;
import me.kyllian.xRay.listeners.PlayerQuitListener;

public class XRayPlugin extends JavaPlugin {

    private XRayHandler xRayHandler;
    private MessageHandler messageHandler;

    public List<String> blocks;
    public HashMap<UUID, PlayerData> playerData;

    public int blocksXrayed = 0;

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            Bukkit.getLogger().warning("xRay has been disabled, please download Protocollib!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        Metrics metrics = new Metrics(this);

        metrics.addCustomChart(new Metrics.SingleLineChart("blocks_xrayed", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return blocksXrayed;
            }
        }));


                getCommand("xray").setExecutor(new CMD_xRay(this));

        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(this), this);

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        blocks = new ArrayList<>(getConfig().getStringList("Settings.xRayBlocks"));
        xRayHandler = new XRayHandler(this);
        playerData = new HashMap<>();
        messageHandler = new MessageHandler(this);
    }

    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            PlayerData data = getPlayerData(player.getUniqueId());
            if (data.xray) {

                xRayHandler.restoreAll(player);
            }
        });
    }

    public XRayHandler getxRayHandler() {
        return xRayHandler;
    }

    public PlayerData getPlayerData(UUID uuid) {
        return playerData.computeIfAbsent(uuid, f -> new PlayerData(uuid));
    }

    public void removePlayerData(UUID uuid) {
        playerData.remove(uuid);
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }
}
