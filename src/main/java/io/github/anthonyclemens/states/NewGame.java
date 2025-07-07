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
import io.github.anthonyclemens.GUI.Fields.NumberField;
import io.github.anthonyclemens.GUI.Fields.TextField;
import io.github.anthonyclemens.GUI.GUIElement;
import io.github.anthonyclemens.GameStates;
import io.github.anthonyclemens.Math.TwoDimensionMath;
import io.github.anthonyclemens.Rendering.RenderUtils;
import io.github.anthonyclemens.SharedData;
import io.github.anthonyclemens.Utils;

public class NewGame extends BasicGameState {
    // Variables
    private Input input;
    private Image backgroundImage;
    private Banner titleBanner;
    private final List<ImageTextButton> menuButtons = new ArrayList<>();
    private final List<GUIElement> fields = new ArrayList<>();

    // Constants
    private static final String TITLE_STRING = "New Game";
    private static final String MAIN_FONT = "fonts/MedievalTimes.ttf";

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
        ImageTextButton startGame = new ImageTextButton(
            buttonImage, "Start Game", Utils.getFont(MAIN_FONT, 32f),
            TwoDimensionMath.getMiddleX(342, container.getWidth()), 600, 342, 114
        );
        ImageTextButton backButton = new ImageTextButton(
            buttonImage, "Back", Utils.getFont(MAIN_FONT, 40f),
            10, 10, 240, 80
        );
        menuButtons.clear();
        menuButtons.addAll(List.of(startGame, backButton));

        // Create fields
        fields.clear();
        TextField nameField = new TextField(
            TwoDimensionMath.getMiddleX(400, container.getWidth()), 300, 20,
            Color.white, Color.lightGray, Utils.getFont(MAIN_FONT, 32f), 8
        );
        nameField.setBgText("Enter World Name");
        fields.add(nameField);

        NumberField seedField = new NumberField(
            TwoDimensionMath.getMiddleX(400, container.getWidth()), 400, 20,
            Color.white, Color.lightGray, Utils.getFont(MAIN_FONT, 32f), 8
        );
        seedField.setBgText("Enter a Seed");
        fields.add(seedField);
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        RenderUtils.drawBackground(backgroundImage, container);
        titleBanner.render(g);
        for (GUIElement field : fields) {
            field.render(g);
        }
        for (ImageTextButton itb : menuButtons) {
            itb.render(g);
        }
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        for (GUIElement field : fields) {
            field.update(input);
        }
        for (ImageTextButton button : menuButtons) {
            button.update(input);
            if (button.isClicked()) {
                switch (button.getText()) {
                    case "Start Game" -> {
                        // Get the seed from the seedField and set SharedData.seed
                        var seedField = (NumberField) fields.get(1);
                        SharedData.setSeed(seedField.getNum());
                        MainMenu.menuJukeBox.stopMusic();
                        SharedData.enterState(GameStates.GAME, game);
                    }
                    case "Back" -> SharedData.enterState(GameStates.MAIN_MENU,game);
                }
            }
        }
    }
}
