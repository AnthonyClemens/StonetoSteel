package io.github.anthonyclemens.GUI.Fields;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

import io.github.anthonyclemens.GUI.GUIElement;

public abstract class Field extends GUIElement{
    protected String value;

    protected Field(float x, float y, float w, float h) {
            super(x, y, w, h);
            this.value = "";
    }

    public String getValue(){
        return this.value;
    }

    @Override
    public abstract void render(Graphics g);

    @Override
    public abstract void update(Input input);
}
