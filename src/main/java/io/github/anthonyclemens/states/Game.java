package io.github.anthonyclemens.states;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

import io.github.anthonyclemens.GameObjects.GameObject;
import io.github.anthonyclemens.Logic.Calender;
import io.github.anthonyclemens.Logic.DayNightCycle;
import io.github.anthonyclemens.Player.Player;
import io.github.anthonyclemens.Rendering.Camera;
import io.github.anthonyclemens.Rendering.IsoRenderer;
import io.github.anthonyclemens.Settings;
import io.github.anthonyclemens.SharedData;
import io.github.anthonyclemens.Sound.JukeBox;
import io.github.anthonyclemens.Sound.SoundBox;
import io.github.anthonyclemens.WorldGen.Biome;
import io.github.anthonyclemens.WorldGen.Chunk;
import io.github.anthonyclemens.WorldGen.ChunkManager;

public class Game extends BasicGameState{

    // Game Variables
    private float zoom = 2.0f;
    private float cameraX = 0;
    private float cameraY = 0;
    private boolean dragging = false;
    private float lastMouseX;
    private float lastMouseY;
    private boolean dayNightSwitch = true;
    private Biome lastBiome = null;

    // Game Constants
    private Image backgroundImage;
    private SpriteSheet tileSheet;
    private static final int TILE_WIDTH = 18;
    private static final int TILE_HEIGHT = 18;

    // Game Objects
    private Camera camera;
    private Player player;
    private DayNightCycle env;
    private Calender calender;
    private IsoRenderer renderer;
    private JukeBox jukeBox;
    private SoundBox ambientSoundBox;

    // Debug Related Variables
    public static boolean showDebug = true;

    // Music and Sound Definitions
    private final List<String> dayMusic = new ArrayList<>(Arrays.asList("music/day/ForestWalk.ogg","music/day/SpringFlowers.ogg"));
    private final List<String> nightMusic = new ArrayList<>(Arrays.asList("music/night/Moonlight-ScottBuckley.ogg","music/night/AdriftAmongInfiniteStars-ScottBuckley.ogg"));

    private final List<String> plainsSounds = new ArrayList<>(Arrays.asList("sounds/Plains/birds.ogg", "sounds/Plains/birds1.ogg"));
    private final List<String> nightSounds = new ArrayList<>(Arrays.asList("sounds/Night/crickets.ogg", "sounds/Night/cicadas.ogg"));
    private final List<String> desertSounds = new ArrayList<>(Arrays.asList("sounds/Desert/wind.ogg"));
    private final List<String> waterSounds = new ArrayList<>(Arrays.asList("sounds/Water/flowingwater.ogg"));
    private final List<String> beachSounds = new ArrayList<>(Arrays.asList("sounds/Beach/waves.ogg"));

