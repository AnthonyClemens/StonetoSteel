package io.github.anthonyclemens.GameObjects;

import java.util.Random;

import org.newdawn.slick.Animation;

import io.github.anthonyclemens.Rendering.IsoRenderer;
import io.github.anthonyclemens.SharedData;
import io.github.anthonyclemens.states.Game;

public class Mob extends GameObject {
    protected transient Animation currentAnimation;
    private transient float renderX;
    private transient float renderY;
    private int destinationX;
    private int destinationY;
    private final int wanderDistance;
    private final Random rand;
    protected float mobSpeed = 1f;

    public Mob(String tileSheet, int x, int y, int chunkX, int chunkY, String objName, int wanderDistance) {
        super(tileSheet, x, y, chunkX, chunkY, objName);
        this.wanderDistance = wanderDistance;
        this.rand = new Random(SharedData.getSeed());
    }

    public void setNewDestination() {
        destinationX = x + rand.nextInt(wanderDistance);
        destinationY = y + rand.nextInt(wanderDistance);
    }

    @Override
    public void render(IsoRenderer r) {
        if (currentAnimation != null) {
            currentAnimation.draw(renderX, renderY, currentAnimation.getWidth() * r.getZoom(), currentAnimation.getHeight() * r.getZoom());
        }
        if (Game.showDebug) {
            r.getGraphics().draw(hitbox);
        }
    }

    @Override
    public void update(IsoRenderer r, int deltaTime) {
        // Update animation state
        renderX = r.calculateIsoX(x, y, chunkX, chunkY);
        renderY = r.calculateIsoY(x, y, chunkX, chunkY);


        if (currentAnimation != null) {
            currentAnimation.update(deltaTime);
            hitbox.setBounds(renderX, renderY, currentAnimation.getWidth() * r.getZoom(), currentAnimation.getHeight() * r.getZoom());
        }
    }

    public float getRenderX() {
        return this.renderX;
    }

    public float getRenderY() {
        return this.renderY;
    }

    public void setRenderX(float renderX) {
        this.renderX = renderX;
    }

    public void setRenderY(float renderY) {
        this.renderY = renderY;
    }

}
