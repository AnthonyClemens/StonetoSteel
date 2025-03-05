package io.github.anthonyclemens.GUI;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Rectangle;

public class Button {
    private Color color;
    private Image img;
    private float bx;
    private float by;
    private float width;
    private float height;
    private Rectangle rect;
    private String text;
    private boolean debug;

    public Button(float x, float y, float width, float height){
        this.bx=x;
        this.by=y;
        this.width=width;
        this.height=height;
        this.rect = new Rectangle(x, y, width, height);
    }

    public Button(Image img, float x, float y, float w, float h){
        this.bx=x;
        this.by=y;
        this.width=w;
        this.height=h;
        this.rect = new Rectangle(x, y, w, h);
        this.img=img;
        this.color=null;
    }

    public Button(Image img, float x, float y, float h){
        float w = h/img.getHeight()*img.getWidth();
        this.bx=x;
        this.by=y;
        this.width=w;
        this.height=h;
        this.rect = new Rectangle(x, y, w, h);
        this.img=img;
        this.color=null;
    }

    public Button(Image img, Color col, float x, float y, float w, float h){
        this.bx=x;
        this.by=y;
        this.width=w;
        this.height=h;
        this.rect = new Rectangle(x, y, w, h);
        this.img=img;
        this.color=col;
    }

    public Button(Color col, float x, float y, float w, float h){
        this.color=col;
        this.bx=x;
        this.by=y;
        this.width=w;
        this.height=h;
        this.img=null;
        this.rect = new Rectangle(x, y, w, h);
    }

    public void render(Graphics g){
        if(this.img==null){
            g.setColor(this.color);
            g.fill(this.rect);
            g.draw(this.rect);
        }else{
            this.img.draw(this.bx,this.by,this.width,this.height,this.color);
        }
        if(debug){
            g.setColor(this.color);
            g.draw(this.rect);
        }
    }

    public boolean update(Input input){
        return this.rect.contains(input.getMouseX(), input.getMouseY()) && input.isMousePressed(Input.MOUSE_LEFT_BUTTON);
    }

    //Getters
    public Rectangle getRect(){
        return this.rect;
    }

    public float getWidth(){
        return this.width;
    }

    public float getHeight(){
        return this.height;
    }

    //Setters
    public void setColor(Color c){
        this.color = c;
    }

    public void enableDebug(){
        this.debug = true;
    }

    public void setColor(int r, int g, int b, int a){
        this.color = new Color(r, g, b, a);
    }

    public void move(float x, float y){
        this.bx=x;
        this.by=y;
        this.rect.setBounds(x, y, this.width, this.height);
    }

}
