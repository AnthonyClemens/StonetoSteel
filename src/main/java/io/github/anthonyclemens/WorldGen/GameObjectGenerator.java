package io.github.anthonyclemens.WorldGen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.github.anthonyclemens.GameObjects.Fish;
import io.github.anthonyclemens.GameObjects.GameObject;
import io.github.anthonyclemens.GameObjects.SingleTileObject;

/**
 * Utility class for generating game objects for different biomes.
 */
public class GameObjectGenerator {

    // Density values for object generation
    private static final double DESERT_CACTUS_DENSITY = 0.04; // 4% density for cactus
    private static final double WATER_FISH_DENSITY = 0.01; // 1% density for fish
    private static final double PLAINS_TREE_DENSITY = 0.3; // 30% density for trees
    private static final double PLAINS_BIG_TREE_PROBABILITY = 0.75; // 75% probability for big trees

    /**
     * Generates a list of GameObjects for a given biome and chunk.
     * @param biome     The biome type.
     * @param rand      Random instance.
     * @param chunkX    Chunk X coordinate.
     * @param chunkY    Chunk Y coordinate.
     * @param chunkSize Size of the chunk.
     * @return List of generated GameObjects.
     */
    public static List<GameObject> generateObjectsForBiome(Biome biome, Random rand, int chunkX, int chunkY, int chunkSize) {
        return switch (biome) {
            case DESERT -> generateDesertObjects(rand, chunkX, chunkY, chunkSize);
            case PLAINS -> generatePlainsObjects(rand, chunkX, chunkY, chunkSize);
            case WATER -> generateWaterObjects(rand, chunkX, chunkY, chunkSize);
            case MOUNTAIN, SWAMP -> new ArrayList<>();
            default -> new ArrayList<>();
        };
    }

    /**
     * Generates cactus objects for desert biomes.
     */
    private static List<GameObject> generateDesertObjects(Random rand, int chunkX, int chunkY, int chunkSize) {
        List<GameObject> gobs = new ArrayList<>();
        for (int y = 0; y < chunkSize - 1; y++) {
            for (int x = 0; x < chunkSize - 1; x++) {
                if (rand.nextFloat() < DESERT_CACTUS_DENSITY) {
                    SingleTileObject newObject = new SingleTileObject("main", "cactus", 9, x, y, chunkX, chunkY);
                    if (!isOverlapping(newObject, gobs)) {
                        gobs.add(newObject); // Add only if no overlap
                    }
                }
            }
        }
        return gobs;
    }

    /**
     * Generates fish objects for water biomes.
     */
    private static List<GameObject> generateWaterObjects(Random rand, int chunkX, int chunkY, int chunkSize) {
        List<GameObject> gobs = new ArrayList<>();
        for (int y = 0; y < chunkSize - 1; y++) {
            for (int x = 0; x < chunkSize - 1; x++) {
                if (rand.nextFloat() < WATER_FISH_DENSITY) {
                    Fish newObject = new Fish("fishes", x, y, chunkX, chunkY, "fish");
                    if (!isOverlapping(newObject, gobs)) {
                        gobs.add(newObject); // Add only if no overlap
                    }
                }
            }
        }
        return gobs;
    }

    /**
     * Generates tree objects for plains biomes.
     */
    private static List<GameObject> generatePlainsObjects(Random rand, int chunkX, int chunkY, int chunkSize) {
        List<GameObject> gobs = new ArrayList<>();
        for (int y = 0; y < chunkSize - 1; y++) {
            for (int x = 0; x < chunkSize - 1; x++) {
                if (rand.nextFloat() < PLAINS_TREE_DENSITY) {
                    SingleTileObject newObject = createPlainsObject(rand, x, y, chunkX, chunkY);
                    if (newObject != null && !isOverlapping(newObject, gobs)) {
                        gobs.add(newObject);
                    }
                }
            }
        }
        return gobs;
    }

    /**
     * Creates a plains tree object, choosing between big and small trees.
     */
    private static SingleTileObject createPlainsObject(Random rand, int x, int y, int chunkX, int chunkY) {
        if (rand.nextFloat() < PLAINS_BIG_TREE_PROBABILITY) {
            return new SingleTileObject("bigtrees", "tree", rand.nextInt(4), x, y, chunkX, chunkY);
        } else {
            return new SingleTileObject("smalltrees", "tree", rand.nextInt(8), x, y, chunkX, chunkY);
        }
    }

    /**
     * Checks if a new object overlaps with any existing objects.
     */
    private static boolean isOverlapping(GameObject newObject, List<GameObject> existingObjects) {
        for (GameObject obj : existingObjects) {
            if (newObject.getHitbox().intersects(obj.getHitbox())) {
                return true; // Overlap detected
            }
        }
        return false; // No overlap
    }
}
