package io.github.anthonyclemens.states;

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

import io.github.anthonyclemens.Logic.Calender;
import io.github.anthonyclemens.Logic.DayNightCycle;
import io.github.anthonyclemens.Player.Player;
import io.github.anthonyclemens.Rendering.Camera;
import io.github.anthonyclemens.Rendering.IsoRenderer;
import io.github.anthonyclemens.Settings;
import io.github.anthonyclemens.SharedData;
import io.github.anthonyclemens.Sound.JukeBox;
import io.github.anthonyclemens.WorldGen.ChunkManager;

public class Game extends BasicGameState{

    private static final int TILE_WIDTH = 18;
    private static final int TILE_HEIGHT = 18;
    private float zoom = 1.0f;
    private float cameraX = 0;
    private float cameraY = 0;
    private IsoRenderer renderer;
    private boolean dragging = false;
    private float lastMouseX;
    private float lastMouseY;
    private SpriteSheet tileSheet;
    private Input input;
    private DayNightCycle env;
    private Calender calender;
    private Settings settings;
    private boolean dayNightSwitch = true;
    private JukeBox jukeBox;
    private boolean showDebug = true;
    private Image backgroundImage;
    private Camera camera;
    private Player player;

    private final List<String> dayMusic = new ArrayList<>(Arrays.asList("music/day/ForestWalk.ogg","music/day/SpringFlowers.ogg"));
    private final List<String> nightMusic = new ArrayList<>(Arrays.asList("music/night/Moonlight-ScottBuckley.ogg","music/night/AdriftAmongInfiniteStars-ScottBuckley.ogg"));

    @Override
    public int getID() {
        return 99;
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        //Initialize the ChunkManager with seed entered or randomly generate one
        ChunkManager chunkManager;
        if(SharedData.seed!=0){
            chunkManager = new ChunkManager(SharedData.seed);
        }else{
            Random r = new Random(Sys.getTime());
            chunkManager = new ChunkManager(r.nextInt());
        }
        renderer = new IsoRenderer(zoom, tileSheet, chunkManager);

        SpriteSheet playerSheet = new SpriteSheet("textures/Player/test.png", 16, 17);
        // Define animations for the player
        Animation[] animations = new Animation[8];
        animations[0] = new Animation(playerSheet, 0, 0, 0, 2, false, 100, true); // Up
        animations[1] = new Animation(playerSheet, 1, 0, 1, 2, false, 100, true); // Up-right
        animations[2] = new Animation(playerSheet, 2, 0, 2, 2, false, 100, true); // Right
        animations[3] = new Animation(playerSheet, 3, 0, 3, 2, false, 100, true); // Down-right
        animations[4] = new Animation(playerSheet, 4, 0, 4, 2, false, 100, true); // Down
        animations[5] = new Animation(playerSheet, 5, 0, 5, 2, false, 100, true); // Down-left
        animations[6] = new Animation(playerSheet, 6, 0, 6, 2, false, 100, true); // Left
        animations[7] = new Animation(playerSheet, 7, 0, 7, 2, false, 100, true); // Up-left


        Animation[] idleAnimations = new Animation[8];
        idleAnimations[0] = new Animation(playerSheet, 0, 1, 0, 1, false, 100, true); // Idle Up
        idleAnimations[1] = new Animation(playerSheet, 1, 1, 1, 1, false, 100, true); // Idle Up-right
        idleAnimations[2] = new Animation(playerSheet, 2, 1, 2, 1, false, 100, true); // Idle Right
        idleAnimations[3] = new Animation(playerSheet, 3, 1, 3, 1, false, 100, true); // Idle Down-right
        idleAnimations[4] = new Animation(playerSheet, 4, 1, 4, 1, false, 100, true); // Idle Down
        idleAnimations[5] = new Animation(playerSheet, 5, 1, 5, 1, false, 100, true); // Idle Down-left
        idleAnimations[6] = new Animation(playerSheet, 6, 1, 6, 1, false, 100, true); // Idle Left
        idleAnimations[7] = new Animation(playerSheet, 7, 1, 7, 1, false, 100, true); // Idle Up-left

        player = new Player(container.getWidth()/2f, container.getHeight()/2f, 0.1f, animations, idleAnimations);
        camera = new Camera(container.getWidth()/2f, container.getHeight()/2f);
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        settings = Settings.getInstance();
        tileSheet = new SpriteSheet("textures/World/tinyBlocks_NOiL.png", TILE_WIDTH, TILE_HEIGHT);
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
                zoom = Math.min(Math.max(0.2f, zoom), 6f);
            }
        });
        input = container.getInput();
        calender = new Calender(16, 3, 2025);
        env = new DayNightCycle(2f, 6f, 19f, calender);
        jukeBox = new JukeBox();
        jukeBox.addSongs("dayMusic", dayMusic);
        jukeBox.addSongs("nightMusic", nightMusic);
        jukeBox.setVolume(0.5f);
        this.backgroundImage = new Image("textures/MissingTexture.png");
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        env.updateDayNightCycle(delta);
        ambientMusic();
        updateKeyboard(game, delta);
        updateMouse();
        player.update(input, delta);
        camera.update(player, input, cameraX, cameraY);
        cameraX=camera.getX();
        cameraY=camera.getY();
        renderer.update(container, zoom, camera.getX(), camera.getY());
    }


    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        backgroundImage.draw(0, 0, container.getWidth(), container.getHeight());
        renderer.render(g);
        player.render(container, zoom, camera.getX(), camera.getY());
        env.renderOverlay(container, g);
        displayHUD(container, g);
        if (showDebug) debugGUI(g, container);
    }


    private void updateKeyboard(StateBasedGame game, int delta) throws SlickException{
        if (input.isKeyDown(Input.KEY_LEFT)) cameraX -= delta * 0.1f * zoom;
        if (input.isKeyDown(Input.KEY_RIGHT)) cameraX += delta * 0.1f * zoom;
        if (input.isKeyDown(Input.KEY_UP)) cameraY -= delta * 0.1f * zoom;
        if (input.isKeyDown(Input.KEY_DOWN)) cameraY += delta * 0.1f * zoom;
        if (input.isKeyPressed(Input.KEY_ESCAPE)) game.enterState(0);
        if (input.isKeyPressed(Input.KEY_F3)) showDebug=!showDebug;
        if (input.isKeyPressed(Input.KEY_F11)){
            boolean toggleFullscreen = !game.getContainer().isFullscreen();
            game.getContainer().setFullscreen(toggleFullscreen);
        }
    }


    private void updateMouse(){
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
        g.setColor(Color.black);
        int[] selectedBlock = renderer.screenToIsometric(input.getMouseX(), input.getMouseY());
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

    private void ambientMusic(){
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
}