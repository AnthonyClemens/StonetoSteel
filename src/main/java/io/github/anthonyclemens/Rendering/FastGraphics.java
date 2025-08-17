package io.github.anthonyclemens.Rendering;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class FastGraphics extends Graphics{
    private Color lastColor = null;

    public FastGraphics(){
        super();
    }

    @Override
    public void setColor(Color color) {
        if (color != null && !color.equals(lastColor)) {
            super.setColor(color);
            lastColor = color;
        }
    }
}
