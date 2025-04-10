package io.github.anthonyclemens.GameObjects;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import io.github.anthonyclemens.Rendering.IsoRenderer;
import io.github.anthonyclemens.states.Game;

public class ImageObject extends GameObject{
    protected String spriteSheetDir = null;
    protected int tw = 0;
    protected int th = 0;
    protected transient final Image i;

    public ImageObject(Image i, int x, int y, int chunkX, int chunkY, String name) {
            super(x, y, chunkX, chunkY, name);
            this.i = i;
    }

    public void setSpriteSheet(String spriteSheetDir, int tw, int th) {
        this.spriteSheetDir = spriteSheetDir;
        this.tw = tw;
        this.th = th;
    }

    public String getSpriteSheetDir(){
        return this.spriteSheetDir;
    }

    public int getTileWidth(){
        return this.tw;
    }

    public int getTileHeight(){
        return this.th;
    }

    public int getWidth(){
        return this.i.getWidth();
    }

    public int getHeight(){
        return this.i.getHeight();
    }

    @Override
    public void render(IsoRenderer r) {
        if(this.i!= null){
            r.drawImageAtCoord(this.i, x, y, chunkX, chunkY);
            if(Game.showDebug){
                r.getGraphics().setColor(Color.black);
                r.getGraphics().drawRect(r.calculateIsoX(x, y, chunkX, chunkY), r.calculateIsoY(x, y, chunkX, chunkY), this.i.getWidth()*r.getZoom(), this.i.getHeight()*r.getZoom());
            }
        }
        hitbox.setBounds(r.calculateIsoX(x, y, chunkX, chunkY), r.calculateIsoY(x, y, chunkX, chunkY), IsoRenderer.getTileSize()*r.getZoom(), IsoRenderer.getTileSize()*r.getZoom());
    }

    @Override
    public void renderBatch(IsoRenderer r) {
        r.drawImageAtCoordBatch(this.i, x, y, chunkX, chunkY);
    }
}
