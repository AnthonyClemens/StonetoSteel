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
    private static final int TILE_SIZE = 18; // Size of the tile in pixels
    private float zoom; // Zoom level for rendering
    private SpriteSheet tileSheet; // SpriteSheet containing the tile images
    private int offsetX;
    private int offsetY;
    private final ChunkManager chunkManager; // Reference to the chunk manager
    private int[] visibleChunks; // Array to store the visible chunks
    private boolean firstFrame = true; // Flag to check if it's the first frame
    private int lastTileType = 0; // Last tile type used for rendering
    private final GameContainer container; // Reference to the game container

    public IsoRenderer(float zoom, SpriteSheet tileSheet, ChunkManager chunkManager, GameContainer container){
        this.zoom = zoom;
        this.tileSheet = tileSheet;
        this.chunkManager = chunkManager;
        this.container = container;
    }

    private int getLODLevel() {
        if (this.zoom > 1.2f) return 0; // High zoom: Full detail (1x1)
        if (this.zoom <= 1.2f && this.zoom > 0.6f) return 1; // Medium zoom: Quarter Chunks
        return 2; // Low zoom: Half Chunks
    }

    public void update(GameContainer container, float zoom, float cameraX, float cameraY) {
        if (this.zoom != zoom || this.offsetX != (container.getWidth() / 2) - (int) (cameraX * zoom) || this.offsetY != (container.getHeight() / 2) - (int) (cameraY * zoom) || firstFrame) {
            this.zoom = zoom;
            this.offsetX = (container.getWidth() / 2) - (int) (cameraX * zoom);
            this.offsetY = (container.getHeight() / 2) - (int) (cameraY * zoom);
            this.visibleChunks = getVisibleChunkRange(container);
            firstFrame=false;
        }

    }

    public void render(Player player){
        if(this.visibleChunks==null) return; // Skip the rendering process if the visible chunks are not set
        for (int chunkX = this.visibleChunks[0]; chunkX <= this.visibleChunks[2]; chunkX++) {
            for (int chunkY = this.visibleChunks[1]; chunkY <= this.visibleChunks[3]; chunkY++) {
                renderChunk(chunkX, chunkY); // Render each chunk in the visible range
            }
        }
        float renderRadius = 800*zoom; // Define the radius for rendering in pixels
        float renderRadiusSquared = renderRadius * renderRadius;

        for (int chunkX = this.visibleChunks[0]; chunkX <= this.visibleChunks[2]; chunkX++) {
            for (int chunkY = this.visibleChunks[1]; chunkY <= this.visibleChunks[3]; chunkY++) {
            float chunkCenterX = calculateIsoX(chunkManager.getChunk(chunkX, chunkY).getChunkSize() / 2, 
                               chunkManager.getChunk(chunkX, chunkY).getChunkSize() / 2, 
                               chunkX, chunkY);
            float chunkCenterY = calculateIsoY(chunkManager.getChunk(chunkX, chunkY).getChunkSize() / 2, 
                               chunkManager.getChunk(chunkX, chunkY).getChunkSize() / 2, 
                               chunkX, chunkY);

            float dx = chunkCenterX - player.getRenderX();
            float dy = chunkCenterY - player.getRenderY();

            if ((dx * dx + dy * dy) <= renderRadiusSquared) {
                this.chunkManager.getChunk(chunkX, chunkY).render(this); // Render the chunk objects
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
        Chunk chunk = this.chunkManager.getChunk(chunkX, chunkY);
        Image block = this.getTile(lastTileType); // Placeholder for the block image
        if(chunk==null) return; // Skip the rendering process if the chunk is not generated yet
        int lodSize = chunk.getChunkSize() / blockSize;
        this.tileSheet.startUse();
        for (int blockY = 0; blockY < lodSize; blockY++) {
            for (int blockX = 0; blockX < lodSize; blockX++) {
                // Calculate isometric position for the block
                float isoX = calculateIsoX(blockX * blockSize, blockY * blockSize, chunkX, chunkY);
                float isoY = calculateIsoY(blockX * blockSize, blockY * blockSize, chunkX, chunkY);

                // Get the aggregated tile or individual tile
                int tileType = blockSize == 1
                    ? chunk.getLODTile(0,blockX, blockY) // Full detail for LOD 0
                    : chunk.getLODTile(blockSize == 2 ? 1 : 2, blockX, blockY);
                // Get the tile image and draw it
                if (tileType != lastTileType) {
                    lastTileType = tileType;
                    block = this.getTile(tileType);
                }
                drawScaledIsoImage(block, isoX, isoY, TILE_SIZE * zoom * blockSize, TILE_SIZE * zoom * blockSize);
            }
        }
        this.tileSheet.endUse();
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
        int chunkSize = chunkManager.getChunk(chunkX, chunkY).getChunkSize();
        return (((x - y) * halfTileWidth + (chunkX - chunkY) * chunkSize * halfTileWidth) * zoom) + this.offsetX;
    }

    public float calculateIsoY(int x, int y, int chunkX, int chunkY) {
        int quarterTileHeight = (TILE_SIZE / 4);
        int chunkSize = chunkManager.getChunk(chunkX, chunkY).getChunkSize();
        return (((x + y) * quarterTileHeight + (chunkX + chunkY) * chunkSize * quarterTileHeight) * zoom) + this.offsetY;
    }

    private Image getTile(int tileType) {
        return this.tileSheet.getSprite(tileType % this.tileSheet.getHorizontalCount(), tileType / this.tileSheet.getHorizontalCount());
    }

    private void drawScaledIsoImage(Image tile, float isoX, float isoY, float width, float height) {
        tile.drawEmbedded(isoX, isoY, width, height);
    }

    public void drawTileBatch(int i, int xPos, int yPos, int chunkX, int chunkY){
        this.getTile(i).drawEmbedded(calculateIsoX(xPos, yPos, chunkX, chunkY), calculateIsoY(xPos, yPos, chunkX, chunkY), getTileSize()*zoom, getTileSize()*zoom);
    }

    public void drawScaledTile(int tileType, int xPos, int yPos, int chunkX, int chunkY, float width, float height){
        this.getTile(tileType).draw(calculateIsoX(xPos, yPos, chunkX, chunkY), calculateIsoY(xPos, yPos, chunkX, chunkY), width, height);
    }

    public void drawTile(int tileType, int xPos, int yPos, int chunkX, int chunkY){
        this.drawScaledTile(tileType, xPos, yPos, chunkX, chunkY, TILE_SIZE * zoom, TILE_SIZE * zoom);
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

    public static int getTileSize(){
        return TILE_SIZE;
    }

    public SpriteSheet getTileSheet(){
        return this.tileSheet;
    }

    public int[] getTileCoords(int tileType){
        return new int[]{tileType % this.tileSheet.getHorizontalCount(), tileType / this.tileSheet.getHorizontalCount()};
    }

    public Graphics getGraphics(){
        return this.container.getGraphics();
    }

    public float getZoom(){
        return this.zoom;
    }

    //Setters

    public void changeTileSheet(SpriteSheet ts){
        this.tileSheet = ts;
    }


    public ChunkManager getChunkManager(){
        return this.chunkManager;
    }

}
