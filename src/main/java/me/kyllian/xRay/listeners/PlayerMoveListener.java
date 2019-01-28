package me.kyllian.xRay.listeners;

import me.kyllian.xRay.XRayPlugin;
import me.kyllian.xRay.utils.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    private XRayPlugin plugin;

    public PlayerMoveListener(XRayPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.getPlayerData(player.getUniqueId());
        if (data.xray) {
            if (event.getFrom().getChunk() != event.getTo().getChunk()) {
                plugin.getxRayHandler().send(player);
            }
        }
    }
}
