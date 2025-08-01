package io.github.anthonyclemens.states;

import java.util.Map;
import java.util.Random;

import org.lwjgl.Sys;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.InputAdapter;
import org.newdawn.slick.util.Log;

import com.codedisaster.steamworks.SteamAPI;

import io.github.anthonyclemens.GameObjects.Mobs.Fish;
import io.github.anthonyclemens.GameObjects.MultiTileObject;
import io.github.anthonyclemens.GameObjects.SingleTileObjects.SingleTileObject;
import io.github.anthonyclemens.GameStates;
import io.github.anthonyclemens.Logic.Calender;
import io.github.anthonyclemens.Logic.DayNightCycle;
import io.github.anthonyclemens.Player.Player;
import io.github.anthonyclemens.Rendering.Camera;
import io.github.anthonyclemens.Rendering.IsoRenderer;
import io.github.anthonyclemens.Settings;
import io.github.anthonyclemens.SharedData;
import io.github.anthonyclemens.Sound.JukeBox;
import io.github.anthonyclemens.Sound.SoundBox;
import io.github.anthonyclemens.Utils;
import io.github.anthonyclemens.WorldGen.Chunk;
import io.github.anthonyclemens.WorldGen.ChunkManager;
import io.github.anthonyclemens.utils.AmbientSoundManager;
import io.github.anthonyclemens.utils.CollisionHandler;
import io.github.anthonyclemens.utils.DebugGUI;
import io.github.anthonyclemens.utils.DisplayHUD;
import io.github.anthonyclemens.utils.Profiler;
import io.github.anthonyclemens.utils.SaveLoadManager;

public class Game extends BasicGameState{

    // Game Variables
    private float zoom = 2.0f;
    private float cameraX = 0;
    private float cameraY = 0;
    private boolean dragging = false;
    private float lastMouseX;
    private float lastMouseY;
    private boolean showHUD = true;
    public static boolean paused = false;
    private int updateAccumulator = 0;
    private final int targetInterval = 1000 / 15;

    // Game Constants
    private Image backgroundImage;
    private static final float MIN_ZOOM = 0.10f;

    // Game Objects
    private Camera camera;
    private Player player;
    private DayNightCycle env;
    private Calender calender;
    private IsoRenderer renderer;
    public static JukeBox jukeBox;
    public static SoundBox ambientSoundBox;
    public static SoundBox passiveMobSoundBox;
    public static SoundBox enemyMobSoundBox;
    public static SoundBox gameObjectSoundBox;
    ChunkManager chunkManager;

    // Debug Related Variables
    public static boolean showDebug = true;
    public static boolean soundDebug = false;
    public static boolean showCursorLoc = false;

    private CollisionHandler collisionHandler;
    private DebugGUI debugGUI;
    private DisplayHUD displayHUD;
    private AmbientSoundManager ambientSoundManager;
    private SaveLoadManager saveLoadManager;


    private Profiler updateProfiler;
    private Profiler renderProfiler;
    int frameCounter = 0;
    final int updateInterval = 8;

    Map<String, Float> updateData = null;
    Map<String, Float> renderData = null;
    Color[] updateColors = null;
    Color[] renderColors = null;


    @Override
    public int getID() {
        return GameStates.GAME.getID();
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        updateProfiler = new Profiler();
        renderProfiler = new Profiler();

        this.backgroundImage = new Image("textures/MissingTexture.png");

        collisionHandler = new CollisionHandler();
        debugGUI = new DebugGUI();
        displayHUD = new DisplayHUD();
        saveLoadManager = new SaveLoadManager();
        Settings settings = Settings.getInstance();
        Log.debug("Entering Game State with hotstart: " + SharedData.isHotstart() + ", loading save: " + SharedData.isLoadingSave()
                + ", new game: " + SharedData.isNewGame());
        ambientSoundBox.setVolume(settings.getMainVolume()*settings.getAmbientVolume());
        jukeBox.setVolume(settings.getMainVolume()*settings.getMusicVolume());
        passiveMobSoundBox.setVolume(settings.getMainVolume()*settings.getFriendlyVolume());
        enemyMobSoundBox.setVolume(settings.getMainVolume()*settings.getEnemyVolume());
        gameObjectSoundBox.setVolume(settings.getMainVolume()*settings.getFriendlyVolume());
        if(SharedData.isHotstart() && !SharedData.isLoadingSave()){
            return;
        }
        SharedData.setHotstart(true);
        SharedData.setGameState(this);
        calender = new Calender(1, 1, 1462);
        env = new DayNightCycle(20f, 7f, 19f, calender);
        if(SharedData.isNewGame()|| !SaveLoadManager.exists(SharedData.getSaveFilePath())){
            //Initialize the ChunkManager with randomly generated seed
            Random r = new Random(Sys.getTime());
            chunkManager = new ChunkManager(r.nextInt());
            createNewPlayer(container.getWidth()/2f, container.getHeight()/2f, 0.075f, 100);
        }else{
            saveLoadManager.loadGame(SharedData.getSaveFilePath(), container);
            chunkManager = saveLoadManager.getRenderer().getChunkManager();
            createNewPlayer(saveLoadManager.getPlayerX(), saveLoadManager.getPlayerY(), saveLoadManager.getPlayerSpeed(), saveLoadManager.getPlayerHealth());
            env = saveLoadManager.getDayNightCycle();
            player.setPlayerInventory(saveLoadManager.getPlayerInventory());
        }
        player.setVolume(settings.getMainVolume()*settings.getPlayerVolume());
        SharedData.setLoadingSave(false);
        renderer = new IsoRenderer(zoom, "main", chunkManager, container);
        chunkManager.attachRenderer(renderer);
        ambientSoundManager = new AmbientSoundManager(jukeBox, ambientSoundBox);
        ambientSoundManager.attachRenderer(renderer);
        camera = new Camera(player.getX(), player.getY());

        if(soundDebug){
            passiveMobSoundBox.setDebug(true);
            enemyMobSoundBox.setDebug(true);
            gameObjectSoundBox.setDebug(true);
        }
    }

