package io.github.anthonyclemens.GameObjects;

import java.util.Random;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;

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
    protected float mobSpeed = 10f;

    public Mob(String tileSheet, int x, int y, int chunkX, int chunkY, String objName, int wanderDistance) {
        super(tileSheet, x, y, chunkX, chunkY, objName);
        this.wanderDistance = wanderDistance;
        this.rand = new Random(SharedData.getSeed());
        setNewDestination();
    }

    public void setNewDestination() {
        destinationX = x + rand.nextInt(wanderDistance * 2) - wanderDistance;
        destinationY = y + rand.nextInt(wanderDistance * 2) - wanderDistance;
    }


    private void moveTowardsDestination(int deltaTime) {
        float dx = destinationX - x;
        float dy = destinationY - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance < 1f) {
            setNewDestination(); // Close enough, pick a new one
            return;
        }

        // Normalize direction
        float dirX = dx / distance;
        float dirY = dy / distance;

        // Move based on speed and deltaTime
        x += dirX * mobSpeed * deltaTime / 1000f;
        y += dirY * mobSpeed * deltaTime / 1000f;
    }


    @Override
    public void render(IsoRenderer r) {
        if (currentAnimation != null) {
            currentAnimation.draw(renderX, renderY, currentAnimation.getWidth() * r.getZoom(), currentAnimation.getHeight() * r.getZoom());
        }
        if (Game.showDebug) {
            // Draw hitbox
            r.getGraphics().setColor(Color.green);
            r.getGraphics().draw(hitbox);

            // Draw line to destination
            float destRenderX = r.calculateIsoX(destinationX, destinationY, chunkX, chunkY);
            float destRenderY = r.calculateIsoY(destinationX, destinationY, chunkX, chunkY);

            r.getGraphics().setColor(Color.red);
            r.getGraphics().drawLine(renderX, renderY, destRenderX, destRenderY);

            // Draw destination marker
            r.getGraphics().setColor(Color.blue);
            r.getGraphics().fillOval(destRenderX - 3, destRenderY - 3, 6, 6);

            // Reset color
            r.getGraphics().setColor(Color.black);
        }
    }

    @Override
    public void update(IsoRenderer r, int deltaTime) {
        // Move toward destination
        moveTowardsDestination(deltaTime);
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
