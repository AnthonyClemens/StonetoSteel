package io.github.anthonyclemens.GameObjects;

import org.newdawn.slick.Color;

import io.github.anthonyclemens.Rendering.IsoRenderer;
import io.github.anthonyclemens.Rendering.SpriteManager;
import io.github.anthonyclemens.states.Game;

public class SingleTileObject extends GameObject{
    protected final int i;
    protected final int tileWidth;
    protected final int tileHeight;

    public SingleTileObject(String tileSheet, String name, int i, int x, int y, int chunkX, int chunkY) {
            super(tileSheet, x, y, chunkX, chunkY, name);
            this.i = i;
            this.tileWidth = SpriteManager.getSpriteWidth(tileSheet);
            this.tileHeight = SpriteManager.getSpriteHeight(tileSheet);
            this.hitbox.setBounds(0, 0, tileWidth, tileHeight);
    }

    @Override
    public void render(IsoRenderer r) {
        r.drawScaledTile(tileSheet,i,x,y,chunkX,chunkY);
        if(Game.showDebug){
            r.getGraphics().setColor(Color.black);
            r.getGraphics().draw(hitbox);
        }
    }

    @Override
    public void update(IsoRenderer r, int deltaTime) {
        hitbox.setBounds(r.calculateIsoX(x, y, chunkX, chunkY), r.calculateIsoY(x, y, chunkX, chunkY), tileWidth*r.getZoom(), tileHeight*r.getZoom());
    }

}
