package io.github.anthonyclemens.Rendering;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

import io.github.anthonyclemens.Player.Player;
import io.github.anthonyclemens.WorldGen.Chunk;
import io.github.anthonyclemens.WorldGen.ChunkManager;

/**
 * The IsoRenderer class is responsible for rendering isometric tiles and chunks
 * in a game environment. It handles zoom levels, visible chunk calculations,
 * and rendering logic for different levels of detail (LOD).
 */
public class IsoRenderer {
    private static int renderDistance = 64; // Render distance in blocks
    private static final int TILE_SIZE = 18; // Size of the tile in pixels
    private float zoom; // Zoom level for rendering
    private SpriteSheet worldTileSheet;
    private int offsetX;
    private int offsetY;
    private final ChunkManager chunkManager; // Reference to the chunk manager
    private int[] visibleChunks; // Array to store the visible chunks
    private boolean firstFrame = true; // Flag to check if it's the first frame
    private int lastTileType = 0; // Last tile type used for rendering
    private final GameContainer container; // Reference to the game container
    private boolean cameraMoving = false;
    private final int chunkSize;

    public IsoRenderer(float zoom, String worldTileSheet, ChunkManager chunkManager, GameContainer container){
        this.zoom = zoom;
        this.worldTileSheet = SpriteManager.getSpriteSheet(worldTileSheet);
        this.chunkManager = chunkManager;
        this.container = container;
        this.chunkSize = ChunkManager.CHUNK_SIZE;
    }

    private int getLODLevel() {
        if (this.zoom > 1f) return 0; // High zoom: Full detail (1x1)
        if (this.zoom > 0.7f) return 1; // Medium zoom: Quarter Chunks
        return 2; // Low zoom: 8x8 blocks
    }

    public void update(GameContainer container, float zoom, float cameraX, float cameraY) {
        int newOffsetX = (container.getWidth() / 2) - (int) (cameraX * zoom);
        int newOffsetY = (container.getHeight() / 2) - (int) (cameraY * zoom);

        cameraMoving = (newOffsetX != offsetX || newOffsetY != offsetY || this.zoom != zoom);

        this.zoom = zoom;
        this.offsetX = newOffsetX;
        this.offsetY = newOffsetY;

        if (cameraMoving || firstFrame) {
            this.visibleChunks = getVisibleChunkRange(container);
            firstFrame = false;
        }
    }



    public void render(){
        if(this.visibleChunks==null) return;
        int lodLevel = getLODLevel();
        for (int chunkX = this.visibleChunks[0]; chunkX <= this.visibleChunks[2]; chunkX++) {
            for (int chunkY = this.visibleChunks[1]; chunkY <= this.visibleChunks[3]; chunkY++) {
                renderChunk(chunkX, chunkY);
            }
        }
        for (int chunkX = this.visibleChunks[0]; chunkX <= this.visibleChunks[2]; chunkX++) {
            for (int chunkY = this.visibleChunks[1]; chunkY <= this.visibleChunks[3]; chunkY++) {
                this.chunkManager.getChunk(chunkX, chunkY).render(this, lodLevel); // Pass LOD level
            }
        }
    }

    public void updateVisibleChunks(int deltaTime, Player player) {
        if (this.visibleChunks == null) return;

        float updateRadiusSquared = (renderDistance * zoom * TILE_SIZE) * (renderDistance * zoom * TILE_SIZE);

        for (int chunkX = this.visibleChunks[0]; chunkX <= this.visibleChunks[2]; chunkX++) {
            for (int chunkY = this.visibleChunks[1]; chunkY <= this.visibleChunks[3]; chunkY++) {
                Chunk chunk = chunkManager.getChunk(chunkX, chunkY);
                if (chunk == null) continue;

                chunk.update(this, deltaTime);

                // Calculate chunk center only once
                float centerX = calculateIsoX(ChunkManager.CHUNK_SIZE / 2, ChunkManager.CHUNK_SIZE / 2, chunkX, chunkY);
                float centerY = calculateIsoY(ChunkManager.CHUNK_SIZE / 2, ChunkManager.CHUNK_SIZE / 2, chunkX, chunkY);

                float dx = centerX - player.getRenderX();
                float dy = centerY - player.getRenderY();

                if ((dx * dx + dy * dy) <= updateRadiusSquared) {
                    chunk.update(this, deltaTime);
                }
            }
        }
    }

