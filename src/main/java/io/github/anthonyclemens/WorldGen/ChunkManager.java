package io.github.anthonyclemens.WorldGen;

import java.util.HashMap;
import java.util.Map;

public class ChunkManager {
    private static final int CHUNK_SIZE = 16;
    private final Map<String, Chunk> chunks = new HashMap<>();

    public Chunk getChunk(int chunkX, int chunkY) {
        String key = chunkX + "," + chunkY;
        return chunks.computeIfAbsent(key, k -> new Chunk(CHUNK_SIZE));
    }

    public int[] getBlockAndChunk(int bX, int bY){
        return new int[]{bX % CHUNK_SIZE, bY % CHUNK_SIZE, bX / CHUNK_SIZE, bY / CHUNK_SIZE};
    }
}
