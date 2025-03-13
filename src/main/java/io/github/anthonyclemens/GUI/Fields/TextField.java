package io.github.anthonyclemens.GUI.Fields;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.TrueTypeFont;

import io.github.anthonyclemens.GUI.GUIElement;

public class TextField extends GUIElement{
    private boolean clicked;
    private final Color active;
    private final Color notActive;
    private String text;
    private final TrueTypeFont ttf;
    private final int padding;
    private final int maxChars;
    private String bgText;

    public TextField(float x, float y, int maxChars, Color active, Color notActive, TrueTypeFont ttf, int padding) {
        super(x, y, ttf.getWidth("Z")*maxChars + (float)padding, ttf.getHeight() + (float)padding);
        this.active = active;
        this.notActive = notActive;
        this.text = "";
        this.ttf = ttf;
        this.padding = padding;
        this.maxChars = maxChars;
    }

    public void setBgText(String str){
        this.bgText=str;
    }

    @Override
    public void render(Graphics g) {
        if(this.clicked){
            g.setColor(this.active);
        }else{
            g.setColor(this.notActive);
        }
        g.fill(this.getRect());
        g.setColor(Color.black);
        g.draw(this.getRect());
        if("".equals(text)){
            ttf.drawString(x+padding/2f, y+padding/2f, bgText, Color.gray);
        }
        ttf.drawString(x+padding/2f, y+padding/2f, text, Color.black);
    }

    @Override
    public void update(Input input) {
        this.clicked = (this.getRect().contains(input.getMouseX(), input.getMouseY()) && input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) ? !this.clicked : this.clicked;
        if(this.clicked){
            this.text = handleInput(input, this.text, this.maxChars);
        }
    }

    public String handleInput(Input input, String currentString, int maxCharacters) {
        StringBuilder inputString = new StringBuilder(currentString);
        // Check for backspace to remove the last character
        if (input.isKeyPressed(Input.KEY_BACK) && !inputString.isEmpty()) {
                String afterBack = inputString.substring(0, inputString.length() - 1);
                inputString.delete(0, inputString.length());
                inputString.append(afterBack);
            }

        // Loop through possible character key codes
        for (int key = Input.KEY_A; key <= Input.KEY_Z; key++) {
            if (input.isKeyPressed(key)) {
                char c = (char) ('a' + (key - Input.KEY_A)); // Convert to character
                if (input.isKeyDown(Input.KEY_LSHIFT) || input.isKeyDown(Input.KEY_RSHIFT)) {
                    c = Character.toUpperCase(c); // Handle uppercase letters
                }
                if (inputString.length() < maxCharacters) {
                    inputString.append(c);
                }
            }
        }
        // Handle digits
        for (int key = Input.KEY_0; key <= Input.KEY_9; key++) {
            if (input.isKeyPressed(key)) {
                char c = (char) ('0' + (key - Input.KEY_0)); // Convert to character
                if (inputString.length() < maxCharacters) {
                    inputString.append(c);
                }
            }
        }
        // Handle spaces
        if (input.isKeyPressed(Input.KEY_SPACE) && inputString.length() < maxCharacters) {
                inputString.append(" ");
            }
        return inputString.toString();
    }

    public String getText(){
        return this.text;
    }

}
