package io.github.anthonyclemens.GameObjects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.github.anthonyclemens.Rendering.IsoRenderer;
import io.github.anthonyclemens.Rendering.SpriteManager;

public class MultiTileObject extends GameObject{
    private final int tileWidth;
    private final int tileHeight;
    private final List<TileBlock> blocks = new ArrayList<>();

    private static class TileBlock implements Serializable{
        private final int x;
        private final int y;
        private final int height;
        private final int tileIndex;

        public TileBlock(int x, int y, int height, int tileIndex) {
            this.x = x;
            this.y = y;
            this.height = height;
            this.tileIndex = tileIndex;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getHeight() {
            return height;
        }

        public int getTileIndex() {
            return tileIndex;
        }
    }

    public MultiTileObject(String tileSheet, int x, int y, int chunkX, int chunkY, String objName) {
        super(tileSheet, x, y, chunkX, chunkY, objName);
        this.tileWidth = SpriteManager.getSpriteWidth(tileSheet);
        this.tileHeight = SpriteManager.getSpriteHeight(tileSheet);
    }

    public MultiTileObject(String loadedMTO) {
        super("", 0,0, 0, 0, "");
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(loadedMTO);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            if (inputStream == null) {
                throw new IllegalArgumentException("File not found: " + loadedMTO);
            }

            // Read the first line for object properties
            String firstLine = reader.readLine();
            if (firstLine == null) {
                throw new IllegalArgumentException("File is empty: " + loadedMTO);
            }

            String[] objectParts = firstLine.split(",");
            if (objectParts.length != 6) {
                throw new IllegalArgumentException("Invalid first line format: " + firstLine);
            }
            // Parse the object properties
            this.x = Integer.parseInt(objectParts[1].trim());
            this.y = Integer.parseInt(objectParts[2].trim());
            this.chunkX = Integer.parseInt(objectParts[3].trim());
            this.chunkY = Integer.parseInt(objectParts[4].trim());
            this.name = objectParts[5].trim();
            this.tileSheet = objectParts[0].trim();

            // Read the remaining lines for blocks
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 4) {
                    throw new IllegalArgumentException("Invalid line format: " + line);
                }

                int index = Integer.parseInt(parts[0].trim());
                int blockX = Integer.parseInt(parts[1].trim());
                int blockY = Integer.parseInt(parts[2].trim());
                int h = Integer.parseInt(parts[3].trim());

                addBlock(index, blockX, blockY, h);
            }
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        this.tileWidth = SpriteManager.getSpriteWidth(tileSheet);
        this.tileHeight = SpriteManager.getSpriteHeight(tileSheet);
    }

    @Override
    public void render(IsoRenderer r) {
        blocks.stream()
              .sorted((b,a) -> {
              int cmpY = Integer.compare(b.getY(), a.getY());
              if (cmpY != 0) return cmpY;
              return Integer.compare(b.getX(), a.getX());
              })
              .forEach(block -> r.drawHeightedTile(this.tileSheet, block.getTileIndex(), getX() + block.getX(), getY() + block.getY(), chunkX, chunkY, block.getHeight()));
    }

    public void addBlock(int index, int x, int y, int h){
        blocks.add(new TileBlock(x, y, h, index));
    }

    @Override
    public void update(IsoRenderer r, int deltaTime) {
        // No update logic for MultiTileObject
    }

}
