package io.github.anthonyclemens.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.util.Log;

import io.github.anthonyclemens.Logic.DayNightCycle;
import io.github.anthonyclemens.Player.Player;
import io.github.anthonyclemens.Rendering.Camera;
import io.github.anthonyclemens.Rendering.IsoRenderer;
import io.github.anthonyclemens.WorldGen.ChunkManager;

public class SaveLoadManager {

    private DayNightCycle loadedEnv;
    private IsoRenderer loadedRenderer;
    private Camera loadedCamera;
    private Player loadedPlayer;

    public void saveGame(String filePath, DayNightCycle env, ChunkManager chunkManager, Camera camera, Player player) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(env);
            oos.writeObject(chunkManager);
            oos.writeObject(camera);
            oos.writeFloat(player.getX());
            oos.writeFloat(player.getY());
        } catch (IOException e) {
            Log.error("Failed to save game: " + e.getMessage());
        }

        File saveFile = new File(filePath);
        if (saveFile.exists()) {
            long fileSize = saveFile.length();
            if (fileSize < 1024 * 1024) {
                Log.debug("Game "+filePath+" saved. File size: " + (fileSize / 1024) + " KB.");
            } else {
                Log.debug("Game "+filePath+" saved. File size: " + (fileSize / (1024 * 1024)) + " MB.");
            }
        } else {
            Log.error("Save file not found after saving.");
        }
    }

    public void loadGame(String filePath, GameContainer container, IsoRenderer renderer, Player player) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            loadedEnv = (DayNightCycle) ois.readObject();
            ChunkManager newChunkManager = (ChunkManager) ois.readObject();
            loadedRenderer = new IsoRenderer(renderer.getZoom(), "main", newChunkManager, container);
            newChunkManager.attachRenderer(loadedRenderer);
            loadedCamera = (Camera) ois.readObject();
            loadedPlayer = player;
            loadedPlayer.setX(ois.readFloat());
            loadedPlayer.setY(ois.readFloat());
        } catch (IOException | ClassNotFoundException e) {
            Log.error("Failed to load game "+filePath+": " + e.getMessage());
        }
        Log.debug("Game loaded.");
    }

    public DayNightCycle getDayNightCycle() {
        return loadedEnv;
    }

    public IsoRenderer getRenderer() {
        return loadedRenderer;
    }

    public Camera getCamera() {
        return loadedCamera;
    }

    public Player getPlayer() {
        return loadedPlayer;
    }
}
