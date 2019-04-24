package me.kyllian.xRay.handlers;

import me.kyllian.xRay.XRayPlugin;
import me.kyllian.xRay.utils.PlayerData;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerHandler {

    private XRayPlugin plugin;
    private Map<UUID, PlayerData> playerData;

    public PlayerHandler (XRayPlugin plugin) {
        this.plugin = plugin;
        playerData = new HashMap<>();
    }

    public PlayerData getPlayerData(Player player) {
        return playerData.computeIfAbsent(player.getUniqueId(), f -> new PlayerData(player.getUniqueId()));
    }

    public void removeData(Player player) {
        playerData.remove(player.getUniqueId());
    }
}
