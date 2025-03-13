package io.github.anthonyclemens.states;

import java.util.Random;

import org.lwjgl.Sys;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.InputAdapter;

import io.github.anthonyclemens.Logic.Calender;
import io.github.anthonyclemens.Rendering.Environment;
import io.github.anthonyclemens.Rendering.Renderer;
import io.github.anthonyclemens.SharedData;
import io.github.anthonyclemens.WorldGen.ChunkManager;
import io.github.anthonyclemens.WorldGen.MultiTileObject;

public class Game extends BasicGameState{

    private static final int TILE_WIDTH = 18;
    private static final int TILE_HEIGHT = 18;
    private float zoom = 1.0f;
    private float cameraX = 0;
    private float cameraY = 0;
    private Renderer renderer;
    private boolean dragging = false;
    private float lastMouseX;
    private float lastMouseY;
    private SpriteSheet tileSheet;
    private Input input;
    private Environment env;
    private Calender calender;

    @Override
    public int getID() {
        return 3;
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) {
        //Initialize the ChunkManager with seed entered or randomly generate one
        ChunkManager chunkManager;
        if(SharedData.seed!=0){
            chunkManager = new ChunkManager(SharedData.seed);
        }else{
            Random r = new Random(Sys.getTime());
            chunkManager = new ChunkManager(r.nextInt());
        }
        renderer = new Renderer(zoom, tileSheet, chunkManager);
        MultiTileObject mto = new MultiTileObject.Builder()
            .setXYPos(8,8)
            .setChunkPos(chunkManager, 0, 0)
            .setTile(30, 8, 8, 0)
            .setTile(30, 8, 9, 1)
            .setTile(30, 8, 10, 2)
            .build();
        chunkManager.addGameObject(mto);
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
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
        calender = new Calender(1, 1, 1847);
        env = new Environment(.5f, 7f, 19f, calender);
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        env.updateDayNightCycle(delta);
        updateKeyboard(game, delta);
        updateMouse();
        renderer.update(container, zoom, cameraX, cameraY);
    }


    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        renderer.render(g);
        env.renderOverlay(container, g); // Apply the overlay
        displayHUD(container, g);
        debugGUI(g);
    }


    private void updateKeyboard(StateBasedGame game, int delta){
        if (input.isKeyDown(Input.KEY_LEFT)) cameraX -= delta * 0.1f * zoom;
        if (input.isKeyDown(Input.KEY_RIGHT)) cameraX += delta * 0.1f * zoom;
        if (input.isKeyDown(Input.KEY_UP)) cameraY -= delta * 0.1f * zoom;
        if (input.isKeyDown(Input.KEY_DOWN)) cameraY += delta * 0.1f * zoom;
        if (input.isKeyDown(Input.KEY_ESCAPE)) game.enterState(0);
        if (input.isKeyDown(Input.KEY_SPACE)){
            cameraX = 0;
            cameraY = 0;
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


    private void debugGUI(Graphics g){
        //Debug GUI
        g.setColor(Color.black);
        int[] selectedBlock = renderer.screenToIsometric(lastMouseX, lastMouseY);
        g.drawString("Mouse: "+lastMouseX+", "+lastMouseY,10,40);
        g.drawString("Tile: "+selectedBlock[0]+", "+selectedBlock[1],10,60);
        g.drawString("Chunk: "+selectedBlock[2]+", "+selectedBlock[3],10,80);
        g.drawString("Zoom level: "+Math.round(zoom*100.0)/100.0+"x", 10, 100);
    }

    private void displayHUD(GameContainer c, Graphics g) {
        g.setColor(Color.black);
        g.drawString("Date: "+calender.toString(),c.getWidth()-200f,0);
        g.drawString("Time: "+env.toString(),c.getWidth()-200f,16);
    }
}