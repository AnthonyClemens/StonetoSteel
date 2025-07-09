package io.github.anthonyclemens.GUI;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.TrueTypeFont;

import io.github.anthonyclemens.Math.TwoDimensionMath;

public class Banner extends GUIElement{
    private final Image image;
    private String text;
    private final TrueTypeFont ttf;
    private Color color;
    private int textWidth;
    private float yOffset;
    private float xOffset;


    public Banner(Image i, String text, TrueTypeFont ttf, float x, float y, float width, float height) {
        super(x, y, width, height);
        this.image = i;
        this.text = text;
        this.ttf = ttf;
        if(this.ttf!=null){
            this.color = Color.black;
            this.textWidth = this.ttf.getWidth(this.text);
            this.yOffset = TwoDimensionMath.getMiddleX(this.ttf.getHeight(), this.getHeight());
            this.xOffset = TwoDimensionMath.getMiddleX(this.textWidth, this.getWidth());
        }
    }

    @Override
    public void render(Graphics g) {
        this.image.draw(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        if(this.ttf!=null){
            g.setColor(this.color);
            this.ttf.drawString(this.xOffset+this.getX(), this.yOffset+this.getY(), this.text, this.color);
        }
    }

    @Override
    public void update(Input i) {
        //Empty since this is a banner
    }

    public void setTextColor(Color c){
        this.color = c;
    }

    public void changeYOffset(float offset){
        this.yOffset=offset;
    }

    public void changeXOffset(float offset){
        this.xOffset=offset;
    }

    public void setText(String text){
        this.text = text;
    }
}
