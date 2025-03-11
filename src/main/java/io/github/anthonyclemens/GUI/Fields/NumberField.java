package io.github.anthonyclemens.GUI.Fields;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.TrueTypeFont;

import io.github.anthonyclemens.GUI.GUIElement;

public class NumberField extends GUIElement{
    private boolean clicked;
    private final Color active;
    private final Color notActive;
    private String text;
    private final TrueTypeFont ttf;
    private final int padding;
    private final int maxChars;
    private String bgText;

    public NumberField(float x, float y, int maxChars, Color active, Color notActive, TrueTypeFont ttf, int padding) {
        super(x, y, ttf.getWidth("Z")*maxChars + (float)padding, ttf.getHeight() + (float)padding);
        this.active = active;
        this.notActive = notActive;
        this.text = "";
        this.ttf = ttf;
        this.padding = padding;
        this.maxChars = maxChars;
        this.bgText = "";
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

    public String handleInput(Input input, String currentText, int maxCharacters) {
        int[] keyCodes = {82, 79, 80, 81, 75, 76, 77, 71, 72, 73,  //Number row
                          11,  2,  3,  4,  5,  6,  7,  8,  9, 10}; //Number pad
        char[] characters = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        // Check for backspace to remove the last character
        if (input.isKeyPressed(Input.KEY_BACK) && !currentText.isEmpty()) {
                currentText = currentText.substring(0, currentText.length() - 1);
        }
        //Check for Minus Symbol
        if(currentText.isEmpty()&&(input.isKeyPressed(Input.KEY_MINUS)||input.isKeyPressed(74))){
            currentText += "-";
        }
        // Handle digits
        for (int i = 0; i < keyCodes.length; i++) {
            if (input.isKeyPressed(keyCodes[i])&& currentText.length() < maxCharacters) {
                currentText+=characters[i%characters.length];
            }
        }
        return currentText;
    }

    public int getNum(){
        return (!"".equals(this.text))? Integer.parseInt(this.text): 0;
    }

}
