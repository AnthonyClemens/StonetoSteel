package io.github.anthonyclemens;

import org.newdawn.slick.Input;

public class Settings {
    private static Settings instance;
    // Private constructor to prevent instantiation
    private Settings() {}

    // Get the single instance of Settings
    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    private int maxFPS;
    private boolean vsync;
    private boolean fullscreen;
    private String resolution;
    private boolean showStats;
    private float mainVolume;
    private float musicVolume;
    private float ambientVolume;
    private float playerVolume;
    private float enemyVolume;
    private String upKey;
    private String downKey;
    private String leftKey;
    private String rightKey;

    // Getters
    public int getMaxFPS() { return maxFPS; }
    public boolean isVsync() { return vsync; }
    public boolean isFullscreen() { return fullscreen; }
    public String getResolution() { return resolution; }
    public int getWidth() { return extractResolutionPart(0); }
    public int getHeight() { return extractResolutionPart(1); }
    public boolean isShowStats() { return showStats; }
    public float getMainVolume() { return mainVolume; }
    public float getMusicVolume() { return musicVolume; }
    public float getAmbientVolume() { return ambientVolume; }
    public float getPlayerVolume() { return playerVolume; }
    public float getEnemyVolume() { return enemyVolume; }
    public String getUpKey() { return upKey; }
    public String getDownKey() { return downKey; }
    public String getLeftKey() { return leftKey; }
    public String getRightKey() { return rightKey; }

    // Setters
    public void setMaxFPS(int maxFPS) { this.maxFPS = maxFPS; }
    public void setVsync(boolean vsync) { this.vsync = vsync; }
    public void setFullscreen(boolean fullscreen) { this.fullscreen = fullscreen; }
    public void setResolution(String resolution) { this.resolution = resolution; }
    public void setShowStats(boolean showStats) { this.showStats = showStats; }
    public void setMainVolume(float mainVolume) { this.mainVolume = mainVolume; }
    public void setMusicVolume(float musicVolume) { this.musicVolume = musicVolume; }
    public void setAmbientVolume(float ambientVolume) { this.ambientVolume = ambientVolume; }
    public void setPlayerVolume(float playerVolume) { this.playerVolume = playerVolume; }
    public void setEnemyVolume(float enemyVolume) { this.enemyVolume = enemyVolume; }
    public void setUpKey(String upKey) { this.upKey = upKey; }
    public void setDownKey(String downKey) { this.downKey = downKey; }
    public void setLeftKey(String leftKey) { this.leftKey = leftKey; }
    public void setRightKey(String rightKey) { this.rightKey = rightKey; }

    private int extractResolutionPart(int index) {
        if (resolution != null && resolution.contains("x")) {
            String[] parts = resolution.split("x");
            try {
                return Integer.parseInt(parts[index]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return -1;
    }

    public int getKeyCode(String action){
        String key = null;
        switch (action) {
            case "up":
                key = getUpKey();
                break;
            case "down":
                key = getDownKey();
                break;
            case "left":
                key = getLeftKey();
                break;
            case "right":
                key = getRightKey();
                break;
        }
        return mapKeyToCode(key);
    }

    private int mapKeyToCode(String key) {
        switch (key.toUpperCase()) {
            case "W":
                return Input.KEY_W;
            case "A":
                return Input.KEY_A;
            case "S":
                return Input.KEY_S;
            case "D":
                return Input.KEY_D;
            // Add more key mappings as needed
            default:
                throw new IllegalArgumentException("Unsupported key: " + key);
        }
    }
}
