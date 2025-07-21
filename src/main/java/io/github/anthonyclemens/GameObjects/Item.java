package io.github.anthonyclemens.GameObjects;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Rectangle;

import io.github.anthonyclemens.Rendering.IsoRenderer;
import io.github.anthonyclemens.states.Game;

public class Item extends SingleTileObject{

    private int quantity = 1;
    private float renderX;
    private float renderY;
    protected float offsetY = 6f;
    protected float hoverSpeed = 0.1f;
    private float hoverTime = 0f;
    private final Color shadowColor = new Color(0, 0, 0, 0.6f);
    private final float shadowOffsetY = 4f;

    public Item(String tileSheet, String name, int i, int x, int y, int chunkX, int chunkY) {
        super(tileSheet, name, i, x, y, chunkX, chunkY);
        this.renderX = IsoRenderer.calculateIsoX(x, y, chunkX, chunkY);
        this.renderY = IsoRenderer.calculateIsoY(x, y, chunkX, chunkY);
    }

    @Override
    public void render(IsoRenderer r, int deltaTime) {
        float zoom = r.getZoom();
        float baseX = IsoRenderer.calculateIsoX(x, y, chunkX, chunkY);
        float baseY = IsoRenderer.calculateIsoY(x, y, chunkX, chunkY);
        float shadowY = baseY + tileHeight * zoom + shadowOffsetY;
        float shadowX = baseX + tileWidth * zoom / 2f;
        float bobOffset = offsetY * (float) Math.sin(hoverTime * hoverSpeed * 2 * Math.PI);
        float scaleFactor = 1f + ((bobOffset / offsetY) * 0.3f);
        float shadowRadius = (tileWidth * zoom / 2f) * scaleFactor;

        // Draw shadow centered under the item
        r.getGraphics().setColor(shadowColor);
        r.getGraphics().fillOval(
            shadowX - shadowRadius / 2f,
            shadowY,
            shadowRadius,
            shadowRadius / 2f
        );
        r.drawTileIso(tileSheet, i, renderX, renderY);

        if (Game.showDebug) {
            r.getGraphics().setColor(Color.black);
            r.getGraphics().draw(hitbox);
        }
    }

    @Override
    public void update(IsoRenderer r, int deltaTime) {
        if (this.hitbox == null) {
            this.hitbox = new Rectangle(x, y, this.tileWidth, this.tileHeight);
        }

        // Accumulate hover time
        hoverTime += deltaTime / 1000f;

        // Bobbing effect
        float bobOffset = offsetY * (float) Math.sin(hoverTime * hoverSpeed * 2 * Math.PI);

        renderX = IsoRenderer.calculateIsoX(x, y, chunkX, chunkY);
        renderY = IsoRenderer.calculateIsoY(x, y, chunkX, chunkY) + bobOffset;

        hitbox.setBounds(renderX, renderY, tileWidth * r.getZoom(), tileHeight * r.getZoom());
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

}
