package io.github.anthonyclemens.WorldGen;

import io.github.anthonyclemens.Rendering.Renderer;

public abstract class GameObject {
    protected int x;
    protected int y;
    protected int chunkX;
    protected int chunkY;

    protected GameObject(int x, int y, int chunkX, int chunkY) {
        this.x = x;
        this.y = y;
        this.chunkX=chunkX;
        this.chunkY=chunkY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public abstract void render(Renderer r);
}
