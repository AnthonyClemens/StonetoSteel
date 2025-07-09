package io.github.anthonyclemens.states;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
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
import io.github.anthonyclemens.SharedData;
import io.github.anthonyclemens.Utils;
import io.github.anthonyclemens.utils.SaveLoadManager;

public class PauseMenu extends BasicGameState {
    private ImageTextButton saveButton, loadButton, videoButton, soundButton, controlButton, exitButton, resumeButton;
    private final List<ImageTextButton> menuButtons = new ArrayList<>();
    private Banner titleBanner;
    private static final String TITLE_STRING = "Paused";
    private static final String MAIN_FONT = "fonts/MedievalTimes.ttf";
    private Image buttonImage, bannerImage;
    private SaveLoadManager saveLoadManager = new SaveLoadManager();

    @Override
    public int getID() {
        return 101;
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        // Load images and fonts
        buttonImage = new Image("textures/GUI/TextField/UI_Paper_Textfield_01.png", false, Image.FILTER_NEAREST);
        bannerImage = new Image("textures/GUI/TextField/UI_Paper_Banner_01_Downward.png", false, Image.FILTER_NEAREST);
        titleBanner = new Banner(bannerImage, TITLE_STRING, Utils.getFont(MAIN_FONT, 48f), TwoDimensionMath.getMiddleX(720, container.getWidth()), 10, 720, 251);
        titleBanner.changeYOffset(120f);

        // Create buttons
        float midX = (int) TwoDimensionMath.getMiddleX(342, container.getWidth());
        int y = 250, step = 70;
        saveButton = new ImageTextButton(buttonImage, "Save", Utils.getFont(MAIN_FONT, 32f), midX, y, 342, 60); y += step;
        loadButton = new ImageTextButton(buttonImage, "Load", Utils.getFont(MAIN_FONT, 32f), midX, y, 342, 60); y += step;
        videoButton = new ImageTextButton(buttonImage, "Video Settings", Utils.getFont(MAIN_FONT, 32f), midX, y, 342, 60); y += step;
        soundButton = new ImageTextButton(buttonImage, "Sound Settings", Utils.getFont(MAIN_FONT, 32f), midX, y, 342, 60); y += step;
        controlButton = new ImageTextButton(buttonImage, "Control Settings", Utils.getFont(MAIN_FONT, 32f), midX, y, 342, 60); y += step;
        exitButton = new ImageTextButton(buttonImage, "Save and Exit", Utils.getFont(MAIN_FONT, 32f), midX, y, 342, 60);
        resumeButton = new ImageTextButton(buttonImage, "Resume", Utils.getFont(MAIN_FONT, 40f), 10, 10, 240, 80);

        menuButtons.clear();
        menuButtons.addAll(List.of(saveButton, loadButton, videoButton, soundButton, controlButton, exitButton, resumeButton));
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        // nothing needed
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        // Render the game state behind the menu
        SharedData.getGameState().render(container, game, g);
        // Draw a translucent overlay
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, container.getWidth(), container.getHeight());
        // Draw menu
        titleBanner.render(g);
        for (ImageTextButton itb : menuButtons) {
            itb.render(g);
        }
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        Input input = container.getInput();
        for (ImageTextButton itb : menuButtons) {
            itb.update(input);
            if (itb.isClicked()) {
                switch (itb.getText()) {
                    case "Save" -> SharedData.getGameState().saveGame(SharedData.getSaveFilePath());
                    case "Load" -> {
                        SharedData.setLoadingSave(true);
                        SharedData.setNewGame(false);
                        SharedData.enterState(GameStates.GAME, game);
                    }
                    case "Video Settings" -> SharedData.enterState(GameStates.VIDEO_SETTINGS, game);
                    case "Sound Settings" -> SharedData.enterState(GameStates.SOUND_SETTINGS, game);
                    case "Control Settings" -> SharedData.enterState(GameStates.CONTROL_SETTINGS, game);
                    case "Save and Exit" -> {
                        SharedData.getGameState().saveGame(SharedData.getSaveFilePath());
                        SharedData.setHotstart(false);
                        SharedData.enterState(GameStates.MAIN_MENU, game);
                    }
                    case "Resume" -> SharedData.enterState(GameStates.GAME, game);
                }
            }
        }
        // Resume game if ESC is pressed again
        if (input.isKeyPressed(Input.KEY_ESCAPE)) {
            SharedData.enterState(GameStates.GAME, game);
        }
    }
}
