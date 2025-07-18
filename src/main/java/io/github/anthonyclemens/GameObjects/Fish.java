package io.github.anthonyclemens.GameObjects;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;

import io.github.anthonyclemens.Rendering.SpriteManager;
import io.github.anthonyclemens.WorldGen.Biome;

public class Fish extends Mob{
    public Fish(String tileSheet, int x, int y, int chunkX, int chunkY, String objName) {
        super(tileSheet, x, y, chunkX, chunkY, objName, 16);
        this.animationLoader = (SerializableSupplier<Animation>) () -> SpriteManager.getAnimation("fishes", 0, 0, 0, 0, 100);
        this.biome = Biome.WATER;
        this.mobSpeed=2f;
        this.smoothness=0.04f;
        this.setSway(3000f);
        this.setColorOverlay(new Color(113, 181, 219));
    }
}
