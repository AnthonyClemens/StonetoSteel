package io.github.anthonyclemens.GameObjects.Mobs;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import org.lwjgl.Sys;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Rectangle;

import io.github.anthonyclemens.GameObjects.GameObject;
import io.github.anthonyclemens.GameObjects.SerializableSupplier;
import io.github.anthonyclemens.Rendering.IsoRenderer;
import io.github.anthonyclemens.SharedData;
import io.github.anthonyclemens.Sound.SoundBox;
import io.github.anthonyclemens.WorldGen.Biome;
import io.github.anthonyclemens.WorldGen.ChunkManager;
import io.github.anthonyclemens.states.Game;

public class Mob extends GameObject {
    // Animation & Visual State
    protected transient Animation currentAnimation;
    protected Map<Direction, SerializableSupplier<Animation>> animationLoaders = new EnumMap<>(Direction.class);
    protected Direction currentDirection = Direction.DOWN;
    protected int animationIndex = 0;
    protected SerializableSupplier<SoundBox> soundLoader;
    protected transient SoundBox soundBox;
    protected Color colorOverlay;          // Optional color tint for the mob

    // Logical Positioning
    private transient float fx;                     // Internal float X position
    private transient float fy;                     // Internal float Y position

    // Pathfinding & Destination
    private transient int destinationX;            // Target X tile
    private transient int destinationY;            // Target Y tile
    private transient int nextDestinationX;            // Target X tile
    private transient int nextDestinationY;            // Target Y tile
    protected final Random rand;           // Seeded RNG for deterministic motion
    protected final int visionDistance; // Sets the vision distance for pathfinding
    protected float intelligence = 0; // Sets the percentage chance that the mob will lock onto the player

    // Sway & Movement Styling
    protected float mobSpeed = 4f;       // Movement speed (pixels per second?)
    protected float smoothness = 0.15f;  // Easing factor for render interpolation
    protected transient float sway = 0f;        // Sway cycle range (for offset randomness)
    private transient float swayTime = 0f;         // Time accumulator for sine/cos motion
    private float swayOffset;            // Phase offset per mob for unique motion
    protected long lastDamageTime = 0; // Timestamp of last time damage was taken (milliseconds)
    protected long hurtFlashEndTime = 0; // Timestamp when hurt flash should end
    protected long damageCooldown = 1000; // Cooldown time between damage in milliseconds
    protected static final int HURT_FLASH_DURATION_MS = 250; // Duration of red flash in ms
    protected byte lod = 0;
    private Direction lastDirection = null;

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public Mob(String tileSheet, int x, int y, int chunkX, int chunkY, String objName, int visionDistance) {
        super(tileSheet, x, y, chunkX, chunkY, objName);
        this.visionDistance = visionDistance;
        this.rand = new Random(SharedData.getSeed()+Sys.getTime());
        this.swayOffset = this.rand.nextFloat()* sway;
        this.fx = x;
        this.fy = y;
        this.alwaysCalcHitbox = true;
        this.destinationX=x;
        this.destinationY=y;
    }

    public void setSway(float newSway){
        this.swayOffset = this.rand.nextFloat()* newSway;
    }

