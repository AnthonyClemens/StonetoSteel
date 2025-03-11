package io.github.anthonyclemens.WorldGen;

import io.github.anthonyclemens.Rendering.Renderer;

public class SingleTileObject extends GameObject{
    private final int i;

    protected SingleTileObject(int i, int x, int y, int chunkX, int chunkY) {
            super(x, y, chunkX, chunkY);
            this.i = i;
    }

    @Override
    public void render(Renderer r) {
        r.drawTile(i,x,y,chunkX,chunkY);
    }

}
