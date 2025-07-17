package io.github.anthonyclemens.WorldGen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.github.anthonyclemens.GameObjects.GameObject;
import io.github.anthonyclemens.Rendering.IsoRenderer;

/**
 * Represents a chunk of the world, containing tiles and game objects.
 * Handles tile generation, LOD, and biome blending.
 */
public class Chunk implements Serializable {
    private final int chunkSize;
    private final List<GameObject> gameObjects;
    private final Random rand;
    private final Biome biome;
    private final int chunkX;
    private final int chunkY;
    private transient ChunkManager chunkManager;
    private final byte[][] tiles;
    private transient byte[][] lod1Tiles;
    private transient byte[][] lod2Tiles; // Add LOD2 storage

    /**
     * Constructs a Chunk with the specified parameters.
     */
    public Chunk(int chunkSize, Biome biome, ChunkManager chunkManager, int chunkX, int chunkY, int seed){
        this.rand = new Random(seed);
        this.chunkSize = chunkSize;
        this.tiles = new byte[chunkSize][chunkSize];
        this.gameObjects = new ArrayList<>();
        this.biome = biome;
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.chunkManager = chunkManager;
        this.generateTiles();
        this.generateLODs();
        this.generateGameObjects();
    }

    /**
     * Generates the tile map for this chunk, blending with neighboring biomes.
     */
    private void generateTiles() {
        // Fetch neighboring biomes for blending
        Biome northBiome = getNeighborBiome(0, -1); // Top
        Biome southBiome = getNeighborBiome(0, 1);  // Bottom
        Biome westBiome = getNeighborBiome(-1, 0);  // Left
        Biome eastBiome = getNeighborBiome(1, 0);   // Right

        for (int x = 0; x < this.chunkSize; x++) {
            for (int y = 0; y < this.chunkSize; y++) {
                // Determine which neighbor biome to blend with based on the tile's position
                Biome blendingBiome = determineBlendingBiome(x, y, northBiome, southBiome, westBiome, eastBiome);

                if (blendingBiome != null) {
                    // Blend with the neighboring biome
                    this.tiles[x][y] = (byte) generateTileForBiomeWithBlending(this.biome, blendingBiome);
                } else {
                    // No blending needed, generate tile for the main biome
                    this.tiles[x][y] = (byte) generateTileForBiome(this.biome);
                }
            }
        }
    }

    /**
     * Blends tile generation between the main biome and a neighbor biome.
     */
    private int generateTileForBiomeWithBlending(Biome mainBiome, Biome neighborBiome) {
        double probability = 0.5; // 50% chance to blend
        if (rand.nextDouble() < probability) {
            return generateTileForBiome(neighborBiome);
        }
        return generateTileForBiome(mainBiome);
    }

    /**
     * Determines which neighbor biome to blend with for a given tile.
     */
    private Biome determineBlendingBiome(int x, int y, Biome northBiome, Biome southBiome, Biome westBiome, Biome eastBiome) {
        if (y < 2 && northBiome != this.biome) return northBiome;
        if (y >= chunkSize - 2 && southBiome != this.biome) return southBiome;
        if (x < 2 && westBiome != this.biome) return westBiome;
        if (x >= chunkSize - 2 && eastBiome != this.biome) return eastBiome;
        return null;
    }

    /**
     * Gets the biome of a neighboring chunk.
     */
    public Biome getNeighborBiome(int offsetX, int offsetY) {
        int neighborChunkX = this.chunkX + offsetX;
        int neighborChunkY = this.chunkY + offsetY;
        return chunkManager.getBiomeForChunk(neighborChunkX, neighborChunkY);
    }

    /**
     * Generates a tile index for a given biome.
     */
    private int generateTileForBiome(Biome biome) {
        return switch (biome) {
            case DESERT -> rand.nextInt(2) + 4; // Sand
            case BEACH -> rand.nextInt(2) + 4;  // Sand
            case PLAINS -> rand.nextBoolean() ? rand.nextInt(4) : rand.nextInt(4) + 10; // Grass
            case WATER -> rand.nextInt(2) + 23; // Water
            case MOUNTAIN -> rand.nextInt(8) + 50; // Rocks
            case SWAMP -> rand.nextInt(2) + 6;  // Dirt, mud
            default -> 0;
        };
    }

