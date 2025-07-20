package io.github.anthonyclemens.GameObjects;

import java.util.Random;

import org.newdawn.slick.Color;

import io.github.anthonyclemens.Rendering.IsoRenderer;
import io.github.anthonyclemens.Rendering.SpriteManager;
import io.github.anthonyclemens.states.Game;

public class Tree extends SingleTileObject{

    private static final double PLAINS_BIG_TREE_PROBABILITY = 0.75; // 75% probability for big trees
    private long shakeDuration = 500; // When shaking should end
    private long lastDamageTime = 0; // Timestamp of last time damage was taken (milliseconds)
    private long endShakeTime = 0; // Timestamp when shaking should end
    private long damageCooldown = 500; // Cooldown time between damage in milliseconds
    private int shakeAggression; // How much the tree shakes when hit
    private float offsetX = 0; // Offset for shaking effect
    private float offsetY = 0; // Offset for shaking effect
    private Random rand;

    public Tree(Random rand, int x, int y, int chunkX, int chunkY) {
        super(null,"",-1, x, y, chunkX, chunkY);
        this.rand = rand;
        if (this.rand.nextFloat() < PLAINS_BIG_TREE_PROBABILITY) {
            this.name = "bigtree";
            this.tileSheet = "bigtrees";
            this.i = (byte) rand.nextInt(4);
            this.health = 100;
            this.maxHealth = this.health;
            this.shakeDuration = 500;
            this.shakeAggression = 10;
        } else {
            this.name = "smalltree";
            this.tileSheet = "smalltrees";
            this.i = (byte) rand.nextInt(8);
            this.health = 20;
            this.maxHealth = this.health;
            this.shakeDuration = 250;
            this.shakeAggression = 2;
        }
        this.tileWidth = SpriteManager.getSpriteWidth(tileSheet);
        this.tileHeight = SpriteManager.getSpriteHeight(tileSheet);
        this.hitbox.setBounds(0, 0, tileWidth, tileHeight);
        
    }

    @Override
    public void render(IsoRenderer r, int lodLevel) {
        float renderX = IsoRenderer.calculateIsoX(x, y, chunkX, chunkY) + offsetX;
        float renderY = IsoRenderer.calculateIsoY(x, y, chunkX, chunkY) + offsetY;
        r.drawTileIso(tileSheet, i, renderX, renderY);
        if(Game.showDebug){
            r.getGraphics().setColor(Color.red);
            r.getGraphics().drawString("Health: "+this.health+"/"+this.maxHealth, renderX, renderY);
            r.getGraphics().setColor(Color.black);
            r.getGraphics().draw(hitbox);
        }
    }

    @Override
    public void update(IsoRenderer r, int deltaTime) {
        super.update(r, deltaTime);
        if(System.currentTimeMillis() < endShakeTime){
            offsetX = rand.nextInt(shakeAggression) - shakeAggression / 2;
            offsetY = rand.nextInt(shakeAggression) - shakeAggression / 2;
        }
    }

    @Override
    public void removeHealth(int amount) {
        long now = System.currentTimeMillis();
        if (now - lastDamageTime < damageCooldown) {
            return;
        }
        super.removeHealth(amount);
        endShakeTime = now + shakeDuration;
        lastDamageTime = now;
    }

}
