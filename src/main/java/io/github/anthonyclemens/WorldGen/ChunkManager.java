package io.github.anthonyclemens.WorldGen;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ChunkManager {
    private static final int CHUNK_SIZE = 16;
    private final Map<String, Future<Chunk>> chunks = new ConcurrentHashMap<>();
    private final ExecutorService executor;
    private final int seed;

    public ChunkManager(int seed) {
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.seed = seed;

        System.out.println("ChunkManager initialized for infinite world generation with seed: "+seed);
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

    public Chunk getChunk(int chunkX, int chunkY){
        String key = chunkX + "," + chunkY;

        Future<Chunk> future = chunks.computeIfAbsent(key, k -> executor.submit(() -> {
            Biome biome = getBiomeForChunk(chunkX, chunkY);
            return new Chunk(CHUNK_SIZE, biome, this, chunkX, chunkY);
        }));

        try {
            return future.get();
        } catch (ExecutionException e) {
            chunks.remove(key); // Remove the failing entry for retry
            return null;
        } catch (InterruptedException e) {
            chunks.remove(key); // Remove the failing entry for retry
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public int[] getBlockAndChunk(int absX, int absY) {
        int chunkX = (absX < 0) ? (absX + 1) / CHUNK_SIZE - 1 : absX / CHUNK_SIZE;
        int chunkY = (absY < 0) ? (absY + 1) / CHUNK_SIZE - 1 : absY / CHUNK_SIZE;

        int tileX = (absX % CHUNK_SIZE + CHUNK_SIZE) % CHUNK_SIZE;
        int tileY = (absY % CHUNK_SIZE + CHUNK_SIZE) % CHUNK_SIZE;

        return new int[]{tileX, tileY, chunkX, chunkY};
    }
}
