package io.github.anthonyclemens.GameObjects;

import java.io.Serializable;

import org.newdawn.slick.geom.Rectangle;

import io.github.anthonyclemens.Rendering.IsoRenderer;
import io.github.anthonyclemens.WorldGen.Biome;

public abstract class GameObject implements Serializable{
    protected int x;
    protected int y;
    protected int chunkX;
    protected int chunkY;
    protected Biome biome;
    protected String name;
    protected Rectangle hitbox;

    protected GameObject(int x, int y, int chunkX, int chunkY, String name) {
        this.x = x;
        this.y = y;
        this.chunkX=chunkX;
        this.chunkY=chunkY;
        this.name=name;
        this.hitbox = new Rectangle(x, y, IsoRenderer.getTileSize(), IsoRenderer.getTileSize());
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public Biome getBiome(){
        return this.biome;
    }

    public String getName(){
        return this.name;
    }

    public int getCX() {
        return this.chunkX;
    }

    public int getCY() {
        return this.chunkY;
    }

    public Rectangle getHitbox(){
        return this.hitbox;
    }

    public abstract void render(IsoRenderer r);

    public abstract void renderBatch(IsoRenderer r);
}
