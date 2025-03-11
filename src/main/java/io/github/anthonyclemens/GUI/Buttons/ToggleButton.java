package io.github.anthonyclemens.GUI.Buttons;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public class ToggleButton extends Button{
    private final Color enabled;
    private final Color disabled;
    private boolean value;

    public ToggleButton(Color enabled, Color disabled, float x, float y, float width, float height){
        super(x,y,width,height);
        this.enabled=enabled;
        this.disabled=disabled;
    }

    @Override
    public void render(Graphics g){
        super.setColor((value)? enabled: disabled);
        super.render(g);
    }

    @Override
    public void update(Input input){
        this.value = !this.value;
    }
}
