package io.github.anthonyclemens.GUI.Buttons;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.TrueTypeFont;

import io.github.anthonyclemens.Math.TwoDimensionMath;

public class ImageTextButton extends Button{
    private Image image;
    private String text;
    private Color textColor;
    private final TrueTypeFont ttf;
    private float textWidth;

    public ImageTextButton(Image i, String text, TrueTypeFont ttf, float x, float y, float width, float height) {
        super(x, y, width, height);
        this.image=i;
        this.text=text;
        this.textColor=Color.black;
        this.ttf=ttf;
        this.textWidth=ttf.getWidth(text);
        this.name = text;
    }

    @Override
    public void render(Graphics g){
        if(!this.render) return;
        this.image.draw(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        g.setColor(this.textColor);
        this.ttf.drawString(TwoDimensionMath.getMiddleX(this.textWidth, this.getWidth())+this.getX(), TwoDimensionMath.getMiddleX(this.ttf.getHeight(), this.getHeight())+this.getY(), this.text, this.textColor);
    }

    public void setTextColor(Color c){
        this.textColor=c;
    }

    public String getText(){
        return this.text;
    }

    public void setImage(Image i){
        this.image = i;
    }

    public void setText(String text) {
        this.text = text;
        this.textWidth = this.ttf.getWidth(text);
    }

}
