package io.github.anthonyclemens.GameObjects;

import java.io.Serializable;

import org.newdawn.slick.geom.Rectangle;

import io.github.anthonyclemens.Rendering.IsoRenderer;
import io.github.anthonyclemens.WorldGen.Biome;

public abstract class GameObject implements Serializable{
    protected int x;
    protected int y;
    protected float previousX;
    protected float previousY;
    protected int chunkX;
    protected int chunkY;
    protected String name;
    protected Rectangle hitbox;
    protected String tileSheet;
    protected Biome biome; // Optional: to associate a biome with the game object

    protected GameObject(String tileSheet, int x, int y, int chunkX, int chunkY, String objName) {
        this.x = x;
        this.y = y;
        this.chunkX=chunkX;
        this.chunkY=chunkY;
        this.name=objName;
        this.tileSheet = tileSheet;
        this.hitbox = new Rectangle(0,0,0,0);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
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

    public String getTileSheetName(){
        return this.tileSheet;
    }

    public float getPreviousX() {
        return previousX;
    }

    public void setPreviousX(float previousX) {
        this.previousX = previousX;
    }

    public float getPreviousY() {
        return previousY;
    }

    public void setPreviousY(float previousY) {
        this.previousY = previousY;
    }

    public void setX(int newX) {
        this.x = newX;
    }

    public void setY(int newY) {
        this.y = newY;
    }

    public void setBiome(Biome biome) {
        this.biome = biome;
    }

    public Biome getBiome() {
        return this.biome;
    }

    public void calculateHitbox(float zoom) {
        // Calculate the hitbox based on the object's position and size
        this.hitbox.setBounds(x * zoom, y * zoom, hitbox.getWidth(), hitbox.getHeight());
    }

    public abstract void render(IsoRenderer r);

    public abstract void update(IsoRenderer r, int deltaTime);
}
