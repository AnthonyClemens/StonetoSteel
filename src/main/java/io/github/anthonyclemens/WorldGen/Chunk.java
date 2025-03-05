package io.github.anthonyclemens.WorldGen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.github.anthonyclemens.Rendering.Renderer;

public class Chunk {
    private int chunkSize;
    private final int[][] tiles;
    private List<GameObject> gameObjects;
    private Random r;

    public Chunk(int chunkSize) {
        this.r = new Random();
        this.chunkSize = chunkSize;
        this.tiles = new int[chunkSize][chunkSize];
        this.gameObjects = new ArrayList<>();
        this.generateTiles();
    }


    private void generateTiles() {
        for (int x = 0; x < this.chunkSize; x++) {
            for (int y = 0; y < this.chunkSize; y++) {
                this.tiles[x][y] = r.nextInt(3);
            }
        }
    }

    public void addGameObject(GameObject obj) {
        gameObjects.add(obj);
    }

    public void addMultiTileObject(MultiTileObject obj) {
        gameObjects.add(obj);
    }

    public int getTileType(int x, int y) {
        return this.tiles[x][y];
    }

    public void render(Renderer r) {
        for (GameObject obj : gameObjects) {
            obj.render(r);
        }
    }

    public int getChunkSize(){
        return this.chunkSize;
    }
}