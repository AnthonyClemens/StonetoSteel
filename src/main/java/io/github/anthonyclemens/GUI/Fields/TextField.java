package io.github.anthonyclemens.GUI.Fields;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.TrueTypeFont;

public class TextField extends Field {
    private boolean focused;
    private boolean justFocused;
    private final Color active;
    private final Color notActive;
    private final TrueTypeFont ttf;
    private final int padding;
    private final int maxChars;
    private String bgText = "";
    private String value = "";

    public TextField(float x, float y, int maxChars, Color active, Color notActive, TrueTypeFont ttf, int padding) {
        super(x, y, ttf.getWidth("Z") * maxChars + padding, ttf.getHeight() + padding);
        this.active = active;
        this.notActive = notActive;
        this.ttf = ttf;
        this.padding = padding;
        this.maxChars = maxChars;
    }

    public void setBgText(String str) {
        this.bgText = str;
    }

    @Override
    public void render(Graphics g) {
        g.setColor(focused ? active : notActive);
        g.fill(this.getRect());
        g.setColor(Color.black);
        g.draw(this.getRect());

        String displayText = value.isEmpty() ? bgText : value;
        ttf.drawString(getX() + padding / 2f, getY() + padding / 2f, displayText, Color.black);
    }

    @Override
    public void update(Input input) {
        boolean clickedInside = this.getRect().contains(input.getMouseX(), input.getMouseY());
        boolean mousePressed = input.isMousePressed(Input.MOUSE_LEFT_BUTTON);

        if (mousePressed) {
            if (clickedInside && !focused) {
                focused = true;
                justFocused = true; // Delay input handling
            } else if (!clickedInside && focused) {
                focused = false; // Clicked outside, lose focus
            }
        }
    }

    public void handleInput(Input input) {
        if (!focused) return;

        if (justFocused) {
            justFocused = false;
            return;
        }

        StringBuilder inputString = new StringBuilder(this.value);

        // Backspace
        if (input.isKeyPressed(Input.KEY_BACK) && inputString.length() > 0) {
            inputString.deleteCharAt(inputString.length() - 1);
        }

        // Letters A-Z
        for (int key = Input.KEY_A; key <= Input.KEY_Z; key++) {
            if (input.isKeyPressed(key) && inputString.length() < maxChars) {
                char c = (char) ('a' + (key - Input.KEY_A));
                if (input.isKeyDown(Input.KEY_LSHIFT) || input.isKeyDown(Input.KEY_RSHIFT)) {
                    c = Character.toUpperCase(c);
                }
                inputString.append(c);
            }
        }

        // Digits 0-9
        for (int key = Input.KEY_0; key <= Input.KEY_9; key++) {
            if (input.isKeyPressed(key) && inputString.length() < maxChars) {
                inputString.append((char) ('0' + (key - Input.KEY_0)));
            }
        }

        // Space
        if (input.isKeyPressed(Input.KEY_SPACE) && inputString.length() < maxChars) {
            inputString.append(' ');
        }

        this.value = inputString.toString();
    }

    public String getText() {
        return value;
    }

    public boolean isActive() {
        return focused;
    }

    public void setActive(boolean active) {
        this.focused = active;
        this.justFocused = active; // Prevent key flush
    }

}