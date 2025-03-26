package io.github.anthonyclemens.Player;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

public class Player {
    private float x;
    private float y;
    private float dx;
    private float dy;
    private final float defaultSpeed; // Default movement speed
    private int direction; // Current look direction
    private boolean cameraLocked = true; // Lock camera to player when true
    private final Animation[] animations; // Array of animations for 8 directions
    private final Animation[] idleAnimations;

    public Player(float startX, float startY, float speed, Animation[] animations, Animation[] idleAnimations) {
        this.x = startX;
        this.y = startY;
        this.defaultSpeed = speed;
        this.animations = animations;
        this.idleAnimations = idleAnimations;
    }

    public void update(Input input, int delta) {
        dx = 0;
        dy = 0;
        float speed=this.defaultSpeed;

        // Player movement logic
        // Need to update to include set key values from settings menu
        if (input.isKeyDown(Input.KEY_W)) dy -= 1; // Up
        if (input.isKeyDown(Input.KEY_S)) dy += 1; // Down
        if (input.isKeyDown(Input.KEY_A)) dx -= 1; // Left
        if (input.isKeyDown(Input.KEY_D)) dx += 1; // Right
        if (input.isKeyDown(Input.KEY_LSHIFT)) speed = this.defaultSpeed*1.5f;

        // Normalize diagonal movement
        if (dx != 0 || dy != 0) {
            float length = (float) Math.sqrt(dx * dx + dy * dy);
            dx /= length;
            dy /= length;

            // Update position and play movement animation
            this.x += dx * speed * delta;
            this.y += dy * speed * delta;

            // Update direction and animation
            updateDirection(dx, dy);
            animations[direction].start(); // Play movement animation
        } else {
            idleAnimations[direction].start(); // Play idle animation
        }

        // Toggle camera lock with space
        if (input.isKeyPressed(Input.KEY_SPACE)) {
            cameraLocked = !cameraLocked;
        }
    }

    private void updateDirection(float dx, float dy) {
        if (dx == 0 && dy == 0) return; // No movement, direction unchanged
        // Calculate direction based on dx and dy
        direction = (int) ((Math.atan2(-dy, dx) / Math.PI + 1) * 4) % 8;
    }

    public void render(GameContainer container, float zoom, float cameraX, float cameraY) {
        float renderX = (x - cameraX) * zoom + container.getWidth() / 2f;
        float renderY = (y - cameraY) * zoom + container.getHeight() / 2f;
        if (dx == 0 && dy == 0) {
            idleAnimations[direction].draw(renderX, renderY, animations[direction].getWidth() * zoom, animations[direction].getHeight() * zoom); // Render idle animation
        } else {
            animations[direction].draw(renderX, renderY, animations[direction].getWidth() * zoom, animations[direction].getHeight() * zoom); // Render movement animation
        }
    }

    public boolean isCameraLocked() {
        return cameraLocked;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
