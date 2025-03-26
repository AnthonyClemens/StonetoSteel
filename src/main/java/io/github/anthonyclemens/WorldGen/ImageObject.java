package io.github.anthonyclemens.WorldGen;

import org.newdawn.slick.Image;

import io.github.anthonyclemens.Rendering.IsoRenderer;

public class ImageObject extends GameObject{
    private final Image i;

    protected ImageObject(Image i, int x, int y, int chunkX, int chunkY) {
            super(x, y, chunkX, chunkY);
            this.i = i;
    }

    @Override
    public void render(IsoRenderer r) {
        r.drawImageAtCoord(this.i, x, y, chunkX, chunkY);
    }

    @Override
    public void renderBatch(IsoRenderer r) {
        r.drawImageAtCoordBatch(this.i, x, y, chunkX, chunkY);
    }
}
