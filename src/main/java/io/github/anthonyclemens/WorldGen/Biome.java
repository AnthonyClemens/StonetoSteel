package io.github.anthonyclemens.WorldGen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum Biome {
    PLAINS,
    DESERT,
    WINTER,
    MOUNTAIN,
    SWAMP;

    public static Biome getBiomeFromNoise(double noiseValue) {
        if (noiseValue < -0.2) return Biome.DESERT;
        if (noiseValue < 0.2) return Biome.PLAINS;
        if (noiseValue < 0.5) return Biome.WINTER;
        if (noiseValue < 0.7) return Biome.SWAMP;
        return Biome.MOUNTAIN;
    }

    public static List<GameObject> generateDesertObjects(Random rand, int chunkX, int chunkY, int chunkSize) {
        List<GameObject> gobs = new ArrayList<>();
        for (int y = 0; y < chunkSize-1; y++) {
            for (int x = 0; x < chunkSize-1; x++) {
                if(rand.nextFloat() < 0.02){
                    gobs.add(new SingleTileObject(9, x, y, chunkX, chunkY));
                }
            }
        }
        return gobs;
    }
}
