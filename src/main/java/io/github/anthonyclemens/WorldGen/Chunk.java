package io.github.anthonyclemens.WorldGen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.github.anthonyclemens.GameObjects.GameObject;
import io.github.anthonyclemens.Rendering.IsoRenderer;

public class Chunk implements Serializable {
    private final int chunkSize;
    private final int[][] tiles;
    private final List<GameObject> gameObjects;
    private final Random rand;
    private int[][] lod1Tiles;
    private final Biome biome;
    private final int chunkX;
    private final int chunkY;
    private final ChunkManager chunkManager;


    public Chunk(int chunkSize, Biome biome, ChunkManager chunkManager, int chunkX, int chunkY, int seed){
        this.rand = new Random(seed);
        this.chunkSize = chunkSize;
        this.tiles = new int[chunkSize][chunkSize];
        this.gameObjects = new ArrayList<>();
        this.biome = biome;
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.chunkManager = chunkManager;
        this.generateTiles();
        this.generateLODs();
        this.generateGameObjects();
    }

    private void generateTiles() {
        //Fetch neighboring biomes for blending
        Biome northBiome = getNeighborBiome(0, -1); // Top
        Biome southBiome = getNeighborBiome(0, 1);  // Bottom
        Biome westBiome = getNeighborBiome(-1, 0);  // Left
        Biome eastBiome = getNeighborBiome(1, 0);  // Right

        for (int x = 0; x < this.chunkSize; x++) {
            for (int y = 0; y < this.chunkSize; y++) {
                //Determine which neighbor biome to blend with based on the tile's position
                Biome blendingBiome = determineBlendingBiome(x, y, northBiome, southBiome, westBiome, eastBiome);

                if (blendingBiome != null) {
                    //Blend with the neighboring biome
                    this.tiles[x][y] = generateTileForBiomeWithBlending(this.biome, blendingBiome);
                } else {
                    //No blending needed, generate tile for the main biome
                    this.tiles[x][y] = generateTileForBiome(this.biome);
                }
            }
        }
    }

    private int generateTileForBiomeWithBlending(Biome mainBiome, Biome neighborBiome) {
        double probability = 0.5; //% chance to blend
        if (rand.nextDouble() < probability) {
            return generateTileForBiome(neighborBiome);
        }
        return generateTileForBiome(mainBiome);
    }

    private Biome determineBlendingBiome(int x, int y, Biome northBiome, Biome southBiome, Biome westBiome, Biome eastBiome) {
        //Blend with the top edge if the tile is near the top
        if (y < 2 && northBiome != this.biome) {
            return northBiome;
        }

        //Blend with the bottom edge if the tile is near the bottom
        if (y >= chunkSize - 2 && southBiome != this.biome) {
            return southBiome;
        }

        //Blend with the left edge if the tile is near the left
        if (x < 2 && westBiome != this.biome) {
            return westBiome;
        }

        //Blend with the right edge if the tile is near the right
        if (x >= chunkSize - 2 && eastBiome != this.biome) {
            return eastBiome;
        }

        //No blending required
        return null;
    }

    public Biome getNeighborBiome(int offsetX, int offsetY) {
        int neighborChunkX = this.chunkX + offsetX;
        int neighborChunkY = this.chunkY + offsetY;

        //Query the ChunkManager for the neighbor's biome
        return chunkManager.getBiomeForChunk(neighborChunkX, neighborChunkY);
    }

    private int generateTileForBiome(Biome biome) {
        return switch (biome) {
            case DESERT ->rand.nextInt(2)+4;// Sand
            case BEACH ->rand.nextInt(2)+4;// Sand
            case PLAINS -> rand.nextBoolean() ? rand.nextInt(4) : rand.nextInt(4) + 10; // Grass
            case WATER ->rand.nextInt(2)+23;// Water
            case MOUNTAIN ->rand.nextInt(8)+50;// Rocks
            case SWAMP ->rand.nextInt(2)+6;// Dirt, mud
            default -> 0;
        };
    }

    private void generateLODs() {
        // LOD 1
        int lod1Size = chunkSize / 2;
        this.lod1Tiles = new int[lod1Size][lod1Size];

        for (int x = 0; x < lod1Size; x++) {
            for (int y = 0; y < lod1Size; y++) {
                this.lod1Tiles[x][y] = aggregateRegion(x * 2, y * 2, 2, 2);
            }
        }
    }

    public Biome getBiome() {
        return biome;
    }

    //Returns the most frequent value in the region defined
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


    public int getLODTile(int lodLevel, int x, int y) {
        if (lodLevel == 1) return lod1Tiles[x][y];
        return tiles[x][y]; // Full detail
    }

    public void addGameObject(GameObject obj) {
        gameObjects.add(obj);
    }

    public void addGameObjects(List<GameObject> gobs) {
        gameObjects.addAll(gobs);
        gameObjects.sort((o1, o2) -> o1.getTileSheetName().compareTo(o2.getTileSheetName()));
    }

    public void removeGameObject(int idx){
        gameObjects.remove(idx);
    }

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    public void render(IsoRenderer r) {
        for (GameObject obj : gameObjects) {
            obj.render(r);
        }
    }

    public void update(IsoRenderer r, int deltaTime) {
        for (GameObject obj : gameObjects) {
            obj.update(r, deltaTime);
        }
    }

    public int getChunkSize(){
        return this.chunkSize;
    }

    private void generateGameObjects() {
        this.addGameObjects(GameObjectGenerator.generateObjectsForBiome(this.biome, this.rand, this.chunkX, this.chunkY, this.chunkSize));
    }

    public int getTile(int x, int y) {
        return tiles[x][y];
    }

}
