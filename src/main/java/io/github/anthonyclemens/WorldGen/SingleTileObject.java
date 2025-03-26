package io.github.anthonyclemens.WorldGen;

import io.github.anthonyclemens.Rendering.IsoRenderer;

public class SingleTileObject extends GameObject{
    private final int i;

    protected SingleTileObject(int i, int x, int y, int chunkX, int chunkY) {
            super(x, y, chunkX, chunkY);
            this.i = i;
    }

    @Override
    public void render(IsoRenderer r) {
        r.drawTile(i,x,y,chunkX,chunkY);
    }

    @Override
    public void renderBatch(IsoRenderer r) {
        r.drawTileBatch(i,x,y,chunkX,chunkY);
    }

}
