package io.github.anthonyclemens.Rendering;

import org.newdawn.slick.Input;

import io.github.anthonyclemens.Player.Player;

public class Camera {
    private float x;
    private float y;

    public Camera(float startX, float startY) {
        this.x = startX;
        this.y = startY;
    }

    public void update(Player player, Input input, float newX, float newY) {
        if (player.isCameraLocked()) {
            this.x = player.getX();
            this.y = player.getY();
        } else {
            if (input.isKeyDown(Input.KEY_UP)) y -= 0.5f;
            if (input.isKeyDown(Input.KEY_DOWN)) y += 0.5f;
            if (input.isKeyDown(Input.KEY_LEFT)) x -= 0.5f;
            if (input.isKeyDown(Input.KEY_RIGHT)) x += 0.5f;
            this.x=newX;
            this.y=newY;
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}