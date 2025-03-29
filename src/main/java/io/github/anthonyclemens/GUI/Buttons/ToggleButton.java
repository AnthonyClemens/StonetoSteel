package io.github.anthonyclemens.GUI.Buttons;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;

public class ToggleButton extends Button{
    private final Image enabled;
    private final Image disabled;
    private boolean value;

    public ToggleButton(Image enabled, Image disabled, float x, float y, float width, float height){
        super(x,y,width,height);
        this.enabled=enabled;
        this.disabled=disabled;
    }

    @Override
    public void render(Graphics g){
        if(value){
            enabled.draw(getX(), getY(), getWidth(), getHeight());
        }else{
            disabled.draw(getX(), getY(), getWidth(), getHeight());
        }
    }

    @Override
    public void update(Input input){
        if(this.r.contains(input.getMouseX(), input.getMouseY()) && input.isMousePressed(Input.MOUSE_LEFT_BUTTON)){
            this.value=!this.value;
        }
    }

    public boolean getValue(){
        return this.value;
    }

    public void setValue(boolean newValue){
        this.value = newValue;
    }
}
