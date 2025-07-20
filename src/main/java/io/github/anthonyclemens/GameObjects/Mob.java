package io.github.anthonyclemens.GameObjects;

import java.util.Random;

import org.lwjgl.Sys;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Rectangle;

import io.github.anthonyclemens.Rendering.IsoRenderer;
import io.github.anthonyclemens.SharedData;
import io.github.anthonyclemens.Sound.SoundBox;
import io.github.anthonyclemens.WorldGen.Biome;
import io.github.anthonyclemens.WorldGen.ChunkManager;
import io.github.anthonyclemens.states.Game;

public class Mob extends GameObject {
    // Animation & Visual State
    protected transient Animation currentAnimation;
    protected SerializableSupplier<Animation> animationLoader;
    protected SerializableSupplier<SoundBox> soundLoader;
    protected transient SoundBox soundBox;
    protected transient float renderX;                // Final screen-space X position
    protected transient float renderY;                // Final screen-space Y position
    private Color colorOverlay;          // Optional color tint for the mob

    // Logical Positioning
    private float fx;                     // Internal float X position
    private float fy;                     // Internal float Y position

    // Pathfinding & Destination
    private int destinationX;            // Target X tile
    private int destinationY;            // Target Y tile
    private final int wanderDistance;    // Max radius for wandering
    private final Random rand;           // Seeded RNG for deterministic motion

    // Sway & Movement Styling
    protected float mobSpeed = 4f;       // Movement speed (pixels per second?)
    protected float smoothness = 0.15f;  // Easing factor for render interpolation
    protected float sway = 0f;        // Sway cycle range (for offset randomness)
    private transient float swayTime = 0f;         // Time accumulator for sine/cos motion
    private float swayOffset;            // Phase offset per mob for unique motion
    protected long lastDamageTime = 0; // Timestamp of last time damage was taken (milliseconds)
    protected long hurtFlashEndTime = 0; // Timestamp when hurt flash should end
    protected long damageCooldown = 1000; // Cooldown time between damage in milliseconds
    protected static final int HURT_FLASH_DURATION_MS = 250; // Duration of red flash in ms

    public Mob(String tileSheet, int x, int y, int chunkX, int chunkY, String objName, int wanderDistance) {
        super(tileSheet, x, y, chunkX, chunkY, objName);
        this.wanderDistance = wanderDistance;
        this.rand = new Random(SharedData.getSeed()+Sys.getTime());
        this.swayOffset = this.rand.nextFloat()* sway;
        this.fx = x;
        this.fy = y;
    }

    public void setSway(float newSway){
        this.swayOffset = this.rand.nextFloat()* newSway;
    }

    public void wander(IsoRenderer renderer) {
        int tries = 20;
        while (tries-- > 0) {
            int candidateX = x + rand.nextInt(wanderDistance * 2) - wanderDistance;
            int candidateY = y + rand.nextInt(wanderDistance * 2) - wanderDistance;

            float isoX = IsoRenderer.calculateIsoX(candidateX, candidateY, chunkX, chunkY);
            float isoY = IsoRenderer.calculateIsoY(candidateX, candidateY, chunkX, chunkY);
            int[] selectedBlock = renderer.screenToIsometric(isoX, isoY);

            Biome candidateBiome = renderer.getChunkManager().getBiomeForChunk(selectedBlock[2], selectedBlock[3]);
            if (candidateBiome == this.biome) {
                destinationX = candidateX;
                destinationY = candidateY;
                return;
            }
        }

        // Fallback to current position
        destinationX = x;
        destinationY = y;
    }

    private void moveTowardsDestination(int deltaTime, IsoRenderer r) {
        float dx = destinationX - fx;
        float dy = destinationY - fy;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance < 1f && peaceful) {
            wander(r);
            return;
        }

        float dirX = dx / distance;
        float dirY = dy / distance;

        fx += dirX * mobSpeed * deltaTime / 1000f;
        fy += dirY * mobSpeed * deltaTime / 1000f;

        int tileSize = ChunkManager.CHUNK_SIZE;

        int oldChunkX = chunkX;
        int oldChunkY = chunkY;
        int newChunkX = chunkX;
        int newChunkY = chunkY;
        // Wrap for left/right boundaries
        while (fx < 0) {
            newChunkX -= 1;
            x += tileSize;
            fx += tileSize;
            destinationX += tileSize;
        }
        while (fx >= tileSize) {
            newChunkX += 1;
            x -= tileSize;
            fx -= tileSize;
            destinationX -= tileSize;
        }

