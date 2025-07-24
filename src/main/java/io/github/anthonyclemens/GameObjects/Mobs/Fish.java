package io.github.anthonyclemens.GameObjects.Mobs;

import java.util.Random;

import org.newdawn.slick.Animation;

import io.github.anthonyclemens.GameObjects.SerializableSupplier;
import io.github.anthonyclemens.GameObjects.SingleTileObjects.Item;
import io.github.anthonyclemens.Rendering.IsoRenderer;
import io.github.anthonyclemens.Rendering.SpriteManager;
import io.github.anthonyclemens.WorldGen.Biome;
import io.github.anthonyclemens.WorldGen.Chunk;
import io.github.anthonyclemens.states.Game;

public class Fish extends Mob{
    private final Item droppedItem;
    private boolean dropItem = false;

    public Fish(String tileSheet, int x, int y, int chunkX, int chunkY, String objName) {
        super(tileSheet, x, y, chunkX, chunkY, objName, 20);
        this.animationLoader = (SerializableSupplier<Animation>) () -> {
            int fish = new Random().nextInt(3);
            return SpriteManager.getAnimation("fishes", fish, 0, fish, 0, 100);
        };
        this.droppedItem = new Item("fishes", "ITEM_FISH", 0, x, y, chunkX, chunkY);
        this.droppedItem.setQuantity(1);
        this.biome = Biome.WATER;
        this.mobSpeed=1f;
        this.smoothness=0.02f;
        this.setSway(1000f);
        this.maxHealth = 10;
        this.health = this.maxHealth;
    }

    @Override
    public void update(IsoRenderer r, int deltaTime) {
        int[] currentLoc = r.screenToIsometric(renderX, renderY);
        if(this.dropItem){
            Chunk thisChunk = r.getChunkManager().getChunk(chunkX, chunkY);
            this.droppedItem.setLocation(currentLoc[0],currentLoc[1],currentLoc[2],currentLoc[3]);
            this.droppedItem.setID(thisChunk.getGameObjects().size());
            thisChunk.addGameObject(this.droppedItem);
        }
        super.update(r,deltaTime);
        if(r.getChunkManager().getBiomeForChunk(currentLoc[2], currentLoc[3])!=this.biome) this.removeHealth(1);
    }

    @Override
    public void removeHealth(int amount){
        long now = System.currentTimeMillis();
        if (now - lastDamageTime < damageCooldown) {
            return; // Only allow damage every 1 second
        }
        this.health -= amount;
        if(this.lod<2) Game.passiveMobSoundBox.playRandomSound("fishHurt");
        if (this.health < 0) {
            this.dropItem = true;
            this.health = 0;
        }
        lastDamageTime = now;
        hurtFlashEndTime = now + HURT_FLASH_DURATION_MS; // Set flash timer
    }
}
