package io.github.anthonyclemens.states;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import io.github.anthonyclemens.GUI.Banner;
import io.github.anthonyclemens.GUI.Buttons.ImageTextButton;
import io.github.anthonyclemens.GameStates;
import io.github.anthonyclemens.Math.TwoDimensionMath;
import io.github.anthonyclemens.Rendering.RenderUtils;
import io.github.anthonyclemens.SharedData;
import io.github.anthonyclemens.Utils;

public class SettingsMenu extends BasicGameState{
    //Variables
    private Input input;
    private Image backgroundImage;
    private Banner titleBanner;
    private final List<ImageTextButton> menuButtons = new ArrayList<>();

    //Constants
    private static final String TITLE_STRING = "Options";
    private static final String MAIN_FONT = "fonts/MedievalTimes.ttf";

    @Override
    public int getID() {
        return 1;
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        // Set background image
        backgroundImage = new Image("textures/Background.png");
        // Create title banner
        Image bannerImage = new Image("textures/GUI/TextField/UI_Paper_Banner_01_Downward.png", false, Image.FILTER_NEAREST);
        titleBanner = new Banner(bannerImage, TITLE_STRING, Utils.getFont(MAIN_FONT, 48f), TwoDimensionMath.getMiddleX(720, container.getWidth()), 10, 720, 251);
        titleBanner.changeYOffset(120f);
        // Load button images
        Image buttonImage = new Image("textures/GUI/TextField/UI_Paper_Textfield_01.png", false, Image.FILTER_NEAREST);
        // Create menu buttons
        ImageTextButton videoSettings = new ImageTextButton(buttonImage, "Video Settings", Utils.getFont(MAIN_FONT, 32f), TwoDimensionMath.getMiddleX(342, container.getWidth()), 300, 342, 114);
        ImageTextButton soundSettings = new ImageTextButton(buttonImage, "Sound Settings", Utils.getFont(MAIN_FONT, 32f), TwoDimensionMath.getMiddleX(342, container.getWidth()), 450, 342, 114);
        ImageTextButton controlSettings = new ImageTextButton(buttonImage, "Control Settings", Utils.getFont(MAIN_FONT, 32f), TwoDimensionMath.getMiddleX(342, container.getWidth()), 600, 342, 114);
        ImageTextButton backButton = new ImageTextButton(buttonImage, "Back", Utils.getFont(MAIN_FONT, 40f), 10, 10, 240, 80);
        menuButtons.clear();
        menuButtons.addAll(List.of(videoSettings,soundSettings,controlSettings,backButton));
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        input = container.getInput();
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        if(SharedData.getLastState() == GameStates.PAUSE_MENU){
            // Render the game state behind the menu
            SharedData.getGameState().render(container, game, g);
            // Draw a translucent overlay
            g.setColor(new org.newdawn.slick.Color(0, 0, 0, 180));
            g.fillRect(0, 0, container.getWidth(), container.getHeight());
        } else {
            RenderUtils.drawBackground(backgroundImage,container);
        }
        
        titleBanner.render(g);
        for(ImageTextButton itb : menuButtons){
            itb.render(g);
        }
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        for(ImageTextButton itb : menuButtons){
            itb.update(input); // Sets the isClicked bool
            if(itb.isClicked()){
                switch(itb.getText()){ // Figure out what button was pressed
                    case "Video Settings"-> SharedData.enterState(GameStates.VIDEO_SETTINGS,game);
                    case "Sound Settings"-> SharedData.enterState(GameStates.SOUND_SETTINGS,game);
                    case "Control Settings"-> SharedData.enterState(GameStates.CONTROL_SETTINGS,game);
                    case "Back"-> SharedData.enterState(GameStates.MAIN_MENU, game);
                }
            }
        }
    }

}
