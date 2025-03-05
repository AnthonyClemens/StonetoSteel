package io.github.anthonyclemens.GUI;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.TrueTypeFont;

public class TextButton extends Button{
    private String buttonString;
    private TrueTypeFont ttf;
    private Color textColor;

    public TextButton(String bs, Color txtColor, Image img, float x, float y, float h){
        super(img, x, y, h);
        this.textColor = txtColor;
        this.buttonString = bs;
    }

    public TextButton(String bs, Color txtColor, TrueTypeFont font, Color c, float x, float y, float h){
        super(c, x, y, font.getWidth(bs),h);
        this.buttonString = bs;
        this.textColor = txtColor;
        this.ttf=font;
    }

    @Override
    public void render(Graphics g){
        super.render(g);
        this.ttf.drawString(super.getRect().getX(), super.getRect().getY(), this.buttonString, this.textColor);
    }

    //Getters

    //Setters
}
