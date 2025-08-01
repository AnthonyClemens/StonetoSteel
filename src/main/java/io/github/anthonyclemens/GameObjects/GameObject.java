package io.github.anthonyclemens.GameObjects;

import java.io.Serializable;

import org.newdawn.slick.geom.Rectangle;

import io.github.anthonyclemens.Rendering.IsoRenderer;
import io.github.anthonyclemens.WorldGen.Biome;

public abstract class GameObject implements Serializable{
    protected int x;
    protected int y;
    protected transient float previousX;
    protected transient float previousY;
    protected int chunkX;
    protected int chunkY;
    protected boolean peaceful = true;
    protected String name;
    protected int id;
    protected transient Rectangle hitbox;
    protected String tileSheet;
    protected Biome biome;
    protected int health = -1;
    protected int maxHealth;
    protected boolean solid = true;

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

    public void setID(int nid){
        this.id=nid;
    }

    public int getID(){
        return this.id;
    }

    public boolean isSolid(){
        return this.solid;
    }

    public void setSolid(boolean nSolid){
        this.solid = nSolid;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getHealth() {
        return this.health;
    }

    public void removeHealth(int amount) {
        this.health -= amount;
        if (this.health < 0) {
            this.health = 0;
        }
    }

    public void addHealth(int amount) {
        this.health += amount;
        if (this.health > this.maxHealth) {
            this.health = this.maxHealth;
        }
    }

    public void setPeaceful(boolean peaceful) {
        this.peaceful = peaceful;
    }

    public abstract void calculateHitbox(IsoRenderer r);

    public void setTileSheet(String tileSheet) {
        this.tileSheet = tileSheet;
    }

    public void setLocation(int x, int y, int chunkX, int chunkY){
        this.x=x;
        this.y=y;
        this.chunkX=chunkX;
        this.chunkY=chunkY;
    }

    public abstract void render(IsoRenderer r, int lodLevel);

    public abstract void update(IsoRenderer r, int deltaTime);
}
