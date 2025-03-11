package io.github.anthonyclemens.GUI;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Rectangle;

import io.github.anthonyclemens.Math.TwoDimensionMath;

public abstract class GUIElement {
    protected float x;
    protected float y;
    protected float w;
    protected float h;
    protected Rectangle r;

    protected GUIElement(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.r=new Rectangle(x, y, w, h);
    }

    // Abstract method for rendering
    public abstract void render(Graphics g);

    // Abstract method for update
    public abstract void update(Input i);

    // Getters and setters for x and y
    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void move(float x, float y){
        this.x=x;
        this.y=y;
        this.r.setLocation(x, y);
    }

    public void setWidth(float w){
        this.w = w;
    }

    public void setHeight(float h){
        this.h = h;
    }

    public float getWidth(){
        return this.w;
    }

    public float getHeight(){
        return this.h;
    }

    public Rectangle getRect(){
        return this.r;
    }

    public void centerX(GameContainer gc, int y){
        this.move(TwoDimensionMath.getMiddleX(this.r, gc.getWidth()),y);
    }
}
