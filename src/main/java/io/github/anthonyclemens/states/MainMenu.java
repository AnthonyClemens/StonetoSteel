package io.github.anthonyclemens.states;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import io.github.anthonyclemens.GUI.Banner;
import io.github.anthonyclemens.GUI.Buttons.ImageTextButton;
import io.github.anthonyclemens.Math.TwoDimensionMath;
import io.github.anthonyclemens.Rendering.RenderUtils;
import io.github.anthonyclemens.Settings;
import io.github.anthonyclemens.Sound.JukeBox;
import io.github.anthonyclemens.Utils;

public class MainMenu extends BasicGameState{
    //Variables
    private Input input;

    //Constants
    private static final String TITLE_STRING = "Stone to Steel";
    private static final String MAIN_FONT = "fonts/MedievalTimes.ttf";
    private final List<String> menuMusic = new ArrayList<>(Arrays.asList("music/menu/Lovely.ogg","music/menu/WalkingHome.ogg"));
    public static JukeBox menuJukeBox;
    private Image backgroundImage;
    private Banner titleBanner;
    private final List<ImageTextButton> menuButtons = new ArrayList<>();

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        Settings settings = Settings.getInstance();
        // Load music and play menu music
        menuJukeBox.setVolume(settings.getMusicVolume());
        menuJukeBox.playRandomSong("menu");
        // Set background image
        backgroundImage = new Image("textures/Background.png");
        // Create title banner
        Image bannerImage = new Image("textures/GUI/TextField/UI_Paper_Banner_01_Downward.png");
        bannerImage.setFilter(Image.FILTER_NEAREST);
        titleBanner = new Banner(bannerImage, TITLE_STRING, Utils.getFont(MAIN_FONT, 60f), TwoDimensionMath.getMiddleX(792, container.getWidth()), 10, 820, 280);
        titleBanner.changeYOffset(120f);
        // Load button images
        Image buttonImage = new Image("textures/GUI/TextField/UI_Paper_Textfield_01.png");
        buttonImage.setFilter(Image.FILTER_NEAREST);
        // Create menu buttons
        ImageTextButton startGame = new ImageTextButton(buttonImage, "Start Game", Utils.getFont(MAIN_FONT, 36f), TwoDimensionMath.getMiddleX(311, container.getWidth()), 300, 311, 104);
        ImageTextButton options = new ImageTextButton(buttonImage, "Options", Utils.getFont(MAIN_FONT, 32f), TwoDimensionMath.getMiddleX(248, container.getWidth()), 420, 248, 82);
        ImageTextButton exit = new ImageTextButton(buttonImage, "Exit", Utils.getFont(MAIN_FONT, 32f), TwoDimensionMath.getMiddleX(193, container.getWidth()), 520, 193, 64);
        ImageTextButton credits = new ImageTextButton(buttonImage, "Credits", Utils.getFont(MAIN_FONT, 24f), TwoDimensionMath.getMiddleX(168, container.getWidth()), 600, 168, 56);
        menuButtons.clear();
        menuButtons.addAll(List.of(startGame,options,exit,credits));
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        input = container.getInput();
        menuJukeBox = new JukeBox();
        menuJukeBox.addSongs("menu", menuMusic);

        Image cursor = new Image("cursors/Icon_Cursor_03c.png");
        container.setMouseCursor(cursor, 0, 0);
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        RenderUtils.drawBackground(backgroundImage,container); // Render the background to fit screen (no stretching)
        titleBanner.render(g); // Render the Title banner
        for(ImageTextButton itb : menuButtons){ // Render all of the buttons
            itb.render(g);
        }
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        for(ImageTextButton itb : menuButtons){
            itb.update(input); // Sets the isClicked bool
            if(itb.isClicked()){
                switch(itb.getText()){ // Figure out what button was pressed
                    case "Start Game"-> game.enterState(2);
                    case "Options"-> game.enterState(1);
                    case "Exit"-> container.exit();
                    case "Credits"->Log.debug("TODO");
                }
            }
        }
    }
}
