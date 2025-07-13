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

import io.github.anthonyclemens.GameObjects.MultiTileObject;
import io.github.anthonyclemens.GameStates;
import io.github.anthonyclemens.Logic.Calender;
import io.github.anthonyclemens.Logic.DayNightCycle;
import io.github.anthonyclemens.Player.Player;
import io.github.anthonyclemens.Rendering.Camera;
import io.github.anthonyclemens.Rendering.IsoRenderer;
import io.github.anthonyclemens.Rendering.SpriteManager;
import io.github.anthonyclemens.Settings;
import io.github.anthonyclemens.SharedData;
import io.github.anthonyclemens.Sound.JukeBox;
import io.github.anthonyclemens.Sound.SoundBox;
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

    // Game Constants
    private Image backgroundImage;
    private static final int TILE_WIDTH = 18;
    private static final int TILE_HEIGHT = 18;
    private static final float minZoom = 0.40f;

    // Game Objects
    private Camera camera;
    private Player player;
    private DayNightCycle env;
    private Calender calender;
    private IsoRenderer renderer;
    public static JukeBox jukeBox;
    public static SoundBox ambientSoundBox;
    ChunkManager chunkManager;

    // Debug Related Variables
    public static boolean showDebug = true;

    private CollisionHandler collisionHandler;
    private DebugGUI debugGUI;
    private DisplayHUD displayHUD;
    private AmbientSoundManager ambientSoundManager;
    private SaveLoadManager saveLoadManager;

    @Override
    public int getID() {
        return 99;
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        Settings settings = Settings.getInstance();
        Log.debug("Entering Game State with hotstart: " + SharedData.isHotstart() + ", loading save: " + SharedData.isLoadingSave()
                + ", new game: " + SharedData.isNewGame());
        if(SharedData.isHotstart() && !SharedData.isLoadingSave()){
            return;
        }
        SharedData.setHotstart(true);
        SharedData.setGameState(this);
        SharedData.setLoadingSave(false);
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
        }
        renderer = new IsoRenderer(zoom, "main", chunkManager, container);
        chunkManager.attachRenderer(renderer);
        ambientSoundManager = new AmbientSoundManager(jukeBox, ambientSoundBox);
        ambientSoundManager.attachRenderer(renderer);
        camera = new Camera(player.getX(), player.getY());

        ambientSoundBox.setVolume(settings.getMainVolume()*settings.getAmbientVolume());
        jukeBox.setVolume(settings.getMainVolume()*settings.getMusicVolume());
        player.setVolume(settings.getMainVolume()*settings.getPlayerVolume());

        MultiTileObject test = new MultiTileObject("main", 8, 8, 0, 0, "test");
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
        chunkManager.addGameObject(test);
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
        SpriteManager.addSpriteSheet("main", "textures/World/18x18.png", TILE_WIDTH, TILE_HEIGHT);
        SpriteManager.addSpriteSheet("fishes", "textures/Organisms/fish.png", 16, 16);
        SpriteManager.addSpriteSheet("bigtrees", "textures/World/48x48.png", 48, 48);
        SpriteManager.addSpriteSheet("smalltrees", "textures/World/16x32.png", 16, 32);
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
                zoom = Math.min(Math.max(minZoom, zoom), 8f);
            }
        });
        calender = new Calender(16, 3, 2025);
        env = new DayNightCycle(4f, 6f, 19f, calender);
        

        this.backgroundImage = new Image("textures/MissingTexture.png");

        collisionHandler = new CollisionHandler();
        debugGUI = new DebugGUI();
        displayHUD = new DisplayHUD();
        saveLoadManager = new SaveLoadManager();
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
        player.update(input, delta, chunkManager.getChunk(playerLoc[2], playerLoc[3]).getTile(playerLoc[0], playerLoc[1]));
        collisionHandler.checkPlayerCollision(player, chunkManager.getChunk(playerLoc[2], playerLoc[3]));

        camera.update(player, input, cameraX, cameraY);
        cameraX = camera.getX();
        cameraY = camera.getY();

        renderer.update(container, zoom, cameraX, cameraY);
        renderer.updateVisibleChunks(delta);
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        backgroundImage.draw(0, 0, container.getWidth(), container.getHeight());
        renderer.render(player);
        player.render(container, zoom, camera.getX(), camera.getY());
        env.renderOverlay(container, g);
        displayHUD.renderHUD(container, g, calender, env);
        if (showDebug) debugGUI.renderDebugGUI(g, container, renderer, player, zoom, jukeBox, ambientSoundBox);
    }

    private void updateKeyboard(StateBasedGame game, int delta, Input input) throws SlickException{
        if (input.isKeyDown(Input.KEY_LEFT)) cameraX -= delta * 0.1f * zoom;
        if (input.isKeyDown(Input.KEY_RIGHT)) cameraX += delta * 0.1f * zoom;
        if (input.isKeyDown(Input.KEY_UP)) cameraY -= delta * 0.1f * zoom;
        if (input.isKeyDown(Input.KEY_DOWN)) cameraY += delta * 0.1f * zoom;
        if (input.isKeyPressed(Input.KEY_ESCAPE)) SharedData.enterState(GameStates.PAUSE_MENU, game);
        if (input.isKeyPressed(Input.KEY_F3)) showDebug=!showDebug;
        if (input.isKeyPressed(Input.KEY_F11)){
            boolean toggleFullscreen = !game.getContainer().isFullscreen();
            game.getContainer().setFullscreen(toggleFullscreen);
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

    public JukeBox getJukeBox() {
        return jukeBox;
    }

    public SoundBox getAmbientSoundManager() {
        return ambientSoundBox;
    }

}