    @Override
    public int getID() {
        return 99;
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        Settings settings = Settings.getInstance();
        //Initialize the ChunkManager with seed entered or randomly generate one
        ChunkManager chunkManager;
        if(SharedData.seed!=0){
            chunkManager = new ChunkManager(SharedData.seed);
        }else{
            Random r = new Random(Sys.getTime());
            chunkManager = new ChunkManager(r.nextInt());
        }
        renderer = new IsoRenderer(zoom, tileSheet, chunkManager, container);
        chunkManager.attachRenderer(renderer);
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

        player = new Player(container.getWidth()/2f, container.getHeight()/2f, 0.075f, animations, idleAnimations);
        camera = new Camera(container.getWidth()/2f, container.getHeight()/2f);

        ambientSoundBox.setVolume(settings.getMainVolume()*settings.getAmbientVolume());
        jukeBox.setVolume(settings.getMainVolume()*settings.getMusicVolume());
        player.setVolume(settings.getMainVolume()*settings.getPlayerVolume());
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        tileSheet = new SpriteSheet("textures/World/18x18.png", TILE_WIDTH, TILE_HEIGHT);
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
                zoom = Math.min(Math.max(0.5f, zoom), 8f);
            }
        });
        calender = new Calender(16, 3, 2025);
        env = new DayNightCycle(2f, 6f, 19f, calender);

        jukeBox = new JukeBox();
        jukeBox.addSongs("dayMusic", dayMusic);
        jukeBox.addSongs("nightMusic", nightMusic);

        ambientSoundBox = new SoundBox();
        ambientSoundBox.addSounds("plainsSounds", plainsSounds);
        ambientSoundBox.addSounds("desertSounds", desertSounds);
        ambientSoundBox.addSounds("plainsSounds", plainsSounds);
        ambientSoundBox.addSounds("waterSounds", waterSounds);
        ambientSoundBox.addSounds("beachSounds", beachSounds);
        ambientSoundBox.addSounds("nightSounds", nightSounds);

        this.backgroundImage = new Image("textures/MissingTexture.png");
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        Input input = container.getInput();
        int[] playerLoc = renderer.screenToIsometric(player.getRenderX(), player.getRenderY());
        int playerBlock = renderer.getChunkManager().getChunk(playerLoc[2], playerLoc[3]).getTile(playerLoc[0], playerLoc[1]);
        env.updateDayNightCycle(delta);
        playAmbientMusic();
        playAmbientSounds(env);
        updateKeyboard(game, delta, input, container);
        updateMouse(input);
        player.update(input, delta, playerBlock);
        checkCollision(player, renderer.getChunkManager().getChunk(playerLoc[2], playerLoc[3]));
        camera.update(player, input, cameraX, cameraY);
        cameraX=camera.getX();
        cameraY=camera.getY();
        renderer.update(container, zoom, camera.getX(), camera.getY());
    }


    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        backgroundImage.draw(0, 0, container.getWidth(), container.getHeight());
        renderer.render(player);
        player.render(container, zoom, camera.getX(), camera.getY());
        env.renderOverlay(container, g);
        displayHUD(container, g);
        if (showDebug) debugGUI(g, container);
    }


    private void updateKeyboard(StateBasedGame game, int delta, Input input, GameContainer container) throws SlickException{
        if (input.isKeyDown(Input.KEY_LEFT)) cameraX -= delta * 0.1f * zoom;
        if (input.isKeyDown(Input.KEY_RIGHT)) cameraX += delta * 0.1f * zoom;
        if (input.isKeyDown(Input.KEY_UP)) cameraY -= delta * 0.1f * zoom;
        if (input.isKeyDown(Input.KEY_DOWN)) cameraY += delta * 0.1f * zoom;
        if (input.isKeyPressed(Input.KEY_ESCAPE)) game.enterState(0);
        if (input.isKeyPressed(Input.KEY_F3)) showDebug=!showDebug;
        if (input.isKeyPressed(Input.KEY_F7)) saveGame();
        if (input.isKeyPressed(Input.KEY_F8)) loadGame(container);
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


    private void debugGUI(Graphics g, GameContainer container){
        //Debug GUI
        Input input = container.getInput();
        g.setColor(Color.black);
        int[] selectedBlock = renderer.screenToIsometric(player.getRenderX(), player.getRenderY());
        g.drawString("FPS: "+container.getFPS(),10,20);
        g.drawString("Mouse: "+input.getMouseX()+", "+input.getMouseY(),10,40);
        g.drawString("Tile: "+selectedBlock[0]+", "+selectedBlock[1],10,60);
        g.drawString("Chunk: "+selectedBlock[2]+", "+selectedBlock[3],10,80);
        g.drawString("Zoom level: "+Math.round(zoom*100.0)/100.0+"x", 10, 100);
        g.drawString("Biome: "+renderer.getChunkManager().getBiomeForChunk(selectedBlock[2],selectedBlock[3]),10,120);
    }

    private void displayHUD(GameContainer c, Graphics g) {
        g.setColor(Color.black);
        g.drawString("Date: "+calender.toString(),c.getWidth()-200f,0);
        g.drawString("Time: "+env.toString(),c.getWidth()-200f,16);
    }

    private void playAmbientMusic(){
        if (env.isSunDown()) {
            if (!dayNightSwitch) { // Transition to night
                Log.debug("Switching to night music...");
                jukeBox.playRandomSong("nightMusic");
                dayNightSwitch = true; // Set the flag to indicate it's night
            }
        } else {
            if (dayNightSwitch) { // Transition to day
                Log.debug("Switching to day music...");
                jukeBox.playRandomSong("dayMusic");
                dayNightSwitch = false; // Set the flag to indicate it's day
            }
        }
    }

    private void playAmbientSounds(DayNightCycle dnc) {
        int[] playerBlock = renderer.screenToIsometric(player.getRenderX(), player.getRenderY());
        if(dnc.isSunDown()&&!ambientSoundBox.isAnySoundPlaying()){
            ambientSoundBox.playRandomSound("nightSounds");
        }
        Biome currentBiome = renderer.getChunkManager().getBiomeForChunk(playerBlock[2], playerBlock[3]);
        if (lastBiome != currentBiome) {
            lastBiome = currentBiome;
            ambientSoundBox.stopAllSounds();
        }
        if(dnc.isSunUp()&&!ambientSoundBox.isAnySoundPlaying()){
            switch (renderer.getChunkManager().getBiomeForChunk(playerBlock[2],playerBlock[3])) {
                    case DESERT -> ambientSoundBox.playRandomSound("desertSounds");
                    case PLAINS -> ambientSoundBox.playRandomSound("plainsSounds");
                    case WATER -> ambientSoundBox.playRandomSound("waterSounds");
                    case MOUNTAIN ->{}
                    case SWAMP ->{}
                    case BEACH -> ambientSoundBox.playRandomSound("beachSounds");
            }
        }
    }

    private void saveGame() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("save.dat"))) {
            oos.writeObject(env);
            oos.writeObject(renderer.getChunkManager());
            oos.writeObject(camera);
            oos.writeFloat(player.getX());
            oos.writeFloat(player.getY());
        } catch (IOException e) {
            Log.error("Failed to save game: " + e.getMessage());
        }
        Log.debug("Game saved.");
    }

    private void loadGame(GameContainer container) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("save.dat"))) {
            env = (DayNightCycle) ois.readObject();
            ChunkManager newChunkManager = (ChunkManager) ois.readObject();
            renderer = new IsoRenderer(zoom, tileSheet, newChunkManager, container);
            newChunkManager.attachRenderer(renderer);
            camera = (Camera) ois.readObject();
            player.setX(ois.readFloat());
            player.setY(ois.readFloat());
        } catch (IOException | ClassNotFoundException e) {
            Log.error("Failed to load game: " + e.getMessage());
        }
        Log.debug("Game loaded.");
    }

    private void checkCollision(Player player, Chunk currentChunk) {
        // Check collision with game objects
        try {
            for (GameObject gob : currentChunk.getGameObjects()) {
                if (gob.getHitbox().intersects(player.getHitbox())) {
                    // Calculate overlap
                    float overlapX = player.getHitbox().getCenterX() - gob.getHitbox().getCenterX();
                    float overlapY = player.getHitbox().getCenterY() - gob.getHitbox().getCenterY();

                    // Smoothly resolve collision by adjusting position incrementally
                    float adjustmentFactor = 0.6f; // Smaller values make the adjustment smoother
                    if (Math.abs(overlapX) > Math.abs(overlapY)) {
                        player.setX(player.getX() + (overlapX > 0 ? adjustmentFactor : -adjustmentFactor));
                    } else {
                        player.setY(player.getY() + (overlapY > 0 ? adjustmentFactor : -adjustmentFactor));
                    }

                    // Handle specific interactions based on the game object's name
                    switch (gob.getName()) {
                        case "cactus":
                            //player.takeDamage(10); // Inflict damage to the player
                            Log.debug("Player took damage from cactus!");
                            break;
                        default:
                            Log.debug("Collision detected with game object: " + gob.getName());
                            break;
                    }
                }
            }
        } catch (NullPointerException e) {
            Log.error("Error checking collision: " + e.getMessage());
        }
    }
}