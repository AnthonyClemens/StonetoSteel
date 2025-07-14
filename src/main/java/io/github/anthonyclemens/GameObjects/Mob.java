package io.github.anthonyclemens.GameObjects;

import java.util.Random;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;

import io.github.anthonyclemens.Rendering.IsoRenderer;
import io.github.anthonyclemens.SharedData;
import io.github.anthonyclemens.WorldGen.Biome;
import io.github.anthonyclemens.states.Game;

public class Mob extends GameObject {
    // 🧍 Animation & Visual State
    protected transient Animation currentAnimation;
    private transient float renderX;                // Final screen-space X position
    private transient float renderY;                // Final screen-space Y position

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
    protected float sway = 1000f;        // Sway cycle range (for offset randomness)
    private transient float swayTime = 0f;         // Time accumulator for sine/cos motion
    private float swayOffset;            // Phase offset per mob for unique motion

    public Mob(String tileSheet, int x, int y, int chunkX, int chunkY, String objName, int wanderDistance) {
        super(tileSheet, x, y, chunkX, chunkY, objName);
        this.wanderDistance = wanderDistance;
        this.rand = new Random(SharedData.getSeed());
        this.swayOffset = this.rand.nextFloat()* sway;
        this.fx = x;
        this.fy = y;
    }

    public void setSway(float newSway){
        this.swayOffset = this.rand.nextFloat()* newSway;
    }

    public void setNewDestination(IsoRenderer renderer) {
        int tries = 20;
        while (tries-- > 0) {
            int candidateX = x + rand.nextInt(wanderDistance * 2) - wanderDistance;
            int candidateY = y + rand.nextInt(wanderDistance * 2) - wanderDistance;

            float isoX = renderer.calculateIsoX(candidateX, candidateY, chunkX, chunkY);
            float isoY = renderer.calculateIsoY(candidateX, candidateY, chunkX, chunkY);
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

        if (distance < 1f) {
            setNewDestination(r);
            return;
        }

        float dirX = dx / distance;
        float dirY = dy / distance;

        fx += dirX * mobSpeed * deltaTime / 1000f;
        fy += dirY * mobSpeed * deltaTime / 1000f;

        // Sync with inherited int coordinates
        x = Math.round(fx);
        y = Math.round(fy);
    }

    @Override
    public void update(IsoRenderer r, int deltaTime) {
        moveTowardsDestination(deltaTime,r);
        if (r.isCameraMoving()) {
            renderX = r.calculateIsoX((int) fx, (int) fy, chunkX, chunkY);
            renderY = r.calculateIsoY((int) fx, (int) fy, chunkX, chunkY);
        } else {
            swayTime += deltaTime;
            float swayX = (float) Math.sin((swayTime + swayOffset) / 300.0) * 0.3f;
            float swayY = (float) Math.cos((swayTime + swayOffset) / 400.0) * 0.3f;

            // Apply sway to logical position, not screen space
            float swayedX = fx + swayX;
            float swayedY = fy + swayY;

            // Then project swayed position to screen
            float renderTargetX = r.calculateIsoX((int) swayedX, (int) swayedY, chunkX, chunkY);
            float renderTargetY = r.calculateIsoY((int) swayedX, (int) swayedY, chunkX, chunkY);

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
    public void render(IsoRenderer r) {
        if (currentAnimation != null) {
            currentAnimation.draw(renderX, renderY,
                currentAnimation.getWidth() * r.getZoom(),
                currentAnimation.getHeight() * r.getZoom());
        }

        if (Game.showDebug&&this.hitbox!=null) {
            r.getGraphics().setColor(Color.green);
            r.getGraphics().draw(hitbox);

            float destRenderX = r.calculateIsoX(destinationX, destinationY, chunkX, chunkY);
            float destRenderY = r.calculateIsoY(destinationX, destinationY, chunkX, chunkY);

            r.getGraphics().setColor(Color.red);
            r.getGraphics().drawLine(renderX, renderY, destRenderX, destRenderY);

            r.getGraphics().setColor(Color.blue);
            r.getGraphics().fillOval(destRenderX - 3, destRenderY - 3, 6, 6);

            r.getGraphics().setColor(Color.black);
        }
    }

    public float getRenderX() { return renderX; }
    public float getRenderY() { return renderY; }
    public void setRenderX(float renderX) { this.renderX = renderX; }
    public void setRenderY(float renderY) { this.renderY = renderY; }
}


