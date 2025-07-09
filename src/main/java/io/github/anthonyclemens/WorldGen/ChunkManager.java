package io.github.anthonyclemens.WorldGen;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.newdawn.slick.util.Log;

import io.github.anthonyclemens.GameObjects.GameObject;
import io.github.anthonyclemens.Rendering.IsoRenderer;

/**
 * Manages world chunks, their generation, and biome assignment.
 * Handles chunk caching and provides utilities for chunk/block lookup.
 */
public class ChunkManager implements Serializable {
    private static final int CHUNK_SIZE = 16;
    private final Map<String, Chunk> chunks = new ConcurrentHashMap<>();
    private final int seed;
    private transient IsoRenderer isoRenderer;

    /**
     * Constructs a ChunkManager with the given world seed.
     * @param seed The world seed.
     */
    public ChunkManager(int seed) {
        this.seed = seed;
        Log.debug("ChunkManager initialized for infinite world generation with seed: " + seed);
    }

    /**
     * Attaches an IsoRenderer for rendering purposes.
     * @param isoRenderer The renderer to attach.
     */
    public void attachRenderer(IsoRenderer isoRenderer) {
        this.isoRenderer = isoRenderer;
    }

    /**
     * Gets the attached IsoRenderer.
     * @return The IsoRenderer instance.
     */
    public IsoRenderer getIsoRenderer() {
        return this.isoRenderer;
    }

    /**
     * Gets the biome for a given chunk coordinate.
     * @param chunkX Chunk X coordinate.
     * @param chunkY Chunk Y coordinate.
     * @return The Biome for the chunk.
     */
    public Biome getBiomeForChunk(int chunkX, int chunkY) {
        double noiseValue = generateCombinedNoise(chunkX * 0.03, chunkY * 0.03, 10, 0.5);
        return Biome.getBiomeFromNoise(noiseValue);
    }

    /**
     * Generates a combined Perlin noise value for biome assignment.
     * @param x          X coordinate (scaled).
     * @param y          Y coordinate (scaled).
     * @param octaves    Number of octaves.
     * @param persistence Persistence factor.
     * @return Normalized noise value.
     */
    public double generateCombinedNoise(double x, double y, int octaves, double persistence) {
        double amplitude = 1.0;
        double frequency = 1.0;
        double noiseSum = 0.0;
        double maxAmplitude = 0.0000000000001; // For normalization

        PerlinNoise noiseGenerator = new PerlinNoise(seed);

        for (int i = 0; i < octaves; i++) {
            noiseSum += noiseGenerator.generate(x * frequency, y * frequency) * amplitude;
            maxAmplitude += amplitude;
            amplitude *= persistence;
            frequency *= 2.0;
        }

        return noiseSum / maxAmplitude; // Normalize the final noise value
    }

    /**
     * Gets or generates a chunk at the specified coordinates.
     * @param chunkX Chunk X coordinate.
     * @param chunkY Chunk Y coordinate.
     * @return The Chunk instance.
     */
    public Chunk getChunk(int chunkX, int chunkY) {
        String key = chunkX + "," + chunkY;
        // Check if the chunk already exists in the map
        return chunks.computeIfAbsent(key, k -> {
            Biome biome = getBiomeForChunk(chunkX, chunkY);
            return new Chunk(CHUNK_SIZE, biome, this, chunkX, chunkY, this.seed + chunks.size());
        });
    }

    /**
     * Converts absolute block coordinates to chunk and block indices.
     * @param absX Absolute X coordinate.
     * @param absY Absolute Y coordinate.
     * @return Array: [blockX, blockY, chunkX, chunkY]
     */
    public int[] getBlockAndChunk(int absX, int absY) {
        int chunkX = (absX < 0) ? (absX + 1) / CHUNK_SIZE - 1 : absX / CHUNK_SIZE;
        int chunkY = (absY < 0) ? (absY + 1) / CHUNK_SIZE - 1 : absY / CHUNK_SIZE;

        int tileX = (absX % CHUNK_SIZE + CHUNK_SIZE) % CHUNK_SIZE;
        int tileY = (absY % CHUNK_SIZE + CHUNK_SIZE) % CHUNK_SIZE;

        return new int[]{tileX, tileY, chunkX, chunkY};
    }

    /**
     * Adds a GameObject to the appropriate chunk.
     * @param obj The GameObject to add.
     */
    public void addGameObject(GameObject obj) {
        this.getChunk(obj.getCX(), obj.getCY()).addGameObject(obj);
    }

    /**
     * Adds multiple GameObjects to a specific chunk.
     * @param gobs   List of GameObjects.
     * @param chunkX Chunk X coordinate.
     * @param chunkY Chunk Y coordinate.
     */
    public void addGameObjects(List<GameObject> gobs, int chunkX, int chunkY) {
        this.getChunk(chunkX, chunkY).addGameObjects(gobs);
    }

    /**
     * Removes a GameObject by index from a specific chunk.
     * @param idx    Index of the GameObject.
     * @param chunkX Chunk X coordinate.
     * @param chunkY Chunk Y coordinate.
     */
    public void removeGameObject(int idx, int chunkX, int chunkY) {
        this.getChunk(chunkX, chunkY).removeGameObject(idx);
    }

    public int getSeed() {
        return seed;
    }
}
