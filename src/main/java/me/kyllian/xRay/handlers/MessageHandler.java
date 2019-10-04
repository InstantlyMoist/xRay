package me.kyllian.xRay.handlers;

import me.kyllian.xRay.XRayPlugin;
import org.bukkit.ChatColor;

public class MessageHandler {

    private XRayPlugin plugin;

    public MessageHandler(XRayPlugin plugin) {
        this.plugin = plugin;
    }

    public String colorTranslate(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getNotAPlayerMessage() {
        return colorTranslate(plugin.getConfig().getString("Messages.NotAPlayer"));
    }

    public String getNoPermissionMessage() {
        return colorTranslate(plugin.getConfig().getString("Messages.NoPermission"));
    }

    public String getUnknownArgumentMessage() {
        return colorTranslate(plugin.getConfig().getString("Messages.UnknownArgument"));
    }

    public String getDisabledxRayMessage() {
        return colorTranslate(plugin.getConfig().getString("Messages.DisabledxRay"));
    }

    public String getEnabledxRayMessage() {
        return colorTranslate(plugin.getConfig().getString("Messages.EnabledxRay"));
    }

    public String getReloadedMessage() {
        return colorTranslate(plugin.getConfig().getString("Messages.Reloaded"));
    }

    public String getAddedBlockMessage() {
        return colorTranslate(plugin.getConfig().getString("Messages.AddedBlock"));
    }

    public String getRemovedBlockMessage() {
        return colorTranslate(plugin.getConfig().getString("Messages.RemovedBlock"));
    }

    public String getUnknownBlockMessage() {
        return colorTranslate(plugin.getConfig().getString("Messages.UnknownBlock"));
    }

    public String getAlreadyAddedMessage() {
        return colorTranslate(plugin.getConfig().getString("Messages.AlreadyAdded"));
    }

    public String getDoesntExistMessage() {
        return colorTranslate(plugin.getConfig().getString("Messages.DoesntExist"));
    }
}
