package io.github.anthonyclemens.GUI.Buttons;

import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Rectangle;

import io.github.anthonyclemens.GUI.GUIElement;
import io.github.anthonyclemens.GUI.OnClickListener;

public class Carousel extends GUIElement {
    private List<String> data;
    private final Button leftButton;
    private final Button rightButton;
    private final TrueTypeFont ttf;
    private final float maxWidth;
    private Color color;
    private Color textColor;
    private int index;
    private final OnClickListener onClickListener;

    // Private constructor for the builder
    private Carousel(Builder builder) {
        super(builder.x, builder.y, 0, 0);
        this.data = builder.data;
        this.ttf = builder.ttf;
        this.color = builder.color;
        this.textColor = Color.black;

        // Determine the longest string for maxWidth
        String longString = "";
        for (String s : builder.data) {
            if (s.length() > longString.length()) {
                longString = s;
            }
        }
        this.maxWidth = builder.ttf.getWidth(longString);

        // Initialize left and right buttons
        this.leftButton = new ImageButton(builder.x, builder.y, builder.leftImage.getWidth(), builder.ttf.getHeight(), builder.leftImage);
        this.rightButton = new ImageButton(builder.x + this.maxWidth + this.leftButton.getWidth(), builder.y, builder.rightImage.getWidth(), builder.ttf.getHeight(), builder.rightImage);
        this.leftButton.setColor(builder.color);
        this.rightButton.setColor(builder.color);

        // Define the bounding rectangle
        this.setWidth(this.leftButton.getWidth() + this.maxWidth + this.rightButton.getWidth());
        this.setHeight(builder.ttf.getHeight());
        this.index = 0;
        this.onClickListener=builder.onClickListener;
    }

    @Override
    public void render(Graphics g) {
        g.setColor(this.color);
        this.leftButton.render(g);
        this.ttf.drawString(
            this.x + this.leftButton.getWidth() + (this.maxWidth - this.ttf.getWidth(this.data.get(index))) / 2,
            this.y,
            this.data.get(this.index),
            this.textColor
        );
        this.rightButton.render(g);
    }

    @Override
    public void update(Input input) {
        this.rightButton.update(input);
        this.leftButton.update(input);
        if (this.rightButton.isClicked()) {
            this.nextVal();
            this.onClick();
        }
        if (this.leftButton.isClicked()) {
            this.prevVal();
            this.onClick();
        }
    }

    private void onClick() {
        if (onClickListener != null) {
            onClickListener.onClick(this.getValue());
        }
    }

    //Setters
    @Override
    public void move(float x, float y) {
        this.x = x;
        this.y = y;
        this.leftButton.move(x, y);
        this.rightButton.move(x + this.leftButton.getWidth() + this.maxWidth, y);
    }

    public void setData(List<String> newData){
        this.data = newData;
    }

    public void setTextColor(Color nc){
        this.textColor=nc;
    }

    public void setButtonsColor(Color nc){
        this.color=nc;
    }

    //Getters
    public String getValue(){
        return this.data.get(this.index);
    }

    public Rectangle getLeftButtonRect() {
        return this.leftButton.getRect();
    }

    public Rectangle getRightButtonRect() {
        return this.rightButton.getRect();
    }

    public TrueTypeFont getFont() {
        return this.ttf;
    }

    //Function Methods
    public void nextVal() {
        if (this.index < this.data.size() - 1) {
            this.index++;
        }
    }

    public void prevVal() {
        if (this.index > 0) {
            this.index--;
        }
    }

    // Builder class
    public static class Builder {
        private List<String> data;
        private Image leftImage;
        private Image rightImage;
        private TrueTypeFont ttf;
        private float x;
        private float y;
        private Color color;
        private OnClickListener onClickListener;

        public Builder data(List<String> data) {
            this.data = data;
            return this;
        }

        public Builder leftImage(Image leftImage) {
            this.leftImage = leftImage;
            return this;
        }

        public Builder rightImage(Image rightImage) {
            this.rightImage = rightImage;
            return this;
        }

        public Builder font(TrueTypeFont ttf) {
            this.ttf = ttf;
            return this;
        }

        public Builder position(float x, float y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder color(Color color) {
            this.color = color;
            return this;
        }

        public Builder onClick(OnClickListener listener) {
            this.onClickListener = listener;
            return this;
        }

        public Carousel build() {
            return new Carousel(this);
        }
    }
}