    private void createNewPlayer(float x, float y, float speed, int health) throws SlickException{
        SpriteSheet playerSheet = new SpriteSheet("textures/Player/test.png", 16, 17);
        // Define animations for the player
        Animation[] animations = new Animation[8];
        int animationDuration = 140; // Duration of each animation frame in milliseconds
        animations[0] = new Animation(playerSheet, 0, 0, 0, 2, false, animationDuration, true); // Up
        animations[1] = new Animation(playerSheet, 1, 0, 1, 2, false, animationDuration, true); // Up-right
        animations[2] = new Animation(playerSheet, 2, 0, 2, 2, false, animationDuration, true); // Right
        animations[3] = new Animation(playerSheet, 3, 0, 3, 2, false, animationDuration, true); // Down-right
        animations[4] = new Animation(playerSheet, 4, 0, 4, 2, false, animationDuration, true); // Down
        animations[5] = new Animation(playerSheet, 5, 0, 5, 2, false, animationDuration, true); // Down-left
        animations[6] = new Animation(playerSheet, 6, 0, 6, 2, false, animationDuration, true); // Left
        animations[7] = new Animation(playerSheet, 7, 0, 7, 2, false, animationDuration, true); // Up-left


        Animation[] idleAnimations = new Animation[8];
        idleAnimations[0] = new Animation(playerSheet, 0, 1, 0, 1, false, animationDuration, true); // Idle Up
        idleAnimations[1] = new Animation(playerSheet, 1, 1, 1, 1, false, animationDuration, true); // Idle Up-right
        idleAnimations[2] = new Animation(playerSheet, 2, 1, 2, 1, false, animationDuration, true); // Idle Right
        idleAnimations[3] = new Animation(playerSheet, 3, 1, 3, 1, false, animationDuration, true); // Idle Down-right
        idleAnimations[4] = new Animation(playerSheet, 4, 1, 4, 1, false, animationDuration, true); // Idle Down
        idleAnimations[5] = new Animation(playerSheet, 5, 1, 5, 1, false, animationDuration, true); // Idle Down-left
        idleAnimations[6] = new Animation(playerSheet, 6, 1, 6, 1, false, animationDuration, true); // Idle Left
        idleAnimations[7] = new Animation(playerSheet, 7, 1, 7, 1, false, animationDuration, true); // Idle Up-left

        player = new Player(x, y, speed, animations, idleAnimations);
        player.setHealth(health);
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        container.getInput().addMouseListener(new InputAdapter() {
            //Drag
            @Override
            public void mousePressed(int button, int x, int y) {
                if (button == Input.MOUSE_RIGHT_BUTTON) {
                    dragging = true;
                    lastMouseX = x;
                    lastMouseY = y;
                }
            }

            @Override
            public void mouseReleased(int button, int x, int y) {
                if (button == Input.MOUSE_RIGHT_BUTTON) {
                    dragging = false;
                }
            }
            //Zoom
            @Override
            public void mouseWheelMoved(int change) {
                zoom += change * 0.001f;
                zoom = Math.min(Math.max(MIN_ZOOM, zoom), 8f);
            }
        });
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        updateProfiler.begin();
        updateAccumulator += delta;
        if (updateAccumulator >= targetInterval) {
            SteamAPI.runCallbacks();
            if(!paused){
                ambientSoundManager.playAmbientMusic(env);
                ambientSoundManager.playAmbientSounds(env, player);
            }
            updateAccumulator -= targetInterval;
        }
        updateProfiler.tick("SteamAPI Callback, Ambient Sound and Music");
        renderer.update(container, zoom, cameraX, cameraY);
        updateProfiler.tick("Renderer update");
        renderer.calculateHitbox(renderer, player);
        updateProfiler.tick("Hitbox Calculation");

        Input input = container.getInput();
        updateKeyboard(game, delta, input);
        updateMouse(input);
        updateProfiler.tick("Input updates");

        int[] playerLoc = renderer.screenToIsometric(player.getRenderX()+player.getHitbox().getWidth()/2, player.getRenderY()+player.getHitbox().getHeight());
        Chunk currentChunk = chunkManager.getChunk(playerLoc[2], playerLoc[3]);
        player.update(input, delta, playerLoc, currentChunk, paused);
        updateProfiler.tick("Player update");
        if(!paused) {
            collisionHandler.checkPlayerCollision(player, chunkManager.getChunk(playerLoc[2], playerLoc[3]));
        }
        updateProfiler.tick("Collision handler");

        camera.update(player, input, cameraX, cameraY);
        cameraX = camera.getX();
        cameraY = camera.getY();
        if(!paused){
            player.interact(input, chunkManager);
            updateProfiler.tick("Player interaction");

            env.updateDayNightCycle(delta);
            renderer.updateVisibleChunks(delta,player);
            updateProfiler.tick("Update Visible Chunks and GameObjects");
            if(showCursorLoc&&input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)){
                int[] cursorLoc = renderer.screenToIsometric(container.getInput().getMouseX(), container.getInput().getMouseY());
                SingleTileObject test = new SingleTileObject("main","snow", 17, cursorLoc[0]-2, cursorLoc[1]-1, cursorLoc[2], cursorLoc[3]);
                test.setSolid(false);
                chunkManager.getChunk(cursorLoc[2], cursorLoc[3]).addGameObject(test);
            }
        }
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        renderProfiler.begin();
        backgroundImage.draw(0, 0, container.getWidth(), container.getHeight());
        renderer.render();
        renderProfiler.tick("Chunks");
        player.render(container, zoom, camera.getX(), camera.getY());
        renderProfiler.tick("Player");
        env.renderOverlay(container, g);
        if (showHUD) displayHUD.renderHUD(container, g, calender, env, player);
        if (showCursorLoc){
            int[] cursorLoc = renderer.screenToIsometric(container.getInput().getMouseX(), container.getInput().getMouseY());
            renderer.drawScaledTile("main", 17, cursorLoc[0]-2, cursorLoc[1]-1, cursorLoc[2], cursorLoc[3]);
        }
        if (showDebug) debugGUI.renderDebugGUI(g, container, renderer, player, zoom, jukeBox, ambientSoundBox);
        frameCounter++;
        if (frameCounter >= updateInterval && showDebug) {
            updateData = updateProfiler.getPercentages();
            renderData = renderProfiler.getPercentages();

            updateColors = generateColors(updateData.size());
            renderColors = generateColors(renderData.size());

            frameCounter = 0;
        }
        if (updateData != null && renderData != null && showDebug) {
            drawLegend(g, updateData, updateColors, 0, container.getHeight()-(updateData.size()*24f), 20);
            drawLegend(g, renderData, renderColors, 400, container.getHeight()-(updateData.size()*24f),20);
        }
    }

    private void updateKeyboard(StateBasedGame game, int delta, Input input) throws SlickException{
        if (input.isKeyDown(Input.KEY_LEFT)) cameraX -= delta * 0.1f * zoom;
        if (input.isKeyDown(Input.KEY_RIGHT)) cameraX += delta * 0.1f * zoom;
        if (input.isKeyDown(Input.KEY_UP)) cameraY -= delta * 0.1f * zoom;
        if (input.isKeyDown(Input.KEY_DOWN)) cameraY += delta * 0.1f * zoom;
        if (input.isKeyPressed(Input.KEY_ESCAPE)) SharedData.enterState(GameStates.PAUSE_MENU, game);
        if (input.isKeyPressed(Input.KEY_F1)) showHUD=!showHUD;
        if (input.isKeyPressed(Input.KEY_F2)) {
            boolean oldShowDebug = showDebug;
            boolean oldShowHUD = showHUD;
            showDebug = false;
            showHUD = false;
            this.render(game.getContainer(),game,game.getContainer().getGraphics());
            Utils.takeScreenShot(game.getContainer().getGraphics(), game.getContainer());
            showDebug = oldShowDebug;
            showHUD = oldShowHUD;
        }
        if (input.isKeyPressed(Input.KEY_F3)) showDebug=!showDebug;
        if (input.isKeyPressed(Input.KEY_F4)) showCursorLoc=!showCursorLoc;
        if (input.isKeyPressed(Input.KEY_F11)){
            boolean toggleFullscreen = !game.getContainer().isFullscreen();
            game.getContainer().setFullscreen(toggleFullscreen);
        }
        if (input.isKeyPressed(Input.KEY_F)){
            int[] clickedLoc = renderer.screenToIsometric(input.getMouseX(), input.getMouseY());
            Chunk clickedChunk = chunkManager.getChunk(clickedLoc[2], clickedLoc[3]);
            int numGobs = clickedChunk.getGameObjects().size();
            Fish nFish = new Fish("fishs", clickedLoc[0], clickedLoc[1], clickedLoc[2], clickedLoc[3], "fish");
            nFish.setID(numGobs);
            clickedChunk.addGameObject(nFish);
        }
        if (input.isKeyPressed(Input.KEY_P)) paused = !paused;
        if (input.isKeyPressed(Input.KEY_B)){
            int[] clickedLoc = renderer.screenToIsometric(input.getMouseX(), input.getMouseY());
            Chunk clickedChunk = chunkManager.getChunk(clickedLoc[2], clickedLoc[3]);
            int numGobs = clickedChunk.getGameObjects().size();
            MultiTileObject test = new MultiTileObject("main", clickedLoc[0], clickedLoc[1], clickedLoc[2], clickedLoc[3], "test");
            test.addBlock(30, 2, 2, 0);
            test.addBlock(30, 2, 2, 1);
            test.addBlock(30, 2, 2, 2);
            test.addBlock(40, 2, 2, 4);
            test.addBlock(40, 3, 2, 3);
            test.addBlock(40, 3, 3, 3);
            test.addBlock(40, 2, 3, 3);
            test.addBlock(40, 1, 3, 3);
            test.addBlock(40, 1, 1, 3);
            test.addBlock(40, 1, 2, 3);
            test.addBlock(40, 2, 1, 3);
            test.addBlock(40, 3, 3, 3);
            test.addBlock(40, 3, 1, 3);
            test.setID(numGobs);
            clickedChunk.addGameObject(test);
        }
    }

    private void updateMouse(Input input){
        // Handle mouse dragging
        if (dragging) {
            float currentMouseX = input.getMouseX();
            float currentMouseY = input.getMouseY();

            // Calculate the delta movement
            float deltaX = (currentMouseX - lastMouseX) / zoom;
            float deltaY = (currentMouseY - lastMouseY) / zoom;

            // Update the camera position
            cameraX -= deltaX;
            cameraY -= deltaY;

            // Update the last mouse position
            lastMouseX = currentMouseX;
            lastMouseY = currentMouseY;
        }
    }

    public IsoRenderer getRenderer() {
        return renderer;
    }

    public Player getPlayer() {
        return player;
    }

    public DayNightCycle getEnv() {
        return env;
    }

    public ChunkManager getChunkManager() {
        return chunkManager;
    }

    public Camera getCamera() {
        return camera;
    }

    public void saveGame(String filepath) {
        saveLoadManager.saveGame(filepath, env, chunkManager, camera, player);
    }

    public static void stopAllSounds() {
        if (jukeBox != null) jukeBox.stopMusic();
        if (ambientSoundBox != null) ambientSoundBox.stopAllSounds();
        if (passiveMobSoundBox != null) passiveMobSoundBox.stopAllSounds();
        if (enemyMobSoundBox != null) enemyMobSoundBox.stopAllSounds();
        if (gameObjectSoundBox != null) gameObjectSoundBox.stopAllSounds();
    }

    public void drawLegend(Graphics g, Map<String, Float> data, Color[] colors, float startX, float startY, int spacingY) {
        int index = 0;
        for (var entry : data.entrySet()) {
            Color c = colors[index % colors.length];
            String label = entry.getKey();
            float percent = entry.getValue();

            g.setColor(c);
            g.fillRect(startX, startY + (index * spacingY), 12, 12); // color box
            g.setColor(Color.white);
            g.drawString(label + String.format(" (%.1f%%)", percent), startX + 16, startY + (index * spacingY));

            index++;
        }
    }

    public Color[] generateColors(int count) {
        Color[] colors = new Color[count];
        for (int i = 0; i < count; i++) {
            float ratio = i / (float) count;
            float r = (float)Math.sin(ratio * Math.PI * 2) * 0.5f + 0.5f;
            float g = (float)Math.sin((ratio + 0.33f) * Math.PI * 2) * 0.5f + 0.5f;
            float b = (float)Math.sin((ratio + 0.66f) * Math.PI * 2) * 0.5f + 0.5f;
            colors[i] = new Color(r, g, b);
        }
        return colors;
    }
}
