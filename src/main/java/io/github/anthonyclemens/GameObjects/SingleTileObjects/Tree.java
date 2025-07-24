package io.github.anthonyclemens.GameObjects.SingleTileObjects;

import java.util.Random;

import org.newdawn.slick.Color;

import io.github.anthonyclemens.Rendering.IsoRenderer;
import io.github.anthonyclemens.Rendering.SpriteManager;
import io.github.anthonyclemens.WorldGen.Chunk;
import io.github.anthonyclemens.states.Game;

public class Tree extends SingleTileObject{

    private long shakeDuration = 500; // When shaking should end
    private long lastDamageTime = 0; // Timestamp of last time damage was taken (milliseconds)
    private long endShakeTime = 0; // Timestamp when shaking should end
    private final long damageCooldown = 500; // Cooldown time between damage in milliseconds
    private final int shakeAggression; // How much the tree shakes when hit
    private float offsetX = 0; // Offset for shaking effect
    private float offsetY = 0; // Offset for shaking effect
    private final Random rand;
    private final Item droppedItem;
    private boolean dropItem = false;

    public Tree(Random rand, int x, int y, int chunkX, int chunkY) {
        super(null,"",-1, x, y, chunkX, chunkY);
        this.rand = rand;
        this.droppedItem = new Item("main", "ITEM_WOOD", 110, x, y, chunkX, chunkY);
        if (this.rand.nextFloat() < 0.75) {
            this.name = "bigTree";
            this.tileSheet = "bigtrees";
            this.i = (byte) rand.nextInt(12);
            this.health = 40;
            this.shakeDuration = 500;
            this.shakeAggression = 6;
            this.droppedItem.setQuantity(6 + rand.nextInt(5));
        } else {
            this.name = "smallTree";
            this.tileSheet = "smalltrees";
            this.i = (byte) rand.nextInt(8);
            this.health = 20;
            this.shakeDuration = 250;
            this.shakeAggression = 2;
            this.droppedItem.setQuantity(2 + rand.nextInt(3));
        }
        this.maxHealth = this.health;
        this.tileWidth = SpriteManager.getSpriteWidth(tileSheet);
        this.tileHeight = SpriteManager.getSpriteHeight(tileSheet);
        this.hitbox.setBounds(0, 0, tileWidth, tileHeight);
    }

    public Tree(Random rand, int x, int y, int chunkX, int chunkY, float bigTreeProbability) {
        super(null,"",-1, x, y, chunkX, chunkY);
        this.rand = rand;
        this.droppedItem = new Item("main", "ITEM_WOOD", 110, x, y, chunkX, chunkY);
        if (this.rand.nextFloat() < bigTreeProbability) {
            this.name = "bigTree";
            this.tileSheet = "bigtrees";
            this.i = (byte) rand.nextInt(12);
            this.health = 40;
            this.shakeDuration = 500;
            this.shakeAggression = 6;
            this.droppedItem.setQuantity(6 + rand.nextInt(5));
        } else {
            this.name = "smallTree";
            this.tileSheet = "smalltrees";
            this.i = (byte) rand.nextInt(8);
            this.health = 20;
            this.shakeDuration = 250;
            this.shakeAggression = 2;
            this.droppedItem.setQuantity(2 + rand.nextInt(3));
        }
        this.maxHealth = this.health;
        this.tileWidth = SpriteManager.getSpriteWidth(tileSheet);
        this.tileHeight = SpriteManager.getSpriteHeight(tileSheet);
        this.hitbox.setBounds(0, 0, tileWidth, tileHeight);
    }

    public Tree(Random rand, int x, int y, int chunkX, int chunkY, boolean specialTrees) {
        super(null,"",-1, x, y, chunkX, chunkY);
        this.rand = rand;
        this.droppedItem = new Item("main", "ITEM_WOOD", 110, x, y, chunkX, chunkY);
        this.name = "bigTree";
        this.tileSheet = "specialtrees";
        this.i = (byte) rand.nextInt(17);
        this.health = 50;
        this.shakeDuration = 500;
        this.shakeAggression = 6;
        this.droppedItem.setQuantity(8 + rand.nextInt(5));
        this.maxHealth = this.health;
        this.tileWidth = SpriteManager.getSpriteWidth(tileSheet);
        this.tileHeight = SpriteManager.getSpriteHeight(tileSheet);
        this.hitbox.setBounds(0, 0, tileWidth, tileHeight);
    }

    @Override
    public void render(IsoRenderer r, int lodLevel) {
        float renderX = r.calculateIsoX(x, y, chunkX, chunkY) + offsetX*r.getZoom();
        float renderY = r.calculateIsoY(x, y, chunkX, chunkY) + offsetY*r.getZoom();
        r.drawTileIso(tileSheet, i, renderX, renderY);
        if(Game.showDebug){
            //r.getGraphics().setColor(Color.red);
           // r.getGraphics().drawString("Health: "+this.health+"/"+this.maxHealth, renderX, renderY);
            r.getGraphics().setColor(Color.black);
            r.getGraphics().draw(hitbox);
        }
    }

    @Override
    public void update(IsoRenderer r, int deltaTime) {
        if(dropItem){
            Chunk thisChunk = r.getChunkManager().getChunk(chunkX, chunkY);
            this.droppedItem.setID(thisChunk.getGameObjects().size());
            thisChunk.addGameObject(this.droppedItem);
        }
        super.update(r, deltaTime);
        if(System.currentTimeMillis() < endShakeTime){
            offsetX = rand.nextInt(shakeAggression) - shakeAggression / 2f;
            offsetY = rand.nextInt(shakeAggression) - shakeAggression / 2f;
        }
    }

    @Override
    public void removeHealth(int amount) {
        long now = System.currentTimeMillis();
        if (now - lastDamageTime < damageCooldown) {
            return;
        }
        this.health -= amount;
        if (this.health <= 0) {
            dropItem = true;
            this.health = 0;
        }
        endShakeTime = now + shakeDuration;
        lastDamageTime = now;
        Game.gameObjectSoundBox.playRandomSound(this.name+"HitSounds");
    }

}
