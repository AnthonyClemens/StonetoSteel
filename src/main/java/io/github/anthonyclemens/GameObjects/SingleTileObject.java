package io.github.anthonyclemens.GameObjects;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Rectangle;

import io.github.anthonyclemens.Rendering.IsoRenderer;
import io.github.anthonyclemens.states.Game;

public class SingleTileObject extends GameObject{
    protected final int i;

    public SingleTileObject(int i, int x, int y, int chunkX, int chunkY, String name) {
            super(x, y, chunkX, chunkY, name);
            this.i = i;
    }

    @Override
    public void render(IsoRenderer r) {
        r.drawTile(i,x,y,chunkX,chunkY);
        if(Game.showDebug){
            r.getGraphics().setColor(Color.black);
            r.getGraphics().drawRect(r.calculateIsoX(x, y, chunkX, chunkY), r.calculateIsoY(x, y, chunkX, chunkY), IsoRenderer.getTileSize()*r.getZoom(), IsoRenderer.getTileSize()*r.getZoom());
        }
        hitbox.setBounds(r.calculateIsoX(x, y, chunkX, chunkY), r.calculateIsoY(x, y, chunkX, chunkY), IsoRenderer.getTileSize()*r.getZoom(), IsoRenderer.getTileSize()*r.getZoom());
    }

    @Override
    public void renderBatch(IsoRenderer r) {
        r.drawTileBatch(i,x,y,chunkX,chunkY);
    }

}
