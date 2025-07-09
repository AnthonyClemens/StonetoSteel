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
import org.newdawn.slick.util.Log;

import io.github.anthonyclemens.GUI.Banner;
import io.github.anthonyclemens.GUI.Buttons.ImageTextButton;
import io.github.anthonyclemens.GameStates;
import io.github.anthonyclemens.Math.TwoDimensionMath;
import io.github.anthonyclemens.Rendering.RenderUtils;
import io.github.anthonyclemens.SharedData;
import io.github.anthonyclemens.Utils;
import io.github.anthonyclemens.utils.SaveLoadManager;

public class NewGame extends BasicGameState {
    // Variables
    private Input input;
    private Image backgroundImage;
    private Banner titleBanner;
    private final List<ImageTextButton> menuButtons = new ArrayList<>();
    private ImageTextButton backButton;
    private ImageTextButton deleteWorldButton;
    private boolean delete = false;

    // Constants
    private static final String MAIN_FONT = "fonts/MedievalTimes.ttf";
    private static final String TITLE_STRING = "World Selection";
    private static final String EMPTY = "- empty -";
    private static final String[] SAVES = {
        "saves/Save1.sav",
        "saves/Save2.sav",
        "saves/Save3.sav",
        "saves/Save4.sav",
        "saves/Save5.sav"
    };

    @Override
    public int getID() {
        return 2;
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        input = container.getInput();
        // Set background image
        backgroundImage = new Image("textures/Background.png");
        // Create title banner
        Image bannerImage = new Image("textures/GUI/TextField/UI_Paper_Banner_01_Downward.png", false, Image.FILTER_NEAREST);
        titleBanner = new Banner(
            bannerImage,
            TITLE_STRING,
            Utils.getFont(MAIN_FONT, 48f),
            TwoDimensionMath.getMiddleX(720, container.getWidth()),
            10,
            720,
            251
        );
        titleBanner.changeYOffset(120f);
        // Load button images
        Image buttonImage = new Image("textures/GUI/TextField/UI_Paper_Textfield_01.png", false, Image.FILTER_NEAREST);

        // Create menu buttons
        backButton = new ImageTextButton(
            buttonImage, "Back", Utils.getFont(MAIN_FONT, 40f),
            10, 10, 240, 80
        );
        deleteWorldButton = new ImageTextButton(
            buttonImage, "Delete World", Utils.getFont(MAIN_FONT, 32f),
            TwoDimensionMath.getMiddleX(342, container.getWidth()), container.getHeight()-120f, 342, 60
        );
        ImageTextButton slot1 = new ImageTextButton(
            buttonImage, "", Utils.getFont(MAIN_FONT, 32f),
            TwoDimensionMath.getMiddleX(342, container.getWidth()), 220, 342, 60
        );
        slot1.setName("Slot1");
        ImageTextButton slot2 = new ImageTextButton(
            buttonImage, "", Utils.getFont(MAIN_FONT, 32f),
            TwoDimensionMath.getMiddleX(342, container.getWidth()), 300, 342, 60
        );
        slot2.setName("Slot2");
        ImageTextButton slot3 = new ImageTextButton(
            buttonImage, "", Utils.getFont(MAIN_FONT, 32f),
            TwoDimensionMath.getMiddleX(342, container.getWidth()), 380, 342, 60
        );
        slot3.setName("Slot3");
        ImageTextButton slot4 = new ImageTextButton(
            buttonImage, "", Utils.getFont(MAIN_FONT, 32f),
            TwoDimensionMath.getMiddleX(342, container.getWidth()), 460, 342, 60
        );
        slot4.setName("Slot4");
        ImageTextButton slot5 = new ImageTextButton(
            buttonImage, "", Utils.getFont(MAIN_FONT, 32f),
            TwoDimensionMath.getMiddleX(342, container.getWidth()), 540, 342, 60
        );
        slot5.setName("Slot5");
        menuButtons.clear();
        menuButtons.addAll(List.of(slot1,slot2,slot3,slot4,slot5,backButton,deleteWorldButton));
        initButtons();
    }

    private void initButtons(){
        for(int i = 0; i < SAVES.length; i++){
            menuButtons.get(i).setText((!SaveLoadManager.getSize(SAVES[i]).equals("0")) ? "World "+(i+1)+" (" + SaveLoadManager.getSize(SAVES[i]) +")" : EMPTY);
        }
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        RenderUtils.drawBackground(backgroundImage, container);
        titleBanner.render(g);
        for (ImageTextButton itb : menuButtons) {
            itb.render(g);
        }
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        if(delete){
            for (ImageTextButton button : menuButtons) {
                if (button.isClicked()) {
                    String name = button.getName();
                    if (name.startsWith("Slot")) {
                        switch (name) {
                            case "Slot1" -> SaveLoadManager.deleteSave(SAVES[0]);
                            case "Slot2" -> SaveLoadManager.deleteSave(SAVES[1]);
                            case "Slot3" -> SaveLoadManager.deleteSave(SAVES[2]);
                            case "Slot4" -> SaveLoadManager.deleteSave(SAVES[3]);
                            case "Slot5" -> SaveLoadManager.deleteSave(SAVES[4]);
                        }
                        initButtons();
                        return;
                    } else if (name.equals("Back")) {
                        delete = false;
                        titleBanner.setText(TITLE_STRING);
                        deleteWorldButton.setRender(true);
                        return;
                    }
                }
            }
        }
        for (ImageTextButton button : menuButtons) {
            button.update(input);
            if (button.isClicked()) {
                String name = button.getName();
                if (name.startsWith("Slot")) {
                    switch (name) {
                        case "Slot1" -> {
                            SharedData.setSaveFilePath(SAVES[0]);
                        }
                        case "Slot2" -> {
                            SharedData.setSaveFilePath(SAVES[1]);
                        }
                        case "Slot3" -> {
                            SharedData.setSaveFilePath(SAVES[2]);
                        }
                        case "Slot4" -> {
                            SharedData.setSaveFilePath(SAVES[3]);
                        }
                        case "Slot5" -> {
                            SharedData.setSaveFilePath(SAVES[4]);
                        }
                    }
                    if (!SaveLoadManager.getSize(SharedData.getSaveFilePath()).equals("0")) {
                        SharedData.setNewGame(false);
                        Log.debug("Loading existing game from: " + SharedData.getSaveFilePath() + " Size: " + SaveLoadManager.getSize(SharedData.getSaveFilePath()));
                    } else {
                        SharedData.setNewGame(true);
                        Log.debug("Starting new game with save file: " + SharedData.getSaveFilePath());
                    }
                    SharedData.enterState(GameStates.GAME, game);
                }else if (name.equals("Delete World")) {
                    delete = true;
                    titleBanner.setText("Delete World?");
                    backButton.setText("Cancel");
                    deleteWorldButton.setRender(false);
                }else if (name.equals("Back")) {
                    SharedData.enterState(GameStates.MAIN_MENU,game);
                }
            }
        }
    }
}
