package io.github.anthonyclemens.GameObjects;

import io.github.anthonyclemens.Rendering.IsoRenderer;

public class MultiTileObject extends GameObject{

    public MultiTileObject(Builder builder) {
        super(0,0,0,0,builder.name);
    }

    @Override
    public void render(IsoRenderer r) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void renderBatch(IsoRenderer r) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static class Builder {
        private String name;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }
    }
}
