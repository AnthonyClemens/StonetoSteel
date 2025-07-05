package io.github.anthonyclemens.GameObjects;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;

import io.github.anthonyclemens.Rendering.SpriteManager;
import io.github.anthonyclemens.WorldGen.Biome;

public class Fish extends Mob{
    public Fish(String tileSheet, int x, int y, int chunkX, int chunkY, String objName) {
        super(tileSheet, x, y, chunkX, chunkY, objName, 8);
        this.currentAnimation = new Animation(new Image[] { SpriteManager.getSpriteSheet("fishes").getSprite(0, 0) }, new int[] { 100 }, true);
        this.biome = Biome.WATER;
    }
}
