package me.kyllian.xRay.player;

import me.kyllian.xRay.tasks.Task;

import java.util.UUID;

public class PlayerData {

    private UUID uuid;

    private Task task;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public boolean inXray() {
        return task != null;
    }
}
