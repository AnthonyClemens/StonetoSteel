package io.github.anthonyclemens.Math;

import org.newdawn.slick.geom.Rectangle;


public class TwoDimensionMath {
    private TwoDimensionMath(){}

    public static float getMiddleX(Rectangle rect, int boundWidth){
        return boundWidth/2f-rect.getWidth()/2f;
    }

    public static float getMiddleX(int itemWidth, int boundWidth){
        return boundWidth/2f-itemWidth/2f;
    }
}
