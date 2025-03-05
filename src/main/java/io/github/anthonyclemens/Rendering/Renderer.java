package io.github.anthonyclemens.Rendering;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

import io.github.anthonyclemens.WorldGen.Chunk;
import io.github.anthonyclemens.WorldGen.ChunkManager;

public class Renderer {
    private static final int TILE_SIZE = 18;
    private float zoom;
    private SpriteSheet tileSheet;
    private int offsetX;
    private int offsetY;
    private Color bgColor = new Color(135, 206, 235);
    private final ChunkManager chunkManager;

    public Renderer(float zoom, SpriteSheet tileSheet, ChunkManager chunkManager) {
        this.zoom = zoom;
        this.tileSheet = tileSheet;
        this.chunkManager = chunkManager;
    }

    public void update(GameContainer container, float zoom, float cameraX, float cameraY){
        this.zoom = zoom;
        this.offsetX = (container.getWidth() / 2) - (int) cameraX;
        this.offsetY = (container.getHeight() / 2) - (int) cameraY;
    }

    public void render(Graphics g){
        g.setBackground(bgColor);

        for (int chunkX = -2; chunkX <= 2; chunkX++) {
            for (int chunkY = -2; chunkY <= 2; chunkY++) {
                renderChunk(chunkX, chunkY, g);
            }
        }
    }

    private void renderChunk(int chunkX, int chunkY, Graphics g) {
        Chunk chunk = this.chunkManager.getChunk(chunkX, chunkY);

        for (int y = 0; y < chunk.getChunkSize(); y++) {
            for (int x = 0; x < chunk.getChunkSize(); x++) {
                float isoX = calculateIsoX(x, y, chunkX, chunkY);
                float isoY = calculateIsoY(x, y, chunkX, chunkY);

                Image tile = this.getTile(chunk.getTileType(x, y));
                drawTile(tile, isoX, isoY);
            }
        }
        // Debug
        float isoX = calculateIsoX(chunk.getChunkSize() / 2, chunk.getChunkSize() / 2, chunkX, chunkY);
        float isoY = calculateIsoY(chunk.getChunkSize() / 2, chunk.getChunkSize() / 2, chunkX, chunkY);
        g.drawString("(" + chunkX + "," + chunkY + ")", isoX, isoY);
    }

    private float calculateIsoX(int x, int y, int chunkX, int chunkY) {
        int halfTileWidth = (TILE_SIZE / 2);
        int chunkSize = chunkManager.getChunk(chunkX, chunkY).getChunkSize();
        return (((x - y) * halfTileWidth + (chunkX - chunkY) * chunkSize * halfTileWidth) * zoom) + this.offsetX;
    }

    private float calculateIsoY(int x, int y, int chunkX, int chunkY) {
        int quarterTileHeight = (TILE_SIZE / 4);
        int chunkSize = chunkManager.getChunk(chunkX, chunkY).getChunkSize();
        return (((x + y) * quarterTileHeight + (chunkX + chunkY) * chunkSize * quarterTileHeight) * zoom) + this.offsetY;
    }

    private float[] calculateIso(int[] pos, int[] chunk){
        int halfTileWidth = (TILE_SIZE / 2);
        int quarterTileHeight = (TILE_SIZE / 4);
        int chunkSize = chunkManager.getChunk(chunk[0],chunk[1]).getChunkSize();
        return new float[] {(((pos[0] - pos[1]) * halfTileWidth + (chunk[0] - chunk[1]) * chunkSize * halfTileWidth) * zoom) + this.offsetX , (((pos[0] + pos[1]) * quarterTileHeight + (chunk[0] + chunk[1]) * chunkSize * quarterTileHeight) * zoom) + this.offsetY};
    }

    private float[] calculateIso(int[] abspos){
        int halfTileWidth = (TILE_SIZE / 2);
        int quarterTileHeight = (TILE_SIZE / 4);
        int chunkSize = chunkManager.getChunk(abspos[2],abspos[3]).getChunkSize();
        return new float[] {(((abspos[0] - abspos[1]) * halfTileWidth + (abspos[2] - abspos[3]) * chunkSize * halfTileWidth) * zoom) + this.offsetX , (((abspos[0] + abspos[1]) * quarterTileHeight + (abspos[2] + abspos[3]) * chunkSize * quarterTileHeight) * zoom) + this.offsetY};
    }

    private Image getTile(int tileType) {
        return this.tileSheet.getSprite(tileType % this.tileSheet.getHorizontalCount(), tileType / this.tileSheet.getHorizontalCount());
    }

    private void drawTile(Image tile, float isoX, float isoY) {
        tile.draw(isoX, isoY, TILE_SIZE * this.zoom, TILE_SIZE * this.zoom);
    }

    public void drawTile(int tileType, int[] pos, int[] chunk){
        float[] coords = calculateIso(pos, chunk);
        this.getTile(tileType).draw(coords[0], coords[1], TILE_SIZE * this.zoom, TILE_SIZE * this.zoom);
    }

    public void drawImageAtCoord(Image i, int x, int y){
        int[] location = chunkManager.getBlockAndChunk(x, y);
        float[] xy = calculateIso(location);
        i.draw(xy[0], xy[1], this.zoom);
    }

    public void drawImageAtPosition(Image i, int screenX, int screenY){
        i.draw(screenX, screenY, this.zoom);
    }

    //Getters

    public static int getTileSize(){
        return TILE_SIZE;
    }

    //Setters

    public void setBgColor(Color c){
        this.bgColor=c;
    }
}
