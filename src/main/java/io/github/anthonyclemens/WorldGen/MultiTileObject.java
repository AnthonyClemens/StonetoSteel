package io.github.anthonyclemens.WorldGen;

import io.github.anthonyclemens.Rendering.Renderer;

public class MultiTileObject extends GameObject {
    protected int[][][] layout;

    public MultiTileObject(int x, int y, int cx, int cy, int[][][] layout) {
        super(x, y, cx, cy);
        this.layout = layout;
    }

    @Override
    public void render(Renderer r){
        for (int z = 0; z < layout.length; z++) { // Iterate over the depth (z-axis)
            for (int x = 0; x < layout[z].length; x++) {
                for (int y = 0; y < layout[z][x].length; y++) {
                    if (layout[z][x][y] != -1) {
                        //r.drawTile(
                        //    layout[z][x][y],
                        //    new int[]{this.x + x, this.y + y, z},
                        //    new int[]{this.chunkX, this.chunkY}
                        //);
                    }
                }
            }
        }
    }
}
