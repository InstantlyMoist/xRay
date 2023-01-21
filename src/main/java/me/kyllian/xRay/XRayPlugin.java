package me.kyllian.xRay;

import com.comphenix.protocol.wrappers.WrappedBlockData;
import me.kyllian.xRay.commands.CMD_xRay;
import me.kyllian.xRay.handlers.MessageHandler;
import me.kyllian.xRay.handlers.PlayerHandler;
import me.kyllian.xRay.handlers.XRayHandler;
import me.kyllian.xRay.listeners.PlayerMoveListener;
import me.kyllian.xRay.listeners.PlayerQuitListener;
import me.kyllian.xRay.tasks.TaskType;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class XRayPlugin extends JavaPlugin {

    private PlayerHandler playerHandler;
    private MessageHandler messageHandler;
    private XRayHandler xRayHandler;

    private Map<String, WrappedBlockData> data;

    @Override
    public void onEnable() {
        initializeConfig();
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null &&
                TaskType.valueOf(getConfig().getString("Settings.Mode")) == TaskType.CHUNK) {
            Bukkit.getLogger().warning("ProtocolLib not found, changing mode to BLOCK");
            getConfig().set("Settings.Mode", "BLOCK");
            saveConfig();
            return;
        } else {
            Map<String, WrappedBlockData> tempData = new HashMap<>();
            for (String block : getConfig().getStringList("Settings.xRayBlocks")) {
                tempData.put(block, WrappedBlockData.createData(Material.valueOf(block)));
            }
            data = Collections.unmodifiableMap(tempData);
        }

        Metrics metrics = new Metrics(this, 1000);

        getCommand("xray").setExecutor(new CMD_xRay(this));

        initializeHandlers();
        initializeListeners();

    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().stream().filter(player -> playerHandler.getPlayerData(player).inXray())
                .forEach(xRayHandler::restore);
    }

    public void initializeHandlers() {
        messageHandler = new MessageHandler(this);
        playerHandler = new PlayerHandler(this);
        xRayHandler = new XRayHandler(this);

    }

    public void initializeListeners() {
        new PlayerQuitListener(this);
        new PlayerMoveListener(this);
    }

    public void initializeConfig() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

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