    /**
     * Generates Level of Detail (LOD) tiles for rendering optimization.
     */
    private void generateLODs() {
        int lod1Size = chunkSize / 2;
        this.lod1Tiles = new byte[lod1Size][lod1Size];
        for (int x = 0; x < lod1Size; x++) {
            for (int y = 0; y < lod1Size; y++) {
                int aggregatedValue = aggregateRegion(x * 2, y * 2, 2, 2);
                if (aggregatedValue < 0 || aggregatedValue > 255) {
                    aggregatedValue = 0;
                }
                this.lod1Tiles[x][y] = (byte) aggregatedValue;
            }
        }
        int lod2Size = chunkSize / 8;
        this.lod2Tiles = new byte[lod2Size][lod2Size];
        for (int x = 0; x < lod2Size; x++) {
            for (int y = 0; y < lod2Size; y++) {
                int aggregatedValue = aggregateRegion(x * 8, y * 8, 8, 8);
                if (aggregatedValue < 0 || aggregatedValue > 255) {
                    aggregatedValue = 0;
                }
                this.lod2Tiles[x][y] = (byte) aggregatedValue;
            }
        }
    }

    /**
     * Gets the biome of this chunk.
     */
    public Biome getBiome() {
        return biome;
    }

    /**
     * Aggregates a region of tiles to determine the most frequent value (for LOD).
     */
    private int aggregateRegion(int startX, int startY, int width, int height) {
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
                int value = tiles[x][y];
                frequencyMap.put(value, frequencyMap.getOrDefault(value, 0) + 1);
            }
        }

        int mostFrequentValue = -1;
        int maxFrequency = -1;
        for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() > maxFrequency) {
                mostFrequentValue = entry.getKey();
                maxFrequency = entry.getValue();
            }
        }
        return mostFrequentValue;
    }

    /**
     * Gets a tile value at a given LOD level.
     * @param lodLevel 0 for full detail, 1 for LOD1, 2 for LOD2.
     * @param x        X coordinate.
     * @param y        Y coordinate.
     * @return Tile value.
     */
    public int getLODTile(int lodLevel, int x, int y) {
        if (lodLevel == 1) return getLod1Tiles()[x][y] & 0xFF;
        if (lodLevel == 2) return getLod2Tiles()[x][y] & 0xFF;
        return tiles[x][y] & 0xFF;
    }

    /**
     * Adds a GameObject to this chunk.
     */
    public void addGameObject(GameObject obj) {
        gameObjects.add(obj);
    }

    /**
     * Adds multiple GameObjects to this chunk.
     */
    public void addGameObjects(List<GameObject> gobs) {
        gameObjects.addAll(gobs);
        gameObjects.sort((o1, o2) -> o1.getTileSheetName().compareTo(o2.getTileSheetName()));
    }

    /**
     * Removes a GameObject by index.
     */
    public void removeGameObject(int idx){
        gameObjects.remove(idx);
    }

    /**
     * Gets the list of GameObjects in this chunk.
     */
    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    /**
     * Renders all GameObjects in this chunk.
     */
    public void render(IsoRenderer r, int lodLevel) {
        for (GameObject obj : gameObjects) {
            obj.render(r, lodLevel);
        }
    }

    /**
     * Updates all GameObjects in this chunk.
     */
    public void update(IsoRenderer r, int deltaTime) {
        for (GameObject obj : gameObjects) {
            obj.update(r, deltaTime);
        }
    }

    /**
     * Gets the size of this chunk.
     */
    public int getChunkSize(){
        return this.chunkSize;
    }

    /**
     * Generates and adds biome-appropriate GameObjects to this chunk.
     */
    private void generateGameObjects() {
        this.addGameObjects(GameObjectGenerator.generateObjectsForBiome(this.biome, this.rand, this.chunkX, this.chunkY, this.chunkSize));
    }

    /**
     * Gets the tile value at the specified coordinates.
     */
    public int getTile(int x, int y) {
        return tiles[x][y] & 0xFF;
    }

    public byte[][] getTiles(){
        return this.tiles;
    }

    private byte[][] getLod1Tiles() {
        if (lod1Tiles == null) {
            generateLODs();
        }
        return lod1Tiles;
    }

    public byte[][] getLOD1() {
        return lod1Tiles;
    }

    private byte[][] getLod2Tiles() {
        if (lod2Tiles == null) {
            generateLODs();
        }
        return lod2Tiles;
    }

    public byte[][] getLOD2() {
        return lod2Tiles;
    }

    public int getChunkX(){
        return chunkX;
    }

    public int getChunkY(){
        return chunkY;
    }

    public void attachChunkManager(ChunkManager cm){
        chunkManager=cm;
    }

    public ChunkManager getChunkManager() {
        return chunkManager;
    }
}
