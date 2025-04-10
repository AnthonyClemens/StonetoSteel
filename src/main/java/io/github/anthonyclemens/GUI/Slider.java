package io.github.anthonyclemens.GUI;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.TrueTypeFont;

public class Slider extends GUIElement{
    private float value;
    private final Image sliderNub;
    private final Color sliderBg;
    private TrueTypeFont ttf;

    public Slider(Image sliderNub, Color sliderBg, float x, float y, float w, float h) {
        super(x, y, w, h);
        this.sliderBg=sliderBg;
        this.sliderNub=sliderNub;
    }

    public Slider(Image sliderNub, Color sliderBg, TrueTypeFont ttf, float x, float y, float w, float h) {
        super(x, y, w, h);
        this.sliderBg=sliderBg;
        this.sliderNub=sliderNub;
        this.ttf=ttf;
    }

    @Override
    public void render(Graphics g) {
        // Draw slider background
        g.setColor(sliderBg);
        g.fillRect(getX(), getY(), getWidth(), getHeight());

        // Draw slider nub based on the current value
        float nubX = getX() + (getWidth() - sliderNub.getWidth()) * value;
        float nubY = getY() + (getHeight() - sliderNub.getHeight()) / 2;
        sliderNub.draw(nubX, nubY);
        // Draw the percentage text if enabled
        if (ttf != null) {
            int percentage = Math.round(value * 100);

            float textX = getX() + getWidth() + 10; // 10px padding to the right of the slider
            float textY = getY() + (getHeight() - ttf.getHeight("100%")) / 2; // Center vertically

            // Draw the text
            ttf.drawString(textX, textY, percentage + "%", Color.black);
        }
    }

    @Override
    public void update(Input i) {
        if (i.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
            int mouseX = i.getMouseX();
            int mouseY = i.getMouseY();
            if (mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() - getHeight()*2 && mouseY <= getY() + getHeight()*2) {
                float newValue = (mouseX - getX()) / getWidth();
                setValue(Math.max(0, Math.min(1, newValue))); // Clamp value between 0 and 1
            }
        }
    }

    public void setValue(float newValue){
        this.value=newValue;
    }

    public float getValue(){
        return this.value;
    }

    public void setTTF(TrueTypeFont ttf){
        this.ttf=ttf;
    }
}
