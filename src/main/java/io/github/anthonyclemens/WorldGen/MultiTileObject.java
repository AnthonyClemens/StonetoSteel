package io.github.anthonyclemens.WorldGen;

import io.github.anthonyclemens.Rendering.Renderer;

public class MultiTileObject extends GameObject {
    protected int[][] layout;

    public MultiTileObject(int x, int y, int cx, int cy, int[][] layout) {
        super(x, y, cx, cy);
        this.layout = layout;
    }

    @Override
    public void render(Renderer r){
        for (int i = 0; i < layout.length; i++) {
            for (int j = 0; j < layout[i].length; j++) {
                if (layout[i][j] != -1) {
                    r.drawTile(layout[i][j], new int[]{this.x+i,this.y+j+1}, new int[]{this.chunkX,this.chunkY});
                }
            }
        }
    }
}
