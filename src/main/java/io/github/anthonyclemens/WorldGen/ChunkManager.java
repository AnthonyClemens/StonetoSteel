package io.github.anthonyclemens.WorldGen;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.newdawn.slick.util.Log;

import io.github.anthonyclemens.GameObjects.GameObject;
import io.github.anthonyclemens.Rendering.IsoRenderer;

public class ChunkManager implements Serializable{
    private static final int CHUNK_SIZE = 16;
    private final Map<String, Chunk> chunks = new ConcurrentHashMap<>();
    private final int seed;
    private transient IsoRenderer isoRenderer;

    public ChunkManager(int seed) {
        this.seed = seed;
        Log.info("ChunkManager initialized for infinite world generation with seed: "+seed);
    }

    public void attachRenderer(IsoRenderer isoRenderer) {
        this.isoRenderer = isoRenderer;
    }

    public IsoRenderer getIsoRenderer() {
        return this.isoRenderer;
    }

    public Biome getBiomeForChunk(int chunkX, int chunkY) {
        double noiseValue = generateCombinedNoise(chunkX * 0.03, chunkY * 0.03, 7, 0.5);
        return Biome.getBiomeFromNoise(noiseValue);
    }

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

    public Chunk getChunk(int chunkX, int chunkY) {
        String key = chunkX + "," + chunkY;

        // Check if the chunk already exists in the map
        return chunks.computeIfAbsent(key, k -> {
            Biome biome = getBiomeForChunk(chunkX, chunkY);
            return new Chunk(CHUNK_SIZE, biome, this, chunkX, chunkY, this.seed+chunks.size());
        });
    }

    public int[] getBlockAndChunk(int absX, int absY) {
        int chunkX = (absX < 0) ? (absX + 1) / CHUNK_SIZE - 1 : absX / CHUNK_SIZE;
        int chunkY = (absY < 0) ? (absY + 1) / CHUNK_SIZE - 1 : absY / CHUNK_SIZE;

        int tileX = (absX % CHUNK_SIZE + CHUNK_SIZE) % CHUNK_SIZE;
        int tileY = (absY % CHUNK_SIZE + CHUNK_SIZE) % CHUNK_SIZE;

        return new int[]{tileX, tileY, chunkX, chunkY};
    }

    public void addGameObject(GameObject obj){
        this.getChunk(obj.getCX(), obj.getCY()).addGameObject(obj);
    }

    public void addGameObjects(List<GameObject> gobs, int chunkX, int chunkY){
        this.getChunk(chunkX, chunkY).addGameObjects(gobs);
    }

    public void removeGameObject(int idx, int chunkX, int chunkY){
        this.getChunk(chunkX, chunkY).removeGameObject(idx);
    }
}
