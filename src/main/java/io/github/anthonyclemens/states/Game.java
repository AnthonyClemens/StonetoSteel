package io.github.anthonyclemens.states;

import java.util.Random;

import org.lwjgl.Sys;
import org.newdawn.slick.Animation;
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

import io.github.anthonyclemens.GameObjects.Mobs.Fish;
import io.github.anthonyclemens.GameObjects.MultiTileObject;
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

    @Override
    public int getID() {
        return GameStates.GAME.getID();
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        calender = new Calender(1, 1, 1462);
        env = new DayNightCycle(20f, 7f, 19f, calender);

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
        Input input = container.getInput();
        int[] playerLoc = renderer.screenToIsometric(player.getRenderX()+player.getHitbox().getWidth()/2, player.getRenderY()+player.getHitbox().getHeight());

        env.updateDayNightCycle(delta);
        ambientSoundManager.playAmbientMusic(env);
        ambientSoundManager.playAmbientSounds(env, player);

        updateKeyboard(game, delta, input);
        updateMouse(input);
        Chunk currentChunk = chunkManager.getChunk(playerLoc[2], playerLoc[3]);
        player.update(input, delta, playerLoc, currentChunk);
        collisionHandler.checkPlayerCollision(player, chunkManager.getChunk(playerLoc[2], playerLoc[3]));

        camera.update(player, input, cameraX, cameraY);
        cameraX = camera.getX();
        cameraY = camera.getY();
        player.interact(input, chunkManager);

        renderer.update(container, zoom, cameraX, cameraY);
        renderer.updateVisibleChunks(delta,player);
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        backgroundImage.draw(0, 0, container.getWidth(), container.getHeight());
        renderer.render();
        player.render(container, zoom, camera.getX(), camera.getY());
        env.renderOverlay(container, g);
        if (showHUD) displayHUD.renderHUD(container, g, calender, env, player);
        if (showCursorLoc){
            int[] cursorLoc = renderer.screenToIsometric(container.getInput().getMouseX(), container.getInput().getMouseY());
            renderer.drawScaledTile("main", 105, cursorLoc[0], cursorLoc[1], cursorLoc[2], cursorLoc[3]);
        }
        if (showDebug) debugGUI.renderDebugGUI(g, container, renderer, player, zoom, jukeBox, ambientSoundBox);
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

}
