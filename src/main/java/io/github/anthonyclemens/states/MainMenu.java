package io.github.anthonyclemens.states;

import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import io.github.anthonyclemens.Settings;

public class MainMenu extends BasicGameState{
    //Variables
    private Rectangle[] buttons;
    private int selectedIndex = 0;
    private Settings settings;
    private Input input;
    private Music menuMusic;
    private float musicVolume;
    private boolean fadingIn = true;

    //Constants
    private static final String TITLE_STRING = "Isometric Game";
    private static final float SPACING = 64;
    private static final float PADDING = 12;
    private final TrueTypeFont titleF = new TrueTypeFont(new Font("Courier", Font.BOLD, 32), true);
    private final TrueTypeFont menuOptionsF = new TrueTypeFont(new Font("Courier", Font.PLAIN, 24),true);
    private final String[] menuOptions = {"Start Game", "Multiplayer", "Options", "Quit"};

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        input = container.getInput();
        settings = Settings.getInstance();
        // Initialize buttons
        buttons = new Rectangle[menuOptions.length];
        for (int i = 0; i < menuOptions.length; i++) {
            buttons[i] = new Rectangle(container.getWidth()/2-(menuOptionsF.getWidth(menuOptions[i]))/2, 200 + i * SPACING, menuOptionsF.getWidth(menuOptions[i])+PADDING*2, menuOptionsF.getHeight()+PADDING*2);
        }
        //Load music
        menuMusic = new Music("music/title.ogg");
        menuMusic.loop(1.0f,settings.getMusicVolume());
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        g.setBackground(Color.gray);
        // Render Title
        titleF.drawString(container.getWidth()/2-titleF.getWidth(TITLE_STRING)/2, 100,TITLE_STRING);
        // Render buttons and menu options
        for (int i = 0; i < menuOptions.length; i++) {
            if (menuOptions[i].equals("Multiplayer")) {
                g.setColor(Color.darkGray);
            } else if (i == selectedIndex) {
                g.setColor(Color.yellow);
            } else {
                g.setColor(Color.white);
            }
            g.fill(buttons[i]);
            g.setColor(Color.black);
            menuOptionsF.drawString(buttons[i].getX()+PADDING, buttons[i].getY()+PADDING, menuOptions[i], g.getColor());
        }
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        if (input.isKeyPressed(settings.getKeyCode("up"))) {
            selectedIndex--;
            if (selectedIndex < 0) {
                selectedIndex = menuOptions.length - 1;
            }
        }
        if (input.isKeyPressed(settings.getKeyCode("up"))) {
            selectedIndex++;
            if (selectedIndex >= menuOptions.length) {
                selectedIndex = 0;
            }
        }
        // Handle mouse input to navigate the menu
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i].contains(input.getMouseX(), input.getMouseY())) {
                selectedIndex = i;
                if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
                    handleMenuSelection(container, game);
                }
            }
        }

        if (input.isKeyPressed(Input.KEY_ENTER)){
            handleMenuSelection(container, game);
        }
    }

    private void handleMenuSelection(GameContainer container, StateBasedGame game){
        switch (menuOptions[selectedIndex]) {
            case "Start Game":
                // Start Game
                menuMusic.stop();
                game.enterState(2); // Change to game state
                break;
            case "Multiplayer":
                System.out.println("Not yet implemented!");
                break;
            case "Options":
                game.enterState(1);
                break;
            case "Quit":
                container.exit();
                break;
        }
    }
}
