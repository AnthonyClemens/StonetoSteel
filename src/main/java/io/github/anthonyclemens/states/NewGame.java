package io.github.anthonyclemens.states;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import io.github.anthonyclemens.GUI.Buttons.ColorTextButton;
import io.github.anthonyclemens.GUI.Fields.NumberField;
import io.github.anthonyclemens.GUI.Fields.TextField;
import io.github.anthonyclemens.GUI.GUIElement;
import io.github.anthonyclemens.Math.TwoDimensionMath;
import io.github.anthonyclemens.Settings;

public class NewGame extends BasicGameState{
    //Variables
    private Settings settings;
    private Input input;
    private ColorTextButton[] sectionButtons;
    private final List<GUIElement> fields = new ArrayList<>();
    private static final String TITLE_STRING = "New Game";
    private float titleX;



    //Constants
    private static final int SPACING = 64;
    private static final int PADDING = 6;
    private final TrueTypeFont titleF = new TrueTypeFont(new Font("Courier", Font.BOLD, 32), true);
    private final TrueTypeFont menuOptionsF = new TrueTypeFont(new Font("Courier", Font.PLAIN, 32),true);
    private final String[] menuOptions = {"Back", "Start Game"};

    @Override
    public int getID() {
        return 2;
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game){
        input = container.getInput();
        settings = Settings.getInstance();
        //Create the Options Tabs
        sectionButtons = new ColorTextButton[menuOptions.length];
        for(int i=0; i<menuOptions.length; i++){
            if(!"Start Game".equals(menuOptions[i])){
                sectionButtons[i] = new ColorTextButton.Builder()
                .textColor(Color.black)
                .backgroundColor(Color.darkGray)
                .font(menuOptionsF)
                .buttonString(menuOptions[i])
                .position(20, i*SPACING+200f)
                .padding(PADDING)
                .build();
            }else{
                sectionButtons[i] = new ColorTextButton.Builder()
                .textColor(Color.black)
                .backgroundColor(Color.green)
                .font(menuOptionsF)
                .buttonString(menuOptions[i])
                .position(20, i*SPACING+200f)
                .padding(PADDING)
                .build();
            }
        }
        //Move all of the options to the center
        for(int i=0; i<menuOptions.length; i++){
            sectionButtons[i].centerX(container,container.getHeight()-SPACING*(i+1));
        }

        TextField nameField = new TextField(0, 0, 20, Color.white, Color.lightGray, menuOptionsF, PADDING);
        nameField.setBgText("Enter World Name");
        nameField.centerX(container, 164);

        NumberField seedField = new NumberField(0, 0, 20, Color.white, Color.lightGray, menuOptionsF, PADDING);
        seedField.setBgText("Enter a Seed");
        seedField.centerX(container, 228);
        //fields.add(nameField);
        fields.add(seedField);
        titleX=TwoDimensionMath.getMiddleX(titleF.getWidth(TITLE_STRING), container.getWidth());
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        g.setBackground(Color.gray);
        titleF.drawString(titleX, 100, TITLE_STRING, Color.black);

        for(GUIElement gui: fields){
            gui.render(g);
        }
        for(GUIElement gui: sectionButtons){
            gui.render(g);
        }
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        for(GUIElement gui: fields){
            gui.update(input);
        }
        // Handle button actions
        for (ColorTextButton button : sectionButtons) {
            button.update(input);
            if (button.isClicked()) {
                switch (button.getText()) {
                    case "Start Game" -> {
                        MainMenu.menuJukeBox.stopMusic();
                        game.enterState(99);
                    }
                    case "Back" -> game.enterState(0);
                    default -> {}
                }
            }
        }
    }
}
