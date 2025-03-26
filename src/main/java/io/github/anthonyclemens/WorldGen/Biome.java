package io.github.anthonyclemens.WorldGen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public enum Biome {
    PLAINS,
    DESERT,
    WATER,
    MOUNTAIN,
    BEACH,
    SWAMP;

    public static Biome getBiomeFromNoise(double noiseValue) {
        if (noiseValue < -0.2) return Biome.WATER;
        if (noiseValue < -0.15) return Biome.BEACH;
        if (noiseValue < 0.2) return Biome.PLAINS;
        if (noiseValue < 0.5) return Biome.DESERT;
        if (noiseValue < 0.7) return Biome.SWAMP;
        return Biome.MOUNTAIN;
    }

    public static List<GameObject> generateDesertObjects(Random rand, int chunkX, int chunkY, int chunkSize) {
        List<GameObject> gobs = new ArrayList<>();
        double probability = 0.02;
        for (int y = 0; y < chunkSize-1; y++) {
            for (int x = 0; x < chunkSize-1; x++) {
                if(rand.nextFloat() < probability){
                    gobs.add(new SingleTileObject(9, x, y, chunkX, chunkY));// Place a cactus
                }
            }
        }
        return gobs;
    }

    public static List<GameObject> generateWaterObjects(Random rand, int chunkX, int chunkY, int chunkSize) throws SlickException {
        List<GameObject> gobs = new ArrayList<>();
        SpriteSheet fishies = new SpriteSheet("textures/Organisms/fish.png",16,16);
        double probability = 0.02;
        for (int y = 0; y < chunkSize-1; y++) {
            for (int x = 0; x < chunkSize-1; x++) {
                if(rand.nextFloat() < probability){
                    Image fish = fishies.getSprite(0, rand.nextInt(fishies.getHorizontalCount()));
                    gobs.add(new ImageObject(fish, x, y, chunkX, chunkY));// Place a cactus
                }
            }
        }
        return gobs;
    }
}
