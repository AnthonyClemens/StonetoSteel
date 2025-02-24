package io.github.anthonyclemens.states;

import java.awt.Font;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import io.github.anthonyclemens.Settings;

public class SettingsMenu extends BasicGameState{
    //Variables
    private Rectangle[] buttons;
    private int selectedIndex = 0;
    private Settings settings;
    private Input input;
    private DisplayMode[] modes;
    private Set<String> validResolutions = new LinkedHashSet<>();

    private static final String TITLE_STRING = "Isometric Game";
    private static final int SPACING = 64;
    private static final int PADDING = 12;
    private final TrueTypeFont titleF = new TrueTypeFont(new Font("Courier", Font.BOLD, 32), true);
    private final TrueTypeFont menuOptionsF = new TrueTypeFont(new Font("Courier", Font.PLAIN, 24),true);
    private final String[] menuOptions = {"Start Game", "Multiplayer", "Options", "Quit"};

    @Override
    public int getID() {
        return 1;
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        input = container.getInput();
        settings = Settings.getInstance();
        try {
            modes = Display.getAvailableDisplayModes();
            Arrays.sort(modes, Comparator.comparingInt(mode -> mode.getWidth() * mode.getHeight()));
            for(DisplayMode m : modes){
                validResolutions.add(m.getWidth()+"x"+m.getHeight());
            }
        } catch (LWJGLException e) {
            System.err.println("Cannot get display modes");
        }
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        g.setBackground(Color.gray);
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        if (input.isKeyPressed(Input.KEY_ENTER)){
            applySettings(container);
        }
    }

    private void applySettings(GameContainer container) throws SlickException{
        AppGameContainer gc = (AppGameContainer) container;
        gc.setDisplayMode(800, 600, false);
    }

}
