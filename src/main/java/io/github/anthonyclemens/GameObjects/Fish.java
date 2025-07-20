package io.github.anthonyclemens.GameObjects;

import java.util.Random;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;

import io.github.anthonyclemens.Rendering.SpriteManager;
import io.github.anthonyclemens.WorldGen.Biome;

public class Fish extends Mob{
    public Fish(String tileSheet, int x, int y, int chunkX, int chunkY, String objName) {
        super(tileSheet, x, y, chunkX, chunkY, objName, 20);
        this.animationLoader = (SerializableSupplier<Animation>) () -> {
            int fish = new Random().nextInt(3);
            return SpriteManager.getAnimation("fishes", fish, 0, fish, 0, 100);
        };
        this.biome = Biome.WATER;
        this.mobSpeed=1f;
        this.smoothness=0.02f;
        this.setSway(1000f);
        this.setColorOverlay(new Color(113, 181, 219));
    }
}
