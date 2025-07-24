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
    SWAMP,
    RAINFOREST,
    FOREST,
    SNOWY_PEAK,
    TAIGA;

    /**
     * Determines the biome type based on a noise value.
     * @param Elevation noise value
     * @param Moisture noise value
     * @param Temperature noise value
     * @return The corresponding Biome.
     */
    public static Biome getBiomeFromClimate(double elevation, double moisture, double temperature) {
        if (elevation > 0.85) return SNOWY_PEAK;
        if (elevation > 0.7) return MOUNTAIN;

        if (moisture > 0.8 && temperature > 0.6) return RAINFOREST;
        if (moisture > 0.6) return FOREST;
        if (moisture < 0.3 && temperature > 0.6) return DESERT;
        if (moisture > 0.75) return SWAMP;
        if (temperature < 0.3) return TAIGA;

        return PLAINS;
    }
}
