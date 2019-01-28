package me.kyllian.xRay.utils;

import org.bukkit.Chunk;
import org.bukkit.GameMode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerData {

    private UUID uuid;
    public List<Chunk> chunkList;
    public boolean xray = false;
    public GameMode gameMode;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        chunkList = new ArrayList<>();
    }
}
