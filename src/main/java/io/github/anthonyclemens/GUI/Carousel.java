package io.github.anthonyclemens.GUI;

import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Rectangle;

public class Carousel {
    private List<String> data;
    private Button left;
    private Button right;
    private float x,y;
    private TrueTypeFont ttf;
    private float maxWidth;
    private Rectangle rect;
    private String longStringg;
    private Color color;
    private int index;

    public Carousel(List<String> d, Image l, Image r, TrueTypeFont font, float x, float y, Color col){
        this.data = d;
        String longString = "";
        for(String s : d){
            if(s.length()>longString.length()){
                longString=s;
            }
        }
        this.maxWidth=font.getWidth(longString);
        this.longStringg = longString;
        this.ttf=font;
        this.x=x;
        this.y=y;
        this.index = 0;
        this.left = new Button(l, x, y, font.getHeight());
        this.right = new Button(r, x+this.maxWidth+this.left.getWidth(), y, font.getHeight());
        this.color = col;
        this.left.setColor(col);
        this.right.setColor(col);
        this.rect = new Rectangle((int)x,(int)y,(int)(this.left.getWidth()+this.maxWidth+this.right.getWidth()),font.getHeight());
    }

    public void render(Graphics g){
        this.left.render(g);
        this.ttf.drawString(this.x+this.left.getWidth(), this.y, this.data.get(this.index));
        this.right.render(g);
    }

    public String update(Input input){
        if(this.right.update(input)){
            return this.nextVal();
        }
        if(this.left.update(input)){
            return this.prevVal();
        }
        return this.data.get(this.index);
    }

    //Setters

    public void move(float x, float y){
        this.x=x;
        this.y=y;
        this.left.move(x, y);
        this.right.move(x+this.left.getWidth()+this.maxWidth,y);
    }

    //Getters

    public Rectangle getRect(){
        return this.rect;
    }

    public Rectangle getLeftRect(){
        return this.left.getRect();
    }

    public Rectangle getRightRect(){
        return this.left.getRect();
    }

    public TrueTypeFont getFont(){
        return this.ttf;
    }

    //Hybrid Getter/Setter

    public String nextVal(){
        if(this.index<this.data.size()-1){
            this.index++;
        }
        return data.get(this.index);
    }

    public String prevVal(){
        if(this.index>0){
            this.index--;
        }
        return data.get(this.index);
    }
}
