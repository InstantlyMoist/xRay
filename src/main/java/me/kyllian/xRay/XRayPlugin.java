package me.kyllian.xRay;

import java.util.*;
import java.util.concurrent.Callable;

import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.google.common.collect.ImmutableMap;
import me.kyllian.xRay.handlers.PlayerHandler;
import me.kyllian.xRay.listeners.PlayerMoveListener;
import me.kyllian.xRay.handlers.MessageHandler;
import me.kyllian.xRay.handlers.XRayHandler;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.kyllian.xRay.commands.CMD_xRay;
import me.kyllian.xRay.listeners.PlayerQuitListener;

public class XRayPlugin extends JavaPlugin {

    private PlayerHandler playerHandler;
    private MessageHandler messageHandler;
    private XRayHandler xRayHandler;

    private Map<String, WrappedBlockData> data;

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            Bukkit.getLogger().warning("xRay has been disabled, please download Protocollib!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        Metrics metrics = new Metrics(this);

        getCommand("xray").setExecutor(new CMD_xRay(this));

        initializeHandlers();
        initializeListeners();
        initializeConfig();
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().stream().filter(player -> playerHandler.getPlayerData(player).inXray()).forEach(xRayHandler::restore);
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

    public void initializeConfig() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();


        Map<String, WrappedBlockData> tempData = new HashMap<>();
        for (String block : getConfig().getStringList("Settings.xRayBlocks")) {
            tempData.put(block, WrappedBlockData.createData(Material.valueOf(block)));
        }
        data = Collections.unmodifiableMap(tempData);
    }

    public Map<String, WrappedBlockData> getData() {
        return data;
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