    public void wander(IsoRenderer r) {
        int tries = 20;
        while (tries-- > 0) {
            int candidateX = x + rand.nextInt(visionDistance * 2) - visionDistance;
            int candidateY = y + rand.nextInt(visionDistance * 2) - visionDistance;

            float isoX = r.calculateIsoX(candidateX, candidateY, chunkX, chunkY);
            float isoY = r.calculateIsoY(candidateX, candidateY, chunkX, chunkY);
            int[] selectedBlock = r.screenToIsometric(isoX, isoY);

            Biome candidateBiome = r.getChunkManager().getBiomeForChunk(selectedBlock[2], selectedBlock[3]);
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

        double angle = Math.atan2(dy, dx);

        if (angle >= -Math.PI / 4 && angle < Math.PI / 4) {
            currentDirection = Direction.RIGHT;
        } else if (angle >= Math.PI / 4 && angle < 3 * Math.PI / 4) {
            currentDirection = Direction.DOWN;
        } else if (angle >= -3 * Math.PI / 4 && angle < -Math.PI / 4) {
            currentDirection = Direction.UP;
        } else {
            currentDirection = Direction.LEFT;
        }

        if (distance < 2f){
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
        if (animationLoaders != null) {
            // Only update animation if direction changed
            if (currentAnimation == null || currentDirection != lastDirection) {
                SerializableSupplier<Animation> loader = animationLoaders.get(currentDirection);
                if (loader != null) {
                    currentAnimation = loader.get();
                    lastDirection = currentDirection;
                }
            }
        }

        swayTime += deltaTime;
        moveTowardsDestination(deltaTime, r);

        if (currentAnimation != null) {
            currentAnimation.update(deltaTime);
        }
    }

    @Override
    public void render(IsoRenderer r, int lodLevel) {
        this.lod=(byte)lodLevel;
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

        if (Game.showDebug&&this.hitbox!=null&&lodLevel<2) {
            //r.getGraphics().setColor(Color.red);
            //r.getGraphics().drawString("Health: "+this.health+"/"+this.maxHealth, renderX, renderY);
            r.getGraphics().setColor((this.peaceful) ? Color.green : Color.red);
            r.getGraphics().draw(hitbox);

            float destRenderX = r.calculateIsoX(destinationX, destinationY, chunkX, chunkY);
            float destRenderY = r.calculateIsoY(destinationX, destinationY, chunkX, chunkY);

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

    @Override
    public void calculateHitbox(IsoRenderer r) {
        if (this.hitbox == null) this.hitbox = new Rectangle(0, 0, 0, 0);

        // Apply sway to logical position
        float swayX = (float) Math.sin((swayTime + swayOffset) / 300.0) * 0.3f;
        float swayY = (float) Math.cos((swayTime + swayOffset) / 400.0) * 0.3f;

        float swayedX = fx + swayX;
        float swayedY = fy + swayY;

        // Project swayed position to screen space
        float renderTargetX = r.calculateIsoX((int) swayedX, (int) swayedY, chunkX, chunkY);
        float renderTargetY = r.calculateIsoY((int) swayedX, (int) swayedY, chunkX, chunkY);

        // Interpolation factor: instant if camera is moving, smooth otherwise
        float interpolationFactor = r.isCameraMoving() ? 0.5f : smoothness;

        renderX += (renderTargetX - renderX) * interpolationFactor;
        renderY += (renderTargetY - renderY) * interpolationFactor;

        if (currentAnimation != null) {
            hitbox.setBounds(renderX, renderY,
                currentAnimation.getWidth() * r.getZoom(),
                currentAnimation.getHeight() * r.getZoom());
        }
    }

    public void setDestinationByGlobalPosition(int[] globalLocation) {
        int chunkSize = ChunkManager.CHUNK_SIZE;

        int absoluteTargetX = globalLocation[2] * chunkSize + globalLocation[0];
        int absoluteTargetY = globalLocation[3] * chunkSize + globalLocation[1];

        int absoluteCurrentX = this.chunkX * chunkSize + this.x;
        int absoluteCurrentY = this.chunkY * chunkSize + this.y;

        int deltaX = absoluteTargetX - absoluteCurrentX;
        int deltaY = absoluteTargetY - absoluteCurrentY;

        this.destinationX = this.x + deltaX + this.rand.nextInt(3)-2;
        this.destinationY = this.y + deltaY + this.rand.nextInt(3)-2;
    }

    public int getVisionRadius(){
        return this.visionDistance;
    }

    public float getIntelligence(){
        return this.intelligence;
    }

    public boolean isPeaceful() {
        return this.peaceful;
    }
}


