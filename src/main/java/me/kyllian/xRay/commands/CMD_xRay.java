package me.kyllian.xRay.commands;

import me.kyllian.xRay.XRayPlugin;
import me.kyllian.xRay.utils.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMD_xRay implements CommandExecutor {

    private XRayPlugin plugin;

    public CMD_xRay(XRayPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
            if (args.length == 0) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(plugin.getMessageHandler().getNotAPlayerMessage());
                    return true;
                }
                Player player = (Player) sender;
                if (player.hasPermission("xray.toggle")) {
                    PlayerData playerData = plugin.getPlayerHandler().getPlayerData(player);
                    if (playerData.inXray()) {
                        player.sendMessage(plugin.getMessageHandler().getDisabledxRayMessage());
                        plugin.getxRayHandler().restoreAll(player);
                        playerData.setTask(null);
                        playerData.setList(null);
                        return true;
                    }
                    player.sendMessage(plugin.getMessageHandler().getEnabledxRayMessage());
                    plugin.getxRayHandler().send(player);
                    return true;
                }
                player.sendMessage(plugin.getMessageHandler().getNoPermissionMessage());
                return true;
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("help")) {
                    sender.sendMessage(plugin.getMessageHandler().colorTranslate("&8&oxRay: &7Welcome to xRay"));
                    sender.sendMessage("");
                    sender.sendMessage(plugin.getMessageHandler().colorTranslate("&7/xray (help/reload)"));
                    return true;
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("xray.reload")) {
                        plugin.reloadConfig();
                        plugin.blocks = plugin.getConfig().getStringList("Settings.xRayBlocks");
                        sender.sendMessage(plugin.getMessageHandler().getReloadedMessage());
                        return true;
                    }
                    sender.sendMessage(plugin.getMessageHandler().getNoPermissionMessage());
                    return true;
                }
            }
        sender.sendMessage(plugin.getMessageHandler().getUnknownArgumentMessage());
        return true;
    }
}
