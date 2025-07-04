package io.github.anthonyclemens.GUI.Fields;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.TrueTypeFont;

import io.github.anthonyclemens.GUI.GUIElement;

/**
 * NumberField is a GUI element for numeric input, supporting both number row and numpad.
 */
public class NumberField extends GUIElement{
    private boolean focused;
    private final Color active;
    private final Color notActive;
    private final TrueTypeFont ttf;
    private final int padding;
    private final int maxChars;
    private String bgText;
    private String value;

    /**
     * Constructs a NumberField.
     * @param x        X position.
     * @param y        Y position.
     * @param maxChars Maximum number of characters.
     * @param active   Color when focused.
     * @param notActive Color when not focused.
     * @param ttf      Font.
     * @param padding  Padding.
     */
    public NumberField(float x, float y, int maxChars, Color active, Color notActive, TrueTypeFont ttf, int padding) {
        super(x, y, ttf.getWidth("Z")*maxChars + (float)padding, ttf.getHeight() + (float)padding);
        this.active = active;
        this.notActive = notActive;
        this.value = "";
        this.ttf = ttf;
        this.padding = padding;
        this.maxChars = maxChars;
        this.bgText = "";
    }

    /**
     * Sets the background text (placeholder).
     * @param str Placeholder text.
     */
    public void setBgText(String str){
        this.bgText=str;
    }

    @Override
    public void render(Graphics g) {
        g.setColor(this.focused ? this.active : this.notActive);
        g.fill(this.r);
        g.setColor(Color.black);
        g.draw(this.r);
        if("".equals(this.value)){
            ttf.drawString(getX()+padding/2f, getY()+padding/2f, bgText, Color.gray);
        }
        ttf.drawString(getX()+padding/2f, getY()+padding/2f, this.value, Color.black);
    }

    @Override
    public void update(Input input) {
        // Focus logic like TextField
        this.focused = (this.getRect().contains(input.getMouseX(), input.getMouseY()) && input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) ? !this.focused : this.focused;
        if(this.focused){
            this.value = handleInput(input, this.value, this.maxChars);
        }
    }

    /**
     * Handles numeric input from both number row and numpad, and allows minus sign.
     */
    private String handleInput(Input input, String currentText, int maxCharacters) {
        StringBuilder inputString = new StringBuilder(currentText);
        // Backspace removes last character
        if (input.isKeyPressed(Input.KEY_BACK) && !inputString.isEmpty()) {
            String afterBack = inputString.substring(0, inputString.length() - 1);
            inputString.delete(0, inputString.length());
            inputString.append(afterBack);
        }
        // Allow minus sign at start
        if (input.isKeyPressed(Input.KEY_MINUS) || input.isKeyPressed(74)) {
            if (inputString.length() == 0 && maxCharacters > 0) {
                inputString.append("-");
            }
        }
        // Handle digits from number row
        for (int key = Input.KEY_0; key <= Input.KEY_9; key++) {
            if (input.isKeyPressed(key) && inputString.length() < maxCharacters) {
                char c = (char) ('0' + (key - Input.KEY_0));
                inputString.append(c);
            }
        }
        // Handle digits from numpad
        for (int key = Input.KEY_NUMPAD0; key <= Input.KEY_NUMPAD9; key++) {
            if (input.isKeyPressed(key) && inputString.length() < maxCharacters) {
                char c = (char) ('0' + (key - Input.KEY_NUMPAD0));
                inputString.append(c);
            }
        }
        return inputString.toString();
    }

    /**
     * Gets the numeric value entered, or 0 if empty or invalid.
     * @return Parsed integer value.
     */
    public int getNum(){
        try {
            return (!"".equals(this.value) && !this.value.equals("-")) ? Integer.parseInt(this.value) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
