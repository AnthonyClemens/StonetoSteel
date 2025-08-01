package io.github.anthonyclemens.states;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import com.codedisaster.steamworks.SteamAPI;

import io.github.anthonyclemens.GUI.Banner;
import io.github.anthonyclemens.GUI.Buttons.ImageTextButton;
import io.github.anthonyclemens.GameStates;
import io.github.anthonyclemens.Math.TwoDimensionMath;
import io.github.anthonyclemens.Rendering.RenderUtils;
import io.github.anthonyclemens.SharedData;
import io.github.anthonyclemens.Utils;

public class MultiplayerMenu extends BasicGameState{

    private Image backgroundImage;
    private Banner titleBanner;
    private final List<ImageTextButton> menuButtons = new ArrayList<>();
    private final List<TextField> menuFields = new ArrayList<>();

    private static final String TITLE_STRING = "Multiplayer";
    private static final String MAIN_FONT = "fonts/MedievalTimes.ttf";


    @Override
    public int getID() {
        return GameStates.MULTIPLAYER_MENU.getID();
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException{
        backgroundImage = new Image("textures/Background.png");
        // Create title banner
        Image bannerImage = new Image("textures/GUI/TextField/UI_Paper_Banner_01_Downward.png");
        bannerImage.setFilter(Image.FILTER_NEAREST);
        titleBanner = new Banner(bannerImage, TITLE_STRING, Utils.getFont(MAIN_FONT, 48f), TwoDimensionMath.getMiddleX(720, container.getWidth()), 10, 720, 251);
        titleBanner.changeYOffset(120f);
        // Load button images
        Image buttonImage = new Image("textures/GUI/TextField/UI_Paper_Textfield_01.png");
        buttonImage.setFilter(Image.FILTER_NEAREST);
        // Create menu buttons
        ImageTextButton backButton = new ImageTextButton(buttonImage, "Back", Utils.getFont(MAIN_FONT, 40f), 10, 10, 240, 80);
        // IP address TextField
        TextField ipAddress = new TextField(
            container,
            Utils.getFont(MAIN_FONT, 48f),
            (int)TwoDimensionMath.getMiddleX(600, container.getWidth()),
            300,
            600,
            Utils.getFont(MAIN_FONT, 48f).getHeight() + 4
        );
        ipAddress.setBackgroundColor(Color.lightGray);
        ipAddress.setBorderColor(Color.green);
        ipAddress.setTextColor(Color.black);

        ipAddress.setMaxLength(20);
        ipAddress.setFocus(true);

        ImageTextButton connectButton = new ImageTextButton(buttonImage, "Connect", Utils.getFont(MAIN_FONT, 40f), TwoDimensionMath.getMiddleX(312, container.getWidth()), 600,312, 104);
        menuFields.clear();
        menuFields.addAll(List.of(ipAddress));
        menuButtons.clear();
        menuButtons.addAll(List.of(backButton,connectButton));
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        Log.debug("Multiplayer Menu Initialized");
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        RenderUtils.drawBackground(backgroundImage,container);
        titleBanner.render(g);
        for(ImageTextButton itb : menuButtons){
            itb.render(g);
        }
        for(TextField tf : menuFields){
            g.setColor(Color.lightGray);
            tf.render(container,g);
        }
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        Input input = container.getInput();
        for (TextField tf : menuFields) {
            tf.setFocus(true);
        }
        for (ImageTextButton itb : menuButtons) {
            itb.update(container.getInput());
            if (!itb.isClicked()) continue;

            switch (itb.getText()) {
                case "Back" -> SharedData.enterState(GameStates.MAIN_MENU, game);
                case "Connect" -> {
                    SharedData.setIPAddress(menuFields.get(0).getText());
                    SharedData.enterState(GameStates.MULTIPLAYER, game);
                }
            }
        }
        SteamAPI.runCallbacks();
    }
}
