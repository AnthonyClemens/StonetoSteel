package io.github.anthonyclemens.GUI.Buttons;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;

import io.github.anthonyclemens.GUI.OnClickListener;

public class ColorTextButton extends Button {
    private String buttonString;
    private final TrueTypeFont ttf;
    private final Color textColor;
    private final int vPadding;
    private final int hPadding;

    // Private constructor to enforce use of Builder
    private ColorTextButton(Builder builder) {
        super(builder.x, builder.y,
              builder.width,
              builder.height);
        super.setColor(builder.backgroundColor);
        this.buttonString = builder.buttonString;
        this.textColor = builder.textColor;
        this.ttf = builder.font;
        this.vPadding=builder.vPadding;
        this.hPadding=builder.hPadding;
        this.onClick(builder.onClickListener);
    }

    @Override
    public void render(Graphics g) {
        super.render(g);
        if (this.ttf != null) {
            this.ttf.drawString(super.getRect().getX()+hPadding/2f, super.getRect().getY()+vPadding/2f, this.buttonString, this.textColor);
        }
    }

    public String getText(){
        return this.buttonString;
    }

    public void changeString(String newString){
        this.buttonString=newString;
    }

    // Builder class
    public static class Builder {
        private String buttonString;
        private Color textColor;
        private TrueTypeFont font;
        private Color backgroundColor;
        private float x;
        private float y;
        private float width;
        private float height;
        private int vPadding;
        private int hPadding;
        private OnClickListener onClickListener;

        public Builder buttonString(String buttonString) {
            this.buttonString = buttonString;
            return this;
        }

        public Builder textColor(Color textColor) {
            this.textColor = textColor;
            return this;
        }

        public Builder font(TrueTypeFont font) {
            this.font = font;
            return this;
        }

        public Builder backgroundColor(Color backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder position(float x, float y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder vPadding(int vPadding){
            this.height = (float)vPadding+this.font.getHeight();
            this.vPadding = vPadding;
            return this;
        }

        public Builder hPadding(int hPadding){
            this.width=(float)hPadding+this.font.getWidth(this.buttonString);
            this.hPadding = hPadding;
            return this;
        }

        public Builder padding(int padding){
            this.width=(float)padding+this.font.getWidth(this.buttonString);
            this.hPadding = padding;
            this.height = (float)padding+this.font.getHeight();
            this.vPadding = padding;
            return this;
        }

        public Builder onClick(OnClickListener listener) {
            this.onClickListener = listener;
            return this;
        }

        public ColorTextButton build() {
            return new ColorTextButton(this);
        }
    }
}
