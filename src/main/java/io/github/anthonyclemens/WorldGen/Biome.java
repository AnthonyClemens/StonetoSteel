package io.github.anthonyclemens.WorldGen;

/**
 * Enum representing different biome types in the world.
 * Provides a utility method to map noise values to biomes.
 */
public enum Biome {
    PLAINS,
    DESERT,
    WATER,
    MOUNTAIN,
    BEACH,
    SWAMP;

    /**
     * Determines the biome type based on a noise value.
     * @param noiseValue The noise value (typically between -1 and 1).
     * @return The corresponding Biome.
     */
    public static Biome getBiomeFromNoise(double noiseValue) {
        if (noiseValue < -0.2) return Biome.WATER;
        if (noiseValue < -0.15 && noiseValue >= -0.2) return Biome.BEACH;
        if (noiseValue < 0.2) return Biome.PLAINS;
        if (noiseValue < 0.5) return Biome.DESERT;
        if (noiseValue < 0.7) return Biome.SWAMP;
        return Biome.MOUNTAIN;
    }
}
