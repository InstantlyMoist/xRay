package me.kyllian.xRay;

import java.util.*;
import java.util.concurrent.Callable;

import me.kyllian.xRay.handlers.PlayerHandler;
import me.kyllian.xRay.listeners.PlayerMoveListener;
import me.kyllian.xRay.handlers.MessageHandler;
import me.kyllian.xRay.utils.PlayerData;
import me.kyllian.xRay.handlers.XRayHandler;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.kyllian.xRay.commands.CMD_xRay;
import me.kyllian.xRay.listeners.PlayerQuitListener;

public class XRayPlugin extends JavaPlugin {

    private PlayerHandler playerHandler;
    private MessageHandler messageHandler;
    private XRayHandler xRayHandler;

    public List<String> blocks;

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

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        blocks = getConfig().getStringList("Settings.xRayBlocks");

        initializeHandlers();
        initializeListeners();
    }

    public void initializeHandlers() {
        messageHandler = new MessageHandler(this);
        playerHandler = new PlayerHandler(this);
        xRayHandler = new XRayHandler(this);

    }

    public void initializeListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(this), this);
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().stream().filter(player -> playerHandler.getPlayerData(player).isXray()).forEach(xRayHandler::restoreAll);
        Bukkit.getOnlinePlayers().forEach(player -> {
            PlayerData playerData = playerHandler.getPlayerData(player);
            if (playerData.isXray()) xRayHandler.restoreAll(player);
        });
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public PlayerHandler getPlayerHandler() {
        return playerHandler;
    }

    public XRayHandler getxRayHandler() {
        return xRayHandler;
    }

}
