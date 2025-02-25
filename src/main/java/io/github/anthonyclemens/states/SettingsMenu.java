package io.github.anthonyclemens.states;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import io.github.anthonyclemens.GUI.Button;
import io.github.anthonyclemens.GUI.Carousel;
import io.github.anthonyclemens.GUI.TextButton;
import io.github.anthonyclemens.GUI.ToggleButton;
import io.github.anthonyclemens.Settings;

public class SettingsMenu extends BasicGameState{
    //Variables
    private Rectangle[] buttons;
    private int selectedIndex = 0;
    private Settings settings;
    private Input input;
    List<String> validResolutions;
    Button testButton;
    Carousel testCarousel;
    TextButton testTextButton;
    ToggleButton testToggleButton;

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

        //Get Valid Screen Resolutions
        Set<String> validRes = new LinkedHashSet<>();
        try {
            DisplayMode[] modes = Display.getAvailableDisplayModes();
            Arrays.sort(modes, Comparator.comparingInt(mode -> mode.getWidth() * mode.getHeight()));
            for(DisplayMode m : modes){
                validRes.add(m.getWidth()+"x"+m.getHeight());
            }
            validResolutions = new ArrayList<>(validRes);
        } catch (LWJGLException e) {
            System.err.println("Cannot get display modes");
        }

        //Create the GUI
        testButton = new Button(new Image("textures/GUI/ArrowsLeft2.png"), container.getWidth()/2, container.getHeight()/2, 44, 84);
        testCarousel = new Carousel(validResolutions, new Image("textures/GUI/ArrowsLeft2.png"), new Image("textures/GUI/ArrowsRight2.png"), menuOptionsF, 200, 200, Color.black);
        testCarousel.move(container.getWidth()/2-(testCarousel.getRect().getWidth()/2), 200);
        testTextButton = new TextButton("Test Text Button", Color.black, menuOptionsF, Color.blue, container.getWidth()/2, 300, 200);
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        g.setBackground(Color.gray);
        g.setColor(Color.black);
        titleF.drawString(container.getWidth()/2-titleF.getWidth(TITLE_STRING)/2, 100, TITLE_STRING, g.getColor());
        testButton.render(g);
        testCarousel.render(g);
        testTextButton.render(g);
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        settings.setResolution(testCarousel.update(input));
        if (input.isKeyPressed(Input.KEY_ENTER)){
            applySettings(container);
        }
    }

    private void applySettings(GameContainer container) throws SlickException{
        AppGameContainer gc = (AppGameContainer) container;
        gc.setDisplayMode(settings.getWidth(), settings.getHeight(), false);
        testCarousel.move(container.getWidth()/2-(testCarousel.getRect().getWidth()/2), 200);
    }

}
