package io.github.anthonyclemens.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.InputAdapter;

import io.github.anthonyclemens.Player.Player;
import io.github.anthonyclemens.Rendering.Renderer;
import io.github.anthonyclemens.WorldGen.ChunkManager;
import io.github.anthonyclemens.WorldGen.MultiTileObject;

public class Game extends BasicGameState{

    private static final int TILE_WIDTH = 18;
    private static final int TILE_HEIGHT = 18;
    private float zoom = 1.0f;
    private float cameraX = 0;
    private float cameraY = 0;
    private Renderer renderer;
    private ChunkManager chunkManager;
    private Player player;
    private int test = 0;

    @Override
    public int getID() {
        return 2;
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        SpriteSheet tileSheet = new SpriteSheet("textures/World/tinyBlocks_NOiL.png", TILE_WIDTH, TILE_HEIGHT);
        SpriteSheet playerSS = new SpriteSheet("textures/Player/test.png", 16, 16);
        player = new Player(playerSS, 0, 0);
        chunkManager = new ChunkManager();
        renderer = new Renderer(zoom, tileSheet, chunkManager);
        MultiTileObject tree = new MultiTileObject(8, 8, 0, 0, new int[][]{
            {-1,40,-1},
            {40,40,40},
            {30,-1,-1}
        });
        chunkManager.getChunk(0, 0).addMultiTileObject(tree);
        container.getInput().addMouseListener(new InputAdapter() {
            @Override
            public void mouseWheelMoved(int change) {
                zoom += change * 0.001f;
                zoom = Math.min(Math.max(0.2f, zoom),6f);
            }
        });
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        Input input = container.getInput();
        if (input.isKeyDown(Input.KEY_LEFT)) cameraX -= delta * 0.1f * zoom;
        if (input.isKeyDown(Input.KEY_RIGHT)) cameraX += delta * 0.1f * zoom;
        if (input.isKeyDown(Input.KEY_UP)) cameraY -= delta * 0.1f * zoom;
        if (input.isKeyDown(Input.KEY_DOWN)) cameraY += delta * 0.1f * zoom;
        if (input.isKeyPressed(Input.KEY_ADD)) test++;
        renderer.update(container, zoom, cameraX, cameraY);
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        renderer.render(g);
        chunkManager.getChunk(0, 0).render(renderer);
        renderer.drawTile(test, new int[]{0,0}, new int[]{0,0});
        player.render(renderer);
    }
}
