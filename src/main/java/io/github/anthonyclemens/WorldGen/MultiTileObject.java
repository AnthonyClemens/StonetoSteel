package io.github.anthonyclemens.WorldGen;

import io.github.anthonyclemens.Rendering.IsoRenderer;

public class MultiTileObject extends GameObject {
    protected int[][][] layout;

    public MultiTileObject(Builder builder) {
        super(builder.x, builder.y, builder.cx, builder.cy);
        this.layout = builder.layout;
    }

    @Override
    public void render(IsoRenderer r) {
        for (int z = layout.length-1; z > 0; z--) { // Iterate over the depth (z-axis)
            for (int x = 0; x < layout[z].length; x++) {
                for (var y = 0; y < layout[z][x].length; y++) {
                    if (layout[z][x][y] != -1) {
                        r.drawTile(layout[z][x][y], this.x, this.y, this.chunkX, this.chunkY);
                    }
                }
            }
        }
    }

    @Override
    public void renderBatch(IsoRenderer r) {
        for (int z = layout.length-1; z > 0; z--) { // Iterate over the depth (z-axis)
            for (int x = 0; x < layout[z].length; x++) {
                for (var y = 0; y < layout[z][x].length; y++) {
                    if (layout[z][x][y] != -1) {
                        r.drawTileBatch(layout[z][x][y], this.x, this.y, this.chunkX, this.chunkY);
                    }
                }
            }
        }
    }


    // Builder class
    public static class Builder {
        private int x;
        private int y;
        private int cx;
        private int cy;
        private int[][][] layout;

        public Builder setXYPos(int x, int y){
            this.x=x;
            this.y=y;
            return this;
        }

        public Builder setChunkPos(ChunkManager cm, int cx, int cy){
            this.cx=cx;
            this.cy=cy;
            int chunkSize = cm.getChunk(cx, cy).getChunkSize();
            this.layout = new int[chunkSize][chunkSize][chunkSize];
            return this;
        }

        public Builder setTile(int tile, int x, int y, int z){
            this.layout[z][x][y] = tile;
            return this;
        }

        public MultiTileObject build() {
            return new MultiTileObject(this);
        }
    }
}
