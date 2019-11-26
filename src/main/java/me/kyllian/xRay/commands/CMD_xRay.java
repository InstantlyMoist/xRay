package me.kyllian.xRay.commands;

import me.kyllian.xRay.XRayPlugin;
import me.kyllian.xRay.utils.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;

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
                    plugin.getxRayHandler().restore(player);
                    playerData.setTask(null);
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
                sender.sendMessage(plugin.getMessageHandler().colorTranslate("&7/xray (add/remove) block"));
                return true;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("xray.reload")) {
                    plugin.reloadConfig();
                    plugin.initializeConfig();
                    sender.sendMessage(plugin.getMessageHandler().getReloadedMessage());
                    return true;
                }
                sender.sendMessage(plugin.getMessageHandler().getNoPermissionMessage());
                return true;
            }
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                if (sender.hasPermission("xray.edit")) {
                    String materialName = args[1];
                    if (Material.valueOf(materialName) == null) {
                        sender.sendMessage(plugin.getMessageHandler().getUnknownBlockMessage());
                        return true;
                    }
                    if (plugin.getData().keySet().contains(materialName)) {
                        sender.sendMessage(plugin.getMessageHandler().getAlreadyAddedMessage());
                        return true;
                    }
                    ArrayList<String> blocks = new ArrayList<>(plugin.getData().keySet());
                    blocks.add(materialName);
                    plugin.getConfig().set("Settings.xRayBlocks", blocks);
                    plugin.saveConfig();
                    plugin.initializeConfig();
                    sender.sendMessage(plugin.getMessageHandler().getAddedBlockMessage());
                    return true;
                }
                sender.sendMessage(plugin.getMessageHandler().getNoPermissionMessage());
                return true;
            }
            if (args[0].equalsIgnoreCase("remove")) {
                if (sender.hasPermission("xray.edit")) {
                    String materialName = args[1];
                    if (Material.valueOf(materialName) == null) {
                        sender.sendMessage(plugin.getMessageHandler().getUnknownBlockMessage());
                        return true;
                    }
                    if (!plugin.getData().keySet().contains(materialName)) {
                        sender.sendMessage(plugin.getMessageHandler().getDoesntExistMessage());
                        return true;
                    }
                    ArrayList<String> blocks = new ArrayList<>(plugin.getData().keySet());
                    blocks.remove(materialName);
                    plugin.getConfig().set("Settings.xRayBlocks", blocks);
                    plugin.saveConfig();
                    plugin.initializeConfig();
                    sender.sendMessage(plugin.getMessageHandler().getRemovedBlockMessage());
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
