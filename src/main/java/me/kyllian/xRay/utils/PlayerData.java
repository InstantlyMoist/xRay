package me.kyllian.xRay.utils;

import org.bukkit.GameMode;

import java.util.UUID;

public class PlayerData {

    private UUID uuid;

    private Task task;
    private Object list;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Object getList() {
        return list;
    }

    public void setList(Object list) {
        this.list = list;
    }

    public boolean inXray() {
        return task != null;
    }
}