    private void renderChunk(int chunkX, int chunkY) {
        int blockSize = switch (getLODLevel()) {
            case 0 -> 1; // LOD 0
            case 1 -> 2; // LOD 1
            case 2 -> 8; // LOD 2
            default -> 1;
        };
        renderChunkWithBlockSize(chunkX, chunkY, blockSize);
    }

    private void renderChunkWithBlockSize(int chunkX, int chunkY, int blockSize) {
        Chunk chunk = chunkManager.getChunk(chunkX, chunkY);
        if (chunk == null) return;

        int lodLevel = switch (blockSize) {
            case 1 -> 0;
            case 2 -> 1;
            default -> 2;
        };

        int lodSize = chunk.getChunkSize() / blockSize;
        float tileRenderSize = TILE_SIZE * zoom * blockSize;
        int spriteCols = worldTileSheet.getHorizontalCount();

        worldTileSheet.startUse();

        for (int blockY = 0; blockY < lodSize; blockY++) {
            for (int blockX = 0; blockX < lodSize; blockX++) {
                int tileType = chunk.getLODTile(lodLevel, blockX, blockY);

                // Only update sprite if tileType has changed
                if (tileType != lastTileType) {
                    lastTileType = tileType;
                }

                Image block = worldTileSheet.getSprite(lastTileType % spriteCols, lastTileType / spriteCols);

                float isoX = calculateIsoX(blockX * blockSize, blockY * blockSize, chunkX, chunkY);
                float isoY = calculateIsoY(blockX * blockSize, blockY * blockSize, chunkX, chunkY);
                if(tileType == 23 || tileType == 24){
                    drawScaledIsoAlpha(block, isoX, isoY, tileRenderSize, tileRenderSize, 0.1f);
                }else{
                    drawScaledIsoImage(block, isoX, isoY, tileRenderSize, tileRenderSize);
                }         
            }
        }

        worldTileSheet.endUse();
    }

    public int[] screenToIsometric(float screenX, float screenY) {
        int halfTileWidth = TILE_SIZE / 2;
        int quarterTileHeight = TILE_SIZE / 4;

        //Remove offset and normalize by zoom
        screenX = (screenX - this.offsetX) / zoom;
        screenY = (screenY - this.offsetY) / zoom;

        //Reverse the isometric transformation
        float isoX = (screenX / halfTileWidth + screenY / quarterTileHeight) / 2;
        float isoY = (screenY / quarterTileHeight - screenX / halfTileWidth) / 2;

        //Adjust for horizontal alignment (offset horizontally by half a tile width)
        isoX -= 0.5f;

        //Convert to integer tile coordinates
        int tileX = Math.round(isoX);
        int tileY = Math.round(isoY);

        return chunkManager.getBlockAndChunk(tileX, tileY);
    }

    private int[] getVisibleChunkRange(GameContainer c) {
        int[] topLeft = screenToIsometric(0, 0);
        int[] topRight = screenToIsometric(c.getWidth() - 1f, 0);
        int[] bottomLeft = screenToIsometric(0, c.getHeight() - 1f);
        int[] bottomRight = screenToIsometric(c.getWidth() - 1f, c.getHeight() - 1f);

        int minChunkX = Math.min(Math.min(topLeft[2], topRight[2]), Math.min(bottomLeft[2], bottomRight[2]));
        int minChunkY = Math.min(Math.min(topLeft[3], topRight[3]), Math.min(bottomLeft[3], bottomRight[3]));
        int maxChunkX = Math.max(Math.max(topLeft[2], topRight[2]), Math.max(bottomLeft[2], bottomRight[2]));
        int maxChunkY = Math.max(Math.max(topLeft[3], topRight[3]), Math.max(bottomLeft[3], bottomRight[3]));

        return new int[]{minChunkX, minChunkY, maxChunkX, maxChunkY};
    }

