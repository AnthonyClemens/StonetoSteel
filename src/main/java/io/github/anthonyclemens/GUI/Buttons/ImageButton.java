package io.github.anthonyclemens.GUI.Buttons;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class ImageButton extends Button{
    private final Image i;

    public ImageButton(float x, float y, float width, float height, Image i) {
        super(x, y, width, height);
        this.i=i;
    }

    @Override
    public void render(Graphics g){
        if(this.getColor()!=null){
            this.i.draw(getX(), getY(), this.getWidth(), this.getHeight(), this.getColor());
        }else{
            this.i.draw(getX(), getY(), this.getWidth(), this.getHeight());
        }
    }

}
