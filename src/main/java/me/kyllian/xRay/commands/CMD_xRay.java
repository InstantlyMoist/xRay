package me.kyllian.xRay.commands;

import java.util.ArrayList;

import me.kyllian.xRay.XRayPlugin;
import me.kyllian.xRay.utils.PlayerData;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.kyllian.xRay.utils.ColorTranslate;

public class CMD_xRay implements CommandExecutor {

    private XRayPlugin plugin;

    public CMD_xRay(XRayPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (command.getName().equalsIgnoreCase("xray")) {
            if (args.length == 0) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(plugin.getMessageHandler().getNotAPlayerMessage());
                    return true;
                }
                Player player = (Player) sender;
                if (player.hasPermission("xray.toggle")) {
                    PlayerData data = plugin.getPlayerData(player.getUniqueId());
                    if (data.xray) {
                        player.sendMessage(plugin.getMessageHandler().getDisabledxRayMessage());
                        plugin.getxRayHandler().restoreAll(player);
                        return true;
                    }
                    player.sendMessage(plugin.getMessageHandler().getEnabledxRayMessage());
                    plugin.getxRayHandler().firstPrepare(player);
                    return true;
                }
                player.sendMessage(plugin.getMessageHandler().getNoPermissionMessage());
                return true;
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("help")) {
                    sender.sendMessage(ColorTranslate.cc("&8&oxRay: &7Welcome to xRay"));
                    sender.sendMessage("");
                    sender.sendMessage(ColorTranslate.cc("&7/xray (help/reload)"));
                    return true;
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("xray.reload")) {
                        plugin.reloadConfig();
                        plugin.blocks = (ArrayList<String>) plugin.getConfig().getStringList("Settings.xRayBlocks");
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
        return true;
    }
}
