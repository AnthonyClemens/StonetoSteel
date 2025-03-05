package io.github.anthonyclemens.Player;

import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

import io.github.anthonyclemens.Rendering.Renderer;

public class Player {
    private SpriteSheet sprite;
    private Image currImage;
    private int pX;
    private int pY;

    public Player(SpriteSheet sprite, int startX, int startY){
        this.sprite = sprite;
        this.pX = startX;
        this.pY = startY;
        this.currImage = sprite.getSprite(4, 1);
    }

    public void render(Renderer r){
        r.drawImageAtCoord(this.currImage,this.pX,this.pY);
    }
}