    public float calculateIsoX(int x, int y, int chunkX, int chunkY) {
        int halfTileWidth = (TILE_SIZE / 2);
        return (((x - y) * halfTileWidth + (chunkX - chunkY) * chunkSize * halfTileWidth) * zoom) + this.offsetX;
    }

    public float calculateIsoY(int x, int y, int chunkX, int chunkY) {
        int quarterTileHeight = (TILE_SIZE / 4);
        return (((x + y) * quarterTileHeight + (chunkX + chunkY) * chunkSize * quarterTileHeight) * zoom) + this.offsetY;
    }

    private Image getTile(String tileSheet, int tileType) {
        SpriteSheet sheet = SpriteManager.getSpriteSheet(tileSheet);
        return sheet.getSprite(tileType % sheet.getHorizontalCount(), tileType / sheet.getHorizontalCount());
    }

    private void drawScaledIsoImage(Image tile, float isoX, float isoY, float width, float height) {
        tile.drawEmbedded(isoX, isoY, width, height);
    }

    private void drawScaledIsoAlpha(Image tile, float isoX, float isoY, float width, float height, float alpha) {
        tile.setAlpha(alpha);
        tile.drawEmbedded(isoX, isoY, width, height);
        tile.setAlpha(1f);
    }

    public void drawScaledTile(String tileSheet, int tileType, int xPos, int yPos, int chunkX, int chunkY){
        this.getTile(tileSheet, tileType).draw(calculateIsoX(xPos, yPos, chunkX, chunkY), calculateIsoY(xPos, yPos, chunkX, chunkY), SpriteManager.getSpriteWidth(tileSheet)*zoom, SpriteManager.getSpriteHeight(tileSheet)*zoom);
    }

    public void drawHeightedTile(String tileSheet, int tileType, int xPos, int yPos, int chunkX, int chunkY, int height){
        this.getTile(tileSheet, tileType).draw(calculateIsoX(xPos, yPos, chunkX, chunkY), calculateIsoY(xPos, yPos, chunkX, chunkY)-SpriteManager.getSpriteWidth(tileSheet)*zoom*height/2f, SpriteManager.getSpriteWidth(tileSheet)*zoom, SpriteManager.getSpriteHeight(tileSheet)*zoom);
    }

    public void drawImageAtCoord(Image i, int xPos, int yPos, int chunkX, int chunkY){
        i.draw(calculateIsoX(xPos, yPos, chunkX, chunkY), calculateIsoY(xPos, yPos, chunkX, chunkY), this.zoom);
    }

    public void drawRectangle(int xPos, int yPos, int chunkX, int chunkY, float width, float height) {
        this.getGraphics().drawRect(calculateIsoX(xPos, yPos, chunkX, chunkY), calculateIsoY(xPos, yPos, chunkX, chunkY), width * zoom, height * zoom);
    }

    public void drawImageAtCoordBatch(Image i, int xPos, int yPos, int chunkX, int chunkY){
        i.drawEmbedded(calculateIsoX(xPos, yPos, chunkX, chunkY), calculateIsoY(xPos, yPos, chunkX, chunkY), this.zoom*i.getWidth(), this.zoom*i.getHeight());
    }

    public void drawImageAtPosition(Image i, int screenX, int screenY){
        i.draw(screenX, screenY, this.zoom);
    }

    //Getters

    public boolean isCameraMoving() {
        return cameraMoving;
    }

    public Graphics getGraphics(){
        return this.container.getGraphics();
    }

    public float getZoom(){
        return this.zoom;
    }
    public static int getRenderDistance() {
        return renderDistance;
    }


    //Setters


    public ChunkManager getChunkManager(){
        return this.chunkManager;
    }

    public static void setRenderDistance(int nrd) {
        IsoRenderer.renderDistance = nrd;
    }

}
