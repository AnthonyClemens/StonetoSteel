package io.github.anthonyclemens.WorldGen;

public enum Biome {
    PLAINS,
    DESERT,
    WATER,
    MOUNTAIN,
    BEACH,
    SWAMP;

    public static Biome getBiomeFromNoise(double noiseValue) {
        if (noiseValue < -0.2) return Biome.WATER;
        if (noiseValue < -0.15 && noiseValue >= -0.2) return Biome.BEACH;
        if (noiseValue < 0.2) return Biome.PLAINS;
        if (noiseValue < 0.5) return Biome.DESERT;
        if (noiseValue < 0.7) return Biome.SWAMP;
        return Biome.MOUNTAIN;
    }
}
