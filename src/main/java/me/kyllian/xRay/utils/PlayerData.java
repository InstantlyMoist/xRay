package me.kyllian.xRay.utils;

import org.bukkit.Chunk;
import org.bukkit.GameMode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerData {

    private UUID uuid;

    private List<Chunk> chunkList;
    private boolean xray = false;
    private GameMode gameMode;
    private ChunkTask task;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        chunkList = new ArrayList<>();
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public List<Chunk> getChunkList() {
        return chunkList;
    }

    public void setChunkList(List<Chunk> chunkList) {
        this.chunkList = chunkList;
    }

    public boolean isXray() {
        return xray;
    }

    public void setXray(boolean xray) {
        this.xray = xray;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public ChunkTask getTask() {
        return task;
    }

    public void setTask(ChunkTask task) {
        this.task = task;
    }
}
