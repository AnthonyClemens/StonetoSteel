package io.github.anthonyclemens.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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
    private float playerX, playerY, playerSpeed;
    private int playerHealth;


    public void saveGame(String filePath, DayNightCycle env, ChunkManager chunkManager, Camera camera, Player player) {
        Path path = Paths.get(filePath);

        try {
            // Create parent directories if they don't exist
            Files.createDirectories(path.getParent());
            // Create the file if it doesn't exist
            if (!Files.exists(path)) {
                Files.createFile(path);
                System.out.println("File created successfully: " + path.toAbsolutePath());
            } else {
                System.out.println("File already exists: " + path.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Failed to create file: " + e.getMessage());
        }

        try (FileOutputStream fos = new FileOutputStream(filePath);
             GZIPOutputStream gzos = new GZIPOutputStream(fos);
             ObjectOutputStream oos = new ObjectOutputStream(gzos)) {
            oos.writeObject(env);
            oos.writeObject(chunkManager);
            oos.writeObject(camera);
            oos.writeFloat(player.getX());
            oos.writeFloat(player.getY());
            oos.writeFloat(player.getSpeed());
            oos.writeInt(player.getHealth());
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

    public void loadGame(String filePath, GameContainer container) {
        try (FileInputStream fis = new FileInputStream(filePath);
             GZIPInputStream gzis = new GZIPInputStream(fis);
             ObjectInputStream ois = new ObjectInputStream(gzis)) {
            loadedEnv = (DayNightCycle) ois.readObject();
            ChunkManager newChunkManager = (ChunkManager) ois.readObject();
            loadedRenderer = new IsoRenderer(1f, "main", newChunkManager, container);
            newChunkManager.attachRenderer(loadedRenderer);
            loadedCamera = (Camera) ois.readObject();
            playerX = ois.readFloat();
            playerY = ois.readFloat();
            playerSpeed = ois.readFloat();
            playerHealth = ois.readInt();
        } catch (IOException | ClassNotFoundException e) {
            Log.error("Failed to load game "+filePath+": " + e.getMessage());
        }
        Log.debug("Game "+filePath+" loaded.");
    }

    public static void deleteSave(String filePath) {
        File saveFile = new File(filePath);
        if (saveFile.exists() && saveFile.delete()) {
            Log.debug("Save file " + filePath + " deleted successfully.");
        } else {
            Log.error("Failed to delete save file: " + filePath);
        }
    }

    public static String getSize(String filePath) {
        File saveFile = new File(filePath);
        if (saveFile.exists()) {
            long fileSize = saveFile.length();
            if (fileSize < 1024 * 1024) {
                return (fileSize / 1024) + " KB";
            } else {
                return (fileSize / (1024 * 1024)) + " MB";
            }
        } else {
            Log.warn("Save file not found: " + filePath);
            return "0";
        }
    }

    public static boolean exists(String filePath) {
        File saveFile = new File(filePath);
        return saveFile.exists();
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

    public float getPlayerX() {
        return playerX;
    }
    public float getPlayerY() {
        return playerY;
    }
    public float getPlayerSpeed() {
        return playerSpeed;
    }
    public int getPlayerHealth() {
        return playerHealth;
    }
}