        // Wrap for top/bottom boundaries
        while (fy < 0) {
            newChunkY -= 1;
            y += tileSize;
            fy += tileSize;
            destinationY += tileSize;
        }
        while (fy >= tileSize) {
            newChunkY += 1;
            y -= tileSize;
            fy -= tileSize;
            destinationY -= tileSize;
        }
        if(newChunkX != oldChunkX || newChunkY != oldChunkY) {
            // This candidate is out of chunk, calculate move to new chunk
            chunkX = newChunkX;
            chunkY = newChunkY;
            r.getChunkManager().moveGameObjectToChunk(this, oldChunkX, oldChunkY, chunkX, chunkY);
        }

    }

    @Override
    public void update(IsoRenderer r, int deltaTime) {
        if (currentAnimation == null && animationLoader != null) {
            currentAnimation = animationLoader.get();
        }
        if(this.hitbox==null) this.hitbox = new Rectangle(0,0,0,0);
        moveTowardsDestination(deltaTime,r);
        if (r.isCameraMoving()) {
            renderX = IsoRenderer.calculateIsoX((int) fx, (int) fy, chunkX, chunkY);
            renderY = IsoRenderer.calculateIsoY((int) fx, (int) fy, chunkX, chunkY);
        } else {
            swayTime += deltaTime;
            float swayX = (float) Math.sin((swayTime + swayOffset) / 300.0) * 0.3f;
            float swayY = (float) Math.cos((swayTime + swayOffset) / 400.0) * 0.3f;

            // Apply sway to logical position, not screen space
            float swayedX = fx + swayX;
            float swayedY = fy + swayY;

            // Then project swayed position to screen
            float renderTargetX = IsoRenderer.calculateIsoX((int) swayedX, (int) swayedY, chunkX, chunkY);
            float renderTargetY = IsoRenderer.calculateIsoY((int) swayedX, (int) swayedY, chunkX, chunkY);

            // Now interpolate
            renderX += (renderTargetX - renderX) * smoothness;
            renderY += (renderTargetY - renderY) * smoothness;
        }

        if (currentAnimation != null) {
            currentAnimation.update(deltaTime);
            hitbox.setBounds(renderX, renderY,
                currentAnimation.getWidth() * r.getZoom(),
                currentAnimation.getHeight() * r.getZoom());
        }
    }

    @Override
    public void render(IsoRenderer r, int lodLevel) {
        if (currentAnimation == null || r == null) return;

        if(System.currentTimeMillis() < hurtFlashEndTime){
            currentAnimation.draw(renderX, renderY,
            currentAnimation.getWidth() * r.getZoom(),
            currentAnimation.getHeight() * r.getZoom(), new Color(255, 0, 0, 120));
        }else{
            currentAnimation.draw(renderX, renderY,
            currentAnimation.getWidth() * r.getZoom(),
            currentAnimation.getHeight() * r.getZoom());
        }

        if (Game.showDebug&&this.hitbox!=null) {
            r.getGraphics().setColor(Color.red);
            r.getGraphics().drawString("Health: "+this.health+"/"+this.maxHealth, renderX, renderY);
            r.getGraphics().setColor(Color.green);
            r.getGraphics().draw(hitbox);

            float destRenderX = IsoRenderer.calculateIsoX(destinationX, destinationY, chunkX, chunkY);
            float destRenderY = IsoRenderer.calculateIsoY(destinationX, destinationY, chunkX, chunkY);

            r.getGraphics().setColor(Color.red);
            r.getGraphics().drawLine(renderX+r.getZoom()*this.currentAnimation.getWidth()/2, renderY+r.getZoom()*this.currentAnimation.getHeight()/2, destRenderX, destRenderY);

            r.getGraphics().setColor(Color.blue);
            r.getGraphics().fillOval(destRenderX - 3, destRenderY - 3, 6, 6);

            r.getGraphics().setColor(Color.black);
        }
    }

    public void setColorOverlay(Color color) {
        this.colorOverlay = color;
    }

    @Override
    public void removeHealth(int amount){
        long now = System.currentTimeMillis();
        if (now - lastDamageTime < damageCooldown) {
            return; // Only allow damage every 1 second
        }
        super.removeHealth(amount);
        lastDamageTime = now;
        hurtFlashEndTime = now + HURT_FLASH_DURATION_MS; // Set flash timer
    }

    public float getRenderX() { return renderX; }
    public float getRenderY() { return renderY; }
    public void setRenderX(float renderX) { this.renderX = renderX; }
    public void setRenderY(float renderY) { this.renderY = renderY; }
}


